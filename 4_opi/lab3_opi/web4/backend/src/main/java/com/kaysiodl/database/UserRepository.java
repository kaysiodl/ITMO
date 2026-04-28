package com.kaysiodl.database;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 * Repository for working with User entities.
 */
@ApplicationScoped
public class UserRepository {
    @PersistenceContext(name = "pg")
    private EntityManager entityManager;

    /**
     * Saves a user to the database.
     * @param user the user to save
     */
    @Transactional
    public void save(User user) {
        entityManager.persist(user);
    }

    /**
     * Finds a user by their login.
     * @param login the user's login
     * @return the user, or null if not found
     */
    public User findByLogin(String login) {
        try {
            return entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
