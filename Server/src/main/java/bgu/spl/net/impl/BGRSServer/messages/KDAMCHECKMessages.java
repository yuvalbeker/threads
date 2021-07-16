package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;
import bgu.spl.net.impl.BGRSServer.Database;

public class KDAMCHECKMessages implements MessageCourseNumber{
    private final short Opcode = 6;
    private final int CourseNumber;

    public KDAMCHECKMessages(int courseNumber) {
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

        if(client_user != null && !db.getUserPermissions(protocol.getUserName()).equals("Admin") && db.ExistCourse(CourseNumber)){ // checks if not login or the course not exist
            String kdamlist = db.getKdamCourses(CourseNumber).toString();
            kdamlist = kdamlist.replaceAll(" ", "");
            return new ACKMessages(Opcode, kdamlist);
        }
        return new ERRORMessages(Opcode);
    }
}
