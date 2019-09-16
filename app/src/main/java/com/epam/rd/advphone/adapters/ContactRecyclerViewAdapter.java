package com.epam.rd.advphone.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.epam.rd.advphone.Constants;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.RequestCodes;
import com.epam.rd.advphone.databinding.ContactItemBinding;
import com.epam.rd.advphone.models.Contact;
import com.epam.rd.advphone.util.ContactBackground;
import com.epam.rd.advphone.util.ContactCommunicator;
import com.epam.rd.advphone.util.OnContactConnectClickListener;
import com.epam.rd.advphone.util.OnContactEditClickListener;
import com.epam.rd.advphone.viewmodels.ContactsViewModel;
import com.epam.rd.advphone.views.ContactActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder>
                                        implements OnContactConnectClickListener, OnContactEditClickListener {
    private ContactCommunicator contactCommunicator;
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
        //init contactCommunicator
        this.contactCommunicator = (ContactCommunicator) parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ContactItemBinding contactItemBinding =
                DataBindingUtil.inflate(inflater, R.layout.contact_item, parent, false);
        contactItemBinding.setOnContactConnectClickListener(this);
        contactItemBinding.setOnContactEditClickListener(this);

        contactItemBinding.getRoot().setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setIcon(R.drawable.delete_alert);
            builder.setTitle(R.string.remove_contact);
            builder.setMessage(contactItemBinding.getContact().getName() + "\n\n" + contactItemBinding.getContact().getPhone());
            builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    viewModel.deleteContact(contactItemBinding.getContact().getId());
                }
            });
            builder.setNegativeButton(R.string.cancel_btn, (dialogInterface, i) -> dialogInterface.dismiss());
            builder.create().show();
            return true;
        });


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
            CircleImageView circleView = holder.contactItemBinding.contactImage;
            circleView.setCircleBackgroundColor(ContactBackground.getColor(recyclerView.getContext(), contact.getName().charAt(0)));
            circleView.setImageResource(R.drawable.account);
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
            holder.contactItemBinding.mainContactInfoContainer.setBackgroundColor((position < countOfFavourite) ?
                    recyclerView.getContext().getResources().getColor(R.color.favourite_contact) : Color.TRANSPARENT);

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
        } else {
            return 0;
        }
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
        Intent intent = new Intent(recyclerView.getContext(), ContactActivity.class);
        intent.putExtra(Constants.CONTACT, contact);
        ((Activity)recyclerView.getContext()).startActivityForResult(intent, RequestCodes.REQUEST_EDIT_CONTACT);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        ContactItemBinding contactItemBinding;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactItemBinding = DataBindingUtil.bind(itemView);
        }
    }
}
