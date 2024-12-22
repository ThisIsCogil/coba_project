package com.example.coba_aplikasi;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.app.AlertDialog;
import android.content.DialogInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Register extends BottomSheetDialogFragment {

    private TextInputEditText etUser, etEmail, etPassword, etFullname;
    private Button btnRegister;
    private TextView tvSwitchToLogin;
    private RequestQueue requestQueue;

    public Register() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_register, container, false);

        etUser = view.findViewById(R.id.edit_user);
        etEmail = view.findViewById(R.id.edit_email);
        etPassword = view.findViewById(R.id.edittext_pass);
        etFullname = view.findViewById(R.id.edit_fullname);
        btnRegister = view.findViewById(R.id.button_Register);
        tvSwitchToLogin = view.findViewById(R.id.tvSwitchToLogin);

        requestQueue = Volley.newRequestQueue(requireContext());

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tvSwitchToLogin.setOnClickListener(v -> {
            // Assuming this fragment is shown in a BottomSheetDialogFragment
            dismiss(); // Dismiss the current fragment
            new Login().show(getParentFragmentManager(), "LoginBottomSheet");
        });

        return view;
    }

    private void registerUser() {
        String fullname = etFullname.getText().toString();
        String email = etEmail.getText().toString();
        String username = etUser.getText().toString();
        String password = etPassword.getText().toString();

        if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
            Toast.makeText(getContext(), "Format Email Tidak Valid", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate username format (must contain both letters and numbers)
        if (!username.matches("^(?=.*[a-zA-Z])(?=.*\\d).+$")) {
            Toast.makeText(getContext(), "Username harus gabungan huruf dan angka", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password length
        if (password.length() < 5) {
            Toast.makeText(getContext(), "Password harus memiliki minimal 5 karakter", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create JSON object
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("fullname", fullname);
            jsonBody.put("email", email);
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.146.156/makaryo2/api.php?action=register",
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
                                dismiss(); // Dismiss the current fragment
                                new Login().show(getParentFragmentManager(), "LoginBottomSheet");
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
                params.put("fullname", fullname);
                params.put("email", email);
                params.put("username", username);
                params.put("password", password);
                return params;
            }

            @Override
            public byte[] getBody() {
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }
        };

        if (fullname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Field Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            requestQueue.add(stringRequest);
        }
    }
}