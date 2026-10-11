package com.collegeclub.dao;

import com.collegeclub.model.Notification;

import java.sql.SQLException;
import java.util.List;

public interface NotificationDao {
    Notification save(Notification notification) throws SQLException;
    List<Notification> findByUserId(int userId) throws SQLException;
    boolean markAsRead(int notificationId, int userId) throws SQLException;
    boolean markAllAsRead(int userId) throws SQLException;
    int countUnread(int userId) throws SQLException;
}
