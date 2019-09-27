package com.epam.rd.advphone.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.databinding.KeypadFragmentBinding;
import com.epam.rd.advphone.util.ContactCommunicator;

public class KeypadFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, ContactCommunicator {
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
        call(binding.getRoot(), binding.editNumber.getText().toString());
    }

    public void callSecondSim() {
        call(binding.getRoot(), binding.editNumber.getText().toString());
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
