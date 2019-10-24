package com.alejandro.ahorcado.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alejandro.ahorcado.R;
import com.alejandro.ahorcado.model.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

//https://www.youtube.com/watch?v=PHdTs_-PrYA

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ItemPlayer>{

    private Player[] players;

    public PlayerAdapter(Player[] players){
        this.players = players;
    }

    @NonNull
    @Override
    public ItemPlayer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int itemLayoutId = R.layout.player_list_holder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(itemLayoutId, parent, false);

        return new ItemPlayer(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemPlayer holder, int position) {

        holder.bind(players[position]);

    }

    @Override
    public int getItemCount() {
        return players.length;
    }

    class ItemPlayer extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView lblPoints, lblPlayerName, lblDate;

        public ItemPlayer(View itemView) {
            super(itemView);

            lblPoints = itemView.findViewById(R.id.lblPoints);
            lblPlayerName = itemView.findViewById(R.id.lblPlayerName);
            lblDate = itemView.findViewById(R.id.lblDate);

        }

        void bind(Player player){

            lblPoints.setText(String.valueOf(player.getPoints()));
            lblPlayerName.setText(player.getName());
            lblDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(player.getDate()));

        }

        @Override
        public void onClick(View view) {
            recyclerV
        }
    }

}
