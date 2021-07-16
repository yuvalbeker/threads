package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;
import bgu.spl.net.impl.BGRSServer.Database;

public class ADMINREGMessages implements MessageUsernamePassword{
    private final short Opcode = 1;
    private final String username;
    private final String password;


    public ADMINREGMessages(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public short getOpcode() {
        return Opcode;
    }

    // register a user according to its type. return reply message with the opcode
    public Message execute(BGRSProtocol protocol) {
        Database db = Database.getInstance();
        String client_user = protocol.getUserName();
        String permission = "Admin";

        if (client_user != null || !db.register(username, password, permission)) // the username is already registered or login
            return new ERRORMessages(Opcode);
        else {
            return new ACKMessages(Opcode);
        }
    }
}
