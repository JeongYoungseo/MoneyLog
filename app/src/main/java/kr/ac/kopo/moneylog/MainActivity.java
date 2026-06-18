package kr.ac.kopo.moneylog;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import kr.ac.kopo.moneylog.adapter.TransactionAdapter;
import kr.ac.kopo.moneylog.model.Transaction;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fabAdd;
    RecyclerView rvTransaction;
    ArrayList<Transaction> transactionList;
    TransactionAdapter adapter;
    ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fabAdd = findViewById(R.id.fabAdd);
        rvTransaction = findViewById(R.id.rvTransaction);

        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList);

        rvTransaction.setLayoutManager(new LinearLayoutManager(this));
        rvTransaction.setAdapter(adapter);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Transaction transaction =
                                    (Transaction) data.getSerializableExtra("transaction");
                            if (transaction != null) {
                                transactionList.add(transaction);
                                adapter.notifyDataSetChanged();
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
    }
}