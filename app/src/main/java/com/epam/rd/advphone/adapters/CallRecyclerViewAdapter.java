package com.epam.rd.advphone.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.databinding.CallLogItemBinding;
import com.epam.rd.advphone.models.Call;
import com.epam.rd.advphone.viewmodels.CallsViewModel;

import java.util.ArrayList;
import java.util.List;

public class CallRecyclerViewAdapter extends RecyclerView.Adapter<CallRecyclerViewAdapter.ViewHolder> {
    private CallsViewModel callsViewModel;
    private Context context;
    private List<Call> callsLogList;


    public CallRecyclerViewAdapter(CallsViewModel callsViewModel) {
        this.callsViewModel = callsViewModel;
        this.callsLogList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CallLogItemBinding callLogItemBinding = DataBindingUtil.inflate(inflater, R.layout.call_log_item, parent, false);

        //init context
        this.context = parent.getContext();

        return new ViewHolder(callLogItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Call call = callsLogList.get(position);
        holder.binding.setCall(call);

        if (call.getCallImage() != null) {
            holder.binding.callImage.setCircleBackgroundColor(Color.RED);
            holder.binding.callImage.setImageResource(R.drawable.account_plus);
        } else {
        holder.binding.callImage.setCircleBackgroundColor(Color.LTGRAY);
        holder.binding.callImage.setImageResource(R.drawable.account_plus);
        }
    }

    @Override
    public int getItemCount() {
        if (callsLogList != null) {
            return callsLogList.size();
        } else return 0;
    }

    public void setCallsLogList(List<Call> callsLogList){
        this.callsLogList = callsLogList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CallLogItemBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }
    }
}
