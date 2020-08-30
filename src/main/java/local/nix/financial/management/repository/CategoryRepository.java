package local.nix.financial.management.repository;

import local.nix.financial.management.db_configuration.DatabaseConfigurator;
import local.nix.financial.management.entity.ExpenseCategory;
import local.nix.financial.management.entity.IncomeCategory;
import local.nix.financial.management.logger.ApplicationLogger;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class CategoryRepository {

    private static Session session;
    private static Logger logger = ApplicationLogger.getLogger();


    public CategoryRepository() {
        SessionFactory sessionFactory = (SessionFactory) DatabaseConfigurator.getConfigurations().get("SessionFactory");
        session = sessionFactory.openSession();
    }

    public IncomeCategory findIncomeCategoryByName(String name) {
        logger.info("Finding the income category by name");
        Transaction tx = session.beginTransaction();
        IncomeCategory result = null;
        try {
            result = session.byNaturalId(IncomeCategory.class).using("name", name).getReference();
            tx.commit();
            logger.info("Category with name " + name + " was found");
        } catch (Exception e) {
            tx.rollback();
            logger.error(e);
        }

        if(result == null) {
            logger.warn("Such income category doesn't exist. findIncomeCategoryByName() method has returned null");
        }

        return result;
    }


    public ExpenseCategory findExpenseCategoryByName(String name) {
        logger.info("Finding the expense category by name");
        Transaction tx = session.beginTransaction();
        ExpenseCategory result = null;
        try {
            result = session.byNaturalId(ExpenseCategory.class).using("name", name).getReference();
            tx.commit();
            logger.info("Category with name " + name + " was found");
        } catch (Exception e) {
            tx.rollback();
            logger.error(e);
        }

        if(result == null) {
            logger.warn("Such expense category doesn't exist. findExpenseCategoryByName() method has returned null");
        }

        return result;
    }
}
