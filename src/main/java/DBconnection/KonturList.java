package DBconnection;

import UTILS.Config;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import models.Product;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class KonturList {
    public static ArrayList<Product> getProductsList() throws IOException{
        String shopId = Config.getShopId();
        String apikey = Config.getApiKey();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.kontur.ru/market/v1/shops/" + shopId + "/products"))
                .header("x-kontur-apikey", apikey)
                .GET()
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
        JsonArray productsArray = jsonObject.getAsJsonArray("items");
        ArrayList<Product> products = new ArrayList<>();
        for (JsonElement jsonElement : productsArray) {
            Product product = gson.fromJson(jsonElement, Product.class);
            products.add(product);
        }
        return products;
    }
}
