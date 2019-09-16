package com.epam.rd.advphone.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.epam.rd.advphone.util.OnContactConnectClickListener;
import com.epam.rd.advphone.util.OnContactEditClickListener;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.databinding.ContactItemBinding;
import com.epam.rd.advphone.models.Contact;
import com.epam.rd.advphone.util.ContactBackground;
import com.epam.rd.advphone.util.ContactCommunicator;
import com.epam.rd.advphone.viewmodels.ContactsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder>
                                        implements OnContactConnectClickListener, OnContactEditClickListener {
    private ContactCommunicator contactCommunicator;
    private Context context;
    private RecyclerView recyclerView;
    private ContactsViewModel viewModel;
    private List<Contact> contacts;
    private int prev_expanded = -1;
    private int countOfFavourite;

    public ContactRecyclerViewAdapter(ContactsViewModel viewModel) {
        this.contacts = new ArrayList<>();
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ContactItemBinding contactItemBinding =
                DataBindingUtil.inflate(inflater, R.layout.contact_item, parent, false);
        contactItemBinding.setOnContactConnectClickListener(this);
        contactItemBinding.setOnContactEditClickListener(this);

        //init context
        this.context = parent.getContext();
        this.contactCommunicator = (ContactCommunicator) parent.getContext();

        return new ContactViewHolder(contactItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        //if previous item is expanded and user scroll recyclerView
        //need setup visibility to GONE
        if (holder.contactItemBinding.contactDetailInfoContainer.getVisibility() == View.VISIBLE) {
            holder.contactItemBinding.contactDetailInfoContainer.setVisibility(View.GONE);
        }

        //set background for favourite contacts
        setFavouriteItemsBackground(holder, position);

        Contact contact = contacts.get(position);
        holder.contactItemBinding.setContact(contact);
        holder.contactItemBinding.setItemPosition(position);

        if (contact.getContactImage() != null) {
            holder.contactItemBinding.contactImage.setImageURI(Uri.parse(contact.getContactImage()));
        } else {
            holder.contactItemBinding.contactImage.setCircleBackgroundColor(ContactBackground.getColor(context, contact.getName().charAt(0)));
            holder.contactItemBinding.contactImage.setImageResource(R.drawable.account);
        }

        holder.contactItemBinding.getRoot().setOnClickListener(view -> {
            final boolean visibility = holder.contactItemBinding.contactDetailInfoContainer.getVisibility() == View.VISIBLE;

            if (!visibility) {
                holder.itemView.setActivated(true);
                holder.contactItemBinding.contactDetailInfoContainer.setVisibility(View.VISIBLE);

                if (prev_expanded != -1 && prev_expanded != position) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(prev_expanded);
                    if (viewHolder != null) {
                        viewHolder.itemView.setActivated(false);
                        viewHolder.itemView.findViewById(R.id.contactDetailInfoContainer).setVisibility(View.GONE);
                    }
                }

                prev_expanded = position;
            } else {
                holder.itemView.setActivated(false);
                holder.contactItemBinding.contactDetailInfoContainer.setVisibility(View.GONE);
            }
            TransitionManager.beginDelayedTransition(recyclerView);
        });
    }

    public void setCountOfFavourite(int countOfFavourite) {
        this.countOfFavourite = countOfFavourite;
    }

    private void setFavouriteItemsBackground(@NonNull ContactViewHolder holder, int position) {
        if (countOfFavourite > 0) {
            if (position < countOfFavourite) {
                holder.contactItemBinding.mainContactInfoContainer.setBackgroundColor(context.getResources().getColor(R.color.favourite_contact));
            } else holder.contactItemBinding.mainContactInfoContainer.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        if (contacts != null) {
            return contacts.size();
        } else return 0;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @Override
    public void onFavouriteClick(int position, Contact contact) {
        contact.setFavourite(!contact.isFavourite());
        viewModel.updateContact(contact);
    }

    @Override
    public void onCallClick(String contactNumber) {
        contactCommunicator.call(contactNumber);
    }

    @Override
    public void onSmsClick(String contactNumber) {
        contactCommunicator.sendSms(contactNumber);
    }

    @Override
    public void onEditClick(Contact contact) {
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        ContactItemBinding contactItemBinding;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactItemBinding = DataBindingUtil.bind(itemView);
        }
    }
}
