package local.nix.financial.management;

import local.nix.financial.management.db_configuration.DatabaseConfigurator;
import local.nix.financial.management.entity.Expense;
import local.nix.financial.management.entity.Income;
import local.nix.financial.management.repository.AccountRepository;
import local.nix.financial.management.repository.CategoryRepository;


import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;


public class Main {

    public static void main(String[] args) {

        String user_id = args[0];
        String nameOfDatabaseUser = args[1];
        String passwordOfDatabaseUser = args[2];
        DatabaseConfigurator.configure(nameOfDatabaseUser, passwordOfDatabaseUser);

        AccountRepository accountRepository = new AccountRepository();

        Income operation1 = new Income();
        operation1.setAmount(7000l);
        operation1.getCategories().add(new CategoryRepository().findIncomeCategoryByName("Финансовые активы"));
        operation1.setTimestamp(Instant.now(Clock.systemUTC()));
        accountRepository.addNewOperation(Long.valueOf(user_id), 1l, operation1);

        Income operation2 = new Income();
        operation2.setAmount(10000l);
        operation2.getCategories().add(new CategoryRepository().findIncomeCategoryByName("Бизнес"));
        operation2.setTimestamp(Instant.now(Clock.systemUTC()));
        accountRepository.addNewOperation(Long.valueOf(user_id), 1l, operation2);

        Expense operation3 = new Expense();
        operation3.setAmount(4000l);
        operation3.getCategories().add(new CategoryRepository().findExpenseCategoryByName("Ремонт автомобиля"));
        operation3.setTimestamp(Instant.now(Clock.systemUTC()));
        accountRepository.addNewOperation(Long.valueOf(user_id), 1l, operation3);

        LocalDate from = LocalDate.of(2020, Month.AUGUST, 1);
        LocalDate to = LocalDate.of(2020, Month.AUGUST, 31);
        accountRepository.getAccountStatementToCsvFile(1l, from, to);


    }
}
