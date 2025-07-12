package com.yourpackage.upisoundbox.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.yourpackage.upisoundbox.models.Transaction;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE type = 'received' ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getReceivedTransactions();

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("DELETE FROM transactions")
    void deleteAll();
}