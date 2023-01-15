package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.viewpager.widget.PagerTabStrip;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.MediaCodec;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sign_up extends AppCompatActivity {


    String email,password;
    TextInputLayout msuEmailLayout,msuPasswordLayout;
    AppCompatEditText msuEmail,msuPassword;
    AppCompatButton  mSign_up_btn;
    TextView Sign_in_TV;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.yellow));

        msuEmailLayout = findViewById(R.id.suEmailLayout);
        msuPasswordLayout = findViewById(R.id.suPasswordLayout);
        msuEmail = findViewById(R.id.suEmail);
        msuPassword = findViewById(R.id.suPassword);
        Sign_in_TV = findViewById(R.id.Sign_in_TV);

        mSign_up_btn = findViewById(R.id.Sign_up_btn);

        mSign_up_btn.setOnClickListener(v -> {registerUser();});

        Sign_in_TV.setOnClickListener(v -> {finish(); startActivity(new Intent(getApplicationContext(),Login_page.class));});


        msuPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                msuPasswordLayout.setHelperText(" ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String password = s.toString();
                if (password.length() >= 8) {

                    Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                    Matcher matcher = pattern.matcher(password);
                    boolean isTextHasSpecialChar = matcher.find();
                    if (isTextHasSpecialChar) {
                        msuPasswordLayout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
                        msuPasswordLayout.setHelperText("Strong Password");
                    }
                    else{
                        msuPasswordLayout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error)));
                        msuPasswordLayout.setHelperText("Weak password include special characters");
                    }
                }
                else{
                    msuPasswordLayout.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error)));
                    msuPasswordLayout.setHelperText("Password must be atleast 8 characters ");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void registerUser() {

        email = msuEmail.getText().toString();
        password = msuPassword.getText().toString();
        // code for registering user

        if (TextUtils.isEmpty(email)) {

            msuEmailLayout.requestFocus();
            msuEmailLayout.setHelperText("Required*");

        }else if (TextUtils.isEmpty(password)){
            msuPasswordLayout.requestFocus();
            msuPasswordLayout.setHelperText("Required*");
        }
        else{

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        finish();
                        startActivity(new Intent(getApplicationContext(),Login_page.class));
                        Toast.makeText(Sign_up.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Sign_up.this, "Registration failed : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
//        Toast.makeText(getApplicationContext(), "Registered successfully ", Toast.LENGTH_SHORT).show();

        // code for navigating back to the login activity

    }
}