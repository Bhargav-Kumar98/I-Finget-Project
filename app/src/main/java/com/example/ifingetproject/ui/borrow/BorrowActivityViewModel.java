package com.example.ifingetproject.ui.borrow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ifingetproject.IFingetDatabaseHelper;
import com.example.ifingetproject.ui.lend.LendDetail;

import java.util.ArrayList;
import java.util.List;

public class BorrowActivityViewModel extends ViewModel {
    private final IFingetDatabaseHelper dbHelper;
    private final MutableLiveData<List<BorrowDetail>> borrowDetails = new MutableLiveData<>();

    public BorrowActivityViewModel(IFingetDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public LiveData<List<BorrowDetail>> getBorrowedTransactions() {
        List<BorrowDetail> bDetails = dbHelper.getBorrowedTransactionDetails();
        borrowDetails.setValue(bDetails != null ? bDetails : new ArrayList<>());
        return borrowDetails;
    }
}
