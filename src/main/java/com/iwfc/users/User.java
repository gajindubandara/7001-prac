package com.iwfc.users;

import java.util.Objects;

// Encapsulation: every field is private final, so once a User exists its identity
// can't be changed from outside - only the getters below can read it.
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

    // Abstract method = compile-time contract, runtime polymorphism: which version runs
    // depends on the real subclass (Administrator/Instructor/Member), not on the `User`
    // type a caller might be holding the reference as.
    public abstract String getDashboardSummary();

    // Equality by id only - two User objects with the same id count as "the same person"
    // even if other fields somehow differed. IWFCFacade's self-booking check
    // (actor.equals(booking.getMember())) relies on exactly this.
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
