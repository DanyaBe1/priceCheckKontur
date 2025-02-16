package DBconnection;

import UTILS.Config;
import models.Product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbUpdate {
    private static final Logger log = LogManager.getLogger(DbUpdate.class);

    private static Connection connection;

    public static boolean updateProducts() throws IOException, InterruptedException {
        log.info("Updating products");
        ArrayList<Product> products = KonturList.getProductsList();
        Set<Integer> productCodes = new HashSet<Integer>();
        if (products == null || products.isEmpty()) {
            log.error("Список товаров пуст. Проблема с KonturAPI");
            return false;
        }
        connection = connectToDb();
        String query = "SELECT * FROM products";
        int updatedRows = 0;
        String barcode;
        Double price;
        int code;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);){
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                barcode = resultSet.getString("barcode");
                price = resultSet.getDouble("price");
                code = resultSet.getInt("code");
                productCodes.add(code);
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
                            log.info("Обновлена цена на {} Предыдущая цена: {} Новая цена: {}", resultSet.getString("name"), price, product.getPrice());
                        }
                        break;
                    }
                }
            }
            for (Product product : products) {
                if (!(productCodes.contains(product.getCode()))) {
                    if (!addProduct(product, connection)) {
                        log.warn("Товар не был добавлен");
                        return false;
                    }
                    else {
                        log.info("Был добавлен товар: {}, цена: {}, код: {}", product.getName(), product.getPrice(), product.getCode());
                        updatedRows++;
                    }
                }
            }
            log.info("Было обновлено {} товаров", updatedRows);
            updatedRows = 0;
            return true;
        }catch (SQLException e){
            log.error("БД не обновлена", e);
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }catch (SQLException e){
                log.error("Соединение с бд не было закрыто в блоке finally метода updateProducts()");
            }
        }
        return false;
    }

    private static boolean addProduct(Product product, Connection connection) {
        String query = "INSERT INTO products (code, name, price, barcode) VALUES (?, ?, ?, ?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);){
            preparedStatement.setInt(1, product.getCode());
            preparedStatement.setString(2, product.getName());
            if (product.getPrice() != null) {
                preparedStatement.setDouble(3, product.getPrice());
            } else {
                preparedStatement.setNull(3, java.sql.Types.DOUBLE);
            }
            List<String> barcodes = product.getBarcodes();
            if (barcodes == null){
                preparedStatement.setNull(4, java.sql.Types.VARCHAR);
                preparedStatement.addBatch();
            }
            else if (barcodes.size() == 1) {
                preparedStatement.setString(4, barcodes.get(0));
                preparedStatement.addBatch();
            } else if (barcodes.size() > 1) {
                preparedStatement.setString(4, barcodes.get(0));
                preparedStatement.addBatch();
                for (int k = 1; k < barcodes.size(); k++) {
                    preparedStatement.setInt(1, product.getCode());
                    preparedStatement.setString(2, product.getName());
                    if (product.getPrice() != null) {
                        preparedStatement.setDouble(3, product.getPrice());
                    } else {
                        preparedStatement.setNull(3, java.sql.Types.DOUBLE);
                    }
                    preparedStatement.setString(4, barcodes.get(k));
                    preparedStatement.addBatch();
                }
            }
            preparedStatement.executeBatch();
            connection.commit();
            return true;
        }catch (SQLException e){
            log.warn("Единичное добавление товара не выполнено", e);
            return false;
        }
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
            log.error("Error while filling DB", e);
            if (connection != null) {
                try {
                    connection.rollback(); // Откат при ошибке
                }
                catch (SQLException e1) {
                    log.error("Error while rollback", e1);
                }
            }
            return false;
        }finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close(); // Закрываем PreparedStatement
                }catch (SQLException e){
                    log.error("Error while closing prepared statement", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close(); // Закрываем соединение
                }catch (SQLException e){
                    log.error("Error while closing connection", e);
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
            log.error("Класс не загружен");
        }
        try {
            connection = DriverManager.getConnection(Config.getDatabaseUrl());
            log.info("Соединение с бд установлено");
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            log.error("Подключение к бд не установлено");
        }
        return null;
    }
}
