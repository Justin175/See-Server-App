package com.treulieb.worktimetool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.treulieb.worktimetool.req.HttpRequest;
import com.treulieb.worktimetool.req.SeeServerRequests;

public class MainActivity extends AppCompatActivity {

    public static boolean __DEBUG = true;

    private boolean stepLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.weiter);
        btn.setEnabled(false);
        btn.setBackgroundColor(Color.argb(255, 255, 0, 0));

        EditText serverIpView = findViewById(R.id.server_ip);
        serverIpView.setHint("https://<IP>:<PORT>");

        // PW und LoginName
        EditText loginName = findViewById(R.id.login_name);
        EditText password = findViewById(R.id.password);
        TextView loginText3 = findViewById(R.id.textView3);
        TextView loginText4 = findViewById(R.id.textView4);

        loginName.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        loginText3.setVisibility(View.INVISIBLE);
        loginText4.setVisibility(View.INVISIBLE);

        if(__DEBUG) {
            serverIpView.setText("http://92.201.106.37:25561");
            btn.setEnabled(true);
        }

        HttpRequest.setup(this);
        SeeServerRequests.setActivity(this);

        serverIpView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btn.setBackgroundColor(s.length() == 0 ? Color.argb(255, 255, 0, 0) : Color.argb(255, 0, 255, 0));
                btn.setEnabled(s.length() > 0);
            }
        });

        btn.setOnClickListener(action -> {
            if (stepLogin) {
                // check login
                SeeServerRequests.login(loginName.getText().toString(), password.getText().toString(), response -> {
                    boolean successful = response.isSuccessful();
                    System.out.println(successful);
                    if(successful) {
                        ((EditText) findViewById(R.id.password)).setText("");
                        startActivity(new Intent(MainActivity.this, MainSite.class));
                    }
                }, error -> Toast.makeText(this, "Ein Fehler beim Anmelden ist aufgetreten.", Toast.LENGTH_LONG).show());
                return;
            }

            SeeServerRequests.getVersion(serverIpView.getText().toString().trim(), response -> {
                String version = response.getVersion();
                System.out.println(version);

                if (version != null && version.startsWith("See")) {
                    SeeServerRequests.setURL(serverIpView.getText().toString().trim());
                    serverIpView.setEnabled(false);

                    loginName.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    loginText3.setVisibility(View.VISIBLE);
                    loginText4.setVisibility(View.VISIBLE);

                    stepLogin = true;
                }
            }, error -> Toast.makeText(this, "Unbekannte URL", Toast.LENGTH_LONG).show());
        });
    }
}