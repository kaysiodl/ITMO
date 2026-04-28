package com.kaysiodl.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password handling (encryption and verification).
 */
public class PasswordUtil {

    /**
     * Encrypts password using BCrypt.
     * @param password original password
     * @return encrypted password
     */
    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifies if password matches the hashed password.
     * @param password original password
     * @param hashedPassword hashed password
     * @return true if password is correct, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
