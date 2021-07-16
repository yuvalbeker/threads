package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;
import bgu.spl.net.impl.BGRSServer.Database;

public class UNREGISTERMessages implements MessageCourseNumber{
    private final short Opcode = 10;
    private final int CourseNumber;

    public UNREGISTERMessages(int courseNumber) {
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

        if (client_user == null || !db.getUserPermissions(client_user).equals("Student") || !db.IsUserRegisteredToCourse(client_user, CourseNumber)){ // not login or not a Student or not registered to course
            return new ERRORMessages(Opcode);
    }
        else{
            db.removeStudentFromCourse(CourseNumber, client_user);
            return new ACKMessages(Opcode);
        }
    }
}
