package com.treulieb.worktimetool.layout;

import android.app.Activity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;
import com.treulieb.worktimetool.data.Bill;
import com.treulieb.worktimetool.data.Posten;
import com.treulieb.worktimetool.req.SeeServerRequests;

import java.util.List;

public class OwnSummaryLayout extends BaseLayout<ScrollView> {

    private BillInfoLayout billInfoLayout;
    private Bill currentBill;

    public OwnSummaryLayout(Activity activity, ViewManager viewManager, ScrollView thisView, BillInfoLayout parentView) {
        super(activity, viewManager, thisView, parentView);

        this.billInfoLayout = parentView;
    }

    @Override
    protected void setup() {

    }

    @Override
    protected void onShowListener() {
        currentBill = billInfoLayout.getCurrentBill();

        setInfos();
    }

    private void setInfos() {
        List<Posten> ownPaided = currentBill.getPostenPaidedFrom(SeeServerRequests.getNAME());
        List<Posten> stillToPay = currentBill.getToPayPosten(SeeServerRequests.getNAME());

        float outcome = (float) ownPaided.stream().mapToDouble(Posten::getCosts).sum();
        float toPay = (float) stillToPay.stream().mapToDouble(Posten::getCosts).sum();

        ((TextView) thisView.findViewById(R.id.ms_bill_info_own_summary_outcome)).setText(outcome + " €");
        ((TextView) thisView.findViewById(R.id.ms_bill_info_own_summary_outcome_open)).setText(outcome + " €");
        ((TextView) thisView.findViewById(R.id.ms_bill_info_own_summary_paided)).setText(toPay + " €");
        ((TextView) thisView.findViewById(R.id.ms_bill_info_own_summary_paided_open)).setText(toPay + " €");
    }
}
