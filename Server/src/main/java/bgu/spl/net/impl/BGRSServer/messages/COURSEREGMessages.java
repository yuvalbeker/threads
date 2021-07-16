package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;
import bgu.spl.net.impl.BGRSServer.Database;


public class COURSEREGMessages implements MessageCourseNumber{
    private final short Opcode = 5;
    private final int CourseNumber;

    public COURSEREGMessages(int courseNumber) {
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

        if (client_user == null || !db.getUserPermissions(client_user).equals("Student") || !db.addStudentToCourse(CourseNumber, client_user)) // not login or not a student
            return new ERRORMessages(Opcode);
        return new ACKMessages(Opcode);
    }
    }
