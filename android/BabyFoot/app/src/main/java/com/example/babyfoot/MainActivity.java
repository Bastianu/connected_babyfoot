package com.example.babyfoot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

import static android.support.constraint.Constraints.TAG;


public class MainActivity extends Activity {

    static final UUID myUUID = UUID.randomUUID();

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        final Button discover = (Button) findViewById(R.id.b_discover);
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(MainActivity.this,DeviceList.class);
                myIntent.putExtra("UUID",myUUID.toString());
                startActivity(myIntent);
            }
        });

        final Button historique = (Button) findViewById(R.id.b_historique);
        historique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(MainActivity.this,ListingMatch.class);
                startActivity(myIntent);
            }
        });

        final Button tournoi = (Button) findViewById(R.id.b_tournoi);
        tournoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(MainActivity.this,Tournoi.class);
                startActivity(myIntent);
            }
        });


        final Button startMatch = (Button) findViewById(R.id.b_start);
        startMatch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startMatch();
            }
        });

        final Button resetBBFoot = (Button) findViewById(R.id.b_reset);
        resetBBFoot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reset();
            }
        });




        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        TextView textView = (TextView) findViewById(R.id.idPhone);
        //textView.setText(myUUID.toString());
        textView.setText(Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
    }



    public void startMatch(){
        TextView textView = (TextView) findViewById(R.id.idPhone);
        textView.setText("match lancé");

    }

    public void reset(){
        TextView textView = (TextView) findViewById(R.id.idPhone);
        textView.setText("reset");
    }



}
