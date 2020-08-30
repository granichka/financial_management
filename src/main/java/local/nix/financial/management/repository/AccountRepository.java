package local.nix.financial.management.repository;

import local.nix.financial.management.db_configuration.DatabaseConfigurator;
import local.nix.finance.management.entity.*;
import local.nix.financial.management.entity.*;
import local.nix.financial.management.logger.ApplicationLogger;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

    private static Connection connection = (Connection) DatabaseConfigurator.getConfigurations().get("Connection");
    private static UserRepository userRepository = new UserRepository();
    private static Logger logger = ApplicationLogger.getLogger();

    public  void getAccountStatementToCsvFile(Long id, LocalDate from, LocalDate to) {

        logger.info("Account statement for account with id=" + id);

        File accountStatement = new File("account(id=" + id + ")statement.csv");
        try {

            logger.info("Creating a file \"account(id=" + id + ")statement.csv\"");

            accountStatement.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Operation> allOperations = getOperationsOfAccountBetweenTwoDays(id, from, to);
        Long incomeOfAllOperations = getTotalIncome(allOperations);
        Long balance = getBalance(allOperations);

        StringBuilder sb = new StringBuilder();

        sb.append("operation_id, amount, timestamp," + "\n");

        for (Operation operation : allOperations) {
            sb.append(operation.getId() + ",");
            sb.append(operation.getAmount() + ",");
            sb.append(operation.getTimestamp() + ",");
            sb.append("\n");
        }

        sb.append("Total income: " + incomeOfAllOperations);
        sb.append("\n");
        sb.append("Balance: " + balance);


        logger.info("Start writing into the file \"account(id=" + id + ")statement.csv\"");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(accountStatement))) {
            bw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void addNewOperation(Long user_id, Long account_id, Operation operation) {

        logger.info("Adding a new operation. user_id = " + user_id + " , account_id = " + account_id);


        Long amountOfOperation = operation.getAmount();
        logger.info("amountOfOperation = " + amountOfOperation);

        if (amountOfOperation < 0) {

            logger.error("Invalid amount of the operation: " + amountOfOperation);

        }

        logger.info("Finding a user and his account by id in a database");

        User user = userRepository.findById(user_id);
        Account accountForOperation = null;
        for (Account account : user.getAccounts()) {
            if (account_id == account.getId()) {
                accountForOperation = account;
            }
        }

        if (accountForOperation == null) {

            logger.error("User with id=" + user_id + " don't have account with id=" + account_id);
        }

        Long amountOfCurrentAccount = accountForOperation.getAmount();
        logger.info("amountOfCurrentAccount before = " + amountOfCurrentAccount);
        Class<? extends Operation> operationClass = operation.getClass();

        if (operationClass.equals(Income.class)) {
            accountForOperation.setAmount(amountOfCurrentAccount + amountOfOperation);
            logger.info("Income operation was successful");

        } else if (operationClass.equals(Expense.class)) {
            if (checkAmountOfAccount(amountOfCurrentAccount, amountOfOperation)) {
                accountForOperation.setAmount(amountOfCurrentAccount - amountOfOperation);
                logger.info("Expense operation was successful");

            } else {
                logger.error("Insufficient account balance");
            }
        }

        logger.info("amountOfCurrentAccount after = " + accountForOperation.getAmount());
        operation.setAccount(accountForOperation);
        accountForOperation.getOperations().add(operation);

        logger.info("User updating in database");

        userRepository.update(user);


    }


    private static boolean checkAmountOfAccount(Long amountOfAccount, Long amountToWithdraw) {
        logger.info("Check the account for balance");
        if (amountOfAccount > amountToWithdraw || amountOfAccount.equals(amountToWithdraw)) {
            return true;
        } else {
            return false;
        }
    }

    private static List<Operation> getOperationsOfAccountBetweenTwoDays(Long id, LocalDate from, LocalDate to) {
        logger.info("Finding all operations between two days");
        List<Operation> result = new ArrayList<>();
        String query = "select operation.id, operation.amount, timestamp\n" +
                "from account inner join operation on account.id = operation.account_id\n" +
                "where account.id = (?) \n" +
                "and timestamp between (?) and (?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(from.atStartOfDay()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(to.atStartOfDay()));
            ResultSet rs = preparedStatement.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    Operation operation = new Operation();
                    operation.setId(rs.getLong("id"));
                    operation.setAmount(rs.getLong("amount"));
                    operation.setTimestamp(rs.getTimestamp("timestamp").toInstant());
                    result.add(operation);
                }
            }
        } catch (SQLException throwables) {
            logger.error(throwables);
        }

        if (result.isEmpty()) {
            logger.warn("getOperationsOfAccountBetweenTwoDays() method has returned empty List");
        }

        return result;

    }

    private static Long getTotalIncome(List<Operation> operationList) {
        logger.info("Finding the total income");
        Long result = 0l;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < operationList.size(); i++) {
            stringBuilder.append("?,");
        }


        String query = "select sum(amount) as income_sum \n" +
                "from operation left join income_categories_of_operations on operation.id = income_categories_of_operations.operation_id\n" +
                "where operation.id in ("
                + stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString() + ")" +
                " and category_id is not null;\n" +
                "; ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int index = 1;
            for (Operation operation : operationList) {
                preparedStatement.setLong(index++, operation.getId());
            }
            ResultSet rs = preparedStatement.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    result = rs.getLong("income_sum");
                }
            }
        } catch (SQLException throwables) {
            logger.error(throwables);
        }


        return result;

    }

    private static Long getTotalExpense(List<Operation> operationList) {
        logger.error("Finding the total expense");
        Long result = 0l;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < operationList.size(); i++) {
            stringBuilder.append("?,");
        }


        String query = "select sum(amount) as expense_sum \n" +
                "from operation left join expense_categories_of_operations on operation.id = expense_categories_of_operations.operation_id\n" +
                "where operation.id in ("
                + stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString() + ")" +
                " and category_id is not null;\n" +
                "; ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int index = 1;
            for (Operation operation : operationList) {
                preparedStatement.setLong(index++, operation.getId());
            }
            ResultSet rs = preparedStatement.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    result = rs.getLong("expense_sum");
                }
            }
        } catch (SQLException throwables) {
            logger.error(throwables);
        }


        return result;

    }

    private static Long getBalance(List<Operation> operationList) {
        logger.info("Finding the balance");
        return getTotalIncome(operationList) - getTotalExpense(operationList);
    }
}
