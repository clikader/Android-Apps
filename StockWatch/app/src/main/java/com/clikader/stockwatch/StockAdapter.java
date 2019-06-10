package com.clikader.stockwatch;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG = "StockAdapter";
    private MainActivity mainActivity;
    private ArrayList<Stock> adaSList;

    public StockAdapter(ArrayList<Stock> stockList, MainActivity ma) {
        mainActivity = ma;
        adaSList = stockList;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_row, parent, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        Stock stock = adaSList.get(position);
        if (stock.getChange() >= 0) {
            holder.code.setText(stock.getCode());
            holder.code.setTextColor(Color.GREEN);
            holder.companyName.setText(stock.getCompanyName());
            holder.companyName.setTextColor(Color.GREEN);
            holder.value.setText(Double.toString(stock.getLastPrice()));
            holder.value.setTextColor(Color.GREEN);
            holder.change.setText("\u25B4" + " " + Double.toString(stock.getChange()) + "("
                    + Double.toString(stock.getPercentage()) + "%)");
            holder.change.setTextColor(Color.GREEN);
        }
        else {
            holder.code.setText(stock.getCode());
            holder.code.setTextColor(Color.RED);
            holder.companyName.setText(stock.getCompanyName());
            holder.companyName.setTextColor(Color.RED);
            holder.value.setText(Double.toString(stock.getLastPrice()));
            holder.value.setTextColor(Color.RED);
            holder.change.setText("\u25BE" + " " + Double.toString(stock.getChange()) + "("
                    + Double.toString(stock.getPercentage()) + "%)");
            holder.change.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return adaSList.size();
    }
}
