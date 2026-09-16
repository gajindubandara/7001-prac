package com.iwfc.users;

public class Administrator extends User {

    public Administrator(String userId, String fullName, String email) {
        super(userId, fullName, email, Role.ADMINISTRATOR);
    }

    @Override
    public String getDashboardSummary() {
        return "Administrator access: manage equipment inventory, user accounts, and the global maintenance log.";
    }
}
