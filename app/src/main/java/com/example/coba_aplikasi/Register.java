package com.example.coba_aplikasi;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends BottomSheetDialogFragment {

    private TextInputEditText etUser, etEmail, etPassword, etFullname;
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
        etFullname = view.findViewById(R.id.edit_fullname);
        btnRegister = view.findViewById(R.id.button_Register);
        tvSwitchToLogin = view.findViewById(R.id.tvSwitchToLogin);

        btnRegister.setOnClickListener(v -> {
            String fullname = etFullname.getText().toString();
            String email = etEmail.getText().toString();
            String user = etUser.getText().toString();
            String password = etPassword.getText().toString();

            if (!fullname.equals("") && !email.equals("") && !user.equals("") && !password.equals("")) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    // Starting Write and Read data with URL
                    // Creating array for parameters
                    String[] field = new String[4];
                    field[0] = "fullname";
                    field[1] = "email";
                    field[2] = "username";
                    field[3] = "password";
                    // Creating array for data
                    String[] data = new String[4];
                    data[0] = fullname;
                    data[1] = email;
                    data[2] = user;
                    data[3] = password;
                    PutData putData = new PutData("http://192.168.1.7/makaryo/signup.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            // Handle registration logic here
                            if (result.equals("Sign Up Success")) {
                                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                dismiss();
                                new Login().show(getParentFragmentManager(), "LoginBottomSheet");
                            } else {
                                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    // End Write and Read data with URL
                });
            }
        });

        tvSwitchToLogin.setOnClickListener(v -> {
            dismiss();
            new Login().show(getParentFragmentManager(), "LoginBottomSheet");
        });

        return view;
    }
}

