package com.yourpackage.upisoundbox.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yourpackage.upisoundbox.R;
import com.yourpackage.upisoundbox.adapters.TransactionAdapter;
import com.yourpackage.upisoundbox.database.TransactionDatabase;

import java.util.ArrayList;
import android.view.View;
import java.util.concurrent.Executors;


public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private TextView tvTotalTransactions, tvEmptyState;
    private Button btnClearHistory;
    private TransactionDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeViews();
        setupRecyclerView();
        setupDatabase();
        setupListeners();
        loadTransactions();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_transactions);
        tvTotalTransactions = findViewById(R.id.tv_total_transactions);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        btnClearHistory = findViewById(R.id.btn_clear_history);
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupDatabase() {
        database = TransactionDatabase.getDatabase(this);
    }

    private void setupListeners() {
        btnClearHistory.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                database.transactionDao().deleteAll();
                runOnUiThread(() -> {
                    adapter.setTransactions(new ArrayList<>());
                    updateUI(0);
                });
            });
        });
    }

    private void loadTransactions() {
        database.transactionDao().getAllTransactions().observe(this, transactions -> {
            if (transactions != null) {
                adapter.setTransactions(transactions);
                updateUI(transactions.size());
            }
        });
    }

    private void updateUI(int count) {
        tvTotalTransactions.setText("Total: " + count);

        if (count == 0) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}