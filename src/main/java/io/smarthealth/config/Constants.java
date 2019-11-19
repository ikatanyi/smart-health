package io.smarthealth.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "en";
    /**
     * Representing Date with pattern "yyyy-MM-dd"
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * Representing Time with pattern "HH:mm:ss"
     */
    public static final String TIME_PATTERN = "HH:mm:ss";
    /**
     * Representing Date and time with pattern "yyyy-MM-dd HH:mm:ss"
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private Constants() {
    }
}
