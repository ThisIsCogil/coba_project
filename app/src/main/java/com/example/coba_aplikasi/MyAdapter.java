package com.example.coba_aplikasi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import model.MyItem;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<MyItem> originaList;
    private List<MyItem> filteredList;
    private Context context;

    public MyAdapter(Context context, List<MyItem> itemList) {
        this.context = context;
        this.originaList = itemList;
        this.filteredList = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyItem item = filteredList.get(position);
        holder.textView1.setText(item.getTextnama());
        holder.textView.setText(item.getText());
        holder.imageView.setImageResource(item.getImageResId());

        holder.button.setOnClickListener(v -> {
            // Handle button click
            // You can show detailed information or navigate to another activity
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // Method to filter the list
    public void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(originaList);
        } else {
            for (MyItem item : originaList) {
                if (item.getTextnama().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView textView1;
        Button button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            textView = itemView.findViewById(R.id.item_text);
            textView1 = itemView.findViewById(R.id.item_text1);
            button = itemView.findViewById(R.id.item_button);
        }
    }
}