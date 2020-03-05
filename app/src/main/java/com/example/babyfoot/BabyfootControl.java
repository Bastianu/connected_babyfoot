package com.example.babyfoot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import static java.lang.Integer.parseInt;

/*
cette classe permet le controle du module arduino (start, reset) et récupère les résultats
envoyés par l'arduino
 */

public class BabyfootControl extends Activity {

    TimePicker tpsMatch;
    Button Start, Reset, Deco;
    Switch Equipe;
    CheckBox valider;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected, isLaunched = false;
    DataInputStream BTdata;
    TextView scores;
    AsyncTask myAscTask;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public volatile int scoreB, scoreR;
    public UserMatchDao MatchDao;
    public String side, time;
    public volatile boolean running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_babyfoot_control);
        MatchDao = AppDataBase.getDatabase(this.getApplicationContext()).UserMatchDao();

        //call the widgets
        Start = (Button)findViewById(R.id.b_start);
        Reset = (Button)findViewById(R.id.b_reset);
        Deco = (Button)findViewById(R.id.dis_btn);
        scores = (TextView) findViewById(R.id.match_direct);
        Equipe =  (Switch)findViewById(R.id.s_equipe);
        valider = (CheckBox)findViewById(R.id.cb_valider);
        tpsMatch = (TimePicker) findViewById(R.id.tps_match);
        tpsMatch.setIs24HourView(true);
        tpsMatch.setCurrentHour(0);


        //l'utilisateur choisit son équipe avant de passer aux commandes du module
        Start.setVisibility(View.GONE);
        Reset.setVisibility(View.GONE);

        //new ConnectBT().execute();

        Start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Start();
            }
        });
        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Reset();
            }
        });
        Deco.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect();
            }
        });

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start.setVisibility(View.VISIBLE);
                Reset.setVisibility(View.VISIBLE);
                valider.setVisibility(View.GONE);
                side = Equipe.isChecked() ? "r" : "b" ;
                time = tpsMatch.getCurrentMinute().toString();
                Equipe.setClickable(false);
                tpsMatch.setEnabled(false);
                scores.setText("en attente de lancement..");
            }
        });




        Equipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(Equipe.isChecked()){
                    Equipe.getThumbDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    Equipe.getTrackDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    Equipe.setTextColor(Color.RED);
                }
                else{
                    Equipe.getThumbDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                    Equipe.getTrackDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                    Equipe.setTextColor(Color.BLUE);
                }
            }
        });
        Equipe.getThumbDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        Equipe.getTrackDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        Equipe.setTextColor(Color.BLUE);


    }
    @Override
    protected void onDestroy(){
        Disconnect();
        super.onDestroy();
    }
    private void Disconnect()
    {
        running = false;
        if (btSocket!=null)
        {
            try
            {
                btSocket.close();
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //retour sur le choix de périphérique

    }

    private void Start()
    {
        isLaunched = true;
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(time.toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
            //startBTreception();

        }

    }

    private void Reset()
    {
        Start.setVisibility(View.GONE);
        Reset.setVisibility(View.GONE);
        valider.setChecked(false);
        valider.setVisibility(View.VISIBLE);
        Equipe.setClickable(true);
        tpsMatch.setEnabled(true);
        running = false;
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("r".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
            scoreB = scoreR = 0;
            scores.setText("match stoppé");
        }
        if(isLaunched &&side=="b") MatchDao.insert(new UserMatch(0, scoreB, scoreR, parseInt(time)));
        else if(isLaunched && side =="r") MatchDao.insert(new UserMatch(0, scoreR, scoreB, parseInt(time)));
        isLaunched = false;
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

/*
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(BabyfootControl.this, "Connecting...", "Veuillez patienter");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
*/
    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    private void startBTreception(){
        myAscTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("BabyfootControl", "le thread démarre");
                //Start.setClickable(false);
                while(running) {
                    byte[] buffer = new byte[256];
                    int bytes;
                    try {
                        BTdata = new DataInputStream(btSocket.getInputStream());
                        bytes = BTdata.read(buffer);
                        String readMessage = new String(buffer, 0, bytes);
                        if(readMessage.contains("f")){
                            setText(scores, "match terminé");
                            if(side=="b"){
                                MatchDao.insert(new UserMatch(0, scoreB, scoreR, parseInt(time)));
                                break;
                            }
                            else{
                                MatchDao.insert(new UserMatch(0, scoreR, scoreB, parseInt(time)));
                                break;
                            }
                        }
                        else if(readMessage.contains("b")){
                            scoreB++;
                        }
                        else if(readMessage.contains("r")){
                            scoreR++;
                        }
                        setText(scores,"Bleus : "+scoreB +" - Rouges :"+scoreR);
                        Log.d("BabyfootControl", readMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}