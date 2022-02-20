package com.example.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaselogin.Adapter.CommentsAdapter;
import com.example.firebaselogin.Retrofit.CommentsApi;
import com.example.firebaselogin.Retrofit.RetrofitClient;
import com.example.firebaselogin.models.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuthInstance;
    private boolean isLoggedIn = false;
    private ArrayList<Comment> comments = new ArrayList<>();
    RecyclerView comments_rec;
    TextView no_comment_txt;
    Button signout;
    CommentsAdapter adapter;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Paper.init(this);
        InitViews();

        try {
            isLoggedIn = Paper.book().read("isLoggedIn", false);
        }catch(Exception e){
            Log.e("DashboardActivity", "could not extract isLoggedIn key");
        }

        if(!isLoggedIn){
            goToLoginPage();
        }else {
            FirebaseUser firebaseUser = firebaseAuthInstance.getCurrentUser();
            if (firebaseUser == null) {
                goToLoginPage();
            }else{
                progressBarUtils.showProgress(DashboardActivity.this, DashboardActivity.this);

                //set up recycler view
                comments_rec.setVisibility(View.GONE);
                no_comment_txt.setVisibility(View.GONE);
                adapter = new CommentsAdapter(this, DashboardActivity.this);
                comments_rec.setLayoutManager(new LinearLayoutManager(this));
                comments_rec.setAdapter(adapter);
                comments_rec.setHasFixedSize(false);

                comments_rec.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (!recyclerView.canScrollVertically(1)) {
                            addData(pos);
                        }
                    }
                });

                Retrofit retrofit = RetrofitClient.getClient();
                CommentsApi commentsApi = retrofit.create(CommentsApi.class);

                //calling get comments api
                Call<ArrayList<Comment>> call = commentsApi.getComments();
                call.enqueue(new Callback<ArrayList<Comment>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Comment>> call, Response<ArrayList<Comment>> response) {
                        if(response.isSuccessful()) {
                            comments = response.body();
                            addData(0);
                            comments_rec.setVisibility(View.VISIBLE);
                            progressBarUtils.hideProgress();
                        }else {
                            no_comment_txt.setVisibility(View.VISIBLE);
                            progressBarUtils.hideProgress();
                            Toast.makeText(DashboardActivity.this, "Some error occurred, Please try again!", Toast.LENGTH_SHORT).show();
                            Log.e("DashboardActivity", "Error in fetching data from API \n" + response.code() + "\n" + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Comment>> call, Throwable t) {
                        no_comment_txt.setVisibility(View.VISIBLE);
                        progressBarUtils.hideProgress();
                        Toast.makeText(DashboardActivity.this, "Some error occurred, Please try again!", Toast.LENGTH_SHORT).show();
                        Log.e("DashboardActivity", "Error in fetching data from API");
                    }
                });
                }
            }

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuthInstance.signOut();
                Toast.makeText(DashboardActivity.this, "Signed-out successfully", Toast.LENGTH_SHORT).show();
                goToLoginPage();
            }
        });

    }

    private void addData(int start) {
        if(start >= comments.size()){
            return;
        }
        if(start > 0) {
            Toast.makeText(DashboardActivity.this, "Loaded more data!", Toast.LENGTH_LONG).show();
        }
        ArrayList<Comment> dataToAdd = new ArrayList<>();
        for(int i = start; i < Math.min(start+100, comments.size()); i++){
            dataToAdd.add(comments.get(i));
        }
        pos += 100;
        adapter.addData(dataToAdd);
    }

    private void goToLoginPage(){
        Paper.book().write("isLoggedIn", false);
        Toast.makeText(this, "Please Log-in to continue", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(DashboardActivity.this, LogInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void InitViews() {
        firebaseAuthInstance = FirebaseAuth.getInstance();
        signout = findViewById(R.id.btn_logout);
        comments_rec = findViewById(R.id.comments_rec);
        no_comment_txt = findViewById(R.id.no_comment_txt);
    }

}