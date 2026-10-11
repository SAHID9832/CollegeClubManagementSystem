package com.collegeclub.service;

import com.collegeclub.exception.ClubNotFoundException;
import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.exception.DuplicateMembershipException;
import com.collegeclub.exception.InvalidInputException;
import com.collegeclub.exception.MembershipLimitReachedException;
import com.collegeclub.model.Club;
import com.collegeclub.model.User;

import java.util.List;

public interface ClubOperations {
    Club createClub(Club club) throws InvalidInputException, ClubSystemException;
    Club getClubById(int id) throws ClubNotFoundException, ClubSystemException;
    List<Club> getAllClubs() throws ClubSystemException;
    List<Club> searchClubs(String query, String category) throws ClubSystemException;
    boolean joinClub(int clubId, int userId) throws ClubNotFoundException, DuplicateMembershipException, MembershipLimitReachedException, ClubSystemException;
    boolean leaveClub(int clubId, int userId) throws ClubNotFoundException, ClubSystemException;
    boolean updateClub(Club club) throws ClubNotFoundException, InvalidInputException, ClubSystemException;
    boolean deleteClub(int clubId) throws ClubNotFoundException, ClubSystemException;
    List<Club> getUserClubs(int userId) throws ClubSystemException;
    List<User> getClubMembers(int clubId) throws ClubNotFoundException, ClubSystemException;
}
