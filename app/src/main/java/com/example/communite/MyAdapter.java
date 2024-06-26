package com.example.communite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    @SuppressLint("RestrictedApi")
    Context context;
    ArrayList<Users> list;

    public MyAdapter(@SuppressLint("RestrictedApi") Context context, ArrayList<Users> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_item_view,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,notification;
        ImageView profileview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileview = itemView.findViewById(R.id.profileview);
            name = itemView.findViewById(R.id.name);
            notification = itemView.findViewById(R.id.notification);

        }
    }
}
