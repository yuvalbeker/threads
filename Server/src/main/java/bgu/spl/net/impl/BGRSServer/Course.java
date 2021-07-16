package bgu.spl.net.impl.BGRSServer;

import java.util.LinkedList;

public class Course {
    private int id;
    private final String coursename;
    private LinkedList<Integer> kdam;
    private int numOfMaxStudents;
    private final LinkedList<String> registeredUsers;
    private int availableSeats;

    public Course(int id, String coursename, LinkedList<Integer> kdam, int numOfMaxStudents) {
        this.id = id;
        this.coursename = coursename;
        this.kdam = kdam;
        this.numOfMaxStudents = numOfMaxStudents;
        this.registeredUsers = new LinkedList<>();
        this.availableSeats = numOfMaxStudents;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoursename() {
        return coursename;
    }

    public LinkedList<Integer> getKdam() {
        return kdam;
    }

    public void setKdam(LinkedList<Integer> kdam) {
        this.kdam = kdam;
    }

    public int getNumOfMaxStudents() {
        return numOfMaxStudents;
    }

    public void setNumOfMaxStudents(int numOfMaxStudents) {
        this.numOfMaxStudents = numOfMaxStudents;
    }

    // add student to the course. If the student is already registered or that there is no available seats - do nothing.
    public synchronized void addStudent(String username){
        if(!IsStudentRegistered(username) && getAvailableSeats()>0){
            registeredUsers.add(username);
            availableSeats--;
        }
    }

    // remove student from the course. If the student is not registered - do nothing.
    public synchronized void removeStudent(String username){
        if(IsStudentRegistered(username)){
            registeredUsers.remove(username);
            availableSeats++;
        }
    }

    public synchronized boolean IsStudentRegistered(String username){
        return registeredUsers.contains(username);
    }

    public synchronized LinkedList<String> getRegisteredUsers() {
        registeredUsers.sort(String::compareTo);
        return registeredUsers;
    }

    public synchronized int getAvailableSeats() {
        return availableSeats;
    }

}


