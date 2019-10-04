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
        binding.editNumber.setShowSoftInputOnFocus(false);

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
        if (view.getId() == R.id.imageButton_backspase) {
            String textDelete = binding.editNumber.getText().toString();
            int positionDelete = binding.editNumber.getSelectionEnd();

            if (textDelete.length() > 0) {

                if (positionDelete != 0) {
                    String beforeCursor = textDelete.substring(0, positionDelete - 1);
                    String afterCursor = textDelete.substring(positionDelete);
                    String enteredString = beforeCursor + afterCursor;
                    binding.editNumber.setText(enteredString);
                    binding.editNumber.setSelection(positionDelete - 1);
                } else if (positionDelete == 0 && binding.editNumber.isFocused()) {

                } else if (!binding.editNumber.isFocused()) {
                    binding.editNumber.setText(textDelete.substring(0, textDelete.length() - 1));
                }
            }
        } else {
            String textIncert = binding.editNumber.getText().toString();
            int positionIncert = binding.editNumber.getSelectionEnd();

            if (!binding.editNumber.isFocused()) {
                binding.editNumber.setText(binding.editNumber.getText() + view.getTag().toString());
                binding.editNumber.setSelection(binding.editNumber.getText().length());
            } else {
                binding.editNumber.setText(textIncert.substring(0, positionIncert) +
                        view.getTag().toString() + textIncert.substring(positionIncert));
                binding.editNumber.setSelection(positionIncert + 1);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.button_number_zero) {
            binding.editNumber.setText("+");
        }
        return true;
    }
}
