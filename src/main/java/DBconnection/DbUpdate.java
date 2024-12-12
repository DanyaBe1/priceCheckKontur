package DBconnection;

import models.Product;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbUpdate {
    private static Connection connection;

    public static boolean updateProducts() throws IOException, InterruptedException {
        ArrayList<Product> products = KonturList.getProductsList();
        if (products == null || products.isEmpty()) {
            System.out.println("Список товаров пуст.");
            return false;
        }
        connection = connectToDb();
        String query = "SELECT * FROM products";
        int updatedRows = 0;
        String name;
        String barcode;
        Double price;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);){
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                barcode = resultSet.getString("barcode");
                price = resultSet.getDouble("price");
                if (barcode == null || barcode.isEmpty() || price == null) {
                    continue;
                }
                for (Product product : products) {
                    List<String> barcodes = product.getBarcodes();
                    Double newPrice = product.getPrice();
                    if ((!(barcodes == null)) && (!(barcodes.isEmpty())) && barcodes.contains(barcode)) {
                        if (newPrice != null && Math.abs(product.getPrice() - price) > 0.0001) {
                            setPrice(product.getPrice(), barcode, connection);
                            updatedRows++;
                            System.out.println("Обновлена цена на " + resultSet.getString("name") + "\nПредыдущая цена: " + price + "\nНовая цена: " + product.getPrice() + "\n");
                        }
                        break;
                    }
                }
            }
            System.out.println("\nБыло обновлено " + updatedRows + " товаров\n");
            updatedRows = 0;
            return true;
        }catch (SQLException e){
            System.out.println("БД не обновлена");
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }catch (SQLException e){
                System.out.println("Соединение с бд не было закрыто в блоке finally метода updateProducts()");
            }
        }
        return false;
    }

    public static boolean fillDb(ArrayList<Product> products) {
        String query = "INSERT INTO products (code, name, price, barcode) VALUES (?, ?, ?, ?);";
        Connection connection = connectToDb();
        PreparedStatement preparedStatement = null;
        int count = 0;
        try {
            preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < products.size(); i++) {
                preparedStatement.setInt(1, products.get(i).getCode());
                preparedStatement.setString(2, products.get(i).getName());
                if (products.get(i).getPrice() != null) {
                    preparedStatement.setDouble(3, products.get(i).getPrice());
                } else {
                    preparedStatement.setNull(3, java.sql.Types.DOUBLE);
                }
                List<String> barcodes = products.get(i).getBarcodes();
                if (barcodes == null){
                    preparedStatement.setNull(4, java.sql.Types.VARCHAR);
                    preparedStatement.addBatch();
                    count++;
                }
                else if (barcodes.size() == 1) {
                    preparedStatement.setString(4, barcodes.get(0));
                    preparedStatement.addBatch();
                    count++;
                } else if (barcodes.size() > 1) {
                    preparedStatement.setString(4, barcodes.get(0));
                    preparedStatement.addBatch();
                    count++;
                    for (int k = 1; k < barcodes.size(); k++) {
                        preparedStatement.setInt(1, products.get(i).getCode());
                        preparedStatement.setString(2, products.get(i).getName());
                        if (products.get(i).getPrice() != null) {
                            preparedStatement.setDouble(3, products.get(i).getPrice());
                        } else {
                            preparedStatement.setNull(3, java.sql.Types.DOUBLE);
                        }
                        preparedStatement.setString(4, barcodes.get(k));
                        preparedStatement.addBatch();
                        count++;
                    }
                }
                if ((count == 100 && i != 0) || (count > 100 && i != 0)) {
                    preparedStatement.executeBatch();
                    count = 0;
                }
            }
            preparedStatement.executeBatch();
            connection.commit();
        }catch (SQLException e){
            System.out.println("Error while filling DB: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback(); // Откат при ошибке
                }
                catch (SQLException e1) {
                    System.out.println("Error while rollback: " + e1.getMessage());
                }
            }
            return false;
        }finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close(); // Закрываем PreparedStatement
                }catch (SQLException e){
                    System.out.println("Error while closing prepared statement: " + e.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close(); // Закрываем соединение
                }catch (SQLException e){
                    System.out.println("Error while closing connection: " + e.getMessage());
                }
            }
        }
        return true;

    }

    private static boolean setPrice(Double price, String barcode, Connection connection) throws SQLException {
        String query = "UPDATE products SET price = ? WHERE barcode = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setDouble(1, price);
        preparedStatement.setString(2, barcode);
        int rowsUpdated = preparedStatement.executeUpdate();
        preparedStatement.close();
        if (rowsUpdated == 1) {
            connection.commit();
            return true;
        }
        return false;
    }

    public static Connection connectToDb(){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Класс не загружен");
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:/home/skazka/priceCheckKontur/src/main/resources/db.db");
            System.out.println("Соединение с бд установлено");
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            System.out.println("Подключение к бд не установлено");
        }
        return null;
    }
}
