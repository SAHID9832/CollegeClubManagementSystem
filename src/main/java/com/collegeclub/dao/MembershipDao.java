package com.collegeclub.dao;

import com.collegeclub.model.Club;
import com.collegeclub.model.User;
import java.sql.SQLException;
import java.util.List;

public interface MembershipDao {
    boolean addMembership(int clubId, int userId) throws SQLException;
    boolean removeMembership(int clubId, int userId) throws SQLException;
    boolean isMember(int clubId, int userId) throws SQLException;
    int countMembers(int clubId) throws SQLException;
    List<Club> findClubsByUserId(int userId) throws SQLException;
    List<User> findMembersByClubId(int clubId) throws SQLException;
}
