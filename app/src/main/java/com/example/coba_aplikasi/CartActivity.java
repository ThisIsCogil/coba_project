package com.example.coba_aplikasi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.CartItem;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView totalPrice;
    private double totalHarga;
    private Button orderNowButton;

    private static final int PICK_IMAGE_REQUEST = 100; // Request code for image selection
    private Uri paymentProofUri;
    private Bitmap selectedImageBitmap;
    private RequestQueue requestQueue;
    private AlertDialog paymentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Atur warna status bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.choco));

        recyclerView = findViewById(R.id.rv_cart_items);
        totalPrice = findViewById(R.id.tv_total_price);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItems, this::onQuantityChange);
        recyclerView.setAdapter(cartAdapter);
        requestQueue = Volley.newRequestQueue(this);

        orderNowButton = findViewById(R.id.btn_order_now);
        orderNowButton.setOnClickListener(v -> showPaymentDialog());

        loadCartItems();
    }

    private void loadCartItems() {
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int customerId = prefs.getInt("customer_id", -1);

        if (customerId == -1) {
            Log.e("CartActivity", "Customer is not logged in");
            return;
        }

        String url = "http://192.168.146.156/makaryo2/api.php?action=get_cart_items&customer_id=" + customerId;

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

                        cartItems.clear();
                        totalHarga = 0;

                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject = data.getJSONObject(i);
                            int itemId = jsonObject.getInt("item_id");
                            String itemName = jsonObject.getString("item_name");
                            double price = jsonObject.getDouble("price");
                            int quantity = jsonObject.getInt("quantity");

                            String imageBase64 = jsonObject.getString("image_item");
                            Bitmap image = null;
                            if (imageBase64 != null && !imageBase64.isEmpty()) {
                                byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                                image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            }

                            CartItem item = new CartItem(itemId, itemName, price, quantity, image);
                            cartItems.add(item);

                            totalHarga += price * quantity;
                        }

                        cartAdapter.notifyDataSetChanged();
                        updateTotalPrice();

                    } catch (JSONException e) {
                        Log.e("CartActivity", "Error parsing JSON: " + e.getMessage());
                        Toast.makeText(this, "Failed to parse response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("CartActivity", "Error fetching cart items: " + error.getMessage());
                    Toast.makeText(this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void onQuantityChange(CartItem item, int newQuantity) {
        if (newQuantity < 1) {
            deleteCartItem(item);
        } else {
            item.setQuantity(newQuantity);
            updateCartItem(item, newQuantity);
        }

        totalHarga = 0;
        for (CartItem cartItem : cartItems) {
            totalHarga += cartItem.getPrice() * cartItem.getQuantity();
        }
        updateTotalPrice();
    }

    private void updateCartItem(CartItem item, int newQuantity) {
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int customerId = prefs.getInt("customer_id", -1);

        if (customerId == -1) {
            Log.e("CartActivity", "Customer is not logged in");
            return;
        }

        String url = "http://192.168.146.156/makaryo2/api.php?action=update_cart_item";

        // Membuat JSON object untuk dikirim
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("customer_id", customerId);
            jsonBody.put("item_id", item.getId());
            jsonBody.put("quantity", newQuantity);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("CartActivity", "JSON error: " + e.getMessage());
            return;
        }

        // Menggunakan JsonObjectRequest
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            Log.d("CartActivity", "Item quantity updated");
                            cartAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("CartActivity", "Error: " + response.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e("CartActivity", "JSON parse error: " + e.getMessage());
                    }
                },
                error -> Log.e("CartActivity", "Error updating item quantity: " + error.getMessage())
        );

        requestQueue.add(jsonRequest);
    }


    private void deleteCartItem(CartItem item) {
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int customerId = prefs.getInt("customer_id", -1);

        if (customerId == -1) {
            Log.e("CartActivity", "Customer is not logged in");
            return;
        }

        String url = "http://192.168.146.156/makaryo2/api.php?action=delete_cart_item";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    cartItems.remove(item);
                    cartAdapter.notifyDataSetChanged();
                    Log.d("CartActivity", "Item removed from cart");
                },
                error -> Log.e("CartActivity", "Error removing item: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customer_id", String.valueOf(customerId));
                params.put("item_id", String.valueOf(item.getId()));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void updateTotalPrice() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        String formattedTotal = "Total: Rp " + decimalFormat.format(totalHarga);
        totalPrice.setText(formattedTotal);
    }

    private void showPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        builder.setView(dialogView);

        paymentDialog = builder.create();

        if (paymentDialog.getWindow() != null) {
            paymentDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        Spinner spinnerPaymentMethod = dialogView.findViewById(R.id.spinner_payment_method);
        ImageView imagePaymentProof = dialogView.findViewById(R.id.image_payment_proof);
        Button buttonUploadProof = dialogView.findViewById(R.id.button_upload_proof);
        Button buttonConfirmPayment = dialogView.findViewById(R.id.button_confirm_payment);

        // Set up spinner options
        String[] paymentMethods = {"Bank BRI", "Bank Mandiri", "Bank BCA"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, paymentMethods);
        spinnerPaymentMethod.setAdapter(adapter);

        // Handle image upload
        buttonUploadProof.setOnClickListener(v -> pickImage());

        // Handle payment confirmation
        buttonConfirmPayment.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
            String customerId = String.valueOf(prefs.getInt("customer_id", -1));

            if (customerId.equals("-1")) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedMethod = spinnerPaymentMethod.getSelectedItem().toString();
            if (selectedImageBitmap == null) {
                Toast.makeText(this, "Harap unggah bukti pembayaran", Toast.LENGTH_SHORT).show();
                return;
            }

            confirmPayment(customerId, selectedMethod);
            paymentDialog.dismiss();
        });

        paymentDialog.show();
    }


    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                // Update the ImageView in the same dialog instance
                if (paymentDialog != null && paymentDialog.isShowing()) {
                    ImageView imagePaymentProof = paymentDialog.findViewById(R.id.image_payment_proof);
                    if (imagePaymentProof != null) {
                        imagePaymentProof.setImageBitmap(selectedImageBitmap);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void confirmPayment(String customerId, String paymentMethod) {
        String url = "http://192.168.146.156/makaryo2/api.php?action=submit_payment";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // Log the API response for debugging
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equals(status)) {
                            Toast.makeText(this, "Pembayaran berhasil dikonfirmasi: " + message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, Dashboard.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Gagal: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle error in case of failure
                    Toast.makeText(this, "Gagal mengonfirmasi pembayaran: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Log parameters being sent
                Map<String, String> params = new HashMap<>();
                params.put("customer_id", customerId);
                params.put("payment_method", paymentMethod);
                params.put("payment_proof", bitmapToString(selectedImageBitmap));
                Log.d("Payment Params", params.toString());
                return params;
            }
        };

        queue.add(stringRequest);
    }


    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}