package kr.ac.kopo.moneylog;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import kr.ac.kopo.moneylog.model.Transaction;
import kr.ac.kopo.moneylog.util.SharedPreferenceManager;

public class StatisticsActivity extends AppCompatActivity {

    TextView tvIncome, tvExpense, tvBalance, tvSelectedMonth;
    MaterialCardView cardMonthSelect;
    PieChart pieChart;
    BarChart barChart;
    RecyclerView rvCategoryDetail;
    BottomNavigationView bottomNavigationView;

    ArrayList<Transaction> transactionList;
    String selectedMonthStr; // yyyy-MM
    ArrayList<CategoryStat> categoryStatList;
    CategoryStatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Find Views
        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        tvBalance = findViewById(R.id.tvBalance);
        tvSelectedMonth = findViewById(R.id.tvSelectedMonth);
        cardMonthSelect = findViewById(R.id.cardMonthSelect);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        rvCategoryDetail = findViewById(R.id.rvCategoryDetail);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Setup RecyclerView
        rvCategoryDetail.setLayoutManager(new LinearLayoutManager(this));
        categoryStatList = new ArrayList<>();
        adapter = new CategoryStatAdapter(categoryStatList);
        rvCategoryDetail.setAdapter(adapter);

        // Set default month to current month
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
        SimpleDateFormat displaySdf = new SimpleDateFormat("yyyy년 M월", Locale.US);
        selectedMonthStr = sdf.format(new java.util.Date());
        tvSelectedMonth.setText(displaySdf.format(new java.util.Date()));

        // Month Picker Click Listener
        cardMonthSelect.setOnClickListener(v -> {
            ArrayList<String> monthsList = new ArrayList<>();
            ArrayList<String> displayMonthsList = new ArrayList<>();
            SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM", Locale.US);
            SimpleDateFormat displaySdfMonth = new SimpleDateFormat("yyyy년 M월", Locale.US);
            Calendar c = Calendar.getInstance(Locale.US);
            for (int i = 0; i < 12; i++) {
                monthsList.add(sdfMonth.format(c.getTime()));
                displayMonthsList.add(displaySdfMonth.format(c.getTime()));
                c.add(Calendar.MONTH, -1);
            }

            new AlertDialog.Builder(this)
                    .setTitle("월 선택")
                    .setItems(displayMonthsList.toArray(new String[0]), (dialog, which) -> {
                        selectedMonthStr = monthsList.get(which);
                        tvSelectedMonth.setText(displayMonthsList.get(which));
                        loadStatistics();
                    })
                    .show();
        });

