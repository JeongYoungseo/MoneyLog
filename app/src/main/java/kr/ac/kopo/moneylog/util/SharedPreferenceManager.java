package kr.ac.kopo.moneylog.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import kr.ac.kopo.moneylog.model.Transaction;

public class SharedPreferenceManager {

    private static final String PREF_NAME = "MoneyLog";
    private static final String KEY_TRANSACTION = "transactions";
    private static final String KEY_DARK_MODE = "dark_mode";

    public static boolean hasTransactions(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE);
        return preferences.contains(KEY_TRANSACTION);
    }

    public static void saveDarkMode(Context context, boolean isDark) {
        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE);
        preferences.edit().putBoolean(KEY_DARK_MODE, isDark).apply();
    }

    public static boolean loadDarkMode(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_DARK_MODE, false);
    }

    public static void saveTransactions(
            Context context,
            ArrayList<Transaction> transactionList) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor editor =
                preferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(transactionList);

        editor.putString(
                KEY_TRANSACTION,
                json);

        editor.apply();
    }

    public static ArrayList<Transaction> loadTransactions(
            Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE);

        Gson gson = new Gson();

        String json =
                preferences.getString(
                        KEY_TRANSACTION,
                        null);

        Type type =
                new TypeToken<ArrayList<Transaction>>() {
                }.getType();

        ArrayList<Transaction> transactionList =
                gson.fromJson(
                        json,
                        type);

        if (transactionList == null) {
            transactionList = new ArrayList<>();
        }

        return transactionList;
    }
}