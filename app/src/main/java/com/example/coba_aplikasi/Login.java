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
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends BottomSheetDialogFragment {

    private TextInputEditText etUserlogin, etPasswordlogin;
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

        etUserlogin = view.findViewById(R.id.edit_user);
        etPasswordlogin = view.findViewById(R.id.edittext_pass);
        btnLogin = view.findViewById(R.id.button_Login);
        tvSwitchToRegister = view.findViewById(R.id.tvSwitchToRegister);

        btnLogin.setOnClickListener(v -> {
            String user = etUserlogin.getText().toString();
            String password = etPasswordlogin.getText().toString();

            if (!user.equals("") && !password.equals("")) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    String[] field = new String[2];
                    field[0] = "username";
                    field[1] = "password";
                    String[] data = new String[2];
                    data[0] = user;
                    data[1] = password;
                    PutData putData = new PutData("http://192.168.1.7/makaryo/login.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();

                            if (result.equals("Login Success")) {
                                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                dismiss();
                                Intent intent = new Intent(getActivity(), Dashboard.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        tvSwitchToRegister.setOnClickListener(v -> {
            dismiss();
            new Register().show(getParentFragmentManager(), "RegisterBottomSheet");
        });

        return view;
    }
}
