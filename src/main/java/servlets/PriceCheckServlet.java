package servlets;

import UTILS.UTILS;
import DBconnection.GetProductFromDB;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Product;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/getProduct")
public class PriceCheckServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String barcode = req.getParameter("barcode");
        if (UTILS.checkBarcode(barcode) == false){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Штрихкод не прошёл проверку");
        }
        Product product = null;
        try {
            product = GetProductFromDB.getProductFromDB(barcode);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (product != null) {
            Gson gson = new Gson();
            String json = gson.toJson(product);
            UTILS.productResponseConstructor(resp, req, json);
        }
        else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Товар не найден");
        }
    }
}
