package com.example.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;

public class LogInActivity extends AppCompatActivity {

    EditText et_email, et_password;
    Button btn_sign_in;
    TextView btn_sign_up;
    FirebaseAuth firebaseAuthInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);
        initViews();

        try {
            boolean isLoggedIn = Paper.book().read("isLoggedIn", false);

            if(isLoggedIn){

            }
        }catch (NullPointerException e){
            Log.e("LogInActivity", "could not read isLoggedIn key");
        }

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String email_txt = et_email.getText().toString().trim();
                    String password_txt = et_password.getText().toString().trim();

                    if(email_txt.equals("") || password_txt.equals("")){
                        Toast.makeText(LogInActivity.this, "Please Enter all details", Toast.LENGTH_SHORT).show();
                    }else{
                        progressBarUtils.showProgress(LogInActivity.this, LogInActivity.this);
                        signIn(email_txt, password_txt);
                    }
                }catch (Exception e){
                    Toast.makeText(LogInActivity.this, "Some Error Occured, Please try again!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    private void signIn(String email_txt, String password_txt) {
        firebaseAuthInstance.signInWithEmailAndPassword(email_txt, password_txt)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuthInstance.getCurrentUser();
                            progressBarUtils.hideProgress();
                            Paper.book().write("isLoggedIn", true);
                            updateUI(user);
                        } else {
                            progressBarUtils.hideProgress();
                            Log.e("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent i = new Intent(LogInActivity.this, DashboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void initViews() {
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        firebaseAuthInstance = FirebaseAuth.getInstance();
    }

}