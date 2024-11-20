package com.example.coba_aplikasi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import model.HistoryItem;
import model.MyItem;

public class HistoryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<HistoryItem> itemList;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set up data and adapter
        itemList = new ArrayList<>();
        populateItemList();
        adapter = new HistoryAdapter(requireContext(), itemList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void populateItemList() {
        // Add sample data
        itemList.add(new HistoryItem("Americano", "01 Jan 2005, 15:48", "Rp 8.000", R.drawable.logo_start));
        itemList.add(new HistoryItem("Americano", "01 Jan 2005, 15:48", "Rp 8.000", R.drawable.logo_start));
        itemList.add(new HistoryItem("Americano", "01 Jan 2005, 15:48", "Rp 8.000", R.drawable.logo_start));
        itemList.add(new HistoryItem("Americano", "01 Jan 2005, 15:48", "Rp 8.000", R.drawable.logo_start));
        itemList.add(new HistoryItem("Americano", "01 Jan 2005, 15:48", "Rp 8.000", R.drawable.logo_start));
        itemList.add(new HistoryItem("Americano", "01 Jan 2005, 15:48", "Rp 8.000", R.drawable.logo_start));
        itemList.add(new HistoryItem("Americano", "01 Jan 2005, 15:48", "Rp 8.000", R.drawable.logo_start));
        itemList.add(new HistoryItem("Americano", "01 Jan 2005, 15:48", "Rp 8.000", R.drawable.logo_start));
        itemList.add(new HistoryItem("Americano", "01 Jan 2005, 15:48", "Rp 8.000", R.drawable.logo_start));
        itemList.add(new HistoryItem("Americano", "01 Jan 2005, 15:48", "Rp 8.000", R.drawable.logo_start));
    }
}
