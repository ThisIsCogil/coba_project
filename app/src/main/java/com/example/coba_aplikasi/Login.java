package com.example.coba_aplikasi;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.coba_aplikasi.Dashboard;
import com.example.coba_aplikasi.R;
import com.example.coba_aplikasi.Register;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends BottomSheetDialogFragment {

    private TextInputEditText etUser, etPassword;
    private Button btnLogin;
    private TextView tvSwitchToRegister;

    public Login() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);

        etUser = view.findViewById(R.id.edit_user);
        etPassword = view.findViewById(R.id.edittext_pass);
        btnLogin = view.findViewById(R.id.button_Login);
        tvSwitchToRegister = view.findViewById(R.id.tvSwitchToRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etUser.getText().toString();
            String password = etPassword.getText().toString();
            if (validateInputs(email, password)) {
                // Handle login logic here
                Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                dismiss();
                Intent intent = new Intent(getActivity(), Dashboard.class);
                startActivity(intent);

            }
        });

        tvSwitchToRegister.setOnClickListener(v -> {
            dismiss();
            new Register().show(getParentFragmentManager(), "RegisterBottomSheet");
        });

        return view;
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            etUser.setError("Email is required");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return false;
        }
        return true;
    }
}
