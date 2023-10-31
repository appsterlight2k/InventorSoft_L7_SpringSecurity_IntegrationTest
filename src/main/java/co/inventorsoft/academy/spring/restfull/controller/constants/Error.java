package co.inventorsoft.academy.spring.restfull.controller.constants;

public final class Error {
    private Error() { }

    public static final String DISABLED_USER_MESSAGE = "Disabled user %s tried to authenticate!";
    public static final String BAD_CREDENTIALS_MESSAGE = "User %s entered incorrect credentials!";
    public static final String USERNAME_NOT_FOUND_MESSAGE = "Username %s not found!";
    public static final String INSUFFICIENT_AUTH_MESSAGE = "Insufficient authentication for user %s!";

}
