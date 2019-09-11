package com.epam.rd.advphone.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.epam.rd.advphone.models.Call;
import com.epam.rd.advphone.repositories.CallsProvider;

import java.util.List;

public class CallsViewModel extends ViewModel {
    private CallsProvider callsProvider;
    private MutableLiveData<List<Call>> callsLogList;

    public MutableLiveData<List<Call>> getCallsLogList() {
        if (callsLogList == null) {
            callsLogList = new MutableLiveData<>();
            if (callsProvider != null) {
                callsLogList.setValue(callsProvider.getCalls());
            }
        }
        return callsLogList;
    }

    public void setCallsProvider(CallsProvider callsProvider) {
        this.callsProvider = callsProvider;
    }
}
