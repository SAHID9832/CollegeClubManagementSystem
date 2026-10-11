package com.collegeclub.model;

/**
 * Student Entity extending User.
 * Demonstrates inheritance, specialized attributes (studentId, rollNumber, department, course, year, semester),
 * and polymorphism.
 */
public class Student extends User {

    private String studentId;
    private String rollNumber;
    private String department;
    private String course = "MCA";
    private int year = 1;
    private int semester = 2;

    public Student() {
        super();
        setRole(Role.STUDENT);
    }

    public Student(int id, String username, String passwordHash, String email, String fullName,
                   String studentId, String department, int semester) {
        super(id, username, passwordHash, email, fullName, Role.STUDENT);
        setStudentId(studentId);
        setDepartment(department);
        setSemester(semester);
    }

    public Student(String username, String passwordHash, String email, String fullName,
                   String studentId, String department, int semester) {
        this(0, username, passwordHash, email, fullName, studentId, department, semester);
    }

    public Student(int id, String username, String passwordHash, String email, String fullName,
                   String rollNumber, String department, String course, int year) {
        super(id, username, passwordHash, email, fullName, Role.STUDENT);
        setRollNumber(rollNumber);
        setDepartment(department);
        setCourse(course);
        setYear(year);
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (studentId != null) {
            this.studentId = studentId.trim();
            this.rollNumber = this.studentId;
        }
    }

    public String getRollNumber() {
        return rollNumber != null ? rollNumber : studentId;
    }

    public void setRollNumber(String rollNumber) {
        if (rollNumber != null) {
            this.rollNumber = rollNumber.trim();
            this.studentId = this.rollNumber;
        }
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department != null) {
            this.department = department.trim();
        }
    }

    public String getCourse() {
        return course != null ? course : "MCA";
    }

    public void setCourse(String course) {
        if (course != null && !course.trim().isEmpty()) {
            this.course = course.trim();
        }
    }

    public int getYear() {
        return year > 0 ? year : (semester > 0 ? (semester + 1) / 2 : 1);
    }

    public void setYear(int year) {
        this.year = year > 0 ? year : 1;
        this.semester = this.year * 2;
    }

    public int getSemester() {
        return semester > 0 ? semester : year * 2;
    }

    public void setSemester(int semester) {
        this.semester = semester;
        this.year = semester > 0 ? (semester + 1) / 2 : 1;
    }

    @Override
    public void displayUser() {
        super.displayUser();
        System.out.println("Student ID: " + studentId);
        System.out.println("Department: " + department);
        System.out.println("Course    : " + course);
        System.out.println("Year      : " + year);
        System.out.println("Semester  : " + semester);
    }
}
