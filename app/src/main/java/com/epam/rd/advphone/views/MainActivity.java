package com.epam.rd.advphone.views;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.epam.rd.advphone.AdvPhoneViewModelFactory;
import com.epam.rd.advphone.Constants;
import com.epam.rd.advphone.ContactDaoInjection;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.RequestCodes;
import com.epam.rd.advphone.adapters.MainViewPager2Adapter;
import com.epam.rd.advphone.databinding.ActivityMainBinding;
import com.epam.rd.advphone.repositories.CallsProvider;
import com.epam.rd.advphone.repositories.ContactsProvider;
import com.epam.rd.advphone.repositories.PhoneCallsProvider;
import com.epam.rd.advphone.repositories.PhoneContactsProvider;
import com.epam.rd.advphone.util.ContactCommunicator;
import com.epam.rd.advphone.viewmodels.CallsViewModel;
import com.epam.rd.advphone.viewmodels.ContactsViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements ContactCommunicator {
    private ActivityMainBinding mainBinding;
    private int[] tabTitles;
    private String tabPanelLocation;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mainBinding.toolBar);

        //getting contacts from address book
        //and insertion to the database
        retrieveContacts();

        obtainViewModel(this, ContactsViewModel.class);

        //getting info about tabs location from shared preferences
        getTabLocation();

        initTabTitles();
        initTabLayoutAndViewPager();

    }

    private void retrieveContacts() {
        String permission = Manifest.permission.READ_CONTACTS;
        if (PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, RequestCodes.PERMISSION_READ_CONTACTS);
        } else {
            ContactsProvider contactsProvider = PhoneContactsProvider.getInstance(this);
            Executors.newSingleThreadExecutor().submit(() -> ContactDaoInjection.provideContactsDao(this)
                    .insertContacts(contactsProvider.getContacts()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case RequestCodes.PERMISSION_READ_CONTACTS: {
                    retrieveContacts();
                    break;
                }
                case RequestCodes.PERMISSION_READ_CALL_LOG: {
                    CallsProvider callsProvider = PhoneCallsProvider.getInstance(this);
                    CallsViewModel callsViewModel = obtainViewModel(this, CallsViewModel.class);
                    callsViewModel.setCallsProvider(callsProvider);
                    callsViewModel.getCallsLogList().setValue(callsProvider.getCalls());
                    break;
                }
            }
        }
    }

    private void getTabLocation() {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        tabPanelLocation = preferences.getString(Constants.PANEL_LOCATION, getString(R.string.tag_location_top));
    }

    private void initTabLayoutAndViewPager() {
        MainViewPager2Adapter viewPager2Adapter = new MainViewPager2Adapter(getSupportFragmentManager(), getLifecycle());
        mainBinding.viewPager2.setAdapter(viewPager2Adapter);

        createTabLayout();

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout,
                mainBinding.viewPager2, (tab, position) -> tab.setText(tabTitles[position]));
        tabLayoutMediator.attach();
    }

    private void createTabLayout() {
        tabLayout = new TabLayout(this);
        tabLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(Color.DKGRAY, getResources().getColor(R.color.colorAccent));

        //setup tabLayout location
        if (tabPanelLocation.equals(getString(R.string.tag_location_bottom))){
            mainBinding.LlForBottomPanel.addView(tabLayout);
        } else mainBinding.LlForTopPanel.addView(tabLayout);
    }

    private void initTabTitles() {
        tabTitles = new int[] {R.string.keypad, R.string.recent, R.string.contacts};
    }

    //init main menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsItem){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static <T extends ViewModel> T obtainViewModel(FragmentActivity activity, Class<T> modelClass) {
        // Use a Factory to inject dependencies into the ViewModel
        AdvPhoneViewModelFactory factory = AdvPhoneViewModelFactory.getInstance(activity.getApplication());

        return (T) ViewModelProviders.of(activity, factory).get(modelClass);
    }

    @Override
    public void call(String contactNumber) {
        String permission = Manifest.permission.CALL_PHONE;
        if (PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, RequestCodes.PERMISSION_CALL_PHONE);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + PhoneNumberUtils.normalizeNumber(contactNumber)));
            startActivity(intent);
        }
    }

    @Override
    public void sendSms(String contactNumber) {
        String permission = Manifest.permission.SEND_SMS;
        if (PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, RequestCodes.PERMISSION_SEND_SMS);
        } else {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("sms:" + PhoneNumberUtils.normalizeNumber(contactNumber)));
            startActivity(intent);
        }
    }

    public void showContactActivity(View view) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivityForResult(intent, RequestCodes.REQUEST_NEW_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.REQUEST_NEW_CONTACT) {
            if (resultCode == RESULT_OK && data.hasExtra(Constants.CONTACT)) {
                Executors.newSingleThreadExecutor().submit(() -> ContactDaoInjection.provideContactsDao(MainActivity.this)
                        .insertContact(data.getParcelableExtra(Constants.CONTACT)));

            }
        }
    }
}
