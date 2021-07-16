#include <KeyboardReader.h>
#include <SocketReader.h>
#include <thread>
#include <mutex>              // std::mutex, std::unique_lock
#include <condition_variable> // std::condition_variable

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }

    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    std::mutex mtx;
    std::condition_variable cv;
    bool shouldTerminate = false; //shared resource

    KeyboardReader keyboardReader(&connectionHandler, mtx, cv, shouldTerminate);
    SocketReader socketReader(&connectionHandler, mtx, cv, shouldTerminate);

    std::thread th1(&KeyboardReader::run, &keyboardReader); //start
    std::thread th2(&SocketReader::run, &socketReader); //start

    th1.join();
    th2.join(); //the client (main thread) goes into a waiting state until the run function is done.

    return 0;
}