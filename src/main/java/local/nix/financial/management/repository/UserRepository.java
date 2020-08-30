package local.nix.financial.management.repository;

import local.nix.financial.management.db_configuration.DatabaseConfigurator;
import local.nix.finance.management.entity.*;
import local.nix.financial.management.entity.User;
import local.nix.financial.management.logger.ApplicationLogger;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


public class UserRepository {

    private static Session session;
    private static Logger logger = ApplicationLogger.getLogger();

    public UserRepository() {
        session = ((SessionFactory) DatabaseConfigurator.getConfigurations().get("SessionFactory")).openSession();
    }

    public void save(User user) {
        logger.info("Saving the user " + user);
        Transaction tx = session.beginTransaction();
        try {
            session.save(user);
            tx.commit();
            logger.info("New user was saved to database");
        } catch (Exception e) {
            tx.rollback();
            logger.error("User was not saved" + e);
        }

    }

    public void update(User user) {
        logger.info("Updating information about user " + user);
        Transaction tx = session.beginTransaction();
        try {
            session.update(user);
            logger.info("Information about user was updated");
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            logger.info("Information about user was not updated" + e);
        }

    }


    public User findById(Long id) {
        logger.info("Finding user with id=" + id);
        Transaction tx = session.beginTransaction();
        User result = null;
        try {
            result = session.get(User.class, id);
            tx.commit();
            logger.info("User was found");
        } catch (Exception e) {
            tx.rollback();
            logger.info("User was not found");
        }

        if (result == null) {
            logger.error("User with id " + id + " doesn't exist");
        }

        return result;
    }


}
