package com.treulieb.worktimetool.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

public class ViewListAdapter extends ArrayAdapter<View> {

    private int ressource;

    public ViewListAdapter(@NonNull Context context, int resource, List<View> items) {
        super(context, resource, items);
        this.ressource = ressource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getItem(position);
    }
}
