package com.kamron.pogoiv.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kamron.pogoiv.R;

import butterknife.ButterKnife;

public class RecalibrateFragment extends Fragment {


    public RecalibrateFragment() {
        super();
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recalibrate, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
