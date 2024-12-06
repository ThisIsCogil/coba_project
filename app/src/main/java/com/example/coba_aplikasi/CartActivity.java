package com.example.coba_aplikasi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.CartItem;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.rv_cart_items);
        totalPrice = findViewById(R.id.tv_total_price);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItems, this::onQuantityChange);
        recyclerView.setAdapter(cartAdapter);

        loadCartItems();
    }

    private void loadCartItems() {
        // Ambil customer_id dari sesi (SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int customerId = prefs.getInt("customer_id", -1);

        if (customerId == -1) {
            Log.e("CartActivity", "Customer is not logged in");
            return;
        }

        String url = "http://192.168.146.156/makaryo2/api.php?action=get_cart_items&customer_id=" + customerId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (!"success".equals(status)) {
                            String message = response.getString("message");
                            Log.e("CartActivity", message);
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Parse data array
                        cartItems.clear();
                        double total = 0;
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject = data.getJSONObject(i);
                            int itemId = jsonObject.getInt("item_id");
                            String itemName = jsonObject.getString("item_name");
                            double price = jsonObject.getDouble("price");
                            int quantity = jsonObject.getInt("quantity");

                            // Decode Base64 image
                            String imageBase64 = jsonObject.getString("image_item");
                            Bitmap image = null;
                            if (imageBase64 != null && !imageBase64.isEmpty()) {
                                byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                                image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            }

                            // Tambahkan item ke daftar
                            CartItem item = new CartItem(itemId, itemName, price, quantity, image);
                            cartItems.add(item);

                            // Hitung total harga
                            total += price * quantity;
                        }

                        // Perbarui UI
                        cartAdapter.notifyDataSetChanged();
                        totalPrice.setText("Total: Rp " + total);

                    } catch (JSONException e) {
                        Log.e("CartActivity", "Error parsing JSON: " + e.getMessage());
                        Toast.makeText(this, "Failed to parse response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("CartActivity", "Error fetching cart items: " + error.getMessage());
                    Toast.makeText(this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                });

        // Kirim request ke server
        queue.add(request);
    }



    private void onQuantityChange(CartItem item, int newQuantity) {
        // Handle updating the quantity of items in the cart
        updateCartItem(item, newQuantity);
    }

    private void updateCartItem(CartItem item, int newQuantity) {
        // Ambil customer_id dari sesi (SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int customerId = prefs.getInt("customer_id", -1);

        if (customerId == -1) {
            Log.e("CartActivity", "Customer is not logged in");
            return;
        }

        String url = "http://192.168.146.156/makaryo2/api.php?action=update_cart_item";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle response if needed
                    Log.d("CartActivity", "Item quantity updated");
                },
                error -> Log.e("CartActivity", "Error updating item quantity: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customer_id", String.valueOf(customerId));
                params.put("item_id", String.valueOf(item.getId()));
                params.put("quantity", String.valueOf(newQuantity));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}
