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

public class SignUpActivity extends AppCompatActivity {

    EditText name, email, password, password_2;
    Button signup;
    TextView signin;
    FirebaseAuth firebaseAuthInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Paper.init(this);

        InitViews();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarUtils.showProgress(SignUpActivity.this, SignUpActivity.this);

                try{
                    String name_txt = name.getText().toString().trim();
                    String email_txt = email.getText().toString().trim();
                    String password_txt = password.getText().toString().trim();
                    String password2_txt = password_2.getText().toString().trim();

                    if(validateData(name_txt, email_txt, password_txt, password2_txt)){
                        firebaseAuthInstance = FirebaseAuth.getInstance();
                        firebaseAuthInstance.createUserWithEmailAndPassword(email_txt, password_txt)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = firebaseAuthInstance.getCurrentUser();
                                            updateUI();
                                        } else {
                                            progressBarUtils.hideProgress();
                                            Toast.makeText(SignUpActivity.this, "Sign Up failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else{
                        progressBarUtils.hideProgress();
                    }

                }catch (Exception e){
                    Toast.makeText(SignUpActivity.this, "Some error occurred, Please try again!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(i);
            }
        });

    }

    private boolean validateData(String name_txt, String email_txt, String password_txt, String password2_txt){
        if(name_txt.equals("") || email_txt.equals("") || password_txt.equals("") || password2_txt.equals("")){
            Toast.makeText(SignUpActivity.this, "Please Enter all details", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!password_txt.equals(password2_txt)){
            Toast.makeText(SignUpActivity.this, "Passwords don't match!!", Toast.LENGTH_SHORT).show();
            return false;
        }else if(password_txt.length() < 8){
            Toast.makeText(SignUpActivity.this, "Password length should be at least 8", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateUI() {
        Toast.makeText(this, "User signed-up successfully!!", Toast.LENGTH_SHORT).show();
        progressBarUtils.hideProgress();
        Toast.makeText(this, "Please Log-in with newly created user!!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(SignUpActivity.this, LogInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void InitViews() {
        name = findViewById(R.id.et_name);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        password_2 = findViewById(R.id.et_password_2);

        signin = findViewById(R.id.btn_login);
        signup = findViewById(R.id.btn_signup);
    }

}