package com.collegeclub.service.impl;

import com.collegeclub.dao.ClubDao;
import com.collegeclub.dao.MembershipDao;
import com.collegeclub.dao.impl.ClubDaoImpl;
import com.collegeclub.dao.impl.MembershipDaoImpl;
import com.collegeclub.exception.ClubNotFoundException;
import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.exception.DuplicateMembershipException;
import com.collegeclub.exception.InvalidInputException;
import com.collegeclub.exception.MembershipLimitReachedException;
import com.collegeclub.model.Club;
import com.collegeclub.model.User;
import com.collegeclub.service.ClubOperations;

import java.sql.SQLException;
import java.util.List;

public class ClubService implements ClubOperations {

    private final ClubDao clubDao;
    private final MembershipDao membershipDao;

    public ClubService() {
        this(new ClubDaoImpl(), new MembershipDaoImpl());
    }

    public ClubService(ClubDao clubDao, MembershipDao membershipDao) {
        this.clubDao = clubDao;
        this.membershipDao = membershipDao;
    }

    @Override
    public Club createClub(Club club) throws InvalidInputException, ClubSystemException {
        if (club == null) {
            throw new InvalidInputException("Club data cannot be null.");
        }
        if (club.getName() == null || club.getName().trim().length() < 3) {
            throw new InvalidInputException("Club name must contain at least 3 characters.");
        }
        if (club.getCoordinator() == null || club.getCoordinator().trim().isEmpty()) {
            throw new InvalidInputException("Coordinator name is required.");
        }

        try {
            if (clubDao.findByName(club.getName()).isPresent()) {
                throw new DuplicateMembershipException("A club with name '" + club.getName() + "' already exists.");
            }
            return clubDao.save(club);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error saving club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public Club getClubById(int id) throws ClubNotFoundException, ClubSystemException {
        try {
            return clubDao.findById(id)
                    .orElseThrow(() -> new ClubNotFoundException("Club with ID " + id + " not found."));
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public List<Club> getAllClubs() throws ClubSystemException {
        try {
            return clubDao.findAll();
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving clubs: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public List<Club> searchClubs(String query, String category) throws ClubSystemException {
        try {
            return clubDao.search(query, category);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error searching clubs: " + e.getMessage(), 500, e);
        }
    }

    public List<Club> searchClubs(String query) throws ClubSystemException {
        return searchClubs(query, null);
    }

    public List<Club> getClubsByCategory(String category) throws ClubSystemException {
        return searchClubs(null, category);
    }

    public List<Club> getStudentClubs(int userId) throws ClubSystemException {
        return getUserClubs(userId);
    }

    public Club createClub(String name, String category, String description, int coordinatorId, int capacity) throws ClubSystemException, InvalidInputException {
        Club c = new Club(name, description, category, "Faculty Coordinator", capacity);
        return createClub(c);
    }

    @Override
    public boolean joinClub(int clubId, int userId) throws ClubNotFoundException, DuplicateMembershipException, MembershipLimitReachedException, ClubSystemException {
        Club club = getClubById(clubId);

        try {
            if (membershipDao.isMember(clubId, userId)) {
                throw new DuplicateMembershipException("You are already a member of this club.");
            }

            int currentCount = membershipDao.countMembers(clubId);
            if (club.getCapacity() > 0 && currentCount >= club.getCapacity()) {
                throw new MembershipLimitReachedException("Club has reached its maximum capacity of " + club.getCapacity() + " members.");
            }

            return membershipDao.addMembership(clubId, userId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error joining club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public boolean leaveClub(int clubId, int userId) throws ClubNotFoundException, ClubSystemException {
        getClubById(clubId); // verifies existence

        try {
            if (!membershipDao.isMember(clubId, userId)) {
                throw new ClubSystemException("NOT_A_MEMBER", "User is not currently a member of this club.", 400);
            }
            return membershipDao.removeMembership(clubId, userId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error leaving club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public boolean updateClub(Club club) throws ClubNotFoundException, InvalidInputException, ClubSystemException {
        if (club == null || club.getId() <= 0) {
            throw new InvalidInputException("Valid Club ID is required for update.");
        }
        getClubById(club.getId()); // ensure exists

        try {
            return clubDao.update(club);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error updating club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public boolean deleteClub(int clubId) throws ClubNotFoundException, ClubSystemException {
        getClubById(clubId); // ensure exists
        try {
            return clubDao.delete(clubId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error deleting club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public List<Club> getUserClubs(int userId) throws ClubSystemException {
        try {
            return membershipDao.findClubsByUserId(userId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving user clubs: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public List<User> getClubMembers(int clubId) throws ClubNotFoundException, ClubSystemException {
        getClubById(clubId);
        try {
            return membershipDao.findMembersByClubId(clubId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving club members: " + e.getMessage(), 500, e);
        }
    }
}
