package com.example.coba_aplikasi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class SettingFragment extends Fragment {

    private static final int REQUEST_EDIT_PROFILE = 1;

    private ImageView profileImage;
    private TextView profileName;
    private Button editProfileButton, aboutButton, logoutButton, customerServiceButton1, customerServiceButton2;

    // Arguments for fragment initialization
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Initialize UI elements
        profileImage = view.findViewById(R.id.profileImage);
        profileName = view.findViewById(R.id.profileName);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        aboutButton = view.findViewById(R.id.aboutButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        customerServiceButton1 = view.findViewById(R.id.customerServiceButton1);
        customerServiceButton2 = view.findViewById(R.id.customerServiceButton2);

        // Retrieve customer_id from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int customerId = prefs.getInt("customer_id", -1); // Retrieve customer_id

        if (customerId != -1) {
            // Fetch profile data from the server
            fetchProfileData(customerId);
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Navigate to Edit Profile
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
            startActivityForResult(intent, REQUEST_EDIT_PROFILE);
        });

        // Show About dialog
        aboutButton.setOnClickListener(v -> showAboutDialog());

        // Logout action
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "You have logged out!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), Starting.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Customer Service 1 - WhatsApp
        customerServiceButton1.setOnClickListener(v -> openWhatsApp("6282234703352"));

        // Customer Service 2 - WhatsApp
        customerServiceButton2.setOnClickListener(v -> openWhatsApp("6289514429221"));

        return view;
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("About the App")
                .setMessage("This app provides a user-friendly interface for editing profiles and accessing settings.\n\n" +
                        "Version: 1.0\n" +
                        "Developer: App Development Team\n\n" +
                        "Thank you for using this app!")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void openWhatsApp(String phoneNumber) {
        String url = "https://wa.me/" + phoneNumber.replace("+", "").trim();
        Log.d("WhatsAppURL", "Generated URL: " + url);

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setPackage("com.whatsapp");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "WhatsApp is not available on your device.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Fetch profile data from the server
    private void fetchProfileData(int customerId) {
        String url = "http://192.168.149.184/makaryo/api.php?action=get_profil&customer_id=" + customerId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Log the response for debugging
                        Log.d("ProfileData", "Response: " + response.toString());

                        // Check for the status field to ensure we received valid data
                        if (response.has("status") && response.getString("status").equals("success")) {
                            // Set the profile name
                            profileName.setText(response.getString("fullname"));

                            // Decode the profile photo from base64
                            String profilePhotoBase64 = response.getString("profile_photo");
                            byte[] decodedBytes = android.util.Base64.decode(profilePhotoBase64, android.util.Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                            // Set the profile photo
                            profileImage.setImageBitmap(bitmap);
                        } else {
                            // Handle error if the response status is not 'success'
                            String errorMessage = response.has("message") ? response.getString("message") : "Unknown error";
                            Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle network errors
                    Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add request to Volley queue
        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_PROFILE && resultCode == getActivity().RESULT_OK && data != null) {
            String newName = data.getStringExtra("NEW_NAME");
            if (newName != null && !newName.isEmpty()) {
                profileName.setText(newName);
            }

            boolean isProfileImageUpdated = data.getBooleanExtra("IS_IMAGE_UPDATED", false);
            if (isProfileImageUpdated) {
                profileImage.setImageURI(data.getData());
            }
        }
    }
}
