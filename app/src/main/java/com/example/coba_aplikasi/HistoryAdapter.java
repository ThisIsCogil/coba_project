package com.example.coba_aplikasi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coba_aplikasi.R;

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
        holder.imageView.setImageResource(item.getImageResId());
        holder.textView.setText(item.getTextnama());
        holder.textHarga.setText(item.getTextharga());
        holder.textTgl.setText(item.getTexttgl());

        // Handle item click
        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Clicked: " + item.getTextnama(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView, textHarga, textTgl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImageView2);
            textView = itemView.findViewById(R.id.itemTextView2);
            textHarga = itemView.findViewById(R.id.itemTextharga);
            textTgl = itemView.findViewById(R.id.itemTextTgl);
        }
    }
}

