package com.iwfc.users;

public class Member extends User {

    public Member(String userId, String fullName, String email) {
        super(userId, fullName, email, Role.MEMBER);
    }

    @Override
    public String getDashboardSummary() {
        return "Member access: view session schedules, book sessions, and receive notifications.";
    }
}
