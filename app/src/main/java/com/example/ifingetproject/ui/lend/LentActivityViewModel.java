/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject.ui.lend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ifingetproject.IFingetDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class LentActivityViewModel extends ViewModel {
    private final IFingetDatabaseHelper dbHelper;
    private final MutableLiveData<List<LendDetail>> lendDetails = new MutableLiveData<>();

    public LentActivityViewModel(IFingetDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public LiveData<List<LendDetail>> getLendTransactions() {
        List<LendDetail> lDetails = dbHelper.getLendTransactionDetails();
        lendDetails.setValue(lDetails != null ? lDetails : new ArrayList<>());
        return lendDetails;
    }
}
