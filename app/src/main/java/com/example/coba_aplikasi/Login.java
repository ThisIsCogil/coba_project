package com.example.coba_aplikasi;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.example.coba_aplikasi.Dashboard;
import com.example.coba_aplikasi.R;
import com.example.coba_aplikasi.Register;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Login extends BottomSheetDialogFragment {

    private TextInputEditText etUserlogin, etPasswordlogin;
    private Button btnLogin;
    private TextView tvSwitchToRegister;
    private RequestQueue requestQueue;

    public Login() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);

        etUserlogin = view.findViewById(R.id.edit_user);
        etPasswordlogin = view.findViewById(R.id.edittext_pass);
        btnLogin = view.findViewById(R.id.button_Login);
        tvSwitchToRegister = view.findViewById(R.id.tvSwitchToRegister);

        requestQueue = Volley.newRequestQueue(requireContext());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        tvSwitchToRegister.setOnClickListener(v -> {
            // Assuming this fragment is shown in a BottomSheetDialogFragment
            dismiss(); // Dismiss the current fragment
            new Register().show(getParentFragmentManager(), "LoginBottomSheet");
        });

        return view;
    }

    private void LoginUser() {
        String user = etUserlogin.getText().toString();
        String password = etPasswordlogin.getText().toString();

        // Create JSON object
        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("username", user);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.146.156/makaryo2/api.php?action=login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ResponseRaw", response); // Log the raw response
                        try {
                            // Attempt to parse the response as JSON
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            if ("success".equals(status)) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                dismiss();
                                Intent intent = new Intent(getActivity(), Dashboard.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage;
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String responseBody = new String(error.networkResponse.data);
                            Log.e("VolleyError", responseBody); // Log the error response
                            errorMessage = "Error: " + responseBody;
                        } else {
                            errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", user);
                params.put("password", password);
                return params;
            }

            @Override
            public byte[] getBody() {
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }
        };

        if (user.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Field Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            requestQueue.add(stringRequest);
        }
    }
}