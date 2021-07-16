package bgu.spl.net.impl.BGRSServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
    private final ConcurrentHashMap<Integer, Course> coursesinfo;
    private final ConcurrentHashMap<String, User> userinfo;



    private static class DatabaseHolder{
        private static Database instance = new Database();
    }

    //to prevent user from creating new Database
    private Database() {
        coursesinfo = new ConcurrentHashMap<>();
        userinfo = new ConcurrentHashMap<>();
        initialize("./Courses.txt");
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return DatabaseHolder.instance;
    }

    /**
     * loades the courses from the file path specified
     * into the Database, returns true if successful.
     */
    boolean initialize(String coursesFilePath) {
        try {
            File myObj = new File(coursesFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                int id = Integer.parseInt(data.substring(0, data.indexOf('|'))); // export course id
                data = data.substring(data.indexOf('|') + 1);

                String coursename = data.substring(0, data.indexOf('|')); // export course name
                data = data.substring(data.indexOf('|') + 1);

                String kdamcourse = data.substring(0, data.indexOf('|')); // export KdamCoursesList
                LinkedList<Integer> kdam = new LinkedList<>();

                while (kdamcourse.indexOf(',') != -1) {
                    kdam.add(Integer.parseInt(kdamcourse.substring(1, kdamcourse.indexOf(','))));
                    kdamcourse = '[' + kdamcourse.substring(kdamcourse.indexOf(',') + 1);
                }
                if(!kdamcourse.equals("[]")){
                    kdam.add(Integer.parseInt(kdamcourse.substring(1, kdamcourse.length()-1)));
                }
                data = data.substring(data.indexOf('|') + 1);

                int numOfMaxStudents = Integer.parseInt(data); // export numOfMaxStudents

                Course course = new Course(id, coursename, kdam, numOfMaxStudents);

                coursesinfo.put(id, course);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    //return true if the username is already registered
    public  boolean Isregister(String username){
            return userinfo.containsKey(username);
    }

    //return true if the username is login. return false if the user does not exist
    public boolean Islogin(String username){
        return userinfo.containsKey(username) && userinfo.get(username).isLogin();
    }

    //registers user - return false if failed
    public boolean register(String username, String password, String permissions){
        return (userinfo.putIfAbsent(username, new User(username, password, permissions))) == null; // prevent two clients to register with the same username
    }

    //login user if exist and password is correct - return false if the user is not registered or already login
    public boolean login(String username, String password) {
        if(userinfo.get(username) != null){
            synchronized (userinfo.get(username)) { //to prevent two clients to login with the same username
                if (!Islogin(username) && password.equals(getUserPassword(username))) {
                    userinfo.get(username).setLogin(true);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    //logout user if exist - return false if the user is not registered or login
    public boolean logout(String username) {
        if (Islogin(username)) {
            userinfo.get(username).setLogin(false);
            return true;
        }
        return false;
    }


    //return the user's courses. return null if the user doesn't exist
    public LinkedList<Integer> getUserCourses(String username){ //// to check
        if(Isregister(username)){
            return userinfo.get(username).getCourses();
        }
        return null;
    }

    //return the user's password, return null if the user is not exist
    public String getUserPassword(String username){
        if(userinfo.containsKey(username))
            return userinfo.get(username).getPassword();
        return null;
    }

    //return the user's Permissions, return null if the user is not exist
    public String getUserPermissions(String username){
        if(Isregister(username))
            return userinfo.get(username).getPermissions();
        return null;
    }

    //return whether the user is registered to the course, return false if the user is not exist
    public Boolean IsUserRegisteredToCourse(String username, int courseId){
        if(Isregister(username))
            return (userinfo.get(username)).ExistCourse(courseId);
        return false;
    }

        //return true is the course is exist.
    public boolean ExistCourse(int courseId){
        return coursesinfo.containsKey(courseId);
    }

    //return the name of the course. return null if the course doesn't exist
    public String getCourseName(int courseId){
        if(ExistCourse(courseId)){
            return coursesinfo.get(courseId).getCoursename();
        }
        return null;
    }


    //return a list of the kdam courses. return null if the course doesn't exist
    public LinkedList<Integer> getKdamCourses(int courseId){
        if(ExistCourse(courseId)){
            return coursesinfo.get(courseId).getKdam();
        }
        return null;
    }


    //register student to course - return false if failed
    public boolean addStudentToCourse(int courseId, String username){
        if (ExistCourse(courseId)) {
            // check if the student has all kdams
            LinkedList<Integer> kdams = getKdamCourses(courseId);
            LinkedList<Integer> usercourses = getUserCourses(username);
            if (usercourses.size() >= kdams.size()) {
                for (int kdam : kdams) { //the student does not have all the Kdam courses
                    if (!usercourses.contains(kdam))
                        return false;
                }
                synchronized(coursesinfo.get(courseId)){
                    if(coursesinfo.get(courseId).getAvailableSeats() > 0){
                        coursesinfo.get(courseId).addStudent(username);
                        userinfo.get(username).AddCourse(courseId);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //unregister student from course
    public void removeStudentFromCourse(int courseId, String username){
        if(Isregister(username) && ExistCourse(courseId)){
            coursesinfo.get(courseId).removeStudent(username);
            userinfo.get(username).RemoveCourse(courseId);
        }
    }

    public String CourseStat(int courseId) {
        if (ExistCourse(courseId)) {
            String courseName = getCourseName(courseId);
            synchronized (coursesinfo.get(courseId)){
                String listOfStudents = coursesinfo.get(courseId).getRegisteredUsers().toString().replaceAll(" ", "");
                String numOfSeatsAvailable = coursesinfo.get(courseId).getAvailableSeats() + "/" + coursesinfo.get(courseId).getNumOfMaxStudents();
                return ("Course: (" + courseId + ") " + courseName + "\n" + "Seats Available: " + numOfSeatsAvailable + "\n" + "Students Registered: " + listOfStudents);
            }
        }
        return null;
    }

}
