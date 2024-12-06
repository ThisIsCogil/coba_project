package com.example.coba_aplikasi;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import model.CartItem;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartActionListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartActionListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.itemName.setText(item.getName());
        holder.itemPrice.setText("Rp " + item.getPrice());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        // Set image
        Bitmap imageBitmap = item.getImage();
        if (imageBitmap != null) {
            holder.itemImage.setImageBitmap(imageBitmap);
        }

        holder.btnIncrease.setOnClickListener(v -> listener.onQuantityChange(item, item.getQuantity() + 1));
        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                listener.onQuantityChange(item, item.getQuantity() - 1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, itemQuantity;
        ImageView itemImage; // Tambahkan ImageView
        Button btnIncrease, btnDecrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.tv_item_name);
            itemPrice = itemView.findViewById(R.id.tv_item_price);
            itemQuantity = itemView.findViewById(R.id.tv_item_quantity);
            itemImage = itemView.findViewById(R.id.img_product); // Inisialisasi ImageView
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);

        }
    }

    public interface OnCartActionListener {
        void onQuantityChange(CartItem item, int newQuantity);

    }
}
