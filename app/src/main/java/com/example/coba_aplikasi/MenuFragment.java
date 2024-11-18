package com.example.coba_aplikasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import model.MyItem;

public class MenuFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private MyAdapter adapter;
    private List<MyItem> itemList;
    private RecyclerView recyclerView;

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Initialize UI components
        SearchView searchView = view.findViewById(R.id.searchBar);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        int numberOfColumns = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Initialize data
        itemList = new ArrayList<>();
        itemList.add(new MyItem("Americano", "Budak 01", R.drawable.image1));
        itemList.add(new MyItem("Latte", "Budak 02", R.drawable.image2));
        itemList.add(new MyItem("Capuccino", "Budak 03", R.drawable.image3));
        itemList.add(new MyItem("Robusta", "Budak 04", R.drawable.image1));
        itemList.add(new MyItem("Arabica", "Budak 05", R.drawable.image2));
        itemList.add(new MyItem("Moccacino", "Budak 06", R.drawable.image3));

        // Set up RecyclerView
        adapter = new MyAdapter(getContext(), itemList);
        recyclerView.setAdapter(adapter);

        // Adjust for Bottom Navigation Bar
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, new androidx.core.view.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), insets.getSystemWindowInsetBottom());
                return insets;
            }
        });

        // Set up SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query); // Filter the adapter
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText); // Update filter as the user types
                return true;
            }
        });

        return view;
    }
}
