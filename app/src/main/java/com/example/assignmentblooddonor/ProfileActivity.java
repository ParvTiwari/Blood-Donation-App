package com.example.assignmentblooddonor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private EditText edtName, edtCity, edtPhone;
    private Button saveProfile, logout;
    Spinner bloodGroup;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        edtName = findViewById(R.id.editText);
        edtPhone = findViewById(R.id.editText1);
        edtCity = findViewById(R.id.editText2);
        saveProfile = findViewById(R.id.button2);
        logout = findViewById(R.id.btnLogout);
        bloodGroup = findViewById(R.id.bloodGroup);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_group, android.R.layout.simple_spinner_item);
        bloodGroup.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        db = FirebaseFirestore.getInstance();
        Intent iprofile=getIntent();
        String email= iprofile.getStringExtra("email");
        Toast.makeText(this, "Welcome" + email, Toast.LENGTH_LONG).show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("Users")
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                String name = snapshot.getString("Name");
                                String city = snapshot.getString("City");
                                String phone = snapshot.getString("Phone");
                                String bloodGroup1 = snapshot.getString("Blood Group");

                                edtName.setText(name);
                                edtCity.setText(city);
                                edtPhone.setText(phone);

                                int index = ((ArrayAdapter) bloodGroup.getAdapter()).getPosition(bloodGroup1);
                                bloodGroup.setSelection(index);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No user data found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


        /*databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LoginActivity.User currentUser = dataSnapshot.getValue(LoginActivity.User.class);
                edtName.setText(currentUser.name);
                edtCity.setText(currentUser.city);
                edtPhone.setText(currentUser.phone);
                int spinnerPosition = adapter.getPosition(currentUser.bloodGroup);
                bloodGroup.setSelection(spinnerPosition);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });*/

        saveProfile.setOnClickListener(view -> {
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String city = edtCity.getText().toString().trim();
            String selectedBloodGroup = bloodGroup.getSelectedItem().toString();

            HashMap<String, Object> user = new HashMap<>();
            user.put("Name", name);
            user.put("Phone", phone);
            user.put("Blood Group", selectedBloodGroup);
            user.put("City", city);

            db.collection("Users").whereEqualTo("Email", email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(!queryDocumentSnapshots.isEmpty()) {
                        String docId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("Users").document(docId).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ProfileActivity.this, "Profile Updated SuccessFully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfileActivity.this, "Update Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else {
                        Toast.makeText(ProfileActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (name.isEmpty() || phone.isEmpty() || city.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            /*LoginActivity.User updatedUser = new LoginActivity.User(name, phone, selectedBloodGroup, city);

            databaseReference.setValue(updatedUser)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show());*/
        });

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }
}