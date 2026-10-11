package com.collegeclub.dao;

import com.collegeclub.model.Club;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ClubDao {
    Club save(Club club) throws SQLException;
    Optional<Club> findById(int id) throws SQLException;
    Optional<Club> findByName(String name) throws SQLException;
    List<Club> findAll() throws SQLException;
    List<Club> search(String query, String category) throws SQLException;
    boolean update(Club club) throws SQLException;
    boolean delete(int id) throws SQLException;
    int countTotalClubs() throws SQLException;
}
