package servlets;

import DBconnection.DbUpdate;
import UTILS.UTILS;
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
            if(DbUpdate.updateProducts()){
                UTILS.responseConstructor(resp, req, "DB has been updated");
            }
            else {
                UTILS.badResponseConstructor(resp, req, "Update failed");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
