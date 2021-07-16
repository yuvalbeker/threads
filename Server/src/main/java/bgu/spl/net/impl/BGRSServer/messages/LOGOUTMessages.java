package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;
import bgu.spl.net.impl.BGRSServer.Database;

public class LOGOUTMessages implements Message{
    private final short Opcode = 4;

    @Override
    public short getOpcode() {
        return Opcode;
    }

    // checks if the username is login - then logout
    public Message execute(BGRSProtocol protocol) {
        Database db = Database.getInstance();
        String client_user = protocol.getUserName();

        if (client_user == null || !db.logout(client_user)) // If no user is logged in or user not logged in, sends an ERROR message
            return new ERRORMessages(Opcode);

        //logout success
        else{
            protocol.setUserName(null);
            protocol.setShouldTerminate(true);
            return new ACKMessages(Opcode);
        }
    }
}
