package com.medical.appointments.exception;

public final class ErrorMessages {

    private ErrorMessages() {}

    // user
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INVALID_PASSWORD = "Invalid password";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String SAME_PASSWORD = "The new password must be different from the current one";
    public static final String SELF_DELETE = "You cannot delete your own account. Use DELETE /users/current instead";
    public static final String USER_MUST_HAVE_AT_LEAST_ONE_ROLE = "User must have at least one role";
    public static final String ROLE_ALREADY_ASSIGNED = "Role already assigned";
    public static final String ROLE_NOT_ASSIGNED = "Role not assigned";
    public static final String CANNOT_REMOVE_LAST_ADMIN = "Cannot remove the last administrator";

    // auth
    public static final String INVALID_CREDENTIALS = "Invalid credentials";

    // token
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";

    // profile
    public static final String PROFILE_NOT_FOUND = "Profile not found";
    public static final String PROFILE_ALREADY_EXISTS = "Profile already exists";

    // reference
    public static final String REFERENCE_ALREADY_EXISTS = "Reference already exists";
    public static final String REFERENCE_NOT_FOUND = "Reference not found";
    public static final String REFERENCE_ALREADY_ASSIGNED = "Reference already assigned";
    public static final String REFERENCE_NOT_ASSIGNED = "Reference not assigned";
}
