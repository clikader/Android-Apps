package com.clikader.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class GovViewHolder extends RecyclerView.ViewHolder {
    public TextView position;
    public TextView name;
    public TextView party;

    public GovViewHolder(View view) {
        super(view);
        position = (TextView) view.findViewById(R.id.govPosition);
        name = (TextView) view.findViewById(R.id.govName);
        party = (TextView) view.findViewById(R.id.govParty);
    }
}
