package com.example.sidebardemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.sidebardemo.mock.Contact;
import com.snailyc.side.AbstractSideBar;
import com.snailyc.side.ScrollerListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Contact> contactList = Contact.getContactList();

    private ContactScrollerAdapter mContactScrollerAdapter;
    private ContactAdapter mContactAdapter;
    private LinearLayoutManager mLayoutManager;

    private AbstractSideBar sideBar;
    private RecyclerView recycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sideBar = findViewById(R.id.bubble_scroller);
        recycler = findViewById(R.id.recycler);


        mContactScrollerAdapter = new ContactScrollerAdapter(contactList);
        mContactAdapter = new ContactAdapter(this, contactList, mContactScrollerAdapter);

        sideBar.setScrollerListener(mScrollerListener);
        sideBar.setSectionIndexerAdapter(mContactScrollerAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        recycler.setAdapter(mContactAdapter);

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                final int firstVisibleItemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                final int lastVisibleItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();

                final int highlightIndex = mContactScrollerAdapter.sectionFromPosition(firstVisibleItemPosition);
                final int highlightRange = mContactScrollerAdapter.sectionFromPosition(lastVisibleItemPosition)-highlightIndex+1;

                sideBar.showSectionHighlight(highlightIndex,highlightRange);
            }
        });

    }


    private final ScrollerListener mScrollerListener = new ScrollerListener() {


        @Override
        public void onScrollPositionChanged(int sectionPosition) {
            mLayoutManager.scrollToPositionWithOffset(mContactScrollerAdapter.positionFromSection(sectionPosition), 0);

        }


    };
}
