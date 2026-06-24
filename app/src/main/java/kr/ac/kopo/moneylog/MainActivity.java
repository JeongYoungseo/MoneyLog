package kr.ac.kopo.moneylog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Locale;

import kr.ac.kopo.moneylog.adapter.TransactionAdapter;
import kr.ac.kopo.moneylog.model.Transaction;
import kr.ac.kopo.moneylog.data.DummyData;
import kr.ac.kopo.moneylog.util.SharedPreferenceManager;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fabAdd;
    RecyclerView rvTransaction;
    ArrayList<Transaction> transactionList;
    TransactionAdapter adapter;
    ActivityResultLauncher<Intent> launcher;
    TextView tvIncome, tvExpense, tvBalance;
    BottomNavigationView bottomNavigationView;
    CalendarView calendarView;
    ArrayList<Transaction> filteredList;
    String selectedDate = "";
    Transaction editingTransaction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isDark = SharedPreferenceManager.loadDarkMode(this);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fabAdd = findViewById(R.id.fabAdd);
        rvTransaction = findViewById(R.id.rvTransaction);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        tvBalance = findViewById(R.id.tvBalance);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        calendarView = findViewById(R.id.calendarView);

        filteredList = new ArrayList<>();
        if (!SharedPreferenceManager.hasTransactions(this)) {
            transactionList = DummyData.getDummyData();
            SharedPreferenceManager.saveTransactions(this, transactionList);
        } else {
            transactionList = SharedPreferenceManager.loadTransactions(this);
        }

        adapter = new TransactionAdapter(
                filteredList,
                position -> {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("거래 관리")
                            .setItems(
                                    new String[]{"수정", "삭제"},
                                    (dialog, which) -> {
                                        Transaction selected =
                                                filteredList.get(position);
                                        if (which == 0) {
                                            editingTransaction = selected;
                                            Intent intent =
                                                    new Intent(
                                                            MainActivity.this,
                                                            AddTransactionActivity.class);
                                            intent.putExtra(
                                                    "transaction",
                                                    selected);
                                            launcher.launch(intent);
                                        } else {
                                            transactionList.remove(selected);
                                            SharedPreferenceManager.saveTransactions(this, transactionList);
                                            filterTransactions();
                                            updateSummary();
                                        }
                                    })
                            .show();
                });

        rvTransaction.setLayoutManager(new LinearLayoutManager(this));
        rvTransaction.setAdapter(adapter);
        filterTransactions();
        updateSummary();

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Transaction transaction =
                                    (Transaction)
                                            data.getSerializableExtra(
                                                    "transaction");
                            if (transaction != null) {
                                if (editingTransaction != null) {
                                    transactionList.remove(
                                            editingTransaction);
                                    editingTransaction = null;
                                }
                                transactionList.add(transaction);
                                SharedPreferenceManager.saveTransactions(this, transactionList);
                                filterTransactions();
                                updateSummary();
                            }
                        }
                    }
                });

        fabAdd.setOnClickListener(v -> {
            Intent intent =
                    new Intent(
                            MainActivity.this,
                            AddTransactionActivity.class);

            launcher.launch(intent);
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.navigation_home) {
                // Already on home, do nothing
                return true;
            }

            if (item.getItemId() == R.id.navigation_statistics) {
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                intent.putExtra("transactionList", transactionList);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }

            if (item.getItemId() == R.id.navigation_settings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }

            return true;
        });
        calendarView.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {

                    month += 1;

                    selectedDate =
                            String.format(
                                    Locale.US,
                                    "%04d-%02d-%02d",
                                    year,
                                    month,
                                    dayOfMonth);

                    filterTransactions();
                });
    }
    private void updateSummary() {

        int income = 0;
        int expense = 0;

        for (Transaction t : transactionList) {

            if (t.getType().equals("수입")) {
                income += t.getAmount();
            } else {
                expense += t.getAmount();
            }
        }

        int balance = income - expense;

        tvIncome.setText("₩" + income);
        tvExpense.setText("₩" + expense);
        tvBalance.setText("₩" + balance);
    }
    private void filterTransactions() {

        filteredList.clear();

        if (selectedDate.isEmpty()) {

            filteredList.addAll(transactionList);

        } else {

            for (Transaction t : transactionList) {

                if (t.getDate().equals(selectedDate)) {

                    filteredList.add(t);

                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}