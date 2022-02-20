package com.example.firebaselogin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaselogin.R;
import com.example.firebaselogin.models.Comment;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.itemViewHolder>{

    Context context;
    Activity activity;
    ArrayList<Comment> comments = new ArrayList<>();

    public CommentsAdapter(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CommentsAdapter.itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new itemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.itemViewHolder holder, int position) {
        holder.name.setText(comments.get(position).getName());
        holder.email.setText(comments.get(position).getEmail());
        holder.body.setText(comments.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, body;
        public itemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.comment_name);
            email = itemView.findViewById(R.id.comment_email);
            body = itemView.findViewById(R.id.comment_body);
        }
    }

    public void addData(ArrayList<Comment> comments){
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }

}
