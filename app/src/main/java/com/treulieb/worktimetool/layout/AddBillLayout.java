package com.treulieb.worktimetool.layout;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;
import com.treulieb.worktimetool.req.SeeServerRequests;

public class AddBillLayout extends BaseLayout<ConstraintLayout> {

    public AddBillLayout(Activity activity, ViewManager viewManager, ConstraintLayout thisView, View parentView) {
        super(activity, viewManager, thisView, parentView);
    }

    @Override
    protected void setup() {
        ((Button) findViewById(R.id.ms_add_bill_create)).setOnClickListener(v -> {
            String billName = ((EditText) findViewById(R.id.ms_add_bill_billname)).getText().toString().trim();

            if(billName.length() > 0) {
                SeeServerRequests.createBill(billName, response -> {
                    if(response.isSuccessful()) {
                        showParent();
                    }
                }, error -> makeErrorToast("Erstellen einer Rechnung"));
            }
        });
    }

    @Override
    protected void onShowListener() {
        ((EditText) findViewById(R.id.ms_add_bill_billname)).setText("");
    }
}
