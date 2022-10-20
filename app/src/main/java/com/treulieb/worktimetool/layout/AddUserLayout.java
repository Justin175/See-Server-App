package com.treulieb.worktimetool.layout;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;
import com.treulieb.worktimetool.req.SeeServerRequests;

public class AddUserLayout extends BaseLayout<ConstraintLayout> {

    public AddUserLayout(Activity activity, ViewManager viewManager, ConstraintLayout thisView, View parentView) {
        super(activity, viewManager, thisView, parentView);
    }

    @Override
    protected void setup() {
        ((Button) findViewById(R.id.ms_add_user_btn_add)).setOnClickListener(x -> {
            doLogin();
        });
    }

    private void doLogin() {
        String name = ((EditText) findViewById(R.id.ms_add_user_name)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.ms_add_user_password)).getText().toString();
        String levelStr = ((EditText) findViewById(R.id.ms_add_user_level)).getText().toString();

        if(name.length() == 0)
            return;
        if(password.length() == 0)
            return;
        if(levelStr.length() == 0)
            return;

        int level = Integer.parseInt(levelStr);

        SeeServerRequests.createUser(name, password, level, response -> {
            if(response.isSuccessful()) {
                showParent();
            }
        }, error -> makeErrorToast("Erstellen des Nutzers"));
    }

    @Override
    protected void onShowListener() {
        ((EditText) findViewById(R.id.ms_add_user_name)).setText("");
        ((EditText) findViewById(R.id.ms_add_user_password)).setText("");
        ((EditText) findViewById(R.id.ms_add_user_level)).setText("");
    }
}
