package com.clikader.stockwatch;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String TAG = "DBHandler";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StockDB";
    private static final String TABLE_NAME = "StockTable";

    private static final String SYMBOL = "Symbol";
    private static final String NAME = "Name";
    private static final String LASTPRICE = "LastPrice";
    private static final String CHANGE = "Change";
    private static final String PERCENT = "Percent";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    NAME + " TEXT not null, " +
                    LASTPRICE + " TEXT not null, " +
                    CHANGE + " TEXT not null, " +
                    PERCENT + " TEXT not null)";

    private SQLiteDatabase database;
    private MainActivity mainActivity;

    public DBHandler(MainActivity context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mainActivity = context;
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: Making new DB");
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public ArrayList<Stock> loadStocks() {
        ArrayList<Stock> stocks = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{SYMBOL, NAME, LASTPRICE, CHANGE, PERCENT},
                null,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String name = cursor.getString(1);
                String dlastprice = cursor.getString(2);
                double lastprice = Double.parseDouble(dlastprice);
                String dchange = cursor.getString(3);
                double change = Double.parseDouble(dchange);
                String dpercent = cursor.getString(4);
                double percent = Double.parseDouble(dpercent);

                Stock s = new Stock(symbol, name, lastprice, change, percent);
                stocks.add(s);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    public void addStock(Stock s) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, s.getCode());
        values.put(NAME, s.getCompanyName());
        values.put(LASTPRICE, s.getLastPrice());
        values.put(CHANGE, s.getChange());
        values.put(PERCENT, s.getPercentage());

        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: " + key);
    }

    public void updateStock(Stock s) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, s.getCode());
        values.put(NAME, s.getCompanyName());
        values.put(LASTPRICE, s.getLastPrice());
        values.put(CHANGE, s.getChange());
        values.put(PERCENT, s.getPercentage());

        long key = database.update(TABLE_NAME, values, SYMBOL + " =?", new String[]{s.getCode()});
        Log.d(TAG, "updateStock: " + key);
    }

    public void deleteStock(String s) {
        int cnt = database.delete(TABLE_NAME, SYMBOL + " =?", new String[]{s});
        Log.d(TAG, "deleteStock: " + cnt);
    }

    public void shutDown() {database.close();}
}
