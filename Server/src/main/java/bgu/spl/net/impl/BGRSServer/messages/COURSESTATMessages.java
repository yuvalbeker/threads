package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;
import bgu.spl.net.impl.BGRSServer.Database;

public class COURSESTATMessages implements MessageCourseNumber{
    private final short Opcode = 7;
    private final int CourseNumber;

    public COURSESTATMessages(int  courseNumber) {
        CourseNumber = courseNumber;
    }

    public int  getCourseNumber() {
        return CourseNumber;
    }

    @Override
    public short getOpcode() {
        return Opcode;
    }

    public Message execute(BGRSProtocol protocol) {
        Database db = Database.getInstance();
        String client_user = protocol.getUserName();

        if (client_user == null || db.getUserPermissions(client_user).equals("Student") || !db.ExistCourse(CourseNumber)) // not login or not an Admin
            return new ERRORMessages(Opcode);
        else{
            return new ACKMessages(Opcode, db.CourseStat(CourseNumber));
        }
    }
}
