package com.treulieb.worktimetool.layout;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;

public class AddLayout extends BaseLayout<ConstraintLayout> {

    private AddUserLayout addUserLayout;
    private AddBillLayout addBillLayout;

    public AddLayout(Activity activity, ViewManager viewManager, ConstraintLayout thisView, View parentView) {
        super(activity, viewManager, thisView, parentView);
    }

    @Override
    protected void setup() {
        addUserLayout = new AddUserLayout(activity, viewManager, activity.findViewById(R.id.ms_add_user), thisView);
        addBillLayout = new AddBillLayout(activity, viewManager, activity.findViewById(R.id.ms_add_bill), thisView);

        ((Button) findViewById(R.id.ms_btn_add_nutzer)).setOnClickListener(x -> {
            addUserLayout.show();
        });

        ((Button) findViewById(R.id.ms_btn_add_rechnung)).setOnClickListener(x -> {
            addBillLayout.show();
        });
    }
}
