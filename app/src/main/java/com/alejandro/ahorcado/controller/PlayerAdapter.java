package com.alejandro.ahorcado.controller;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//https://www.youtube.com/watch?v=PHdTs_-PrYA

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ItemPlayer>{

    private int itemsCount;

    public PlayerAdapter(int itemsCount){
        this.itemsCount = itemsCount;
    }

    @NonNull
    @Override
    public ItemPlayer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemPlayer holder, int position) {

    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    class ItemPlayer extends RecyclerView.ViewHolder {

        public ItemPlayer(View itemView) {
            super(itemView);
        }



    }

}
