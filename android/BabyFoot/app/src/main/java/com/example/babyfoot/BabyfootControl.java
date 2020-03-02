package com.example.babyfoot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

/*
cette classe permet le controle du module arduino (start, reset) et récupère les résultats
envoyés par l'arduino
 */

public class BabyfootControl extends Activity {

    Button Start, Reset, Deco;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    DataInputStream BTdata;
    TextView scores;
    AsyncTask myAscTask;



    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public int scoreB, scoreR;
    public UserMatchDao MatchDao;

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

        new ConnectBT().execute();

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


    }
    @Override
    protected void onDestroy(){
        Disconnect();
        super.onDestroy();
    }
    private void Disconnect()
    {
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
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("s".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
            if( myAscTask!=null && myAscTask.getStatus()==AsyncTask.Status.RUNNING){
                myAscTask.cancel(true);
            }
            startBTreception();

        }

    }

    private void Reset()
    {
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
            if(myAscTask!=null && myAscTask.getStatus()==AsyncTask.Status.RUNNING){
                myAscTask.cancel(true);
            }
            startBTreception();
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       // getMenuInflater().inflate(R.menu.menu_babyfoot_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


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

    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    private void startBTreception(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Start.setActivated(false);
                scoreB = scoreR = 0;
                while(true) {
                    byte[] buffer = new byte[256];
                    int bytes;
                    try {
                        BTdata = new DataInputStream(btSocket.getInputStream());
                        bytes = BTdata.read(buffer);
                        String readMessage = new String(buffer, 0, bytes);
                        if(readMessage.contains("f")){
                            setText(scores, readMessage);
                            MatchDao.insert(new UserMatch(0, scoreB, scoreR, 60)); //bleu - rouge
                            break;
                        }
                        else if(readMessage.contains("b")){
                            scoreB++;
                        }
                        else if(readMessage.contains("r")){
                            scoreR++;
                        }
                        setText(scores,"Bleus :"+ scoreB+" - Rouges : "+scoreR);
                        Log.d("BabyfootControl", readMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}