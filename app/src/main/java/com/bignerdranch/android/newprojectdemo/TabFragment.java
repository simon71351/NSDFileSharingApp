package com.bignerdranch.android.newprojectdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by simon on 6/29/16.
 */
public class TabFragment extends Fragment {

    private int position;

    public TabFragment() {
    }

    public TabFragment(int pos) {
        position = pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabs, container, false);

        ((TextView)view.findViewById(R.id.textView)).setText("This is tab layout "+position);

        return view;
    }
}
