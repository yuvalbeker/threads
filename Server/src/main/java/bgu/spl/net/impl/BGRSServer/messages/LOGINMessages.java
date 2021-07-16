package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;
import bgu.spl.net.impl.BGRSServer.Database;

public class LOGINMessages implements MessageUsernamePassword{
    private final short Opcode = 3;
    private String username;
    private String password;


    public LOGINMessages(String username, String password) {
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

    public Message execute(BGRSProtocol protocol) {
        Database db = Database.getInstance();
        String client_user = protocol.getUserName();


        // checks if the username is registered, someone already logged in with the same user or password doesn’t match
        if (client_user != null || !db.login(username, password))
            return new ERRORMessages(Opcode);
        //login success
        else {
            protocol.setUserName(username);
            return new ACKMessages(Opcode);
        }
    }
}
