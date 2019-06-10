package com.clikader.stockwatch;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class StockViewHolder extends RecyclerView.ViewHolder {
    public TextView code;
    public TextView value;
    public TextView change;
    public TextView companyName;

    public StockViewHolder(View view) {
        super(view);
        code = (TextView) view.findViewById(R.id.stockCode);
        value = (TextView) view.findViewById(R.id.stockValue);
        change = (TextView) view.findViewById(R.id.stockChange);
        companyName = (TextView) view.findViewById(R.id.companyName);
    }
}
