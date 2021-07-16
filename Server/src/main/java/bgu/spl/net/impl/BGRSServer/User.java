package bgu.spl.net.impl.BGRSServer;

import java.util.LinkedList;

public class User {
    private String username;
    private String password;
    private String permissions;
    private LinkedList<Integer> courses;
    private boolean isLogin;

    public User(String username, String password, String permissions) {
        this.username = username;
        this.password = password;
        this.permissions = permissions;
        this.courses = new LinkedList<>();
        this.isLogin = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public LinkedList<Integer> getCourses() {
        return courses;
    }

    public void setCourses(LinkedList<Integer> courses) {
        this.courses = courses;
    }

    // synchronized - to prevent two clients to login to the same user
    public synchronized boolean isLogin() {
        return isLogin;
    }

    // synchronized - to prevent two clients to login to the same user
    public synchronized void setLogin(boolean login) {
        isLogin = login;
    }

    // add course to the user's courses list - if the list already contains the course - do nothing
    public void AddCourse(Integer courseId){
        if(!ExistCourse(courseId))
            courses.add(courseId);
    }

    public void RemoveCourse(Integer courseId){
        courses.remove(courseId);
    }

    //return whether the user is registered to the course
    public Boolean ExistCourse(Integer courseId){
        return courses.contains(courseId);
    }

}
