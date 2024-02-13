import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "1234";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";

    private static final String SQL_SELECT_TASKS = "SELECT * FROM task ORDER BY id DESC";
    private static final String SQL_UPDATE_TASK_STATE = "UPDATE task SET state = 'DONE' WHERE id = ?";
    private static final String SQL_INSERT_TASK = "INSERT INTO task (name, state) VALUES (?, 'IN_PROCESS')";
    private static final String SQL_DELETE_TASK = "DELETE FROM task WHERE id = ?";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.println("1. Show all tasks");
                System.out.println("2. Finish task");
                System.out.println("3. Create Task");
                System.out.println("4. Delete Task");
                System.out.println("5. Exit");

                int command = scanner.nextInt();

                switch (command) {
                    case 1:
                        showAllTasks(connection);
                        break;
                    case 2:
                        finishTask(connection, scanner);
                        break;
                    case 3:
                        createTask(connection, scanner);
                        break;
                    case 4:
                        deleteTask(connection, scanner);
                        break;
                    case 5:
                        System.exit(0);
                        break;
                    default:
                        System.err.println("Wrong command");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showAllTasks(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SQL_SELECT_TASKS)) {

            System.out.println("ID\t\tName\t\tState");
            System.out.println("---------------------------------------");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String state = resultSet.getString("state");

                System.out.printf("%-5d\t%-20s\t%-10s\n", id, name, state);
            }
        }
    }


    private static void finishTask(Connection connection, Scanner scanner) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_TASK_STATE)) {
            System.out.println("Input task ID: ");
            int taskId = scanner.nextInt();
            preparedStatement.setInt(1, taskId);
            preparedStatement.executeUpdate();
        }
    }

    private static void createTask(Connection connection, Scanner scanner) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_TASK)) {
            System.out.println("Enter task name: ");
            scanner.nextLine();
            String taskName = scanner.nextLine();
            preparedStatement.setString(1, taskName);
            preparedStatement.executeUpdate();
        }
    }

    private static void deleteTask(Connection connection, Scanner scanner) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_TASK)) {
            System.out.println("Input task ID to delete: ");
            int taskId = scanner.nextInt();
            preparedStatement.setInt(1, taskId);
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Task deleted successfully");
            } else {
                System.out.println("No task found with ID: " + taskId);
            }
        }
    }
}
