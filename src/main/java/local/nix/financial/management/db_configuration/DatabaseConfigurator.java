package local.nix.financial.management.db_configuration;

import local.nix.financial.management.hibernate.util.HibernateSessionFactoryUtil;
import local.nix.financial.management.logger.ApplicationLogger;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public interface DatabaseConfigurator {

    Map<String, Object> configurations = new HashMap<>();
    Logger logger = ApplicationLogger.getLogger();

    static void configure(String name, String password) {
            logger.info("Database configuration");
            hibernateConfig(name, password);
            jdbcConfig(name, password);
    }

    static Map<String, Object> getConfigurations() {
        return configurations;
    }

    private static void hibernateConfig(String name, String password) {
        logger.info("SessionFactory configuration");
        SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory(name, password);
        configurations.put("SessionFactory", sessionFactory);
    }

    private static void jdbcConfig(String name, String password) {
        logger.info("JDBC configuration");
        Connection connection = null;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.error(e);
        }

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/finance_database", name, password);
        } catch (SQLException throwables) {
            logger.error(throwables);
        }

        configurations.put("Connection", connection);
    }

}
