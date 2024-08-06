package com.example.dutysmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Signin extends AppCompatActivity {
    EditText editText_username, editText_password;
    Button button_signin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);
        editText_username = (EditText) findViewById(R.id.username);
        editText_password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        button_signin = (Button) findViewById(R.id.signin);


        button_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (!validateUsername() | !validatePassword()) {
                    progressBar.setVisibility(View.GONE);

                } else {
                    // checkUser();
                    checkUserNew();
                }

            }
        });

    }

    private void checkUserNew() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userUsername = editText_username.getText().toString().trim();
                String userPassword = editText_password.getText().toString().trim();
                if (snapshot.exists()) {
                    boolean userFound = false;

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        //  String username = userSnapshot.child("name").getValue(String.class);
                        String username = userSnapshot.getKey();

                        if (username != null && username.equals(userUsername)) {
                            userFound = true;
                            String password = userSnapshot.child("password").getValue(String.class);

                            if (password != null && password.equals(userPassword)) {
                                // Successful login
                                editText_username.setError(null);
                                Intent intent = new Intent(Signin.this, Sidenavigatiobbar.class);
                                startActivity(intent);
                                progressBar.setVisibility(View.GONE);
                                // Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // Invalid password
                                editText_password.setError("Invalid Credentials");
                                editText_password.requestFocus();
                                progressBar.setVisibility(View.GONE);
                            }
                            break; // Stop loop as we found the user
                        }
                    }
                    if (!userFound) {
                        Toast.makeText(Signin.this, "User not found", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                    }

                } else {
                    Toast.makeText(Signin.this, "No users exist", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public Boolean validateUsername(){
        String val=editText_username.getText().toString();
        if (val.isEmpty()){
            editText_username.setError("Username is Empty");
            return false;
        }else {
            editText_username.setError(null);
            return true;
        }
    }
    public Boolean validatePassword(){
        String val=editText_password.getText().toString();
        if (val.isEmpty()){
            editText_password.setError("Password is Empty");
            return false;
        }else {
            editText_password.setError(null);
            return true;
        }
    }
    public void checkUser(){
        String userUsername=editText_username.getText().toString().trim();
        String userPassword=editText_password.getText().toString().trim();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase=reference.orderByChild("Divya").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    editText_username.setError(null);
                    String passwordFromBD=snapshot.child(userUsername).child("password").getValue(String.class);
                    if (!Objects.equals(passwordFromBD,userPassword)){
                        editText_username.setError(null);
                        Intent intent=new Intent(Signin.this, Sidenavigatiobbar.class);
                        startActivity(intent);
                    }else {
                        editText_password.setError("Invalid Credentials");
                        editText_password.requestFocus();
                    }
                }else {
                    editText_username.setError("Invalid Not Credentials");
                    editText_username.requestFocus();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
