package com.example.coba_aplikasi;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.provider.MediaStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends Activity {

    private static final int GALLERY_REQUEST_CODE = 1001;
    private static final int PICK_IMAGE_REQUEST = 2;

    private EditText editProfileName;
    private ImageView profileImageEdit;
    private Button saveButton, changePhotoButton, changePasswordButton;

    private SharedPreferences sharedPreferences;
    private int customerId;

    private boolean isImageUpdated = false; // Indikator apakah foto telah diubah
    private Uri selectedImageUri; // URI gambar yang dipilih

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Inisialisasi elemen
        profileImageEdit = findViewById(R.id.profileImageEdit);
        editProfileName = findViewById(R.id.editProfileName);
        saveButton = findViewById(R.id.saveButton);
        changePhotoButton = findViewById(R.id.changePhotoButton);
        changePasswordButton = findViewById(R.id.changePasswordButton); // Menambahkan tombol Ganti Password

        // Ambil customer_id dari SharedPreferences
        // Inside EditProfileActivity
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int customerId = prefs.getInt("customer_id", -1); // Retrieve customer_id



        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });
        // Event listener untuk tombol ganti foto profil
        changePhotoButton.setOnClickListener(v -> openGallery());

        // Event listener untuk tombol simpan
        saveButton.setOnClickListener(v -> saveProfileData());
    }

    // Membuka galeri untuk memilih gambar
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    // Menangani hasil pemilihan gambar dari galeri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {
            selectedImageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profileImageEdit.setImageBitmap(bitmap);
                isImageUpdated = true; // Menandakan bahwa gambar telah diperbarui
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveProfileData() {
        String fullname = editProfileName.getText().toString();
        Bitmap bitmap = ((BitmapDrawable) profileImageEdit.getDrawable()).getBitmap();
        String profilePhoto = encodeImageToBase64(bitmap);

        // Retrieve customer_id from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        final int customerId = prefs.getInt("customer_id", -1); // Default value is -1

        // Send data to the server using Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://192.168.149.184/makaryo/api.php?action=update_profile";

        // Create a JSONObject to hold the data
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("customer_id", customerId);
            jsonObject.put("fullname", fullname);
            jsonObject.put("profile_photo", profilePhoto);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send the request with JSON body
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if ("success".equals(status)) {
                                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // Set content type to JSON
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }




    // Encode gambar menjadi Base64 untuk dikirim ke server
    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
