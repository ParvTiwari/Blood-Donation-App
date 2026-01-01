package com.example.assignmentblooddonor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    Spinner bloodGroup;
    EditText edtCity;
    Button search;
    ListView list;
    ArrayList<String> donorList;
    ArrayList<String> phoneList;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        edtCity = findViewById(R.id.editCity);
        bloodGroup = findViewById(R.id.bloodType);
        list = findViewById(R.id.list);
        search = findViewById(R.id.btnSearch);
        db = FirebaseFirestore.getInstance();

        donorList = new ArrayList<>();
        phoneList = new ArrayList<>();

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.blood_group, android.R.layout.simple_spinner_item);
        bloodGroup.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city= edtCity.getText().toString().trim().toLowerCase();
                String bloodType= bloodGroup.getSelectedItem().toString();

                if(city.isEmpty()){
                    Toast.makeText(SearchActivity.this, "Invalid City", Toast.LENGTH_SHORT).show();
                }

                db.collection("Users").whereEqualTo("City",city).whereEqualTo("Blood Group",bloodType).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        donorList.clear();
                        if(queryDocumentSnapshots.isEmpty()){
                            Toast.makeText(SearchActivity.this, "No Record Found", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            for(QueryDocumentSnapshot document:queryDocumentSnapshots ){
                                String name = document.getString("name");
                                String phone = document.getString("phone");
                                donorList.add("Name : "+name+" || Phone: "+phone);
                                phoneList.add(phone);
                            }
                            ArrayAdapter<String> arrayadapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1, donorList);
                            list.setAdapter(arrayadapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SearchActivity.this, "No Donor Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String phoneNum = phoneList.get(i);
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:"+phoneNum));
                startActivity(call);
            }
        });
    }
}