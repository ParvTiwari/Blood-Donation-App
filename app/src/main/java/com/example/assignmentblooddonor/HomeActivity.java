package com.example.assignmentblooddonor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button find, profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        find = findViewById(R.id.button);
        profile = findViewById(R.id.button1);

        Intent ihome=getIntent();
        String email= ihome.getStringExtra("email");
        Toast.makeText(this, "Welcome: " + email, Toast.LENGTH_LONG).show();
        find.setOnClickListener(v-> startActivity(new Intent(HomeActivity.this, SearchActivity.class)));
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("email",email);
                startActivity(intent);
            }
        });
    }
}