package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;
import bgu.spl.net.impl.BGRSServer.Database;

public class MYCOURSESMessages implements Message{
    private final short Opcode = 11;

    @Override
    public short getOpcode() {
        return Opcode;
    }

    public Message execute(BGRSProtocol protocol) {
        Database db = Database.getInstance();
        String client_user = protocol.getUserName();

        if (client_user == null || !db.getUserPermissions(client_user).equals("Student")) // not login or not a Student
            return new ERRORMessages(Opcode);
        String courses = db.getUserCourses(client_user).toString();
        courses = courses.replaceAll(" ", "");
        return new ACKMessages(Opcode, courses);
    }

}
