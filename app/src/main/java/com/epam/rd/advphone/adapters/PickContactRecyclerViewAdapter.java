package com.epam.rd.advphone.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.Constants;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.models.Contact;
import com.epam.rd.advphone.util.ContactBackground;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PickContactRecyclerViewAdapter extends RecyclerView.Adapter<PickContactRecyclerViewAdapter.ContactViewHolder> {
    private List<Contact> contacts;

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(Constants.CONTACT_NUMBER, contacts.get(position).getPhone());
            intent.putExtra(Constants.CONTACT_NAME, contacts.get(position).getName());
            Activity activity = ((Activity) holder.itemView.getContext());
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
        });

        Contact contact = contacts.get(position);
        CircleImageView circleView = holder.contactImage;

        if (contact != null) {
            holder.contactName.setText(contacts.get(position).getName());

            if (contact.getContactImage() != null){
                circleView.setImageURI(Uri.parse(contact.getContactImage()));
            } else{
                circleView.setCircleBackgroundColor(ContactBackground.getColor(holder.itemView.getContext(), contact.getName().charAt(0)));
                circleView.setImageResource(R.drawable.account);
            }
        }
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

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        CircleImageView contactImage;
        TextView contactName;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contactImage);
            contactName = itemView.findViewById(R.id.contactName);
        }
    }
}
