package com.musicsyncer.behzad.android;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
//Bluetooth class
public class Bluetooth extends AppCompatActivity {
    Button listen, musicpicker, tryagain, btnDiscover;
    ImageButton pause, send;
    ImageView tic;
    TextView msg_box, status, bothdevices, mess1,mess2, scroller;
    EditText writeMsg;
    BluetoothAdapter myBluetoothAdapter;
    BluetoothDevice[] btnArray;
    ListView listView;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    SendReceive sendReceive;
    public Uri audioUri;
    public MediaPlayer nplayer;
    public boolean clicked = false;

    pl.droidsonroids.gif.GifImageView pointerone;
    pl.droidsonroids.gif.GifImageView pointertwo;

    public String url;
    WebView webplayer;
    boolean playing = false;
    public int counter = 0;
    boolean initialized = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initialework();
        tic.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.INVISIBLE);
        scroller.setVisibility(View.INVISIBLE);
        pointertwo.setVisibility(View.INVISIBLE);
        webplayer.setVisibility(View.INVISIBLE);
        initialize();
        //request to enable bluetooth
        if(!myBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1);
        }

    }
    private void initialize(){
        //discover devices
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);
                scroller.setVisibility(View.VISIBLE);
                pointerone.setVisibility(View.INVISIBLE);
                pointertwo.setVisibility(View.VISIBLE);
                audioUri = null;
                url = null;
                Set<BluetoothDevice> bt = myBluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btnArray = new BluetoothDevice[bt.size()];
                int index = 0;
                if(bt.size() > 0){
                    for(BluetoothDevice device : bt){
                        btnArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointerone.setVisibility(View.INVISIBLE);
                pointertwo.setVisibility(View.VISIBLE);
                ServerClass serverClass = new ServerClass();
                serverClass.start();
                send.setAlpha((float) 0.5);
                send.setEnabled(false);
                pause.setVisibility(View.INVISIBLE);
                tryagain.setVisibility(View.INVISIBLE);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                ClientClass clientClass = new ClientClass(btnArray[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String string = String.valueOf(writeMsg.getText());
                //sendReceive.write(string.getBytes());
                audioUri = popup.getaudiouri();
                url = popup.getUrl();

                if(audioUri != null && msg_box.getText().equals("Music Selected") && counter >=2 && initialized){
                    final String msg = "Playing";
                    nplayer.stop();
                    nplayer.seekTo(0);
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    send.setVisibility(View.INVISIBLE);
                    msg_box.setText(msg);
                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(Bluetooth.this, "Not connected yet", Toast.LENGTH_SHORT).show();

                    }
                    try {
                        Thread.sleep(234);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    player();
                }
                else if(audioUri != null && msg_box.getText().equals("Music Selected") && counter >=2){
                    final String msg = "Playing";
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    send.setVisibility(View.INVISIBLE);
                    msg_box.setText(msg);
                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(Bluetooth.this, "Not connected yet", Toast.LENGTH_SHORT).show();

                    }
                    try {
                        Thread.sleep(234);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    player();
                }

                else if(audioUri != null && msg_box.getText().equals("Waiting")){
                    final String msg = "Playing";
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    send.setVisibility(View.INVISIBLE);
                    msg_box.setText(msg);
                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(Bluetooth.this, "Not connected yet", Toast.LENGTH_SHORT).show();
                    }
                    /*
                    try {
                        Thread.sleep(234);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    player();
                    msg_box.setText("playing");
                }
                else if(msg_box.getText().equals("Pause")){
                    send.setVisibility(View.INVISIBLE);
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    if(audioUri != null){
                        try{
                            playing = true;
                            nplayer.seekTo(nplayer.getCurrentPosition());
                            nplayer.start();
                        }catch (Exception e){
                            Toast.makeText(Bluetooth.this, "Can't tryagain", Toast.LENGTH_SHORT).show();
                        }

                        final String msge = "Resume";
                        msg_box.setText(msge);
                        try{
                            sendReceive.write(msge.getBytes());
                        }catch (Exception e){
                            Toast.makeText(Bluetooth.this, "Not connected", Toast.LENGTH_SHORT).show();

                        }
                    }
                    else if(url != null){
                        final String msgee = "Playing";
                        pause.setVisibility(View.VISIBLE);
                        pause.setElevation(30);
                        send.setVisibility(View.INVISIBLE);
                        msg_box.setText(msgee);
                        try{
                            sendReceive.write(msgee.getBytes());
                        }catch (Exception e){
                            Toast.makeText(Bluetooth.this, "Not connected yet", Toast.LENGTH_SHORT).show();
                        }
                    /*
                    try {
                        Thread.sleep(235);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                        listView.setVisibility(View.INVISIBLE);
                        playyoutube();
                    }
                }
                else if (url != null){
                    final String msgee = "Playing";
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    send.setVisibility(View.INVISIBLE);
                    msg_box.setText(msgee);
                    try{
                        sendReceive.write(msgee.getBytes());
                    }catch (Exception e){
                        Toast.makeText(Bluetooth.this, "Not connected yet", Toast.LENGTH_SHORT).show();
                    }
                    try {
                        Thread.sleep(235);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    listView.setVisibility(View.INVISIBLE);
                    playyoutube();
                }
                else if(audioUri!=null && msg_box.getText().equals("Resume")){
                    if(nplayer.isPlaying()) {
                        pause.setVisibility(View.VISIBLE);
                        pause.setElevation(30);
                        send.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    Toast.makeText(Bluetooth.this, "Select a music first.", Toast.LENGTH_SHORT).show();
                }
                clicked = true;


            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioUri != null){
                    nplayer.pause();
                    playing = false;
                    final String msg = "Pause";
                    msg_box.setText(msg);

                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(Bluetooth.this, "Not connected", Toast.LENGTH_SHORT).show();
                        pause.setVisibility(View.INVISIBLE);
                        send.setElevation(30);
                        send.setVisibility(View.VISIBLE);
                    }

                    send.setElevation(30);
                    send.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.INVISIBLE);
                    clicked = false;
                }
                else if(url != null){
                    webplayer.setVisibility(View.INVISIBLE);
                    webplayer.clearCache(true);
                    final String msg = "Pause";
                    msg_box.setText(msg);
                    MainActivity.mute(Bluetooth.this);

                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(Bluetooth.this, "Not connected", Toast.LENGTH_SHORT).show();
                        pause.setVisibility(View.INVISIBLE);
                        send.setElevation(30);
                        send.setVisibility(View.VISIBLE);
                    }

                    send.setElevation(30);
                    send.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.INVISIBLE);
                    clicked = false;

                }

            }
        });


        tryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioUri != null && clicked == false){
                    clicked = true;
                    final String msg = "Playing";
                    nplayer.stop();
                    nplayer.seekTo(0);
                    pause.setVisibility(View.VISIBLE);
                    pause.setElevation(30);
                    send.setVisibility(View.INVISIBLE);
                    msg_box.setText(msg);
                    try{
                        sendReceive.write(msg.getBytes());
                    }catch (Exception e){
                        Toast.makeText(Bluetooth.this, "Not connected yet", Toast.LENGTH_SHORT).show();
                    }
                    /**
                    try {
                        Thread.sleep(234);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    player();
                }
                else if(url != null){
                    Toast.makeText(Bluetooth.this, "Sorry try again does not work with youtube video.", Toast.LENGTH_SHORT).show();
                }
                else if(clicked){
                    Toast.makeText(Bluetooth.this, "Pause the song and try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        musicpicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent pickAudioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(pickAudioIntent, 1);
                //bothdevices.setVisibility(View.INVISIBLE);
                if(playing == false){
                    audioUri = null;
                    url = null;
                    counter++;
                    Intent pop = new Intent(Bluetooth.this, popup.class);
                    startActivity(pop);
                    if(counter>=2){
                        try{
                            String msg = "Music Selected";
                            sendReceive.write(msg.getBytes());
                            msg_box.setText(msg);

                        }catch (Exception e){
                            Toast.makeText(Bluetooth.this, "Can't change music, not connected", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //Intent pickAudioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    // startActivityForResult(pickAudioIntent, 1);
                    bothdevices.setVisibility(View.INVISIBLE);
                    pointertwo.setVisibility(View.INVISIBLE);
                    pointertwo.setAlpha((float) 0.00001);
                }
                else{
                    Toast.makeText(Bluetooth.this, "Pause the music first", Toast.LENGTH_SHORT).show();
                }




            }
        });
    }
        //change the text based on the host phone
        Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    url = popup.getUrl();
                    audioUri = popup.getaudiouri();
                    byte[]  readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    msg_box.setText(tempMsg);

                    if(tempMsg.equals("Playing")){
                        if(audioUri != null){
                            player();
                            msg_box.setText("playing");
                        }
                        else if(url != null){
                            playyoutube();
                            //pointerthree.setVisibility(View.INVISIBLE);
                            msg_box.setText("playing");
                        }
                        else{
                            Toast.makeText(Bluetooth.this, "Select a music first and try again", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else if(tempMsg.equals("Pause")){
                        playing = false;
                        if(audioUri != null){
                            nplayer.pause();
                        }
                        else if(url != null){
                            MainActivity.mute(Bluetooth.this);
                            webplayer.setVisibility(View.INVISIBLE);
                            webplayer.clearCache(true);
                        }

                    }
                    else if(tempMsg.equals("Resume")){
                        try{
                            nplayer.seekTo(nplayer.getCurrentPosition());
                            nplayer.start();
                        }catch (Exception e){
                            Toast.makeText(Bluetooth.this, "Can't tryagain", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else if(tempMsg.equals("Music Selected") && counter >=2){
                        if(audioUri != null){
                            nplayer.stop();;
                            nplayer.seekTo(0);
                        }
                    }

                    break;
            }
            return true;
        }
    });
    //server class
    private class ServerClass extends Thread{
        private BluetoothServerSocket serverSocket;
        public ServerClass(){
            try{
                serverSocket = myBluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //bluetoothsocket to get and recieve message using bluetooth
        @Override
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null){
                try{
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }
                if(socket != null){
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    //listView.setVisibility(View.VISIBLE);
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    break;
                }
            }
        }
    }
    private class ClientClass extends Thread{
        private BluetoothDevice device;
        private BluetoothSocket socket;
        public ClientClass(BluetoothDevice device1){
            device = device1;
            try{
                socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                //listView.setVisibility(View.VISIBLE);
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }
    private class SendReceive extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tempIn;
            outputStream = tempOut;

        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while(true){
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void write(final byte[] bytes){
            Thread m = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });m.start();
        }
    }
    public void player() {
        playing = true;
        initialized = true;
        Thread aeq = new Thread(new Runnable() {
            @Override
            public void run() {
                nplayer = new MediaPlayer();
                try {
                    nplayer.setDataSource(Bluetooth.this, audioUri);
                    nplayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                nplayer.start();
            }
        });aeq.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {

                //the selected audio.
                //audioUri = data.getData();
                //tic.setVisibility(View.VISIBLE);
                pointertwo.setVisibility(View.INVISIBLE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initialework(){
        btnDiscover = findViewById(R.id.discover);
        listView = findViewById(R.id.listBluetoothe);
        listen = findViewById(R.id.listen);
        send = findViewById(R.id.send);
        msg_box = findViewById(R.id.msg);
        status = findViewById(R.id.status);
        musicpicker = (Button) findViewById(R.id.musicpicker);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pause = (ImageButton) findViewById(R.id.stop);
        tryagain = (Button) findViewById(R.id.resume);
        bothdevices = (TextView) findViewById(R.id.textView3);
        tic = (ImageView) findViewById(R.id.imageView3);
        mess1 = findViewById(R.id.textView7);
        mess2 = findViewById(R.id.textView8);
        pointerone = findViewById(R.id.pointer1);
        pointertwo = findViewById(R.id.pointer2);
        scroller = findViewById(R.id.textView12);
        webplayer = findViewById(R.id.player);
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
        MainActivity.unmute(Bluetooth.this);
        webplayer.setVisibility(View.VISIBLE);
        webplayer.clearCache(true);
        webplayer.setWebViewClient(new WebViewClient());
        webplayer.getSettings().setJavaScriptEnabled(true);
        webplayer.getSettings().setDomStorageEnabled(true);
        webplayer.loadUrl(url);
        Toast.makeText(this, "Click on tap to unmute", Toast.LENGTH_SHORT).show();
    }




}
