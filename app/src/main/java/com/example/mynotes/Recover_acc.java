package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class Recover_acc extends AppCompatActivity {


    TextInputLayout mprEmailLayout;
    AppCompatEditText mprEmail;
    AppCompatButton mprSubmit;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_acc);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.yellow));

        mprEmailLayout = findViewById(R.id.prEmailLayout);
        mprEmail = findViewById(R.id.prEmail);
        mprSubmit = findViewById(R.id.prSubmit);

        firebaseAuth = FirebaseAuth.getInstance();

        mprSubmit.setOnClickListener(this::onClick);
    }

    private void onClick(View v) {

        String email = mprEmail.getText().toString();
        //check email already exist or not.
        if (TextUtils.isEmpty(email)) {
            mprEmailLayout.setHelperText("required*");
            Toast.makeText(this, "Enter a valid Email ", Toast.LENGTH_LONG).show();
        } else {
//                firebaseAuth.sendPasswordResetEmail(email);
//                                    Toast.makeText(Recover_acc.this, "Password reset link is sent to "+email, Toast.LENGTH_LONG).show();
//                                    startActivity(new Intent(getApplicationContext(),Login_page.class));
//                                    finish();

//                // checking if the user email exists or not
            firebaseAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                        if (isNewUser) {
                            // new user
                            Toast.makeText(Recover_acc.this, "No user found, Sign in !", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), Sign_up.class));
                            Log.e("TAG", "Is New User!");
                        } else {
                            // old user
                            firebaseAuth.sendPasswordResetEmail(email);
                            Toast.makeText(Recover_acc.this, "Password reset link is sent to " + email, Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), Login_page.class));
                            onBackPressed();
                            Log.e("TAG", "Is Old User!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "failed to send", Toast.LENGTH_SHORT).show();
                    });

        }

    }
}




