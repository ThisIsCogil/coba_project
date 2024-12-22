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
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
    private List<MyItem> filteredItemList;
    private Button btnHot, btnIce, btnTea, btnSnack;
    private ProgressBar progressBar;

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
        filteredItemList = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(), filteredItemList);
        btnCart = view.findViewById(R.id.cartButton);
        recyclerView.setAdapter(myAdapter);
        btnHot = view.findViewById(R.id.btnHotDrink);
        btnIce = view.findViewById(R.id.btnIceDrink);
        btnTea = view.findViewById(R.id.btnTea);
        btnSnack = view.findViewById(R.id.btnSnack);
        progressBar = view.findViewById(R.id.progressBar);

        // Button listeners for category filtering
        btnHot.setOnClickListener(v -> {
            // Set selected state for Hot Drink button
            btnHot.setSelected(true);
            btnIce.setSelected(false);
            btnTea.setSelected(false);
            btnSnack.setSelected(false);

            // Load items for Hot Drink category
            loadItemsFromApi("Hot");
        });

        btnIce.setOnClickListener(v -> {
            // Set selected state for Ice Drink button
            btnHot.setSelected(false);
            btnIce.setSelected(true);
            btnTea.setSelected(false);
            btnSnack.setSelected(false);

            // Load items for Ice Drink category
            loadItemsFromApi("Ice");
        });

        btnTea.setOnClickListener(v -> {
            // Set selected state for Tea button
            btnHot.setSelected(false);
            btnIce.setSelected(false);
            btnTea.setSelected(true);
            btnSnack.setSelected(false);

            // Load items for Tea category
            loadItemsFromApi("Tea");
        });

        btnSnack.setOnClickListener(v -> {
            // Set selected state for Snack button
            btnHot.setSelected(false);
            btnIce.setSelected(false);
            btnTea.setSelected(false);
            btnSnack.setSelected(true);

            // Load items for Snack category
            loadItemsFromApi("Snack");
        });


        // Navigate to CartActivity when the cart button is clicked
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CartActivity.class);
            startActivity(intent);
        });

        Bundle args = getArguments();
        if (args != null) {
            String category = args.getString("category");
            if (category != null) {
                // Load items based on the category received
                loadItemsFromApi(category);

                // Set the corresponding button to selected
                switch (category) {
                    case "Hot":
                        btnHot.setSelected(true);
                        loadItemsFromApi("Hot");
                        break;
                    case "Ice":
                        btnIce.setSelected(true);
                        loadItemsFromApi("Ice");
                        break;
                    case "Tea":
                        btnTea.setSelected(true);
                        loadItemsFromApi("Tea");
                        break;
                    case "Snack":
                        btnSnack.setSelected(true);
                        loadItemsFromApi("Snack");
                        break;
                }
            } else {
                loadItemsFromApi(null); // Load default items if no category
            }
        } else {
            loadItemsFromApi(null); // Load default items if no arguments
        }


    // Adjust for Bottom Navigation Bar
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, insets) -> {
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        // Setup SearchView to search via API
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchItemsFromApi(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadItemsFromApi(null);  // Reload all items if query is cleared
                } else {
                    searchItemsFromApi(newText);
                }
                return false;
            }
        });
    }

    private void loadItemsFromApi(String category) {
        String url = "http://192.168.146.156/makaryo2/api.php?action=get_items";

        if (category != null && !category.isEmpty()) {
            url += "&category=" + category;
        }

        progressBar.setVisibility(View.VISIBLE); // Tampilkan ProgressBar

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE); // Sembunyikan ProgressBar
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
                            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                            itemList.add(new MyItem(id, name, price, imageBitmap));
                        }
                        filteredItemList.clear();
                        filteredItemList.addAll(itemList);
                        myAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("Volley", "JSON Parsing Error: " + e.getMessage());
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE); // Sembunyikan ProgressBar
                    Log.e("Volley", "Error: " + error.getMessage());
                }
        );

        requestQueue.add(jsonArrayRequest);
    }


    private void searchItemsFromApi(String query) {
        String url = "http://192.168.146.156/makaryo2/api.php?action=search_item&query=" + query;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    filteredItemList.clear();
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

                            // Add the item to the filtered list
                            filteredItemList.add(new MyItem(id, name, price, imageBitmap));
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
