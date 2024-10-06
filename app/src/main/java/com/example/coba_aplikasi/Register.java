package com.example.coba_aplikasi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class Register extends AppCompatActivity {

    private TextInputEditText edtemail, edtuser, edtpass;
    private static final String KEY_NAME = "username";
    private static final String KEY_PASS = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        edtemail = findViewById(R.id.edit_email);
        edtuser = findViewById(R.id.edit_user);
        edtpass = findViewById(R.id.edittext_pass);
        Button btnreg = findViewById(R.id.btnregister);

        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtemail.getText().toString();
                String username = edtuser.getText().toString();
                String password = edtpass.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString(KEY_NAME, username);
                editor.putString(KEY_PASS, password);
                editor.apply();

                Intent intent = new Intent(Register.this, Login.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });



}
}
