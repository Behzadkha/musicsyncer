package com.musicsyncer.behzad.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    //variables
    Button btnOnOff, btnSend, musicpicker, tryagain, btnDiscover;
    ImageButton pause;
    ImageView tic;
    ListView listView;
    TextView read_msg_box, connectionStatus, bothdevices, scroll;
    pl.droidsonroids.gif.GifImageView pointerone;
    pl.droidsonroids.gif.GifImageView pointertwo;
    pl.droidsonroids.gif.GifImageView pointerthree;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    static final int MESSAGE_READ = 1;
    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;

    public Uri audioUri;
    String passer = "passer";
    //public Socket msocket;
    public Timer t;
    public MediaPlayer nplayer;
    public boolean clicked = false;
    public String url;
    WebView webplayer;
    public static int currentvolume;
    boolean playing = false;
    boolean firsttimeused = false;
    public int counter = 0;
    boolean initialized = false;
    AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialWork();
        exqListener();
        pause.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.INVISIBLE);
        scroll.setVisibility(View.INVISIBLE);
        pointerone.setVisibility(View.VISIBLE);
        pointertwo.setVisibility(View.INVISIBLE);
        pointerthree.setVisibility(View.INVISIBLE);
        webplayer.setVisibility(View.INVISIBLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        currentvolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);



    }
    //gets the message from another phone
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    url = popup.getUrl();
                    audioUri = popup.getaudiouri();
                    byte[] readBuff = (byte[]) msg.obj;
                    final String tempMsg = new String(readBuff, 0, msg.arg1);
                    read_msg_box.setText(tempMsg);
                    //if the other phone is playing, start playing
                    if(tempMsg.equals("Playing")){
                        if(audioUri != null){
                            player();
                            read_msg_box.setText("playing");
                        }
                        else if(url != null){
                            playyoutube();
                            pointerthree.setVisibility(View.INVISIBLE);
                            read_msg_box.setText("playing");
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Select a music first and try again", Toast.LENGTH_SHORT).show();
                        }

                    }
                    //if the other phone is clicked on pause
                    else if(tempMsg.equals("Pause")){
                        playing = false;
                        if(audioUri != null){
                            nplayer.pause();
                        }
                        else if(url != null){
                            mute(MainActivity.this);
                            webplayer.setVisibility(View.INVISIBLE);
                            webplayer.clearCache(true);
                        }

                    }
                    //if the other phone is clicked on pause
                    else if(tempMsg.equals("Resume")){
                        try{
                            nplayer.seekTo(nplayer.getCurrentPosition());
                            nplayer.start();
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                    //if the other phone is clicked on pause
                    else if(tempMsg.equals("Music Selected") && counter >=2){
                        if(audioUri != null && initialized){
                            nplayer.stop();
                            nplayer.seekTo(0);
                        }
                    }
                    break;
            }
            return true;
        }
    });

    private void exqListener() {
        //on and off button
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioUri != null){
                    if(nplayer.isPlaying()){
                        Toast.makeText(MainActivity.this, "Pause the music first", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        wifiManager.setWifiEnabled(false);
                        wifiManager.setWifiEnabled(true);
                        Toast.makeText(MainActivity.this, "Wait a moment!!", Toast.LENGTH_SHORT).show();
                        listView.setVisibility(View.VISIBLE);
                        scroll.setVisibility(View.VISIBLE);
                        btnSend.setVisibility(View.VISIBLE);
                        btnSend.setElevation(30);
                        btnSend.setAlpha(1);
                        pause.setVisibility(View.INVISIBLE);
                        pause.setElevation(10);
                        tryagain.setVisibility(View.VISIBLE);
                        read_msg_box.setText("Waiting");
                        bothdevices.setVisibility(View.VISIBLE);
                        audioUri = null;
                        tic.setVisibility(View.INVISIBLE);
                        pointerthree.setVisibility(View.VISIBLE);
                        tryagain.setVisibility(View.VISIBLE);
                        nplayer.stop();
                        nplayer.seekTo(0);
                    }

                }else {
                    Toast.makeText(MainActivity.this, "Nothing has changed to restart", Toast.LENGTH_SHORT).show();
                }

                   // webplayer.setVisibility(View.INVISIBLE);
                    //btnOnOff.setText("OFF");

            }
        });
        //Discovers other devices
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listView.setVisibility(View.VISIBLE);
                scroll.setVisibility(View.VISIBLE);
                pointerone.setVisibility(View.INVISIBLE);
                pointertwo.setVisibility(View.VISIBLE);


                /////////////////////////

                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionStatus.setText("Discovery started");
                        listView.setVisibility(View.VISIBLE);
                        scroll.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(int reason) {
                        connectionStatus.setText("Discovery stoped");
                        listView.setVisibility(View.VISIBLE);
                        scroll.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        //show other devices
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                pointertwo.setVisibility(View.INVISIBLE);
                pointerthree.setVisibility(View.VISIBLE);

                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Connected to" + device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    //choose music from phone
        musicpicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(playing == false){
                    audioUri = null;
                    url = null;
                    counter ++;
                    Intent pop = new Intent(MainActivity.this, popup.class);
                    startActivity(pop);
                    if(counter>=2){
                        try{
                            String msg = "Music Selected";
                            sendReceive.write(msg.getBytes());
                            read_msg_box.setText(msg);

                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "Can't change music, not connected", Toast.LENGTH_SHORT).show();
                        }
                    }


                    //Intent pickAudioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    // startActivityForResult(pickAudioIntent, 1);
                    bothdevices.setVisibility(View.INVISIBLE);
                    pointerthree.setVisibility(View.INVISIBLE);
                    pointerthree.setAlpha((float) 0.00001);

                }
                else{
                    Toast.makeText(MainActivity.this, "Pause the music first", Toast.LENGTH_SHORT).show();
                }


            }
        });
        //start button
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioUri = popup.getaudiouri();
                url = popup.getUrl();
                if(audioUri != null && read_msg_box.getText().equals("Music Selected") && counter >=2 && initialized){
                    final String msg = "Playing";
                    nplayer.stop();
                    nplayer.seekTo(0);
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    btnSend.setVisibility(View.INVISIBLE);
                    read_msg_box.setText(msg);
                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Not connected yet", Toast.LENGTH_SHORT).show();

                    }
                    try {
                        Thread.sleep(234);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    player();
                }
                else if(audioUri != null && read_msg_box.getText().equals("Music Selected") && counter >=2){
                    final String msg = "Playing";
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    btnSend.setVisibility(View.INVISIBLE);
                    read_msg_box.setText(msg);
                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Not connected yet", Toast.LENGTH_SHORT).show();

                    }
                    try {
                        Thread.sleep(234);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    player();
                }

                else if(audioUri != null && read_msg_box.getText().equals("Waiting")){
                    final String msg = "Playing";
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    btnSend.setVisibility(View.INVISIBLE);
                    read_msg_box.setText(msg);
                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Not connected yet", Toast.LENGTH_SHORT).show();
                    }
                    try {
                        Thread.sleep(234);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    player();

                    read_msg_box.setText("playing");
                }

                else if(read_msg_box.getText().equals("Pause")){
                    btnSend.setVisibility(View.INVISIBLE);
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    if(audioUri != null){
                        try{
                            playing = true;
                            nplayer.seekTo(nplayer.getCurrentPosition());
                            nplayer.start();
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "Can't tryagain", Toast.LENGTH_SHORT).show();
                        }

                        final String msge = "Resume";
                        read_msg_box.setText(msge);
                        try{
                            sendReceive.write(msge.getBytes());
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else if(url != null){
                        //currentvolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        //unmute(MainActivity.this);
                        final String msgee = "Playing";
                        pause.setVisibility(View.VISIBLE);
                        pause.setElevation(30);
                        btnSend.setVisibility(View.INVISIBLE);
                        read_msg_box.setText(msgee);
                        try{
                            sendReceive.write(msgee.getBytes());
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "Not connected yet", Toast.LENGTH_SHORT).show();
                        }
                    /*
                    try {
                        Thread.sleep(235);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                        listView.setVisibility(View.INVISIBLE);
                        scroll.setVisibility(View.INVISIBLE);
                        playyoutube();
                    }
                }
                else if (url != null){
                    //currentvolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                   // unmute(MainActivity.this);
                    final String msgee = "Playing";
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    btnSend.setVisibility(View.INVISIBLE);
                    read_msg_box.setText(msgee);
                    try{
                        sendReceive.write(msgee.getBytes());
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Not connected yet", Toast.LENGTH_SHORT).show();
                    }
                    try {
                        Thread.sleep(235);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    playyoutube();
                }
                else if(audioUri!=null && read_msg_box.getText().equals("Resume")){
                    if(nplayer.isPlaying()) {
                        pause.setVisibility(View.VISIBLE);
                        pause.setElevation(30);
                        btnSend.setVisibility(View.INVISIBLE);
                    }
                }

                else {
                    Toast.makeText(MainActivity.this, "Select a music first.", Toast.LENGTH_SHORT).show();
                }

                //sendReceive.write(msg.getBytes());
                clicked = true;


            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioUri != null){
                    playing = false;
                    nplayer.pause();
                    final String msg = "Pause";
                    read_msg_box.setText(msg);

                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_SHORT).show();
                        pause.setVisibility(View.INVISIBLE);
                        btnSend.setElevation(30);
                        btnSend.setVisibility(View.VISIBLE);
                    }

                    btnSend.setElevation(30);
                    btnSend.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.INVISIBLE);
                    clicked = false;
                }
                else if(url != null){
                    webplayer.setVisibility(View.INVISIBLE);
                    webplayer.clearCache(true);
                    final String msg = "Pause";
                    read_msg_box.setText(msg);
                    mute(MainActivity.this);

                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_SHORT).show();
                        pause.setVisibility(View.INVISIBLE);
                        btnSend.setElevation(30);
                        btnSend.setVisibility(View.VISIBLE);
                    }

                    btnSend.setElevation(30);
                    btnSend.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.INVISIBLE);
                    clicked = false;
                }

            }
        });
        tryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioUri = popup.getaudiouri();
                if(audioUri != null && clicked == false){
                    clicked = true;
                    final String msg = "Playing";
                    nplayer.stop();
                    nplayer.seekTo(0);
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    btnSend.setVisibility(View.INVISIBLE);
                    read_msg_box.setText(msg);
                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Not connected yet", Toast.LENGTH_SHORT).show();

                    }
                    try {
                        Thread.sleep(234);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    player();
                }
                else if(url != null){
                    Toast.makeText(MainActivity.this, "Sorry try again does not work with youtube video.", Toast.LENGTH_SHORT).show();
                }
                else if(clicked){
                    Toast.makeText(MainActivity.this, "Pause the song and try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {

                //the selected audio.
                //audioUri = data.getData();
                tic.setVisibility(View.VISIBLE);
                pointerthree.setVisibility(View.INVISIBLE);
                pointerthree.setAlpha((float) 0.00000001);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initialWork() {
        btnOnOff = (Button) findViewById(R.id.onOff);
        btnDiscover = findViewById(R.id.discover);
        btnSend = (Button) findViewById(R.id.sendButton);
        listView = (ListView) findViewById(R.id.peerListView);
        read_msg_box = (TextView) findViewById(R.id.readMsg);
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);
        musicpicker = (Button) findViewById(R.id.musicpicker);
        pause = (ImageButton) findViewById(R.id.stop);
        tryagain = (Button) findViewById(R.id.resume);
        bothdevices = (TextView) findViewById(R.id.textView3);
        tic = (ImageView) findViewById(R.id.imageView3);
        pointerone = findViewById(R.id.pointer);
        pointertwo = findViewById(R.id.pointer1);
        pointerthree = findViewById(R.id.pointer2);
        webplayer = findViewById(R.id.player);
        scroll = findViewById(R.id.textView13);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mReceiver = new WiFiDirectBroadCastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }
    //devices
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;
                int i = 1;
                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    deviceNameArray[index] = i + ". " + device.deviceName;
                    deviceArray[index] = device;
                    index++;
                    i++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                listView.setAdapter(adapter);
            }
            if (peers.size() == 0) {
                Toast.makeText(MainActivity.this, "No Device found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };
    //server class
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnderAddress = wifiP2pInfo.groupOwnerAddress;
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                connectionStatus.setText("Host");

                serverClass = new ServerClass();
                serverClass.start();
                btnSend.setEnabled(true);
                btnSend.setAlpha(1);
                pause.setEnabled(true);
                pause.setAlpha((float) 0.999);
                listView.setVisibility(View.INVISIBLE);
                scroll.setVisibility(View.INVISIBLE);
                pointerone.setVisibility(View.INVISIBLE);
                pointertwo.setVisibility(View.INVISIBLE);
                pointerthree.setVisibility(View.VISIBLE);

    //client class
            } else if (wifiP2pInfo.groupFormed) {
                connectionStatus.setText("Client");

                clientClass = new ClientClass(groupOwnderAddress);
                clientClass.start();

                btnSend.setEnabled(false);
                btnSend.setAlpha((float) 0.5);
                pause.setEnabled(false);
                pause.setAlpha((float) 0.5);
                tryagain.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.INVISIBLE);
                scroll.setVisibility(View.INVISIBLE);
                pointerone.setVisibility(View.INVISIBLE);
                pointertwo.setVisibility(View.INVISIBLE);
                pointerthree.setVisibility(View.VISIBLE);

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendReceive extends Thread {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt) {
            socket = skt;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(final byte[] bytes) {
            Thread m = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            m.start();

        }
    }

    public class ClientClass extends Thread {
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress) {
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final Runnable updateUI = new Runnable() {
        public void run() {
            try {
                //update ur ui here
                //pass.setText((nplayer.getCurrentPosition()/nplayer.getDuration())*100);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private final Handler music = new Handler();

    public void player() {
        playing = true;
        initialized = true;
        Thread aeq = new Thread(new Runnable() {
            @Override
            public void run() {
                
                    nplayer = new MediaPlayer();
                    try {
                        nplayer.setDataSource(MainActivity.this, audioUri);
                        nplayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                nplayer.start();
            }
        });aeq.start();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(audioUri != null){
                nplayer.stop();
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    public void playyoutube(){
        unmute(MainActivity.this);
        webplayer.setVisibility(View.VISIBLE);
        webplayer.clearCache(true);
        webplayer.setWebViewClient(new WebViewClient());
        webplayer.getSettings().setJavaScriptEnabled(true);
        webplayer.getSettings().setDomStorageEnabled(true);
        webplayer.loadUrl(url);
        Toast.makeText(this, "Click on tap to unmute", Toast.LENGTH_SHORT).show();
    }
    public static void mute(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int mute_volume = 0;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mute_volume, 0);
    }
    public static void unmute(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
    }






}




