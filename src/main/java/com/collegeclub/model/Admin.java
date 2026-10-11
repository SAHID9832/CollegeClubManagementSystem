package com.collegeclub.model;

/**
 * Admin Entity extending User.
 * Demonstrates inheritance and administrative privileges.
 */
public class Admin extends User {

    private String adminLevel;
    private String officeLocation;

    public Admin() {
        super();
        setRole(Role.ADMIN);
    }

    public Admin(int id, String username, String passwordHash, String email, String fullName,
                 String adminLevel, String officeLocation) {
        super(id, username, passwordHash, email, fullName, Role.ADMIN);
        this.adminLevel = adminLevel;
        this.officeLocation = officeLocation;
    }

    public Admin(String username, String passwordHash, String email, String fullName,
                 String adminLevel, String officeLocation) {
        this(0, username, passwordHash, email, fullName, adminLevel, officeLocation);
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        if (adminLevel != null) {
            this.adminLevel = adminLevel.trim();
        }
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        if (officeLocation != null) {
            this.officeLocation = officeLocation.trim();
        }
    }

    @Override
    public void displayUser() {
        super.displayUser();
        System.out.println("Admin Lvl : " + adminLevel);
        System.out.println("Office    : " + officeLocation);
    }
}
