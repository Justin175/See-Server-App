package com.treulieb.worktimetool.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;
import com.treulieb.worktimetool.data.Bill;
import com.treulieb.worktimetool.data.Posten;
import com.treulieb.worktimetool.req.SeeServerRequests;
import com.treulieb.worktimetool.utils.ViewListAdapter;

import java.util.LinkedList;
import java.util.List;

public class BillInfoLayout extends BaseLayout<ScrollView> {

    private Bill currentBill;

    private AddPostenLayout addPostenLayout;
    private BillUsersLayout billUsersLayout;
    private BillInfoSummaryLayout summaryLayout;
    private OwnSummaryLayout ownSummaryLayout;

    public BillInfoLayout(Activity activity, ViewManager viewManager, ScrollView thisView, View parentView) {
        super(activity, viewManager, thisView, parentView);
    }

    @Override
    protected void setup() {
        addPostenLayout = new AddPostenLayout(activity, viewManager, activity.findViewById(R.id.ms_bill_info_add_posten), this);
        billUsersLayout = new BillUsersLayout(activity, viewManager, activity.findViewById(R.id.ms_bill_info_users), this);
        summaryLayout = new BillInfoSummaryLayout(activity, viewManager, activity.findViewById(R.id.ms_bill_info_costs_summary), this);
        ownSummaryLayout = new OwnSummaryLayout(activity, viewManager, activity.findViewById(R.id.ms_bill_info_own_summary), this);
    }

    public Bill getCurrentBill() {
        return currentBill;
    }

    private void deleteCurrentBill() {
        SeeServerRequests.deleteBill(((TextView) findViewById(R.id.ms_bill_info_billname)).getText().toString(), response -> {
            viewManager.openView(viewManager.getCurrentParent());
        }, error -> makeErrorToast("L??schen der Rechnung"));
    }

    public void setBillInfos(Bill bill) {
        String creator;
        currentBill = bill;

        ((TextView) findViewById(R.id.ms_bill_info_billname)).setText(bill.getName());
        ((TextView) findViewById(R.id.ms_bill_info_created)).setText(bill.getCreated());
        ((TextView) findViewById(R.id.ms_bill_info_creator)).setText(creator = bill.getCreator().getName());
        ((TextView) findViewById(R.id.ms_bill_info_costs_sum)).setText(bill.getCostsSum() + " ???");

        LinearLayout postenView = findViewById(R.id.ms_bill_info_posten);
        LayoutInflater inflater = LayoutInflater.from(activity);
        List<View> items = new LinkedList();

        for (Posten posten : bill.getPosten()) {
            View postenInfoView = inflater.inflate(R.layout.ms_bill_info_posten, null);
            ((TextView) postenInfoView.findViewById(R.id.ms_bill_info_posten_id)).setText(posten.getId());
            ((TextView) postenInfoView.findViewById(R.id.ms_bill_info_posten_title)).setText(posten.getTitle());
            ((TextView) postenInfoView.findViewById(R.id.ms_bill_info_posten_costs)).setText("" + posten.getCosts() + " ???");

            PopupMenu menu = new PopupMenu(activity, postenInfoView);

            menu.getMenu().add("Erstellt von " + posten.getCreator()).setOnMenuItemClickListener(item -> false);
            menu.getMenu().add("Erstellt am " + posten.getCreated()).setOnMenuItemClickListener(item -> false);
            if(hasPrivileg(Bill.BillPrivilege.WRITE))
                menu.getMenu().add(createColoredString(Color.RED, "L??schen")).setOnMenuItemClickListener(item -> {
                    deletePosten(posten);
                    return true;
                } );

            postenInfoView.setOnLongClickListener(v -> {
                menu.show();
                return true;
            });

            postenView.addView(postenInfoView);
        }

        setupBillPopupMenu();
    }

    private void deletePosten(Posten posten) {
        SeeServerRequests.deletePosten(currentBill.getName(), posten.getId(), response -> {
            if (response.optBoolean("successful")) {
                makeToast("Posten wurde erfolgreich gel??scht.");
                SeeServerRequests.getBill(currentBill.getName(), response1 -> {
                    if (response1.isSuccessful()) {
                        setBillInfos(response1.getBill());
                    }
                }, error -> {
                });
            }
        }, error -> {
        });
    }

    private void setupBillPopupMenu() {
        PopupMenu menu = new PopupMenu(activity, thisView.findViewById(R.id.ms_bill_info_btn_open_info));
        if(hasPrivileg(Bill.BillPrivilege.CONFIGURE)) {
            menu.getMenu().add(createColoredString(Color.RED, "L??schen")).setOnMenuItemClickListener(item -> {
                deleteCurrentBill();
                return true;
            });
            menu.getMenu().add("Benutzer hinzuf??gen").setOnMenuItemClickListener(item -> {
                setupAndShowAddUserPopup();
                return true;
            });
        }
        if(hasPrivileg(Bill.BillPrivilege.WRITE))
            menu.getMenu().add("Posten hinzuf??gen").setOnMenuItemClickListener(item -> {
                addPostenLayout.show();
                return true;
            });
        if(hasPrivileg(Bill.BillPrivilege.READ)){
            menu.getMenu().add("Benutzer anzeigen").setOnMenuItemClickListener(item -> {
                billUsersLayout.show();
                return true;
            });
        }

        menu.getMenu().add("Gesamt??bersicht anzeigen").setOnMenuItemClickListener(item -> {
            summaryLayout.show();
            return true;
        });

        menu.getMenu().add("Eigene ??bersicht anzeigen").setOnMenuItemClickListener(item -> {
            ownSummaryLayout.show();
            return true;
        });

        // Listener hinzuf??gen

        thisView.findViewById(R.id.ms_bill_info_btn_open_info).setOnClickListener(v -> menu.show());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupAndShowAddUserPopup() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View popupView = inflater.inflate(R.layout.ms_bill_info_add_user, null);

        if(hasPrivileg(Bill.BillPrivilege.CONFIGURE))
            popupView.findViewById(R.id.ms_bill_info_add_user_priv_configure).setVisibility(View.VISIBLE);

        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(thisView, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });

        popupView.findViewById(R.id.ms_bill_info_add_user_btn_break).setOnClickListener(v -> {
            popupWindow.dismiss();
        });

        popupView.findViewById(R.id.ms_bill_info_add_user_btn).setOnClickListener(v -> {
            addUserToBill(popupView, popupWindow);
        });

    }

    private void addUserToBill(View popupView, final PopupWindow popupWindow) {
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
        SeeServerRequests.addUserToBill(currentBill.getName(), name, privString, response -> {
            if(response.isSuccessful()) {
                makeToast("Der Nutzer wurde erfolgreich hinzugef??gt.");
                currentBill.addUser(new Bill.BillUser(name, Bill.BillPrivilege.fromString(finalPrivString)));
                popupWindow.dismiss();
            }
            else makeToast(response.getMessage());
        }, error -> {
            popupWindow.dismiss();
        });
    }

    protected boolean hasPrivileg(Bill.BillPrivilege priv) {
        return super.hasPrivileg(priv, currentBill);
    }
}
