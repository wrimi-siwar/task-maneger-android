package com.selmi.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
import android.util.Patterns;
import android.graphics.drawable.ColorDrawable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class loginActivity extends AppCompatActivity {
    Button loginButtonL,signupButtonL;
    TextInputLayout loginLayoutMail  ,loginLayoutPassword;
    TextInputEditText loginInputMail ,loginInputPassword;
    SwitchCompat switchCompat;
    TextView forgotPassword;
    private SharedPreferences sharedPref;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_login);
        loginButtonL =findViewById(R.id.btn_login);
        signupButtonL=findViewById(R.id.login_signup_button);
        loginLayoutMail  = findViewById(R.id.txtLay_user);
        loginInputMail =(TextInputEditText) loginLayoutMail.getEditText();
        loginLayoutPassword= findViewById(R.id.txtLay_pass);
        loginInputPassword=(TextInputEditText) loginLayoutPassword.getEditText();
        forgotPassword = findViewById(R.id.txt_forPassword);
        switchCompat = findViewById(R.id.sh_remember);
        auth = FirebaseAuth.getInstance();
        sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            String email = loginInputMail.getText().toString().trim();
            String pass = loginInputPassword.getText().toString().trim();
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                if (isChecked) {
                    // Le SwitchCompat est activé, donc stocker les données dans SharedPreferences
                    editor.putString("email", email);
                    editor.putString("password", pass);
                    editor.apply();
                    Toast.makeText(loginActivity.this, "saved coordinates", Toast.LENGTH_SHORT).show();

                } else {
                    // Le SwitchCompat est désactivé, donc supprimer les données de SharedPreferences
                    editor.remove("email");
                    editor.remove("password");
                    editor.apply();
                    Toast.makeText(loginActivity.this, "saving coordinates canceled", Toast.LENGTH_SHORT).show();

                }
            }
        });

        loginButtonL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = loginInputMail.getText().toString().trim();
                String pass = loginInputPassword.getText().toString().trim();
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(loginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(loginActivity.this, MenuActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(loginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    } else {
                        loginInputPassword.setError("Empty password are not allowed");
                    }
                } else if (email.isEmpty()) {
                    loginLayoutMail.setError("Empty email are not allowed");
                } else {
                    loginInputMail.setError("Please enter correct email");
                }
            }
        });

        signupButtonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, signupActivity.class);
                startActivity(intent);
            }
        });



        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.ressetpassword, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();
                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            Toast.makeText(loginActivity.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(loginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(loginActivity.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
    }

}