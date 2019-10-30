package com.alejandro.ahorcado.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alejandro.ahorcado.R;
import com.alejandro.ahorcado.model.Player;
import com.alejandro.ahorcado.utils.Utils;

import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ItemPlayer> {

    private ArrayList<Player> players;
    private OnItemClickListener listener;

    PlayerAdapter(ArrayList<Player> players){
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

    /**
     * Borra el item de la lista del adaptador y notifica a la vista
     * del cambio para refrescar la vista
     * @param position Posicion del item en la lista a borrar
     */
    void deleteItem(int position){

        players.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, players.size());

    }

    class ItemPlayer extends RecyclerView.ViewHolder {

        private TextView lblPoints, lblPlayerName, lblDate;

        ItemPlayer(View itemView) {
            super(itemView);

            lblPoints = itemView.findViewById(R.id.lblPoints);
            lblPlayerName = itemView.findViewById(R.id.lblPlayerName);
            lblDate = itemView.findViewById(R.id.lblDate);

            itemView.setOnClickListener((view) -> { //EVENTO CLICK DE LAS CARDS VIEW

                int position = getAdapterPosition();

                if(position != RecyclerView.NO_POSITION && listener != null)
                    listener.onItemClick(players.get(position));

            });

        }

        /**
         * Enlaza los datos del jugador con los componentes de la vista
         * @param player Jugador a enlazar
         */
        void bind(Player player){

            lblPoints.setText(String.valueOf(player.getPoints()));
            lblPlayerName.setText(player.getName());
            lblDate.setText(Utils.dateToString(player.getDate()));

        }

    }

    void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    /**
     * Interfaz utilizada para conectar el evento click del cardview con la UserActivity
     */
    public interface OnItemClickListener{
        void onItemClick(Player player);
    }

}
