package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;
import bgu.spl.net.impl.BGRSServer.Database;

public class ISREGISTEREDMessages implements MessageCourseNumber{
    private final short Opcode = 9;
    private final int CourseNumber;

    public ISREGISTEREDMessages(int courseNumber) {
        CourseNumber = courseNumber;
    }

    public int getCourseNumber() {
        return CourseNumber;
    }

    @Override
    public short getOpcode() {
        return Opcode;
    }

    public Message execute(BGRSProtocol protocol) {
        Database db = Database.getInstance();
        String client_user = protocol.getUserName();

        if (client_user == null || !db.getUserPermissions(client_user).equals("Student")) // not login or not a Student
            return new ERRORMessages(Opcode);
        if (db.IsUserRegisteredToCourse(client_user, CourseNumber))
            return new ACKMessages(Opcode, "REGISTERED");
        else
            return new ACKMessages(Opcode, "NOT REGISTERED");
    }
}
