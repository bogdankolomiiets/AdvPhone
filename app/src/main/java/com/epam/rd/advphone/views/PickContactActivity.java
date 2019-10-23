package com.epam.rd.advphone.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.Constants;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.adapters.PickContactRecyclerViewAdapter;
import com.epam.rd.advphone.viewmodels.PickContactViewModel;

import java.util.regex.Pattern;

public class PickContactActivity extends AppCompatActivity {
    private PickContactViewModel pickContactViewModel;
    private static final String PHONE_PATTERN = "^[+]?[\\d]*[(]?\\d+[)]?\\d*[-]?\\d*[-]?\\d*";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contacts);

        //init pickContactViewModel
        pickContactViewModel = ViewModelProviders.of(this).get(PickContactViewModel.class);
        pickContactViewModel.getContactsLive().observe(this, contacts -> {
            pickContactViewModel.setContactsList(contacts);
            String pattern = pickContactViewModel.getPattern().getValue();
            pickContactViewModel.setPattern(pattern == null ? "" : pattern);
        });

        setupInputPhoneNumber();

        setupPickContactRecycler();

        setupSearchView();
    }

    private void setupInputPhoneNumber() {
        TextView addPhoneNumber = findViewById(R.id.addPhoneNumber);
        EditText phoneNumber = findViewById(R.id.phoneNumber);
        addPhoneNumber.setOnClickListener(v -> {
            String tempPhone = phoneNumber.getText().toString();
            if (Pattern.matches(PHONE_PATTERN, tempPhone)) {
                Intent intent = new Intent();
                intent.putExtra(Constants.CONTACT_NUMBER, PhoneNumberUtils.normalizeNumber(tempPhone));
                intent.putExtra(Constants.CONTACT_NAME, tempPhone);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(PickContactActivity.this, R.string.incorrect_phone, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    pickContactViewModel.setPattern(newText);
                } else {
                    pickContactViewModel.setPattern("");
                }
                return true;
            }
        });
    }

    private void setupPickContactRecycler() {
        RecyclerView pickContactRecycler = findViewById(R.id.pickContactRecycler);
        pickContactRecycler.setLayoutManager(new LinearLayoutManager(this));
        PickContactRecyclerViewAdapter adapter = new PickContactRecyclerViewAdapter();
        pickContactRecycler.setAdapter(adapter);
        pickContactViewModel.getPattern().observe(this, s -> adapter.setContacts(pickContactViewModel.getContacts()));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
