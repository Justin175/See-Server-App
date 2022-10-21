package com.treulieb.worktimetool.layout;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;


import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;
import com.treulieb.worktimetool.data.Bill;
import com.treulieb.worktimetool.req.SeeServerRequests;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class AddPostenLayout extends BaseLayout<ScrollView> {

    private BillInfoLayout billInfoLayout;
    private Bill currentBill;
    private boolean[] billMarkedUseres;

    public AddPostenLayout(Activity activity, ViewManager viewManager, ScrollView thisView, BillInfoLayout parentView) {
        super(activity, viewManager, thisView, parentView);

        this.billInfoLayout = parentView;
    }

    @Override
    protected void setup() {
        super.thisView.findViewById(R.id.ms_bill_info_add_posten_btn).setOnClickListener(v -> addPosten());
    }

    @Override
    protected void onShowListener() {
        currentBill = this.billInfoLayout.getCurrentBill();
        if(currentBill == null)
            return;

        ((EditText) thisView.findViewById(R.id.ms_bill_info_add_posten_title)).setText("");
        ((EditText) thisView.findViewById(R.id.ms_bill_info_add_posten_costs)).setText("");
        ((EditText) thisView.findViewById(R.id.ms_bill_info_add_posten_info)).setText("");
        ((EditText) thisView.findViewById(R.id.ms_bill_info_add_posten_creator)).setText("");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item);
        List<Bill.BillUser> users = new LinkedList<>();
        users.add(currentBill.getCreator());
        Collections.addAll(users, currentBill.getUsers());
        users.stream().map(Bill.BillUser::getName).forEach(arrayAdapter::add);

        billMarkedUseres = new boolean[arrayAdapter.getCount()];
        ((ListView) findViewById(R.id.ms_bill_info_add_posten_users)).setAdapter(arrayAdapter);
    }

    private void addPosten() {
        String title = ((EditText) thisView.findViewById(R.id.ms_bill_info_add_posten_title)).getText().toString().trim();
        String costs = ((EditText) thisView.findViewById(R.id.ms_bill_info_add_posten_costs)).getText().toString().trim();
        String info  = ((EditText) thisView.findViewById(R.id.ms_bill_info_add_posten_info)).getText().toString().trim();
        String creator = ((EditText) thisView.findViewById(R.id.ms_bill_info_add_posten_creator)).getText().toString().trim();

        if(title.length() == 0 || costs.length() == 0) {
            makeToast("Es wurde kein Titel / Kosten angegeben.");
            return;
        }

        // check costs str
        if(!costs.matches("[0-9\\.,]+")) {
            makeToast("Du hast bei den Kosten keine Zahl angegeben.");
            return;
        }

        costs = costs.replace(',', '.');

        if(info.length() == 0)
            info = null;
        if(creator.length() == 0)
            creator = null;

        List<String> toUsersNames = new LinkedList<>();
        ArrayAdapter<String> userNamesListAdapter = (ArrayAdapter<String>) ((ListView) findViewById(R.id.ms_bill_info_add_posten_users)).getAdapter();
        for(int i = 0; i < billMarkedUseres.length; i++) {
            if(billMarkedUseres[i]){
                toUsersNames.add(userNamesListAdapter.getItem(i));
            }
        }

        SeeServerRequests.addPosten(currentBill.getName(), title, Float.parseFloat(costs), info, toUsersNames.toArray(new String[toUsersNames.size()]), creator, response -> {
            if(response.isSuccessful()){
                SeeServerRequests.getBill(currentBill.getName(), response1 -> {
                    if(response1.isSuccessful())
                       billInfoLayout.setBillInfos(response1.getBill());

                    showParent();
                }, error -> {});
            }
        }, error -> {});
    }
}
