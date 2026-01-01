package com.example.assignmentblooddonor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button login, signUp, dummy;
    TextView nameTxt, phoneTxt, cityTxt;
    EditText email, password, name, phone, city;
    Spinner bloodGroup;
    private DatabaseReference database;
    private FirebaseAuth auth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        nameTxt = findViewById(R.id.textView4);
        phoneTxt = findViewById(R.id.textView5);
        cityTxt = findViewById(R.id.cty);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        name = findViewById(R.id.editText);
        phone = findViewById(R.id.editText2);
        city = findViewById(R.id.editText3);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signup);
        dummy = findViewById(R.id.dummy);
        bloodGroup = findViewById(R.id.bloodGroup);
        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.blood_group, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        bloodGroup.setAdapter(adapter);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailip = email.getText().toString().trim();
                String passwordip = password.getText().toString().trim();

                if (isValidLoginInput(emailip, passwordip)){
                    db.collection("Users").whereEqualTo("Email", emailip).whereEqualTo("Password", passwordip).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("email",emailip);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    /*auth.signInWithEmailAndPassword(emailip, passwordip).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }
                else{
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
            private boolean isValidLoginInput(String email, String password) {
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        });

        dummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setVisibility(View.GONE);
                nameTxt.setVisibility(View.VISIBLE);
                phoneTxt.setVisibility(View.VISIBLE);
                cityTxt.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                phone.setVisibility(View.VISIBLE);
                city.setVisibility(View.VISIBLE);
                bloodGroup.setVisibility(View.VISIBLE);

                dummy.setVisibility(View.GONE);
                signUp.setVisibility(View.VISIBLE);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String nameip = name.getText().toString();
                    String phoneip = phone.getText().toString();
                    String cityip = city.getText().toString();
                    String bloodGroupip = bloodGroup.getSelectedItem().toString();
                    String emailip = email.getText().toString();
                    String passwordip = password.getText().toString();

                    if (isValidSignUpInput(nameip, phoneip, cityip, emailip, passwordip)) {
                        HashMap<String, Object> user = new HashMap<>();
                        user.put("Name", nameip);
                        user.put("Email", emailip);
                        user.put("Phone", phoneip);
                        user.put("Blood Group", bloodGroupip);
                        user.put("City", cityip);
                        user.put("Password", passwordip);
                        db.collection("Users").add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                name.setText("");
                                email.setText("");
                                phone.setText("");
                                city.setText("");
                                password.setText("");
                                bloodGroup.setSelection(0);
                                Toast.makeText(LoginActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            }
                        });
                        auth.createUserWithEmailAndPassword(emailip, passwordip).addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    String uid = auth.getCurrentUser().getUid();
//                                    User user = new User(nameip, phoneip, bloodGroupip, cityip, emailip, passwordip);
                                    /*database.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "User data saved", Toast.LENGTH_SHORT).show();
                                            } else {
                                                String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                                Toast.makeText(LoginActivity.this, "Failed to save data: " + error, Toast.LENGTH_LONG).show();
                                                Log.e("FirebaseDB", "Data write error", task.getException());
                                            }
                                        }
                                    });*/
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();
                                }
                                else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                    Toast.makeText(LoginActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
                                    Log.e("FirebaseAuth", "Registration error", task.getException());
                                }
                        });
                    }
            }
            private boolean isValidSignUpInput(String name, String phone, String city, String email, String password) {
                if(name.isEmpty() || phone.isEmpty() || city.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        });
    }
    /*public static class User {
        public String name, phone, bloodGroup, city, email, password;
        public User(){

        }
        public User(String name, String phone, String bloodGroup, String city) {
            this.name = name;
            this.phone = phone;
            this.bloodGroup = bloodGroup;
            this.city = city;
        }
        public User(String name, String phone, String bloodGroup, String city, String email, String password) {
            this.name = name;
            this.phone = phone;
            this.bloodGroup = bloodGroup;
            this.city = city;
            this.email = email;
            this.password = password;
        }
    }*/
}