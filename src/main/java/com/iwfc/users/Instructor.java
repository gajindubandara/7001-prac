package com.iwfc.users;

public class Instructor extends User {

    public Instructor(String userId, String fullName, String email) {
        super(userId, fullName, email, Role.INSTRUCTOR);
    }

    @Override
    public String getDashboardSummary() {
        return "Instructor access: schedule sessions, track equipment usage, and report maintenance issues.";
    }
}
