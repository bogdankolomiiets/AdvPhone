package com.epam.rd.advphone.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.databinding.SmsItemBinding;
import com.epam.rd.advphone.models.Sms;
import com.epam.rd.advphone.util.ContactCommunicator;
import com.epam.rd.advphone.viewmodels.SmsViewModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SmsRecyclerViewAdapter extends RecyclerView.Adapter<SmsRecyclerViewAdapter.SmsHolder> implements ContactCommunicator {
    private List<Sms> smsList;

    public SmsRecyclerViewAdapter() {
        smsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SmsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.sms_item, parent, false);
        return new SmsHolder(itemView.getRootView());
    }

    @Override
    public void onBindViewHolder(@NonNull SmsHolder holder, int position) {
        Sms sms = smsList.get(position);
        holder.smsItemBinding.setSms(sms);
        holder.itemView.setOnLongClickListener(v -> {
            showDialog(position, v);
            return true;
        });
        holder.smsItemBinding.getRoot().setOnClickListener(v -> sendSms(holder.itemView, sms.getRecipientNumber(), sms.getRecipientName()));

        CircleImageView circleView = holder.smsItemBinding.contactImage;
        if (sms.getRecipientName() != null) {
            circleView.setImageResource(R.drawable.transparent);
            holder.smsItemBinding.contactInitials.setText(String.valueOf(sms.getRecipientName().charAt(0)));
        } else {
            circleView.setImageResource(R.drawable.account);
        }
    }

    private void showDialog(int position, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(R.string.delete)
                .setMessage(R.string.delete_sms_chain)
                .setPositiveButton(R.string.ok_btn, (dialog, which) -> ViewModelProviders.of((FragmentActivity) v.getContext()).get(SmsViewModel.class)
                        .deleteMessagesByRecipient(smsList.get(position).getRecipientNumber()))
                .setNegativeButton(R.string.cancel_btn, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public void setSmsList(List<Sms> smsList) {
        this.smsList.clear();
        this.smsList.addAll(smsList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public class SmsHolder extends RecyclerView.ViewHolder {
        SmsItemBinding smsItemBinding;

        public SmsHolder(@NonNull View itemView) {
            super(itemView);
            smsItemBinding = DataBindingUtil.bind(itemView);
        }
    }
}
