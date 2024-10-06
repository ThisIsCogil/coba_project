package com.example.coba_aplikasi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {
    private TextInputEditText edtuser, edtpass;
    private static final String KEY_NAME = "username";
    private static final String KEY_PASS = "password";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        edtuser = findViewById(R.id.edit_user);
        edtpass = findViewById(R.id.edittext_pass);
        Intent intent = getIntent();
        String username = intent.getStringExtra(KEY_NAME);
        edtuser.setText(username);
        Button btnlog = findViewById(R.id.btnlogin);


        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputUsername = edtuser.getText().toString();
                String inputPassword = edtpass.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String savedUsername = sharedPreferences.getString(KEY_NAME, null);
                String savedPassword = sharedPreferences.getString(KEY_PASS, null);

                if (inputUsername.equals(savedUsername) && inputPassword.equals(savedPassword)) {
                    Intent intent = new Intent(Login.this, Dashboard.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(Login.this, "Username atau Password salah", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
