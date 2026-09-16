package com.iwfc.users;

import java.util.Objects;

public abstract class User {

    private final String userId;
    private final String fullName;
    private final String email;
    private final Role role;

    protected User(String userId, String fullName, String email, Role role) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.fullName = Objects.requireNonNull(fullName, "fullName must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public abstract String getDashboardSummary();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return role + " " + fullName + " (" + userId + ")";
    }
}
