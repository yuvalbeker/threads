package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class Reactor<T> implements Server<T> {

    private final int port;
    private final Supplier<MessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> readerFactory;
    private final ActorThreadPool pool;
    private Selector selector;

    private Thread selectorThread;
    private final ConcurrentLinkedQueue<Runnable> selectorTasks = new ConcurrentLinkedQueue<>();

    public Reactor(
            int numThreads,
            int port,
            Supplier<MessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> readerFactory) {

        this.pool = new ActorThreadPool(numThreads);
        this.port = port;
        this.protocolFactory = protocolFactory;
        this.readerFactory = readerFactory;
    }

    @Override
    public void serve() {
	selectorThread = Thread.currentThread();
        try (Selector selector = Selector.open();
                ServerSocketChannel serverSock = ServerSocketChannel.open()) {

            this.selector = selector; //just to be able to close

            serverSock.bind(new InetSocketAddress(port));
            serverSock.configureBlocking(false);
            serverSock.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Server started");

            while (!Thread.currentThread().isInterrupted()) {

                selector.select(); //Blocks until at least one channel is selected
                runSelectionThreadTasks(); //removes a task (Runnable) from the selectorTasks if there is one and runs it

                for (SelectionKey key : selector.selectedKeys()) {

                    if (!key.isValid()) { //A key is valid upon creation and remains so until it is cancelled, its channel is closed, or its selector is closed.
                        continue;
                    } else if (key.isAcceptable()) {
                        handleAccept(serverSock, selector);
                    } else {
                        handleReadWrite(key);
                    }
                }

                selector.selectedKeys().clear(); //clear the selected keys set so that we can know about new events

            }

        } catch (ClosedSelectorException ex) {
            //do nothing - server was requested to be closed
        } catch (IOException ex) {
            //this is an error
            ex.printStackTrace();
        }

        System.out.println("server closed!!!");
        pool.shutdown();
    }

    /*package*/ void updateInterestedOps(SocketChannel chan, int ops) {
        final SelectionKey key = chan.keyFor(selector);
        if (Thread.currentThread() == selectorThread) {
            key.interestOps(ops); // Sets this key's interest set to the given value. only the selectorThread can do that directly
        } else { // when this method is called by one of the actors by lambda (for example in continueread function).
            selectorTasks.add(() -> {
                key.interestOps(ops); //only the selectorThread can set the ops of a key. so we add this task to the reactor
            });
            selector.wakeup(); //Causes the first selection operation that has not yet returned to return immediately.
        }
    }


    private void handleAccept(ServerSocketChannel serverChan, Selector selector) throws IOException {
        SocketChannel clientChan = serverChan.accept(); //Accepts a connection made to this channel's socket (ServerSocket). The socket channel returned by this method, if any, will be in blocking mode regardless of the blocking mode of this channel.
        clientChan.configureBlocking(false); // set the client channel to non-blocking
        final NonBlockingConnectionHandler<T> handler = new NonBlockingConnectionHandler<>(
                readerFactory.get(),
                protocolFactory.get(),
                clientChan,
                this);
        clientChan.register(selector, SelectionKey.OP_READ, handler);
    }

    private void handleReadWrite(SelectionKey key) {
        @SuppressWarnings("unchecked")
        NonBlockingConnectionHandler<T> handler = (NonBlockingConnectionHandler<T>) key.attachment();

        if (key.isReadable()) { //Tests whether this key's channel is ready for reading.
            Runnable task = handler.continueRead();
            if (task != null) {
                pool.submit(handler, task); //ask the thread pool to do the reading
            }
        }

	    if (key.isValid() && key.isWritable()) { //Tests whether this key's channel is ready for writing.
            handler.continueWrite();
        }
    }

    private void runSelectionThreadTasks() { //removes a task (Runnable) from the selectorTasks if there is one and run it
        while (!selectorTasks.isEmpty()) {
            selectorTasks.remove().run();
        }
    }

    @Override
    public void close() throws IOException {
        selector.close();
    }

}
