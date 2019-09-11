package com.epam.rd.advphone.views;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.RequestCodes;
import com.epam.rd.advphone.adapters.CallRecyclerViewAdapter;
import com.epam.rd.advphone.models.Call;
import com.epam.rd.advphone.repositories.CallsProvider;
import com.epam.rd.advphone.repositories.PhoneCallsProvider;
import com.epam.rd.advphone.viewmodels.CallsViewModel;

import java.util.List;

public class RecentFragment extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.recent_fragment, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        CallsProvider callsProvider = null;

        String permission = Manifest.permission.READ_CALL_LOG;
        if (PermissionChecker.checkSelfPermission(view.getContext(), permission) == PermissionChecker.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{permission}, RequestCodes.PERMISSION_READ_CALL_LOG);
        } else {
            callsProvider = PhoneCallsProvider.getInstance(getContext());
        }

        //init recyclerView for all contacts
        RecyclerView recentRecyclerView = view.findViewById(R.id.recentRecyclerView);
        recentRecyclerView.setHasFixedSize(true);

        CallsViewModel callsViewModel = MainActivity.obtainViewModel(getActivity(), CallsViewModel.class);
        callsViewModel.setCallsProvider(callsProvider);

        CallRecyclerViewAdapter adapter = new CallRecyclerViewAdapter(callsViewModel);

        callsViewModel.getCallsLogList().observe(this, calls -> adapter.setCallsLogList(calls));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recentRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(view.getContext(), layoutManager.getOrientation());
        recentRecyclerView.addItemDecoration(decoration);
        recentRecyclerView.setAdapter(adapter);
    }
}
