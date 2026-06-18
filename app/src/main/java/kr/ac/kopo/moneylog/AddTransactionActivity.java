package kr.ac.kopo.moneylog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.kopo.moneylog.model.Transaction;

public class AddTransactionActivity extends AppCompatActivity {
    EditText etAmount, etMemo;
    Spinner spinnerCategory;
    Button btnSave;
    RadioButton rbIncome, rbExpense;
    TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_transaction);

        etAmount = findViewById(R.id.etAmount);
        etMemo = findViewById(R.id.etMemo);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSave);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        tvDate = findViewById(R.id.tvDate);

        String[] categories = {
                "식비",
                "교통",
                "쇼핑",
                "생활비",
                "문화",
                "기타"
        };
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            String type;
            if (rbIncome.isChecked()) {
                type = "수입";
            } else if (rbExpense.isChecked()) {
                type = "지출";
            } else {
                return;
            }
            String amountText = etAmount.getText().toString();
            if (amountText.isEmpty()) {
                etAmount.setError("금액을 입력하세요");
                return;
            }
            int amount = Integer.parseInt(amountText);
            String category =
                    spinnerCategory.getSelectedItem().toString();
            String memo =
                    etMemo.getText().toString();
            String date =
                    tvDate.getText().toString();
            Transaction transaction =
                    new Transaction(
                            type,
                            amount,
                            category,
                            memo,
                            date
                    );
            Intent intent = new Intent();
            intent.putExtra("transaction", transaction);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}