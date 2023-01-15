package com.example.mynotes;

import static com.example.mynotes.R.color.androidx_core_ripple_material_light;
import static com.example.mynotes.R.color.fab_bg;
import static com.example.mynotes.R.color.white;
import static com.example.mynotes.R.color.yellow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.app.StatusBarManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.zip.Inflater;

public class Login_page extends AppCompatActivity {

    TextInputLayout mliEmailLayout, mliPasswordLayout;
    AppCompatEditText mliEmail, mliPassword;
    AppCompatButton mLogin_btn;
    TextView mSignup_TV, mForgot_pass_TV;

   private boolean isConnected = false;

    String email, password;

    FirebaseAuth firebaseAuth;

    ProgressBar mProgressBarOfLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // setting the status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, yellow));

        firebaseAuth = FirebaseAuth.getInstance();

        // binding the variables
        mliEmailLayout = findViewById(R.id.liEmailLayout);
        mliEmail = findViewById(R.id.liEmail);
        mliPasswordLayout = findViewById(R.id.liPasswordLayout);
        mliPassword = findViewById(R.id.liPassword);
        mLogin_btn = findViewById(R.id.Login_btn);
        mSignup_TV = findViewById(R.id.Signup_TV);
        mForgot_pass_TV = findViewById(R.id.Forgot_pass_TV);

        mProgressBarOfLogin = findViewById(R.id.ProgressBarOfLogin);

        // login button
        mLogin_btn.setOnClickListener(v -> {
            validateUser();
        });

        // forgot password
        mForgot_pass_TV.setOnClickListener(v -> {
            startActivity(new Intent(Login_page.this, Recover_acc.class));
        });

        // sign up
        mSignup_TV.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Sign_up.class));
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void validateUser() {

        email = mliEmail.getText().toString();
        password = mliPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mliEmailLayout.requestFocus();
            mliEmailLayout.setError("Required*");
        } else if (TextUtils.isEmpty(password)) {

            mliPasswordLayout.requestFocus();
            mliPasswordLayout.setError("Required*");
        } else {

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mProgressBarOfLogin.setVisibility(View.VISIBLE);
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            if (currentUser.isEmailVerified()) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                Toast.makeText(Login_page.this, "Login successful", Toast.LENGTH_SHORT).show();
                            } else {
                                mProgressBarOfLogin.setVisibility(View.INVISIBLE);
                                currentUser.sendEmailVerification();
                                Toast.makeText(getApplicationContext(), "Email verification link is sent to " + email, Toast.LENGTH_LONG).show();
                            }


                        }
                    })
                    .addOnFailureListener(e -> {
                        mProgressBarOfLogin.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Login failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

    }


    private boolean isEmailValid(String email) {

        // this function recognises the pattern or skeleton of the email
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}






//    @Override
//    protected void onStart() {
//        super.onStart();
// check whether user is connected to the internet or not

//        isConnected = checkInternet();

//         redirecting the user to the appropriate page
//        if ( !isConnected ) {
//            finish();
//            startActivity(new Intent(getApplicationContext(),RetryPage.class));
//        }
//    }
//
//    private boolean checkInternet() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
//
//        // Checking if the user is connected to the internet or not
//
//        if (connectivityManager.getActiveNetworkInfo() != null
//                && connectivityManager.getActiveNetworkInfo().isConnected()
//                && connectivityManager.getActiveNetworkInfo().isAvailable()) {
//            return true;
//        } else return false;


















