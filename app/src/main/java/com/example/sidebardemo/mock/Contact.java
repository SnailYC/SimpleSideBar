package com.example.sidebardemo.mock;

import com.example.sidebardemo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Contact {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private int mImageResId;
    private String mName;


    public Contact(int imageResId, String name) {
        mImageResId = imageResId;
        mName = name;
    }

    public int getImageResId() {
        return mImageResId;
    }

    public String getName() {
        return mName;
    }

    public static List<Contact> getContactList() {
        List<Contact> contacts = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 26; i++) {
            String firstName = ALPHABET.substring(i, i + 1);
            int count = random.nextInt(4)+1;
            for (int j = 0; j < count; j++) {
                contacts.add(new Contact(R.mipmap.ic_launcher, firstName + j));
            }

        }

        Collections.sort(contacts, COMPARATOR);
        return contacts;
    }

    public static final Comparator<Contact> COMPARATOR = new Comparator<Contact>() {
        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

}
