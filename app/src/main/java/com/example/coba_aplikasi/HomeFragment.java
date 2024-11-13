package com.example.coba_aplikasi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import model.MyItem;


public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Slider and RecyclerView components
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        sliderAdapter = new SliderAdapter(images, getContext());
        viewPager.setAdapter(sliderAdapter);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize the item list
        itemList = new ArrayList<>();
        itemList.add(new MyItem("Americano", "Budak 1", R.drawable.image1));
        itemList.add(new MyItem("Latte", "Budak 2", R.drawable.image2));
        itemList.add(new MyItem("Cappuccino", "Budak 3", R.drawable.image3));
        // Add more items as needed

        myAdapter = new MyAdapter(getContext(), itemList);
        recyclerView.setAdapter(myAdapter);

        // Set up automatic sliding
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
        handler.postDelayed(runnable, 3000); // Start sliding after 3 seconds
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
