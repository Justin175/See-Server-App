package com.treulieb.worktimetool.layout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;
import com.treulieb.worktimetool.data.Bill;
import com.treulieb.worktimetool.data.Posten;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BillInfoSummaryLayout extends BaseLayout<ConstraintLayout> {

    private BillInfoLayout infoLayout;
    private LayoutInflater inflater;
    private LinearLayout overviews;
    private Bill currentBill;

    private HashMap<String, UserOverview> overviewMap;

    public BillInfoSummaryLayout(Activity activity, ViewManager viewManager, ConstraintLayout thisView, BillInfoLayout parentView) {
        super(activity, viewManager, thisView, parentView);

        this.infoLayout = parentView;
    }

    @Override
    protected void setup() {
        this.inflater = activity.getLayoutInflater();
        this.overviews = thisView.findViewById(R.id.ms_bill_info_costs_summary_overviews);
    }

    @Override
    protected void onShowListener() {
        this.currentBill = this.infoLayout.getCurrentBill();
        this.overviews.removeAllViews();

        setupOverviewList();
        setupSummaryView();
    }

    private void setupOverviewList() {
        overviewMap = new HashMap<>();
        overviewMap.put(currentBill.getCreator().getName(), new UserOverview(currentBill.getCreator()));
        Arrays.stream(currentBill.getUsers()).map(UserOverview::new).forEach(userOverview -> overviewMap.put(userOverview.name, userOverview));

        for(com.treulieb.worktimetool.data.Posten posten : currentBill.getPosten()) {
            UserOverview ov = this.overviewMap.get(posten.getCreator());
            ov.addPosten(posten, currentBill);
        }
    }

    private void setupSummaryView() {
        overviewMap.values().forEach(this::createOverview);
    }

    private void createOverview(UserOverview user) {
        View overview = this.inflater.inflate(R.layout.ms_bill_info_costs_info, null);

        ((TextView) overview.findViewById(R.id.ms_bill_info_costs_name)).setText(user.name);
        ((TextView) overview.findViewById(R.id.ms_bill_info_costs_sum_all)).setText(user.sum + " €");
        ((TextView) overview.findViewById(R.id.ms_bill_info_costs_sum_open)).setText(user.open + " €");

        LinearLayout costsView = (LinearLayout) overview.findViewById(R.id.ms_bill_info_costs_from);
        for(FromPerson p : user.persons.values()) {
            View toPayView = inflater.inflate(R.layout.ms_bill_info_costs_info_user_info, null);

            ((TextView) toPayView.findViewById(R.id.user_name)).setText(p.name);
            ((TextView) toPayView.findViewById(R.id.open)).setText(p.open + " €");
            ((TextView) toPayView.findViewById(R.id.sum)).setText(p.sum + " €");

            costsView.addView(toPayView);
        }

        this.overviews.addView(overview);
    }

    private class UserOverview {
        String name;
        float sum = 0, open = 0;
        HashMap<String, FromPerson> persons;

        public UserOverview(Bill.BillUser user) {
            this.name = user.getName();
            this.persons = new HashMap<>();
        }

        public void addPosten(Posten posten, Bill bill) {
            // add to here
            this.sum += posten.getCosts();
            this.open += posten.getCosts();

            // add to users
            if(posten.isForAllUsers()){
                for(Bill.BillUser to : bill.getAllUsers(this.name)){
                    FromPerson person = this.persons.get(to.getName());

                    if(person == null) {
                        person = new FromPerson(to.getName(), posten, bill);
                        this.persons.put(person.name, person);
                    }
                    else {
                        updatePerson(person, posten, currentBill);
                    }
                }
            }
            else {
                for(String to : posten.getTo()) {
                    FromPerson person = this.persons.get(to);

                    if(person == null) {
                        person = new FromPerson(to, posten, bill);
                        this.persons.put(person.name, person);
                    }
                    else {
                        updatePerson(person, posten, currentBill);
                    }
                }
            }

        }

        private void updatePerson(FromPerson person, Posten posten, Bill bill){
            person.sum += (posten.isForAllUsers() ? posten.getCosts() / bill.getUsersCount() : posten.getCosts() / posten.getTo().length);
            person.open += (posten.isForAllUsers() ? posten.getCosts() / bill.getUsersCount() : posten.getCosts() / posten.getTo().length);
            // TODO: OPEN
        }
    }

    private class FromPerson {
        String name;
        float sum, open;
        com.treulieb.worktimetool.data.Posten posten;

        public FromPerson(String name, com.treulieb.worktimetool.data.Posten posten, Bill bill) {
            this.name = name;

            if(posten.isForAllUsers())
                this.sum = posten.getCosts() / bill.getUsersCount();
            else
                this.sum = posten.getCosts() / posten.getTo().length;

            this.open = sum;
            this.posten = posten;
        }
    }
}
