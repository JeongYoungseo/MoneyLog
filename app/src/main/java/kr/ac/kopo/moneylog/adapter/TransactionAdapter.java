package kr.ac.kopo.moneylog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ac.kopo.moneylog.R;
import kr.ac.kopo.moneylog.model.Transaction;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private ArrayList<Transaction> transactionList;
    public TransactionAdapter(ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.tvCategory.setText(transaction.getCategory());
        holder.tvMemo.setText(transaction.getMemo());
        if (transaction.getType().equals("수입")) {
            holder.tvAmount.setText("+ " + transaction.getAmount() + "원");
        } else {
            holder.tvAmount.setText("- " + transaction.getAmount() + "원");
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        TextView tvMemo;
        TextView tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvMemo = itemView.findViewById(R.id.tvMemo);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}