package com.example.coba_aplikasi;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends BottomSheetDialogFragment {

    private TextInputEditText etUser,etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvSwitchToLogin;

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
        etConfirmPassword = view.findViewById(R.id.edittext_confirmpass);
        btnRegister = view.findViewById(R.id.button_Register);
        tvSwitchToLogin = view.findViewById(R.id.tvSwitchToLogin);

        btnRegister.setOnClickListener(v -> {
            String user = etUser.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            if (validateInputs(user, email, password, confirmPassword)) {
                // Handle registration logic here
                Toast.makeText(getContext(), "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                dismiss();
                new Login().show(getParentFragmentManager(), "LoginBottomSheet");
            }
        });

        tvSwitchToLogin.setOnClickListener(v -> {
            dismiss();
            new Login().show(getParentFragmentManager(), "LoginBottomSheet");
        });

        return view;
    }

    private boolean validateInputs(String user,String email, String password, String confirmPassword) {
        if (user.isEmpty()) {
            etUser.setError("Username Tidak Boleh Kosong");
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email Tidak Boleh Kosong");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password Tidak Boleh Kosong");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password Tidak Sama");
            return false;
        }
        return true;
    }
}