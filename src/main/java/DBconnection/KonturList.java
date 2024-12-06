package DBconnection;

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
        String shopId = "64094396-0da1-431a-9c3f-e0e61d9f539e";
        String apikey = "9616a96c-cf17-17d4-d341-cf6186ea6433";
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
