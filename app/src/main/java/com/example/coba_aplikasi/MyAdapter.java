package com.example.coba_aplikasi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.MyItem;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    private List<MyItem> items;

    public MyAdapter(Context context, List<MyItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyItem item = items.get(position);

        // Set item name
        holder.itemName.setText(item.getTextnama());

        // Format harga dengan DecimalFormat
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Pemisah ribuan
        symbols.setDecimalSeparator(','); // Pemisah desimal

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        String formattedPrice = "Rp " + decimalFormat.format(item.getPrice());
        holder.itemPrice.setText(formattedPrice);

        // Debug harga
        Log.d("DebugHarga", "Harga asli: " + item.getPrice());
        Log.d("DebugHarga", "Harga terformat: " + formattedPrice);

        // Set item image (Bitmap)
        if (item.getImageItem() != null) {
            holder.itemImage.setImageBitmap(item.getImageItem());
        } else {
            holder.itemImage.setImageResource(R.drawable.logo_start);
        }

        // Set onClickListener untuk tombol "Add to Cart"
        holder.addToCartButton.setOnClickListener(v -> addToCart(item.getId(), item.getTextnama(), 1)); // Default quantity = 1
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void addToCart(int itemId, String itemName, int quantity) {
        SharedPreferences prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int customerId = prefs.getInt("customer_id", -1); // Get customer_id dari session

        if (customerId == -1) {
            Log.e("Cart", "Customer is not logged in");
            Toast.makeText(context, "Gagal menambahkan ke keranjang, login diperlukan", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.149.184/makaryo/api.php?action=add_to_cart";

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Cart", "Response: " + response);
                    Toast.makeText(context, itemName + " berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Cart", "Error: " + error.getMessage());
                    Toast.makeText(context, "Gagal menambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("item_id", String.valueOf(itemId));
                params.put("quantity", String.valueOf(quantity));
                params.put("item_name", itemName); // Tidak perlu String.valueOf
                params.put("customer_id", String.valueOf(customerId)); // Kirim customer_id
                return params;
            }
        };

        queue.add(request);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice;
        ImageView itemImage;
        Button addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_text1);
            itemPrice = itemView.findViewById(R.id.item_text);
            itemImage = itemView.findViewById(R.id.item_image);
            addToCartButton = itemView.findViewById(R.id.item_button);
        }
    }
}
