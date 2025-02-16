package UTILS;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class UTILS {

    public static boolean checkBarcode(String barcode) {
        if (barcode == null || barcode.isEmpty() || barcode.length() > 13) {
            return false;
        }
        return true;
    }

    public static void productResponseConstructor(HttpServletResponse resp, HttpServletRequest req, String json) throws IOException {
        PrintWriter writer = null;
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            writer = resp.getWriter();
            writer.println(json);
            resp.setStatus(HttpServletResponse.SC_OK);
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public static void responseConstructor(HttpServletResponse resp, HttpServletRequest req, String message) throws IOException {
        Map<String, String> jsonObject = new HashMap<>();
        jsonObject.put("message", message);
        PrintWriter writer = null;
        Gson gson = new Gson();
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            writer = resp.getWriter();
            String json = gson.toJson(jsonObject);
            writer.println(json);
            resp.setStatus(HttpServletResponse.SC_OK);
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public static void badResponseConstructor(HttpServletResponse resp, HttpServletRequest req, String message) throws IOException {
        Map<String, String> jsonObject = new HashMap<>();
        jsonObject.put("message", message);
        PrintWriter writer = null;
        Gson gson = new Gson();
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            String json = gson.toJson(jsonObject);
            writer = resp.getWriter();
            writer.println(json);
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            writer.flush();
        } finally {
            writer.close();
        }
    }


}
