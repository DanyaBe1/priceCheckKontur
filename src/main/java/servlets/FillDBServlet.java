package servlets;

import DBconnection.DbUpdate;
import DBconnection.KonturList;
import UTILS.UTILS;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Product;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/fillDB")
public class FillDBServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ArrayList<Product> products = KonturList.getProductsList();
        Gson gson = new Gson();
        if (DbUpdate.fillDb(products)){
            UTILS.responseConstructor(resp, req, "DataBase has been filled successfully");
        }
        else {
            UTILS.badResponseConstructor(resp, req, "DataBase could not be filled");
        }

    }

}
