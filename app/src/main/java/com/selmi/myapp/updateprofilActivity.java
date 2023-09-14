package com.selmi.myapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class updateprofilActivity extends AppCompatActivity {
    Button updatebutton ;
    TextInputLayout txtLayoutUsername ,txtLayoutMail ,txtLayoutPassword,txtLayoutPasswordConf;
    TextInputEditText txtInputUsername,txtInputMail,txtInputPassword,txtInputPasswordConf;
    FirebaseAuth auth  ;
    FirebaseUser currentUser  ;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofil);
        updatebutton = findViewById(R.id.btn_update);
        txtLayoutUsername = findViewById(R.id.txtLay_username);
        txtInputUsername =(TextInputEditText) txtLayoutUsername.getEditText();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        String uid = currentUser.getUid();
        DatabaseReference userRef = usersRef.child(uid);

        // Retrieve the user data from the database
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the user object from the dataSnapshot
                HelperClass user = dataSnapshot.getValue(HelperClass.class);

                if (user != null) {
                    txtInputUsername.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get the user object from the dataSnapshot
                        HelperClass user = dataSnapshot.getValue(HelperClass.class);

                        if (user != null) {
                            updateUser(user.getIdUser(), user.getMail(), user.getPassword());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors here
                    }
                });

            }
        });
    }
    private void updateUser(String uid, String email, String password) {
        String username = txtInputUsername.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating User...");
        progressDialog.show();

        // update user information in Firebase Realtime Database
        HelperClass user = new HelperClass(uid, username, email, password);
        usersRef.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(updateprofilActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(updateprofilActivity.this, MenuActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(updateprofilActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }}