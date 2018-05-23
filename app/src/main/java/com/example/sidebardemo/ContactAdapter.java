package com.example.sidebardemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sidebardemo.mock.Contact;
import com.example.sidebardemo.mock.Section;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> mContacts;
    private LayoutInflater mInflater;
    private ContactScrollerAdapter mContactScrollerAdapter;

    public ContactAdapter(Context c, List<Contact> contacts, ContactScrollerAdapter contactScrollerAdapter) {
        mContacts = contacts;
        mInflater = LayoutInflater.from(c);
        mContactScrollerAdapter = contactScrollerAdapter;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contact = mInflater.inflate(R.layout.view_contact, parent, false);
        return new ContactViewHolder(contact);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        holder.mImg.setImageResource(contact.getImageResId());
        holder.mName.setText(contact.getName());
        Section s = mContactScrollerAdapter.getSectionFromItemIndex(position);
        if (s.getIndex() == position) {
            holder.title.setText(s.getTitle());
        } else {
            holder.title.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView mImg;
        private TextView mName;

        ContactViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_index);
            mImg = itemView.findViewById(R.id.contact_img);
            mName = itemView.findViewById(R.id.contact_name);
        }
    }
}