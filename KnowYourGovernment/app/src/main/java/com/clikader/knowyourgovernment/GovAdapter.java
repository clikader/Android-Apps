package com.clikader.knowyourgovernment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class GovAdapter extends RecyclerView.Adapter<GovViewHolder> {
    private static final String TAG = "GovAdapter";
    private MainActivity mainActivity;
    private ArrayList<Governor> adaGList;

    public GovAdapter(ArrayList<Governor> govList, MainActivity ma) {
        mainActivity = ma;
        adaGList = govList;
    }

    @NonNull
    @Override
    public GovViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gov_row, parent, false);
        itemView.setOnClickListener(mainActivity);

        return new GovViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GovViewHolder holder, int position) {
        Governor gov = adaGList.get(position);
        holder.position.setText(gov.getPosition());
        holder.name.setText(gov.getName());
        if (!gov.getParty().equals("")) {
            holder.party.setText("(" + gov.getParty() + ")");
        } else {
            holder.party.setText("(Unknown)");
        }
    }

    @Override
    public int getItemCount() {
        return adaGList.size();
    }
}
