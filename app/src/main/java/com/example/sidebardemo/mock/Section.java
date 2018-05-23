package com.example.sidebardemo.mock;

public class Section {
    private int mStartIndex;
    private String mTitle;
    private int mWeight;
    
    public Section(int index, String title, int weight) {
        mStartIndex = index;
        mTitle = title;
        mWeight = weight;
    }

    public int getIndex() {
        return mStartIndex;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getWeight() {
        return mWeight;
    }


    @Override
    public String toString() {
        return "Section{" +
                "mStartIndex=" + mStartIndex +
                ", mTitle='" + mTitle + '\'' +
                ", mWeight=" + mWeight +
                '}';
    }
}
