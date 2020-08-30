package local.nix.financial.management.hibernate.util;

import local.nix.finance.management.entity.*;
import local.nix.financial.management.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactoryUtil {
    private static SessionFactory sessionFactory;

    private HibernateSessionFactoryUtil() {}

    public static SessionFactory getSessionFactory(String username, String password) {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.setProperty("hibernate.connection.username", username);
                configuration.setProperty("hibernate.connection.password", password);
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Account.class);
                configuration.addAnnotatedClass(Operation.class);
                configuration.addAnnotatedClass(Income.class);
                configuration.addAnnotatedClass(Expense.class);
                configuration.addAnnotatedClass(IncomeCategory.class);
                configuration.addAnnotatedClass(ExpenseCategory.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());

            } catch (Exception e) {
                System.out.println("HibernateSessionFactoryUtilException:" + e);
            }
        }
        return sessionFactory;
    }
}
