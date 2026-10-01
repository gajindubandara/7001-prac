package com.iwfc.users;

// Extends User and fills in the one abstract method - the shared id/name/email/role
// handling and validation all live once in User's constructor, not repeated here.
public class Administrator extends User {

    public Administrator(String userId, String fullName, String email) {
        super(userId, fullName, email, Role.ADMINISTRATOR);
    }

    @Override
    public String getDashboardSummary() {
        return "Administrator access: manage equipment inventory, user accounts, and the global maintenance log.";
    }
}
