package com.example.firebaselogin.Retrofit;

import com.example.firebaselogin.models.Comment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CommentsApi {

    @GET("comments")
    Call<ArrayList<Comment>> getComments();

}
