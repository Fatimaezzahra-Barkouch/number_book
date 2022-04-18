package com.example.mynumber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.MyViewHolder>  {
    List<Contact> data;
    Context context;

    public myAdapter(List<Contact> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.contact_item, parent, false);
        return new MyViewHolder(view);
    }


    public void filterList(ArrayList<Contact> filterContact) {
        data = filterContact;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.id.setText(data.get(position).getId());
        holder.nom.setText(data.get(position).getNom());
        holder.numero.setText(data.get(position).getNumero());
        holder.country.setText(data.get(position).getCountry());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView id, nom, numero, country;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.id = (TextView)  itemView.findViewById(R.id.idInput);
            this.nom = (TextView)  itemView.findViewById(R.id.nomInput);
            this.numero = (TextView)  itemView.findViewById(R.id.numeroInput);
            this.country = (TextView)  itemView.findViewById(R.id.country);
        }
    }

}
