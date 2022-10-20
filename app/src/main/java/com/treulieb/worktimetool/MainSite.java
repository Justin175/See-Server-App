package com.treulieb.worktimetool;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.treulieb.worktimetool.data.Bill;
import com.treulieb.worktimetool.data.Posten;
import com.treulieb.worktimetool.layout.AddLayout;
import com.treulieb.worktimetool.layout.BillInfoLayout;
import com.treulieb.worktimetool.layout.RechnungenLayout;
import com.treulieb.worktimetool.req.SeeServerRequests;
import com.treulieb.worktimetool.utils.ViewListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class MainSite extends AppCompatActivity {

    private RechnungenLayout layoutRechnungen;
    private AddLayout layoutAdd;

    private ViewManager viewManager = new ViewManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_site);
        SeeServerRequests.setActivity(this);

        TabLayout tabLayout = findViewById(R.id.ms_tabs);

        // Views Setzen
        setupViews();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String option = tab.getText().toString().toLowerCase();

                if(option.equals("rechnungen")) {
                    layoutRechnungen.show();
                }
                else if(option.equals("hinzufügen")) {
                    layoutAdd.show();
                }
                else if(option.equals("einstellungen")) {

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // default startview
        // viewManager.openView(v_rechnungen);
        layoutRechnungen.show();
    }

    @Override
    public void onBackPressed() {
        View parentView;
        if((parentView = viewManager.getCurrentParent()) != null) {
            viewManager.openView(parentView);
            return;
        }

        SeeServerRequests.logout(response -> {}, error -> makeErrorToast("Ausloggen"));
        super.onBackPressed();
    }

    private void setupViews() {
        layoutRechnungen = new RechnungenLayout(this, viewManager, findViewById(R.id.ms_rechnungen), null);
        layoutAdd = new AddLayout(this, viewManager, findViewById(R.id.ms_add), null);
    }

    private SpannableString createColoredString(@ColorInt int color, String str) {
        SpannableString txt = new SpannableString(str);
        txt.setSpan(new ForegroundColorSpan(color), 0, str.length(), 0);
        return txt;
    }

    private <T> boolean checkIfContains(Iterator<T> iter, T toSearch) {
        Holder<T> holder = new Holder<>(null);
        while(iter.hasNext()) {
            if(iter.next().equals(toSearch))
                return true;
        }

        return false;
    }

    private class Holder<T> {
        public T content;

        public Holder(T defaultContent) {
            content = defaultContent;
        }
    }

    private void makeErrorToast(String errorCategorieStr) {
        makeToast("Ein Fehler beim " + errorCategorieStr + " ist aufgetreten.");
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}