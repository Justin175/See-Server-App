package com.treulieb.worktimetool.layout;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.textview.MaterialTextView;
import com.treulieb.worktimetool.R;
import com.treulieb.worktimetool.ViewManager;
import com.treulieb.worktimetool.req.SeeServerRequests;

public class RechnungenLayout extends BaseLayout<ListView> {

    private ArrayAdapter<String> rechnungenNamesAdapter;

    private BillInfoLayout layoutBillInfo;

    public RechnungenLayout(Activity activity, ViewManager viewManager, ListView thisView, View parentView) {
        super(activity, viewManager, thisView, parentView);
    }

    @Override
    protected void setup() {
        //setup childs
        layoutBillInfo = new BillInfoLayout(activity, viewManager, findViewById(R.id.ms_bill_info), thisView);

        // setup adapter
        rechnungenNamesAdapter = new ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item);
        thisView.setAdapter(rechnungenNamesAdapter);

        thisView.setOnItemClickListener((parent, view, position, id) -> {
            String rechnungsName = ((MaterialTextView) view).getText().toString();
            SeeServerRequests.getBill(rechnungsName, response -> {
                if(response.isSuccessful()) {
                    layoutBillInfo.setBillInfos(response.getBill());
                    layoutBillInfo.show();
                }
            }, error -> makeErrorToast("Laden der Rechnungsinformationen"));
        });
    }

    @Override
    protected void onShowListener() {
        SeeServerRequests.getBills(response -> {
            if(response.isSuccessful()){
                rechnungenNamesAdapter.clear();
                rechnungenNamesAdapter.addAll(response.getBillNames());
            }
        }, error -> makeErrorToast("Laden der Rechnungen"));
    }
}
