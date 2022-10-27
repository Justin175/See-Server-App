package com.treulieb.worktimetool.layout;

import android.app.Activity;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;


import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;
import com.treulieb.worktimetool.data.Bill;
import com.treulieb.worktimetool.req.SeeServerRequests;
import com.treulieb.worktimetool.utils.BillUserListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddPostenLayout extends BaseLayout<ScrollView> {

    private BillInfoLayout billInfoLayout;
    private Bill currentBill;
    private BillUserListAdapter billUserListAdapter;

    private List<Bill.BillUser> billMarkedUseres;
    private List<Bill.BillUser> allUsers;

    private final int markedColor =  Color.parseColor("#a3e6f5");

    public AddPostenLayout(Activity activity, ViewManager viewManager, ScrollView thisView, BillInfoLayout parentView) {
        super(activity, viewManager, thisView, parentView);

        this.billInfoLayout = parentView;
    }

    @Override
    protected void setup() {
        super.thisView.findViewById(R.id.ms_bill_info_add_posten_btn).setOnClickListener(v -> addPosten());

        this.billMarkedUseres = new LinkedList<>();
        this.allUsers = new ArrayList<>();

        this.findViewById(R.id.ms_bill_info_add_posten_btn_markall).setOnClickListener(v -> {
            if(billUserListAdapter != null) {
                int marked = this.billMarkedUseres.size();
                boolean markAll;

                if(markAll = (marked < currentBill.getUsersCount())) { // mark all
                    this.billMarkedUseres.clear();
                    this.billMarkedUseres.addAll(this.allUsers);
                }
                else { // unmark all
                    this.billMarkedUseres.clear();
                }

                for(int i = 0; i < this.billUserListAdapter.getCount(); i++)
                    this.billUserListAdapter.getView(i, null, null).setBackgroundColor(markAll ? this.markedColor : 0);
            }
        });
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

        this.billMarkedUseres.clear();
        this.allUsers.clear();

        allUsers.add(currentBill.getCreator());
        allUsers.addAll(Arrays.asList(currentBill.getUsers()));

        billUserListAdapter = new BillUserListAdapter(activity, allUsers, true);
        ListView usersListView = ((ListView) findViewById(R.id.ms_bill_info_add_posten_users));
        usersListView.setAdapter(billUserListAdapter);
        usersListView.setOnItemClickListener((parent, view, position, id) -> {
            Bill.BillUser clickedOn = this.allUsers.get(position);
            boolean contains;

            if(contains = this.billMarkedUseres.contains(clickedOn))
                this.billMarkedUseres.remove(clickedOn);
            else
                this.billMarkedUseres.add(clickedOn);

            view.setBackgroundColor(!contains ? markedColor : 0);
        });

        billMarkedUseres.addAll(allUsers); // all added
        //mark all
        for(int i = 0; i < billUserListAdapter.getCount(); i++)
            billUserListAdapter.getView(i, null, null).setBackgroundColor(markedColor); // because of marked
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

        if(this.billMarkedUseres.size() == 0) {
            makeToast("Es wurden eine Personen angegeben, an die die Rechnung gehen soll.");
            return;
        }

        costs = costs.replace(',', '.');

        if(info.length() == 0)
            info = null;
        if(creator.length() == 0)
            creator = null;

        SeeServerRequests.addPosten(
                currentBill.getName(),
                title, Float.parseFloat(costs),
                info,
                billMarkedUseres.stream().map(Bill.BillUser::getName).collect(Collectors.toList()).toArray(new String[0]),
                creator,
                response -> {
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
