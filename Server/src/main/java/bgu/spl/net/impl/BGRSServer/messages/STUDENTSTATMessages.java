package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;
import bgu.spl.net.impl.BGRSServer.Database;

public class STUDENTSTATMessages implements Message<String>{
    private final short Opcode = 8;
    private final String Student_Username;

    public STUDENTSTATMessages(String student_Username) {
        Student_Username = student_Username;
    }

    public String getStudent_Username() {
        return Student_Username;
    }

    @Override
    public short getOpcode() {
        return Opcode;
    }

    public Message execute(BGRSProtocol protocol) {
        Database db = Database.getInstance();
        String client_user = protocol.getUserName();

        if (client_user == null || !db.getUserPermissions(client_user).equals("Admin") || !db.Isregister(Student_Username) || !db.getUserPermissions(Student_Username).equals("Student")) // not login or not an Admin
            return new ERRORMessages(Opcode);

        String courses = db.getUserCourses(Student_Username).toString();
        courses = courses.replaceAll(" ", "");
        return new ACKMessages(Opcode, "Student: " + Student_Username + "\n" + "Courses: " +courses);
    }
}
