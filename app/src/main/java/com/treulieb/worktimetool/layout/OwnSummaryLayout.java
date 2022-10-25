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

import java.math.BigDecimal;
import java.util.List;

import static com.treulieb.worktimetool.utils.MathUtils.decimal;

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
        List<Posten> ownPaid = currentBill.getPostenPaidedFrom(SeeServerRequests.getNAME());
        List<Posten> stillToPay = currentBill.getToPayPosten(SeeServerRequests.getNAME());

        BigDecimal outcome = ownPaid.stream().map(Posten::getCosts).reduce(decimal(0), BigDecimal::add);
        BigDecimal toPay = stillToPay.stream().filter(posten -> !posten.getCreator().equals(SeeServerRequests.getNAME())).map(posten -> posten.getPartialCosts(currentBill.getPart(posten))).reduce(decimal(0), BigDecimal::add);

        ((TextView) thisView.findViewById(R.id.ms_bill_info_own_summary_outcome)).setText(outcome + " €");
        ((TextView) thisView.findViewById(R.id.ms_bill_info_own_summary_outcome_open)).setText(outcome + " €");
        ((TextView) thisView.findViewById(R.id.ms_bill_info_own_summary_paided)).setText(toPay + " €");
        ((TextView) thisView.findViewById(R.id.ms_bill_info_own_summary_paided_open)).setText(toPay + " €");
    }
}
