package com.clikader.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private static String marketWatchURL = "https://www.marketwatch.com/investing/stock/";

    public ArrayList<Stock> stockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StockAdapter sAdapter;
    private DBHandler dbHandler;
    private SwipeRefreshLayout swiper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DBHandler(this);
        ArrayList<Stock> list = dbHandler.loadStocks();
        stockList.addAll(list);
        Collections.sort(stockList);

        recyclerView = (RecyclerView) findViewById(R.id.stockRecycler);
        sAdapter = new StockAdapter(stockList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sAdapter);

        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (networkCheck("Updated")) {
                    doRefresh();
                    //Toast.makeText(getApplicationContext(), "The stocks have been updated.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onRefresh: No internet found.");
                    swiper.setRefreshing(false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        dbHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuNew:
                if (networkCheck("Added")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    final EditText et = new EditText(this);
                    et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                    et.setGravity(Gravity.CENTER_HORIZONTAL);

                    builder.setView(et);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<HashMap<String, String>> stocksGot =
                                    loadStocks(et.getText().toString().toUpperCase());

                            for (int ii = 0; ii < stockList.size(); ii++) {
                                if ((stockList.get(ii).getCode().toUpperCase()).equals(et.getText().toString().toUpperCase())) {
                                    displayDuplicate(et.getText().toString().toUpperCase());
                                    return;
                                }
                            }

                            if (stocksGot.size() == 0 || stocksGot == null) {
                                displayNothingFound(et.getText().toString().toUpperCase());
                            }else if (stocksGot.size() == 1){
                                HashMap<String, String> detailedStock = loadStockDetail(stocksGot.get(0).get("Symbol"));

                                if (detailedStock == null) {
                                    displayNoData(stocksGot.get(0).get("Symbol"), stocksGot.get(0).get("Name"));
                                    return;
                                }

                                String newCode = detailedStock.get("Symbol");
                                String newName = stocksGot.get(0).get("Name");
                                String snewPrice = detailedStock.get("LastPrice");
                                double newPrice = Double.parseDouble(snewPrice);
                                String snewChange = detailedStock.get("Change");
                                double newChange = Double.parseDouble(snewChange);
                                String snewPercent = detailedStock.get("Percent");
                                double newPercent = Double.parseDouble(snewPercent) * 100;
                                String percentNumStr = String.format("%.2f", newPercent);
                                double percentNum = Double.parseDouble(percentNumStr);

                                Stock newStock = new Stock(newCode, newName, newPrice, newChange, percentNum);
                                stockList.add(0, newStock);
                                Collections.sort(stockList);
                                dbHandler.addStock(newStock);
                                sAdapter.notifyDataSetChanged();
                                return;
                            } else {
                                displaySelectionBox(stocksGot);
                            }

                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // canceled
                        }
                    });
                    builder.setMessage("Please enter the stock symbol:");
                    builder.setTitle("Add New Stock");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                } else {
                    Log.d(TAG, "onOptionsItemSelected: No Network Connection Found.");
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        String selectedCode = stockList.get(pos).getCode();
        String url = marketWatchURL + selectedCode;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View view) {
        final int pos = recyclerView.getChildLayoutPosition(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_warning_black_24dp);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHandler.deleteStock(stockList.get(pos).getCode());
                stockList.remove(pos);
                sAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: User selected not to delete.");
            }
        });

        builder.setMessage("Delete the selected stock with symbol "
                + stockList.get(pos).getCode() + " ?");
        builder.setTitle("Delete Stock");
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    public boolean networkCheck(String command) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be " + command + " Without A Network Connection");
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
    }

    public ArrayList<HashMap<String, String>> loadStocks(String userInput) {
        ArrayList<HashMap<String, String>> resultFromAsyncStock = new ArrayList<>();
        if (AsyncLoadStock.running) {
            Toast.makeText(this, "Wait for Async Loading to be done.", Toast.LENGTH_SHORT).show();
            return null;
        }

        AsyncLoadStock.running = true;
        try {
            resultFromAsyncStock = new AsyncLoadStock(this).execute(userInput).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultFromAsyncStock;
    }

    public void displaySelectionBox(final ArrayList<HashMap<String, String>> stocksForSelect) {
        final DecimalFormat df = new DecimalFormat("#.0000");
        final CharSequence[] choices = new CharSequence[stocksForSelect.size()];

        for (int i = 0; i < stocksForSelect.size(); i++) {
            choices[i] = (stocksForSelect.get(i).get("Symbol"))
                    + " - " + (stocksForSelect.get(i).get("Name"));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make A Selection");
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: " + stocksForSelect.get(i).get("Symbol"));
                //Toast.makeText(this, choices[i].toString(), Toast.LENGTH_SHORT).show();
                HashMap<String, String> detailGot = new HashMap<>();
                detailGot = loadStockDetail(stocksForSelect.get(i).get("Symbol"));

                if (detailGot == null) {
                    displayNoData(stocksForSelect.get(i).get("Symbol"), stocksForSelect.get(i).get("Name"));
                    return;
                }

                String newCode = detailGot.get("Symbol");
                String newName = stocksForSelect.get(i).get("Name");
                String snewPrice = detailGot.get("LastPrice");
                double newPrice = Double.parseDouble(snewPrice);
                String snewChange = detailGot.get("Change");
                double newChange = Double.parseDouble(snewChange);
                String snewPercent = detailGot.get("Percent");
                double newPercent = Double.parseDouble(snewPercent) * 100;
                String percentNumStr = String.format("%.2f", newPercent);
                double percentNum = Double.parseDouble(percentNumStr);

                Stock newStock = new Stock(newCode, newName, newPrice, newChange, percentNum);
                stockList.add(0, newStock);
                Collections.sort(stockList);
                dbHandler.addStock(newStock);
                sAdapter.notifyDataSetChanged();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void displayNothingFound(String userInput) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Symbol Not Found: " + userInput);
        builder.setMessage("No data for stock symbol " + userInput + " found.");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void displayDuplicate(String userInput) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Duplicate Stock");
        builder.setMessage("Stock Symbol " + userInput + " is already displayed.");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void displayNoData(String symbol, String cname) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Data Available");
        builder.setMessage("There is no data found for: " + "\n"
                + "Stock Symbol: " + symbol + "\n"
                + "Company Name: " + cname);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void displayMessage(String title, String message, boolean isWarning) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        if (isWarning) {
            builder.setIcon(R.drawable.ic_warning_black_24dp);
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public HashMap<String, String> loadStockDetail(String selC) {
        HashMap<String, String> detailResult = new HashMap<>();
        if (AsyncLoadStockDetail.running) {
            Toast.makeText(this, "Wait for Async loading details to finish", Toast.LENGTH_SHORT).show();
            //return null;
        }

        AsyncLoadStockDetail.running = true;
        try {
            detailResult = new AsyncLoadStockDetail(this).execute(selC).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return detailResult;
    }

    public void doRefresh() {
        if (AsyncRefresh.running) {
            return;
        }

        AsyncRefresh.running = true;
        try {
            Log.d(TAG, "doRefresh: Doing refresh.");
            int finishCode = new AsyncRefresh(this).execute().get();

            if (finishCode == 0) {
                String title = "Network Problem";
                String message = "The stock API returned 404, please check your network and try again.";
                displayMessage(title, message, true);
            } else if (finishCode == -1) {
                String title = "Error Reading Data";
                String message = "An error occurred when reading the stock data, please try again.";
                displayMessage(title, message, true);
            } else if (finishCode == 1) {
                sAdapter.notifyDataSetChanged();
                swiper.setRefreshing(false);
                Log.d(TAG, "doRefresh: Refresh done.");
            } else {
                String title = "Unknown Problem";
                String message = "An unknown problem occurred when reading stock data, please report the bug.";
                displayMessage(title, message, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
