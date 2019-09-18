import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Main {
    static Connection connection;
    static Statement statement;
    static PreparedStatement preparedStatement;
    static PreparedStatement preparedStatementUpdate;
    static PreparedStatement preparedStatement1;
    static PreparedStatement preparedStatement2;

    public static void main(String[] args) {
        try {
            connect();
            clearTabl();
            autoInsert();
            run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        statement = connection.createStatement();
        preparedStatement = connection.prepareStatement("INSERT INTO products (prodid,title,cost) VALUES (?, ?, ?);");
        preparedStatementUpdate = connection.prepareStatement("update products set cost = ? WHERE title = ?;");

    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void autoInsert() throws SQLException {
        connection.setAutoCommit(false);
        for (int i = 1; i < 10; i++) {
            preparedStatement.setInt(1, i);
            preparedStatement.setString(2, "товар" + i);
            preparedStatement.setInt(3, i * 10);
            preparedStatement.execute();
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    private static void clearTabl() throws SQLException {
        statement.executeUpdate("DELETE FROM products;");
    }

    private static void selectEx() throws SQLException {
        ResultSet rst = statement.executeQuery("SELECT * FROM products;");
        while (rst.next()) {
            System.out.println(rst.getInt(1) + " " + rst.getString(2) + " "
                    + rst.getString(3) + " " + rst.getInt(4));
        }
    }

    public static void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Ведите команду");
            String str = reader.readLine();
            if (str.startsWith("/цена")) {
                costTitle(str);
            } else if (str.startsWith("/сменитьцену")) {
                updateCost(str);
            } else if (str.startsWith("/товарыпоцене")) {
                selectTitle(str);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void selectTitle(String str) throws SQLException {
        String[] words = str.split(" ");
        ResultSet resultSet =
                statement.executeQuery("select title FROM products WHERE cost BETWEEN "
                        + words[1] + " and " + words[2] + " ;");
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1));
        }
    }

    public static void updateCost(String str) throws SQLException {
        String[] words = str.split(" ");
        preparedStatementUpdate.setInt(1,Integer.parseInt(words[2]));
        preparedStatementUpdate.setString(2,words[1]);
        preparedStatementUpdate.execute();

    }

    public static void costTitle(String str) throws SQLException {
        String[] words = str.split(" ");
        ResultSet rst = statement.executeQuery("SELECT cost FROM products WHERE title = '" + words[1] + "';");
        if (rst.next()) {
            System.out.println(rst.getInt(1));
        } else {
            System.out.println("Такого товара нет");
        }

    }
}




