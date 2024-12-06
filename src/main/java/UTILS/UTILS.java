package UTILS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class UTILS {

    public static boolean checkBarcode(String barcode) {
        if (barcode == null || barcode.isEmpty() || barcode.length() != 13) {
            return false;
        }
        return true;
    }

    public static void responseConstructor(HttpServletResponse resp, HttpServletRequest req, String json) throws IOException {
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


}
