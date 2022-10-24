package com.treulieb.worktimetool.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;
import com.treulieb.worktimetool.data.Bill;
import com.treulieb.worktimetool.req.SeeServerRequests;
import com.treulieb.worktimetool.utils.BillUserListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        menu.getMenu().add(createColoredString(Color.argb(255, 173, 118, 14), "Bearbeiten")).setOnMenuItemClickListener(item -> {
            setupAndShowUpdateUserPrivsPopup();
            return true;
        });
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

    @SuppressLint("ClickableViewAccessibility")
    private void setupAndShowUpdateUserPrivsPopup() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View popupView = inflater.inflate(R.layout.ms_bill_info_add_user, null);

        popupView.findViewById(R.id.ms_bill_info_add_user_priv_configure).setVisibility(billInfoLayout.hasPrivileg(Bill.BillPrivilege.CONFIGURE) ? View.VISIBLE : View.GONE);

        // setup current
        Bill.BillUser user = billInfoLayout.getCurrentBill().getUsers()[this.userIndex];

        ((CheckBox)  popupView.findViewById(R.id.ms_bill_info_add_user_priv_read)).setChecked(user.hasPrivilige(Bill.BillPrivilege.READ));
        ((CheckBox)  popupView.findViewById(R.id.ms_bill_info_add_user_priv_write)).setChecked(user.hasPrivilige(Bill.BillPrivilege.WRITE));
        ((CheckBox) popupView.findViewById(R.id.ms_bill_info_add_user_priv_configure)).setChecked(user.hasPrivilige(Bill.BillPrivilege.CONFIGURE));

        EditText userNameEditText = popupView.findViewById(R.id.ms_bill_info_add_user_name);
        userNameEditText.setText(user.getName());
        userNameEditText.setEnabled(false);

        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(thisView, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });

        popupView.findViewById(R.id.ms_bill_info_add_user_btn_break).setOnClickListener(v -> {
            popupWindow.dismiss();
        });

        Button updateBtn = popupView.findViewById(R.id.ms_bill_info_add_user_btn);
        updateBtn.setOnClickListener(v -> {
            updatePrivileges(popupView, popupWindow);
        });
        updateBtn.setText("Aktualisieren");
    }

    private void updatePrivileges(View popupView, final PopupWindow popupWindow) {
        String name = ((EditText) popupView.findViewById(R.id.ms_bill_info_add_user_name)).getText().toString();

        boolean privRead = ((CheckBox)  popupView.findViewById(R.id.ms_bill_info_add_user_priv_read)).isChecked();
        boolean privWrite = ((CheckBox)  popupView.findViewById(R.id.ms_bill_info_add_user_priv_write)).isChecked();
        boolean privConfigure = ((CheckBox) popupView.findViewById(R.id.ms_bill_info_add_user_priv_configure)).isChecked();

        String privString = "";
        if(privRead)
            privString += Bill.BillPrivilege.toString(Bill.BillPrivilege.READ);
        if(privWrite)
            privString += Bill.BillPrivilege.toString(Bill.BillPrivilege.WRITE);
        if(privConfigure)
            privString += Bill.BillPrivilege.toString(Bill.BillPrivilege.CONFIGURE);

        final String finalPrivString = privString;
        SeeServerRequests.updateUserPrivilegesFromBill(billInfoLayout.getCurrentBill().getName(), name, privString, response -> {
            if(response.isSuccessful()) {
                makeToast("Die Berechtigungen wurden erfolgreich geändert.");
                billInfoLayout.getCurrentBill().getUsers()[userIndex].setPrivileges(Bill.BillPrivilege.fromString(finalPrivString)); // TODO: Liste updaten
                popupWindow.dismiss();
                setupUsersList();
            }
            else makeToast(response.getMessage());
        }, error -> {
            popupWindow.dismiss();
        });
    }
}
