package com.treulieb.worktimetool.layout;

import android.app.Activity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;
import com.treulieb.worktimetool.data.Bill;
import com.treulieb.worktimetool.req.SeeServerRequests;

import java.util.function.Consumer;

public abstract class BaseLayout<T extends View> {

    protected final Activity activity;
    protected final ViewManager viewManager;
    protected final T thisView;
    protected final View parentView;

    public BaseLayout(Activity activity, ViewManager viewManager, T thisView, BaseLayout parentView) {
        this(activity, viewManager, thisView, parentView.getThisView(), null, null);
    }

    public BaseLayout(Activity activity, ViewManager viewManager, T thisView, View parentView) {
        this(activity, viewManager, thisView, parentView, null, null);
    }

    public BaseLayout(Activity activity, ViewManager viewManager, T thisView, View parentView, @Nullable Runnable onShowListener, @Nullable Consumer<View> onChangeListener) {
        this.activity = activity;
        this.viewManager = viewManager;
        this.thisView = thisView;
        this.parentView = parentView;

        viewManager.addView(thisView, parentView, onShowListener == null ? this::onShowListener : onShowListener, onChangeListener == null ? this::onChangeListener : onChangeListener);
        setup();
    }

    protected void onShowListener() {

    }

    protected void onChangeListener(View view) {

    }

    protected abstract void setup();

    public void show() {
        viewManager.openView(thisView);
    }

    public void showParent() {
        if(parentView != null)
            viewManager.openView(viewManager.getCurrentParent());
    }

    public T getThisView() {
        return thisView;
    }

    public View getParentView() {
        return parentView;
    }

    public Activity getActivity() {
        return activity;
    }

    public ViewManager getViewManager() {
        return viewManager;
    }

    protected  <T extends View> T findViewById(@IdRes int id) {
        return activity.findViewById(id);
    }

    protected boolean hasPrivileg(Bill.BillPrivilege priv, Bill bill) {
        Bill.BillUser thisUser = bill.getUser(SeeServerRequests.getNAME());
        return thisUser.hasPrivilige(priv);
    }

    protected void makeErrorToast(String errorCategorieStr) {
        makeToast("Ein Fehler beim " + errorCategorieStr + " ist aufgetreten.");
    }

    protected void makeToast(String text) {
        Toast.makeText(this.activity, text, Toast.LENGTH_LONG).show();
    }

    protected SpannableString createColoredString(@ColorInt int color, String str) {
        SpannableString txt = new SpannableString(str);
        txt.setSpan(new ForegroundColorSpan(color), 0, str.length(), 0);
        return txt;
    }
}
