package com.yourpackage.upisoundbox.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yourpackage.upisoundbox.R;
import com.yourpackage.upisoundbox.models.Transaction;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions = new ArrayList<>();

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.tvAmount.setText("₹" + transaction.getAmount());
        holder.tvSender.setText("From: " + transaction.getSender());
        holder.tvTimestamp.setText(transaction.getTimestamp());
        holder.tvSource.setText("Source: " + transaction.getSource().toUpperCase());
        holder.tvType.setText(transaction.getType().toUpperCase());

        // Set color based on transaction type
        if ("received".equals(transaction.getType())) {
            int creditColor = holder.itemView.getContext().getColor(R.color.transaction_credit);
            holder.tvAmount.setTextColor(creditColor);
            holder.tvType.setTextColor(creditColor);
        } else {
            int debitColor = holder.itemView.getContext().getColor(R.color.transaction_debit);
            holder.tvAmount.setTextColor(debitColor);
            holder.tvType.setTextColor(debitColor);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvSender, tvTimestamp, tvSource, tvType;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvSender = itemView.findViewById(R.id.tv_sender);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvSource = itemView.findViewById(R.id.tv_source);
            tvType = itemView.findViewById(R.id.tv_type);
        }
    }
}