package kr.ac.kopo.moneylog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.DatePickerDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import kr.ac.kopo.moneylog.model.Transaction;
import kr.ac.kopo.moneylog.util.DateUtil;

public class AddTransactionActivity extends AppCompatActivity {

    EditText etAmount, etMemo;
    Button btnSave;
    RadioButton rbIncome, rbExpense;
    TextView tvDate;
    String selectedCategory = "식비";
    com.google.android.material.card.MaterialCardView cardFood, cardTransport, cardShopping, cardLiving, cardCulture, cardOther;
    android.widget.ImageView ivFood, ivTransport, ivShopping, ivLiving, ivCulture, ivOther;
    TextView tvFood, tvTransport, tvShopping, tvLiving, tvCulture, tvOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_transaction);

        etAmount = findViewById(R.id.etAmount);
        etMemo = findViewById(R.id.etMemo);
        cardFood = findViewById(R.id.cardFood);
        cardTransport = findViewById(R.id.cardTransport);
        cardShopping = findViewById(R.id.cardShopping);
        cardLiving = findViewById(R.id.cardLiving);
        cardCulture = findViewById(R.id.cardCulture);
        cardOther = findViewById(R.id.cardOther);

        ivFood = findViewById(R.id.ivFood);
        ivTransport = findViewById(R.id.ivTransport);
        ivShopping = findViewById(R.id.ivShopping);
        ivLiving = findViewById(R.id.ivLiving);
        ivCulture = findViewById(R.id.ivCulture);
        ivOther = findViewById(R.id.ivOther);

        tvFood = findViewById(R.id.tvFood);
        tvTransport = findViewById(R.id.tvTransport);
        tvShopping = findViewById(R.id.tvShopping);
        tvLiving = findViewById(R.id.tvLiving);
        tvCulture = findViewById(R.id.tvCulture);
        tvOther = findViewById(R.id.tvOther);

        cardFood.setOnClickListener(v -> updateCategorySelection("식비"));
        cardTransport.setOnClickListener(v -> updateCategorySelection("교통"));
        cardShopping.setOnClickListener(v -> updateCategorySelection("쇼핑"));
        cardLiving.setOnClickListener(v -> updateCategorySelection("생활비"));
        cardCulture.setOnClickListener(v -> updateCategorySelection("문화"));
        cardOther.setOnClickListener(v -> updateCategorySelection("기타"));

        updateCategorySelection("식비");
        btnSave = findViewById(R.id.btnSave);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        tvDate = findViewById(R.id.tvDate);
        tvDate.setText(DateUtil.getToday());
        tvDate.setOnClickListener(v -> {
            String currentDate = tvDate.getText().toString();
            int year = 2026, month = 5, day = 23;
            try {
                String[] parts = currentDate.split("-");
                if (parts.length == 3) {
                    year = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]) - 1;
                    day = Integer.parseInt(parts[2]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddTransactionActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String newDate = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        tvDate.setText(newDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        Transaction oldTransaction =
                (Transaction) getIntent()
                        .getSerializableExtra("transaction");
        if (oldTransaction != null) {
            etAmount.setText(String.valueOf(oldTransaction.getAmount()));
            etMemo.setText(oldTransaction.getMemo());
            tvDate.setText(oldTransaction.getDate());

            if (oldTransaction.getType().equals("수입")) {
                rbIncome.setChecked(true);
            } else {
                rbExpense.setChecked(true);
            }

            updateCategorySelection(oldTransaction.getCategory());
        }

        btnSave.setOnClickListener(v -> {
            String type;
            if (rbIncome.isChecked()) {
                type = "수입";
            }
            else if (rbExpense.isChecked()) {
                type = "지출";
            }
            else {
                return;
            }

            String amountText = etAmount.getText().toString();

            if (amountText.isEmpty()) {
                etAmount.setError("금액을 입력하세요");
                return;
            }

            int amount = Integer.parseInt(amountText);
            String category = selectedCategory;

            String memo = etMemo.getText().toString();
            String date = tvDate.getText().toString();

            Transaction transaction =
                    new Transaction(
                            type,
                            amount,
                            category,
                            memo,
                            date);

            Intent intent = new Intent();
            intent.putExtra("transaction", transaction);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void updateCategorySelection(String category) {
        selectedCategory = category;

        resetCardState(cardFood, ivFood, tvFood, "식비".equals(category));
        resetCardState(cardTransport, ivTransport, tvTransport, "교통".equals(category));
        resetCardState(cardShopping, ivShopping, tvShopping, "쇼핑".equals(category));
        resetCardState(cardLiving, ivLiving, tvLiving, "생활비".equals(category));
        resetCardState(cardCulture, ivCulture, tvCulture, "문화".equals(category));
        resetCardState(cardOther, ivOther, tvOther, "기타".equals(category));
    }

    private void resetCardState(com.google.android.material.card.MaterialCardView card, android.widget.ImageView icon, TextView text, boolean isSelected) {
        if (isSelected) {
            card.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primaryContainer)));
            card.setStrokeColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary)));
            card.setStrokeWidth(dpToPx(2));
            icon.setImageTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary)));
            text.setTextColor(getResources().getColor(R.color.primary));
        } else {
            card.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.surface)));
            card.setStrokeColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.outline)));
            card.setStrokeWidth(dpToPx(1));
            icon.setImageTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.onSurfaceVariant)));
            text.setTextColor(getResources().getColor(R.color.onSurface));
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}