        // Bottom Navigation Setup
        bottomNavigationView.setSelectedItemId(R.id.navigation_statistics);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.navigation_settings) {
                Intent intent = new Intent(StatisticsActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Always load transactions fresh from SharedPreferences
        transactionList = SharedPreferenceManager.loadTransactions(this);
        loadStatistics();
    }

    private void loadStatistics() {
        int income = 0;
        int expense = 0;
        HashMap<String, Integer> categoryAmountMap = new HashMap<>();
        HashMap<String, Integer> categoryCountMap = new HashMap<>();

        if (transactionList != null) {
            for (Transaction t : transactionList) {
                if (t.getDate().startsWith(selectedMonthStr)) {
                    if (t.getType().equals("수입")) {
                        income += t.getAmount();
                    } else {
                        expense += t.getAmount();
                        String cat = t.getCategory();
                        categoryAmountMap.put(cat, categoryAmountMap.getOrDefault(cat, 0) + t.getAmount());
                        categoryCountMap.put(cat, categoryCountMap.getOrDefault(cat, 0) + 1);
                    }
                }
            }
        }

        int balance = income - expense;

        // Set Text
        tvIncome.setText("총 수입 : " + income + "원");
        tvExpense.setText("총 지출 : " + expense + "원");
        tvBalance.setText("잔액 : " + balance + "원");

        // Load RecyclerView List Data
        categoryStatList.clear();
        double totalExpenseDb = (double) expense;
        for (String cat : categoryAmountMap.keySet()) {
            int amt = categoryAmountMap.get(cat);
            int cnt = categoryCountMap.get(cat);
            double pct = totalExpenseDb > 0 ? (double) amt / totalExpenseDb * 100.0 : 0.0;
            categoryStatList.add(new CategoryStat(cat, amt, cnt, pct));
        }

        // Sort descending by amount
        Collections.sort(categoryStatList, (o1, o2) -> Integer.compare(o2.amount, o1.amount));
        adapter.notifyDataSetChanged();

        // Load PieChart
        setupPieChart(categoryAmountMap, expense);

        // Load BarChart
        setupBarChart();
    }

    private void setupPieChart(HashMap<String, Integer> categoryAmountMap, int totalExpense) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<Integer> sliceColors = new ArrayList<>();

        int[] catColors = {
                getResources().getColor(R.color.cat_food_icon),
                getResources().getColor(R.color.cat_transport_icon),
                getResources().getColor(R.color.cat_shopping_icon),
                getResources().getColor(R.color.cat_living_icon),
                getResources().getColor(R.color.cat_culture_icon),
                getResources().getColor(R.color.cat_other_icon)
        };
        String[] categories = {"식비", "교통", "쇼핑", "생활비", "문화", "기타"};

        for (int i = 0; i < categories.length; i++) {
            int amount = categoryAmountMap.getOrDefault(categories[i], 0);
            if (amount > 0) {
                pieEntries.add(new PieEntry(amount, categories[i]));
                sliceColors.add(catColors[i]);
            }
        }

        if (pieEntries.isEmpty()) {
            pieEntries.add(new PieEntry(1f, "지출 없음"));
            sliceColors.add(getResources().getColor(R.color.outline));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(sliceColors);
        pieDataSet.setDrawValues(false);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(getResources().getColor(R.color.surface));
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setHoleRadius(75f);

        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("총 지출\n" + totalExpense + "원");
        pieChart.setCenterTextColor(getResources().getColor(R.color.onSurface));
        pieChart.setCenterTextSize(16f);

        pieChart.setExtraOffsets(10, 10, 10, 10);
        pieChart.animateXY(600, 600);
        pieChart.invalidate();
    }

    private void setupBarChart() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> barLabels = new ArrayList<>();

        Calendar cal = Calendar.getInstance(Locale.US);
        try {
            SimpleDateFormat yyyymm = new SimpleDateFormat("yyyy-MM", Locale.US);
            cal.setTime(yyyymm.parse(selectedMonthStr));
        } catch (Exception e) {
            e.printStackTrace();
        }

        cal.add(Calendar.MONTH, -5);

        int[] barColors = new int[6];
        int activeColor = getResources().getColor(R.color.primary);
        int inactiveColor = getResources().getColor(R.color.primaryContainer);

        for (int i = 0; i < 6; i++) {
            String monthKey = new SimpleDateFormat("yyyy-MM", Locale.US).format(cal.getTime());
            String label = new SimpleDateFormat("M월", Locale.US).format(cal.getTime());
            barLabels.add(label);

            int monthlyExpense = 0;
            if (transactionList != null) {
                for (Transaction t : transactionList) {
                    if (t.getType().equals("지출") && t.getDate().startsWith(monthKey)) {
                        monthlyExpense += t.getAmount();
                    }
                }
            }

            barEntries.add(new BarEntry(i, monthlyExpense));

            if (monthKey.equals(selectedMonthStr)) {
                barColors[i] = activeColor;
            } else {
                barColors[i] = inactiveColor;
            }

            cal.add(Calendar.MONTH, 1);
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(barColors);
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value);
                if (index >= 0 && index < barLabels.size()) {
                    return barLabels.get(index);
                }
                return "";
            }
        });
        xAxis.setTextColor(getResources().getColor(R.color.onSurfaceVariant));

        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisLeft().setGridColor(getResources().getColor(R.color.outline));
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisLeft().setTextColor(getResources().getColor(R.color.onSurfaceVariant));
        barChart.getAxisRight().setEnabled(false);

        barChart.setExtraOffsets(0, 10, 0, 10);
        barChart.animateY(600);
        barChart.invalidate();
    }

    private static class CategoryStat {
        String category;
        int amount;
        int count;
        double percentage;

        CategoryStat(String category, int amount, int count, double percentage) {
            this.category = category;
            this.amount = amount;
            this.count = count;
            this.percentage = percentage;
        }
    }

    private static class CategoryStatAdapter extends RecyclerView.Adapter<CategoryStatAdapter.ViewHolder> {
        private final ArrayList<CategoryStat> list;

        CategoryStatAdapter(ArrayList<CategoryStat> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CategoryStat item = list.get(position);
            holder.tvCategoryName.setText(item.category);
            holder.tvCategoryDetails.setText(String.format(Locale.US, "%.1f%% • %d건", item.percentage, item.count));
            holder.tvCategoryAmount.setText("₩" + item.amount);
            holder.progressBar.setProgress((int) Math.round(item.percentage));

            int bgColor = R.color.cat_other_bg;
            int iconColor = R.color.cat_other_icon;
            int iconRes = R.drawable.ic_category_other;

            switch (item.category) {
                case "식비":
                    bgColor = R.color.cat_food_bg;
                    iconColor = R.color.cat_food_icon;
                    iconRes = R.drawable.ic_category_food;
                    break;
                case "교통":
                    bgColor = R.color.cat_transport_bg;
                    iconColor = R.color.cat_transport_icon;
                    iconRes = R.drawable.ic_category_transport;
                    break;
                case "쇼핑":
                    bgColor = R.color.cat_shopping_bg;
                    iconColor = R.color.cat_shopping_icon;
                    iconRes = R.drawable.ic_category_shopping;
                    break;
                case "생활비":
                    bgColor = R.color.cat_living_bg;
                    iconColor = R.color.cat_living_icon;
                    iconRes = R.drawable.ic_category_living;
                    break;
                case "문화":
                    bgColor = R.color.cat_culture_bg;
                    iconColor = R.color.cat_culture_icon;
                    iconRes = R.drawable.ic_category_culture;
                    break;
            }

            holder.flIconBg.setBackgroundTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(bgColor)));
            holder.ivCategoryIcon.setImageResource(iconRes);
            holder.ivCategoryIcon.setImageTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(iconColor)));
            holder.progressBar.setIndicatorColor(holder.itemView.getContext().getResources().getColor(iconColor));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            FrameLayout flIconBg;
            ImageView ivCategoryIcon;
            TextView tvCategoryName;
            TextView tvCategoryDetails;
            TextView tvCategoryAmount;
            LinearProgressIndicator progressBar;

            ViewHolder(View itemView) {
                super(itemView);
                flIconBg = itemView.findViewById(R.id.flIconBg);
                ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                tvCategoryDetails = itemView.findViewById(R.id.tvCategoryDetails);
                tvCategoryAmount = itemView.findViewById(R.id.tvCategoryAmount);
                progressBar = itemView.findViewById(R.id.progressBar);
            }
        }
    }
}