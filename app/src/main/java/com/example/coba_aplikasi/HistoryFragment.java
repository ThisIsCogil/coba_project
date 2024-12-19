package com.example.coba_aplikasi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.HistoryItem;

public class HistoryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Button btnProses, btnSelesai;
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

        btnProses = view.findViewById(R.id.btnProses);
        btnSelesai = view.findViewById(R.id.btnSelesai);
        recyclerView = view.findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        itemList = new ArrayList<>();
        adapter = new HistoryAdapter(requireContext(), itemList);
        recyclerView.setAdapter(adapter);

        btnProses.setSelected(true);
        // Fetch "process" orders by default
        fetchOrders("process");

        // Set button click listeners
        btnProses.setOnClickListener(v -> {
            setActiveButton(btnProses);
            fetchOrders("process");
        });

        btnSelesai.setOnClickListener(v -> {
            setActiveButton(btnSelesai);
            fetchOrders("finished");
        });

        return view;
    }

    private void setActiveButton(Button activeButton) {
        // Set semua tombol ke state tidak aktif
        btnProses.setSelected(false);
        btnSelesai.setSelected(false);

        // Set tombol yang dipilih ke state aktif
        activeButton.setSelected(true);
    }

    private void fetchOrders(String status) {
        String url = "http://192.168.146.156/makaryo2/api.php?action=get_order_history";
        SharedPreferences prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int customerId = prefs.getInt("customer_id", -1);

        if (customerId == -1) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        itemList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String orderMenu = jsonObject.getString("order_menu");
                            String orderDate = jsonObject.getString("order_date");
                            double totalAmount = jsonObject.getDouble("total_amount");
                            itemList.add(new HistoryItem(orderMenu, orderDate, totalAmount));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("status", status);
                params.put("customer_id", String.valueOf(customerId));
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }
}