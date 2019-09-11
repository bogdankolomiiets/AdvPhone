package com.epam.rd.advphone.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.adapters.ContactRecyclerViewAdapter;
import com.epam.rd.advphone.viewmodels.ContactsViewModel;

public class ContactsFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.contacts_fragment, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //init recyclerView for all contacts
        RecyclerView contactRecyclerView = view.findViewById(R.id.contactRecyclerView);
        contactRecyclerView.setHasFixedSize(true);

        ContactsViewModel contactsViewModel = MainActivity.obtainViewModel(getActivity(), ContactsViewModel.class);

        ContactRecyclerViewAdapter adapter = new ContactRecyclerViewAdapter(contactsViewModel);

        contactsViewModel.getContactsList().observe(this, contacts -> adapter.setContacts(contacts));

        contactsViewModel.getCountOfFavourite().observe(this, integer -> adapter.setCountOfFavourite(integer));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration decoration = new DividerItemDecoration(view.getContext(), layoutManager.getOrientation());
        contactRecyclerView.setLayoutManager(layoutManager);
        contactRecyclerView.addItemDecoration(decoration);
        contactRecyclerView.setAdapter(adapter);
    }
}
