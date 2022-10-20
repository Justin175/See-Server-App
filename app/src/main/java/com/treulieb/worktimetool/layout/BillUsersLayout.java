package com.treulieb.worktimetool.layout;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;
import com.treulieb.worktimetool.data.Bill;
import com.treulieb.worktimetool.req.SeeServerRequests;
import com.treulieb.worktimetool.utils.BillUserListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class BillUsersLayout extends BaseLayout<LinearLayout> {

    private BillInfoLayout billInfoLayout;
    private ListView userListView;
    private int userIndex = -1;

    public BillUsersLayout(Activity activity, ViewManager viewManager, LinearLayout thisView, BillInfoLayout parentView) {
        super(activity, viewManager, thisView, parentView);

        this.billInfoLayout = parentView;
    }

    @Override
    protected void setup() {
        userListView = thisView.findViewById(R.id.ms_bill_info_users_list);
        userListView.setOnItemLongClickListener(this::createPopupMenu);
    }

    private boolean createPopupMenu(AdapterView<?> adapterView, View view, int position, long id) {
        if(position == 0 || !billInfoLayout.hasPrivileg(Bill.BillPrivilege.CONFIGURE))
            return false;

        this.userIndex = position - 1;

        PopupMenu menu = new PopupMenu(activity, view);
        menu.getMenu().add(createColoredString(Color.RED, "Bearbeiten"));
        menu.getMenu().add(createColoredString(Color.RED, "Löschen")).setOnMenuItemClickListener(item -> {
            return removeUserFromBill();
        });
        menu.show();

        return true;
    }

    private boolean removeUserFromBill() {
        if(this.userIndex < 0)
            return false;

        Bill.BillUser user = billInfoLayout.getCurrentBill().getUsers()[this.userIndex];
        final int userIndex = this.userIndex;
        SeeServerRequests.removeUserFromBill(billInfoLayout.getCurrentBill().getName(), user.getName(), response -> {
            if(response.isSuccessful()) {
                this.billInfoLayout.getCurrentBill().removeUser(userIndex);
                setupUsersList();
            }
            else {
                makeErrorToast("Löschen eines Nutzers");
            }
        }, error -> {
            makeErrorToast("Löschen eines Nutzers");
        });


        return true;
    }

    @Override
    protected void onShowListener() {
        setupUsersList();
    }

    private void setupUsersList() {
        Bill bill = billInfoLayout.getCurrentBill();

        List<Bill.BillUser> users = new ArrayList<>(1 + bill.getUsers().length);
        users.add(bill.getCreator());
        users.addAll(Arrays.asList(bill.getUsers()));

        userListView.setAdapter(new BillUserListAdapter(activity, users));
    }
}
