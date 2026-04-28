package com.kaysiodl.service;

import com.kaysiodl.database.User;
import com.kaysiodl.database.UserRepository;
import com.kaysiodl.utils.PasswordUtil;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for user authentication and session management.
 */
@Stateless
public class AuthService {
    @Inject
    UserRepository userRepository;

    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    /**
     * Registers a new user.
     * @param login user login
     * @param password user password
     * @throws RuntimeException if user already exists or save error
     */
    public void register(String login, String password) {
        if (exists(login)) {
            throw new RuntimeException("Такой пользователь уже существует");
        }
        User user = new User();
        user.setLogin(login);
        String hashed =  PasswordUtil.encryptPassword(password);
        user.setPassword(hashed);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Logs in the user and creates a session.
     * @param login user login
     * @param password user password
     * @return session identifier
     * @throws RuntimeException if user not found or password incorrect
     */
    public String login(String login, String password) {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new RuntimeException("Пароль неверный");
        }
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, user);
        return sessionId;
    }

    /**
     * Logs out the user and removes the session.
     * @param sessionId session identifier
     */
    public void logout(String sessionId) {
        sessions.remove(sessionId);
    }

    /**
     * Gets user by session identifier.
     * @param sessionId session identifier
     * @return user
     * @throws RuntimeException if session not found
     */
    public User getUserBySession(String sessionId) {
        User user = sessions.get(sessionId);
        System.out.println("Found user: " + user);
        if (user == null) {
            throw new RuntimeException("Сессия не найдена или истекла");
        }
        return user;
    }

    /**
     * Checks if user exists by login.
     * @param login user login
     * @return true if user exists, false otherwise
     */
    public boolean exists(String login) {
        return userRepository.findByLogin(login) != null;
    }
}
