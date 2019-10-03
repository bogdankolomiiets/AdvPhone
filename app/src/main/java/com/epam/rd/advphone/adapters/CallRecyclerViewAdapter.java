package com.epam.rd.advphone.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.epam.rd.advphone.Constants;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.RequestCodes;
import com.epam.rd.advphone.databinding.CallLogItemBinding;
import com.epam.rd.advphone.models.Call;
import com.epam.rd.advphone.repositories.PhoneCallsProvider;
import com.epam.rd.advphone.util.ContactCommunicator;
import com.epam.rd.advphone.util.OnCallInsertClickListener;
import com.epam.rd.advphone.views.ContactActivity;

import java.util.ArrayList;
import java.util.List;

public class CallRecyclerViewAdapter extends RecyclerView.Adapter<CallRecyclerViewAdapter.ViewHolder>
        implements ContactCommunicator, OnCallInsertClickListener {

    private RecyclerView recyclerView;
    private List<Call> callsLogList;
    private int prev_expanded = -1;


    public CallRecyclerViewAdapter() {
        this.callsLogList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CallLogItemBinding callLogItemBinding = DataBindingUtil.inflate(inflater, R.layout.call_log_item, parent, false);
        callLogItemBinding.setContactCommunicator(this);
        callLogItemBinding.setView(recyclerView);
        callLogItemBinding.setOnCallInsertClickListener(this);

        return new ViewHolder(callLogItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (holder.callLogItemBinding.callDetailInfoContainer.getVisibility() == View.VISIBLE) {
            holder.callLogItemBinding.callDetailInfoContainer.setVisibility(View.GONE);
        }

        Call call = callsLogList.get(position);
        holder.callLogItemBinding.setCall(call);
//        holder.callLogItemBinding.setItemPosition(position);

        if (call.getPhoto() != null) {
            holder.callLogItemBinding.callImage.setImageURI(Uri.parse(call.getPhoto()));
        } else {
            holder.callLogItemBinding.callImage.setCircleBackgroundColor(Color.LTGRAY);
            holder.callLogItemBinding.callImage.setImageResource(R.drawable.account);
        }

        holder.callLogItemBinding.getRoot().setOnClickListener(view -> {
            boolean visibility = holder.callLogItemBinding.callDetailInfoContainer.getVisibility() == View.VISIBLE;

            if (!visibility) {
                holder.itemView.setActivated(true);
                holder.callLogItemBinding.callDetailInfoContainer.setVisibility(View.VISIBLE);

                if (prev_expanded != -1 && prev_expanded != position) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(prev_expanded);
                    if (viewHolder != null) {
                        viewHolder.itemView.setActivated(false);
                        viewHolder.itemView.findViewById(R.id.callDetailInfoContainer).setVisibility(View.GONE);
                    }
                }
                prev_expanded = position;
            } else {
                holder.itemView.setActivated(false);
                holder.callLogItemBinding.callDetailInfoContainer.setVisibility(View.GONE);
            }

            TransitionManager.beginDelayedTransition(recyclerView);
        });

        holder.callLogItemBinding.getRoot().setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(recyclerView.getContext());
            builder.setTitle("Delete contact")
                    .setMessage("Do you want delete this contact?")
                    .setPositiveButton("OK", (dialog, arg1) ->
                            PhoneCallsProvider.getInstance(view.getContext()).deleteCalls(call.getId()));
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            });
            builder.create().show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        if (callsLogList != null) {
            return callsLogList.size();
        } else return 0;
    }

    public void setCallsLogList(List<Call> callsLogList) {
        this.callsLogList = callsLogList;
        notifyDataSetChanged();
    }

    @Override
    public void onInsertClick(Call call) {
        Intent intent = new Intent(recyclerView.getContext(), ContactActivity.class);
        intent.putExtra(Constants.CONTACT_NUMBER, call.getPhone());
        ((Activity) recyclerView.getContext()).startActivityForResult(intent, RequestCodes.REQUEST_NEW_CONTACT);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CallLogItemBinding callLogItemBinding;

        public ViewHolder(View view) {
            super(view);
            callLogItemBinding = DataBindingUtil.bind(view);
        }
    }
}
