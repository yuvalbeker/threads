package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        Database database = Database.getInstance(); //one shared object

        Server.reactor(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[0]), //port
                () -> new BGRSProtocol(), //protocol factory
                BGRSMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();

    }
}

