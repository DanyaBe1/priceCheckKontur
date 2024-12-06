package servlets;

import DBconnection.DbUpdate;
import UTILS.UTILS;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebServlet("/updateProducts")
public class UpdateProductsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            DbUpdate.updateProducts();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Gson json = new Gson();
        UTILS.responseConstructor(resp, req, json.toJson("DB has been updated"));
    }
}
