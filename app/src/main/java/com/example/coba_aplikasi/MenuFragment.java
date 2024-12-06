package com.example.coba_aplikasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import model.MyItem;

public class MenuFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private MyAdapter myAdapter;
    private List<MyItem> itemList;
    private RecyclerView recyclerView;
    private ImageView btnCart;

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
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        SearchView searchView = view.findViewById(R.id.searchBar);
        recyclerView = view.findViewById(R.id.recycler_view);

        // Setup RecyclerView
        int numberOfColumns = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Initialize data
        itemList = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(), itemList);
        btnCart = view.findViewById(R.id.cartButton);
        recyclerView.setAdapter(myAdapter);

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CartActivity.class);
            startActivity(intent);
        });

        // Load items from API
        loadItemsFromApi();

        // Adjust for Bottom Navigation Bar
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, insets) -> {
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });
    }

    private void loadItemsFromApi() {
        String url = "http://192.168.146.156/makaryo2/api.php?action=get_items"; // Replace with your API URL

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    itemList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("item_id");
                            String name = jsonObject.getString("item_name");
                            double price = jsonObject.getDouble("price");

                            // Decode Base64 image
                            String base64Image = jsonObject.getString("image_item");
                            byte[] imageBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);

                            // Create a bitmap from the image bytes
                            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                            // Add the item to the list
                            itemList.add(new MyItem(id, name, price, imageBitmap));
                        }
                        myAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("Volley", "JSON Parsing Error: " + e.getMessage());
                    }
                },
                error -> Log.e("Volley", "Error: " + error.getMessage())
        );

        requestQueue.add(jsonArrayRequest);
    }
}