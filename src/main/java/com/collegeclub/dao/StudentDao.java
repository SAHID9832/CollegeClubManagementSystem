package com.collegeclub.dao;

import com.collegeclub.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StudentDao {
    Student save(Student student) throws SQLException;
    Optional<Student> findByUserId(int userId) throws SQLException;
    Optional<Student> findByRollNumber(String rollNumber) throws SQLException;
    boolean updateProfile(int userId, String fullName, String email, String department, String course, int year) throws SQLException;
    List<Student> findAll() throws SQLException;
    int countTotalStudents() throws SQLException;
}
