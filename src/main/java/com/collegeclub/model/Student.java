package com.collegeclub.model;

/**
 * Student Entity extending User.
 * Demonstrates inheritance, specialized attributes (studentId, department, semester),
 * and polymorphism.
 */
public class Student extends User {

    private String studentId;
    private String department;
    private int semester;

    public Student() {
        super();
        setRole(Role.STUDENT);
    }

    public Student(int id, String username, String passwordHash, String email, String fullName,
                   String studentId, String department, int semester) {
        super(id, username, passwordHash, email, fullName, Role.STUDENT);
        this.studentId = studentId;
        this.department = department;
        this.semester = semester;
    }

    public Student(String username, String passwordHash, String email, String fullName,
                   String studentId, String department, int semester) {
        this(0, username, passwordHash, email, fullName, studentId, department, semester);
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (studentId != null) {
            this.studentId = studentId.trim();
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

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    @Override
    public void displayUser() {
        super.displayUser();
        System.out.println("Student ID: " + studentId);
        System.out.println("Department: " + department);
        System.out.println("Semester  : " + semester);
    }
}
