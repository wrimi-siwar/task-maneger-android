package com.selmi.myapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signupActivity extends AppCompatActivity {
    Button loginButton,signupButton;
    TextInputLayout txtLayoutUsername ,txtLayoutMail ,txtLayoutPassword,txtLayoutPasswordConf;
    TextInputEditText txtInputUsername,txtInputMail,txtInputPassword,txtInputPasswordConf;
    FirebaseDatabase database;
    private FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseUser useruid ;
    private SharedPreferences sharedPref;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        loginButton = findViewById(R.id.signup_login_button);
        signupButton = findViewById(R.id.btn_signup);
        txtLayoutUsername = findViewById(R.id.txtLay_username);
        txtInputUsername =(TextInputEditText) txtLayoutUsername.getEditText();
        txtLayoutMail = findViewById(R.id.txtLay_emailAdd);
        txtInputMail =(TextInputEditText) txtLayoutMail.getEditText();
        txtLayoutPassword = findViewById(R.id.txtLay_pass_signup);
        txtInputPassword =(TextInputEditText) txtLayoutPassword.getEditText();
        txtLayoutPasswordConf = findViewById(R.id.txtLay_passConf_signup);
        txtInputPasswordConf =(TextInputEditText) txtLayoutPasswordConf.getEditText();
        sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database=FirebaseDatabase.getInstance();
                reference =database.getReference("users");
                String username = txtInputUsername.getText().toString();
                String email = txtInputMail.getText().toString();
                String password = txtInputPassword.getText().toString();

                String confirmPassword = txtInputPasswordConf.getText().toString().trim();
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    if (!password.isEmpty() ) {
                        if(isPasswordValid(password))
                        {
                            if (!password.equals(confirmPassword))
                            {
                                Toast.makeText(getApplicationContext(), " Passwords do not match"   , Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            useruid = FirebaseAuth.getInstance().getCurrentUser();
                                            String uid = useruid.getUid();
                                            Toast.makeText(signupActivity.this, "SignUp Successful ", Toast.LENGTH_SHORT).show();
                                            HelperClass user = new HelperClass(uid,username, email, password);
                                            reference.child(uid).setValue(user);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("email", email);
                                            editor.putString("password", password);
                                            editor.apply();
                                            Intent intent = new Intent(signupActivity.this, MenuActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(signupActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                        else
                        {
                            txtLayoutPassword.setError("Please enter correct password : Le mot de passe doit contenir :\n- Au moins une lettre majuscule\n- Au moins une lettre minuscule \n- un chiffre \n- un caractère spécial (@#$%^&+=) et doit avoir une longueur d'au moins 8 caractères. Veuillez entrer un mot de passe valide");
                        }

                    } else
                    {
                        txtLayoutPassword.setError("Empty password are not allowed");
                    }
                } else if (email.isEmpty()) {
                    txtLayoutMail.setError("Empty email are not allowed");
                } else {
                    txtLayoutMail.setError("Please enter correct email");
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signupActivity.this, loginActivity.class);
                startActivity(intent);


            }
        });
    }
    public boolean isPasswordValid(String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(pattern);
    }
}