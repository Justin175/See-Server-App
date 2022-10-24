package com.treulieb.worktimetool.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.data.Bill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BillUserListAdapter extends ArrayAdapter<Bill.BillUser> {

    private LayoutInflater inflater;
    private boolean hidePrivileges;

    public BillUserListAdapter(@NonNull Context context) {
        this(context, new ArrayList<>(), false);
    }

    public BillUserListAdapter(@NonNull Context context, boolean hidePrivileges) {
        this(context, new ArrayList<>(), hidePrivileges);
    }

    public BillUserListAdapter(@NonNull Context context, List<Bill.BillUser> users) {
        this(context, users, false);
    }

    public BillUserListAdapter(@NonNull Context context, List<Bill.BillUser> users, boolean hidePrivileges) {
        super(context, R.layout.support_simple_spinner_dropdown_item, users);

        this.inflater = LayoutInflater.from(context);
        this.hidePrivileges = hidePrivileges;
    }

    public boolean isHidingPrivileges() {
        return hidePrivileges;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Bill.BillUser user = getItem(position);

        View view = inflater.inflate(R.layout.ms_bill_info_users_userinfo, null);

        ((TextView) view.findViewById(R.id.ms_bill_info_users_userinfo_name)).setText(user.getName());
        ((TextView) view.findViewById(R.id.ms_bill_info_users_userinfo_privs)).setText(this.hidePrivileges ? "" : Bill.BillPrivilege.toString(user.getPrivileges()));

        return view;
    }
}
