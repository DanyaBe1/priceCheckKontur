package DBconnection;

import models.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetProductFromDB {
    private static final Logger log = LogManager.getLogger(GetProductFromDB.class);

    public static Product getProductFromDB(String barcode) throws SQLException {
        Connection connection = DbUpdate.connectToDb();
        Product product = new Product();
        String query = "SELECT name, price FROM products WHERE barcode = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, barcode);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                product.setName(resultSet.getString("name"));
                product.setPrice(resultSet.getDouble("price"));
            }
            return product;
        } catch (SQLException e) {
            log.error("Запрос не выполнен");;
        }finally {
            connection.close();
        }
        return null;
    }
}
