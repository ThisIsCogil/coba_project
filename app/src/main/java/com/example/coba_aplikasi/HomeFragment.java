package com.example.coba_aplikasi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private SliderAdapter sliderAdapter;
    private int[] images = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3
    };
    private Handler handler;
    private Runnable runnable;
    private int currentPage = 0;

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<MyItem> itemList;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the ViewPager for image slider
        viewPager = view.findViewById(R.id.sliderImage);
        sliderAdapter = new SliderAdapter(images, getContext());
        viewPager.setAdapter(sliderAdapter);

        // Setup automatic sliding
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == images.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000); // Change slide every 3 seconds
            }
        };
        handler.postDelayed(runnable, 3000);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        itemList = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(), itemList);
        recyclerView.setAdapter(myAdapter);

        // Load items from API
        loadItemsFromApi();
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

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Stop sliding when fragment is paused
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000); // Resume sliding when fragment is resumed
    }
}
