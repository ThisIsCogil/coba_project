package com.example.coba_aplikasi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import model.HistoryItem;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context context;
    private List<HistoryItem> itemList;

    public HistoryAdapter(Context context, List<HistoryItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = itemList.get(position);

        holder.orderName.setText(item.getOrderName());
        holder.orderDate.setText(item.getOrderDate());

        // Format jumlah dengan DecimalFormat
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        holder.totalAmount.setText("Rp " + decimalFormat.format(item.getTotalAmount()));

        // Set gambar default dan efek hover
        holder.itemImage.setImageResource(R.drawable.logo_start);

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderName, orderDate, totalAmount;
        ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.itemTextView2);
            orderDate = itemView.findViewById(R.id.itemTextTgl);
            totalAmount = itemView.findViewById(R.id.itemTextharga);
            itemImage = itemView.findViewById(R.id.itemImageView2);
        }
    }
}
