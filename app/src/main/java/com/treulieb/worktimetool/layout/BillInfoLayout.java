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

public class BillInfoLayout extends BaseLayout<ConstraintLayout> {

    private Bill currentBill;
    private boolean[] billMarkedUseres;

    private AddPostenLayout addPostenLayout;

    public BillInfoLayout(Activity activity, ViewManager viewManager, ConstraintLayout thisView, View parentView) {
        super(activity, viewManager, thisView, parentView);
    }

    @Override
    protected void setup() {
        addPostenLayout = new AddPostenLayout(activity, viewManager, activity.findViewById(R.id.ms_bill_info_add_posten), this);
    }

    public Bill getCurrentBill() {
        return currentBill;
    }

    private void deleteCurrentBill() {
        SeeServerRequests.deleteBill(((TextView) findViewById(R.id.ms_bill_info_billname)).getText().toString(), response -> {
            viewManager.openView(viewManager.getCurrentParent());
        }, error -> makeErrorToast("Löschen der Rechnung"));
    }

    public void setBillInfos(Bill bill) {
        String creator;
        currentBill = bill;

        ((TextView) findViewById(R.id.ms_bill_info_billname)).setText(bill.getName());
        ((TextView) findViewById(R.id.ms_bill_info_created)).setText(bill.getCreated());
        ((TextView) findViewById(R.id.ms_bill_info_creator)).setText(creator = bill.getCreator().getName());
        ((TextView) findViewById(R.id.ms_bill_info_costs_sum)).setText(bill.getCostsSum() + " €");

        // findViewById(R.id.ms_bill_info_btn_delete).setVisibility(SeeServerRequests.getNAME().equals(creator) ? View.VISIBLE : View.GONE);

        // if(bill.getUser(SeeServerRequests.getNAME()).hasPrivilige(Bill.BillPrivilege.WRITE))
        //    findViewById(R.id.ms_bill_info_btn_add_posten).setVisibility(View.VISIBLE);
        //else
        //   findViewById(R.id.ms_bill_info_btn_add_posten).setVisibility(View.GONE);

        ListView postenView = findViewById(R.id.ms_bill_info_posten);
        LayoutInflater inflater = LayoutInflater.from(activity);
        List<View> items = new LinkedList();

        for (Posten posten : bill.getPosten()) {
            View postenInfoView = inflater.inflate(R.layout.ms_bill_info_posten, null);
            ((TextView) postenInfoView.findViewById(R.id.ms_bill_info_posten_id)).setText(posten.getId());
            ((TextView) postenInfoView.findViewById(R.id.ms_bill_info_posten_title)).setText(posten.getTitle());
            ((TextView) postenInfoView.findViewById(R.id.ms_bill_info_posten_costs)).setText("" + posten.getCosts() + " €");

            items.add(postenInfoView);
        }

        postenView.setAdapter(new ViewListAdapter(activity, R.layout.support_simple_spinner_dropdown_item, items));
        postenView.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu menu = new PopupMenu(activity, view);
            String postenID = ((TextView) view.findViewById(R.id.ms_bill_info_posten_id)).getText().toString();

            menu.setOnMenuItemClickListener(item -> {
                if(item.getTitle().toString().contains("Löschen")){
                    SeeServerRequests.deletePosten(currentBill.getName(), postenID, response -> {
                        if(response.optBoolean("successful")) {
                            makeToast("Posten wurde erfolgreich gelöscht.");
                            SeeServerRequests.getBill(currentBill.getName(), response1 -> {
                                if(response1.isSuccessful()) {
                                    setBillInfos(response1.getBill());
                                }
                            }, error -> {});
                        }
                    }, error -> {});
                }
                return true;
            });

            Posten posten = bill.getPosten(postenID);

            menu.getMenu().add("Erstellt von " + posten.getCreator());
            menu.getMenu().add("Erstellt am " + posten.getCreated());
            if(hasPrivileg(Bill.BillPrivilege.WRITE))
                menu.getMenu().add(createColoredString(Color.RED, "Löschen"));
            menu.show();

            return true;
        });

        setupBillPopupMenu();
    }

    private void setupBillPopupMenu() {
        PopupMenu menu = new PopupMenu(activity, thisView.getViewById(R.id.ms_bill_info_btn_open_info));
        if(hasPrivileg(Bill.BillPrivilege.CONFIGURE)) {
            menu.getMenu().add(createColoredString(Color.RED, "Löschen")).setOnMenuItemClickListener(item -> {
                deleteCurrentBill();
                return true;
            });
            menu.getMenu().add("Benutzer hinzufügen").setOnMenuItemClickListener(item -> {
                setupAndShowAddUserPopup();
                return true;
            });
        }
        if(hasPrivileg(Bill.BillPrivilege.WRITE))
            menu.getMenu().add("Posten hinzufügen").setOnMenuItemClickListener(item -> {addPostenLayout.show(); return true;});

        // Listener hinzufügen

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

        SeeServerRequests.addUserToBill(currentBill.getName(), name, privString, response -> {
            if(response.isSuccessful()) {
                makeToast("Der Nutzer wurde erfolgreich hinzugefügt.");
                popupWindow.dismiss();
            }
            else makeToast(response.getMessage());
        }, error -> {
            popupWindow.dismiss();
        });
    }

    private boolean hasPrivileg(Bill.BillPrivilege priv){
        Bill.BillUser thisUser = currentBill.getUser(SeeServerRequests.getNAME());
        return thisUser.hasPrivilige(priv);
    }
}
