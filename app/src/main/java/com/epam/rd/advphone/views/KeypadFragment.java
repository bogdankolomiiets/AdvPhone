package com.epam.rd.advphone.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.databinding.KeypadFragmentBinding;

import java.util.Locale;

public class KeypadFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private KeypadFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.keypad_fragment, container, false);
        binding.setFragment(this);

        setOnclickListeners();

        return binding.getRoot();
    }

    public void callFirstSim() {
        String toDial = "tel:" + Uri.encode(binding.editNumber.getText().toString());
        Intent callIntent = new Intent();
        callIntent.setAction(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(toDial));
        startActivity(callIntent);
    }

    public void callSecondSim() {
        String toDial = "tel:" + Uri.encode(binding.editNumber.getText().toString());
        Intent callIntent = new Intent();
        callIntent.setAction(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(toDial));
        startActivity(callIntent);
    }

    private void setOnclickListeners() {
        binding.buttonNumberOne.setOnClickListener(this);
        binding.buttonNumberTwo.setOnClickListener(this);
        binding.buttonNumberThree.setOnClickListener(this);
        binding.buttonNumberFour.setOnClickListener(this);
        binding.buttonNumberFive.setOnClickListener(this);
        binding.buttonNumberSix.setOnClickListener(this);
        binding.buttonNumberSeven.setOnClickListener(this);
        binding.buttonNumberEight.setOnClickListener(this);
        binding.buttonNumberNine.setOnClickListener(this);
        binding.buttonNumberZero.setOnClickListener(this);
        binding.buttonAsterisk.setOnClickListener(this);
        binding.buttonSharp.setOnClickListener(this);
        binding.imageButtonBackspase.setOnClickListener(this);
        binding.buttonNumberZero.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton_backspase:
                String text = binding.editNumber.getText().toString();
                if (text.length() > 0) {
                    binding.editNumber.setText(text.substring(0, text.length() - 1));
                }
                break;
            default:
                binding.editNumber.setText(binding.editNumber.getText() + view.getTag().toString());
                break;
        }
    }


    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.button_number_zero:
                binding.editNumber.setText("+");
                break;
        }
        return true;
    }
}
