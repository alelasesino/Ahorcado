package com.alejandro.ahorcado.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alejandro.ahorcado.R;
import com.alejandro.ahorcado.model.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ItemPlayer> {

    private ArrayList<Player> players;
    private OnItemClickListener listener;

    public PlayerAdapter(ArrayList<Player> players){
        this.players = players;
    }

    @NonNull
    @Override
    public ItemPlayer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int itemLayoutId = R.layout.player_card_view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(itemLayoutId, parent, false);

        return new ItemPlayer(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemPlayer holder, int position) {
        holder.bind(players.get(position));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public void deleteItem(int position){
        players.remove(position);
        notifyItemRemoved(position);
        //notifyItemRangeChanged(position, players.size());
    }

    class ItemPlayer extends RecyclerView.ViewHolder {

        private TextView lblPoints, lblPlayerName, lblDate;

        public ItemPlayer(View itemView) {
            super(itemView);

            lblPoints = itemView.findViewById(R.id.lblPoints);
            lblPlayerName = itemView.findViewById(R.id.lblPlayerName);
            lblDate = itemView.findViewById(R.id.lblDate);

            itemView.setOnClickListener((view) -> {

                int position = getAdapterPosition();

                if(position != RecyclerView.NO_POSITION && listener != null)
                    listener.onItemClick(players.get(position));

            });

        }

        public void bind(Player player){

            lblPoints.setText(String.valueOf(player.getPoints()));
            lblPlayerName.setText(player.getName());
            lblDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(player.getDate()));

        }

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(Player player);
    }

}
