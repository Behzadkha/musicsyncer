package com.musicsyncer.behzad.android;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

public class popup extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    Button local, youtube, done, close;
    EditText Ylink;
    public static Uri audioUri;
    public static String url;

    BillingProcessor bp;
    String key = "KEY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        local = findViewById(R.id.local);
        youtube = findViewById(R.id.youtube);
        done = findViewById(R.id.done);
        done.setVisibility(View.INVISIBLE);
        Ylink = findViewById(R.id.editText);
        Ylink.setVisibility(View.INVISIBLE);
        close = findViewById(R.id.close);
        bp = new BillingProcessor(this, key, this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.7), (int)(height*.5));

        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickAudioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickAudioIntent, 1);
                url = null;

            }
        });
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(popup.this, "youtube_syncer");
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Ylink.getText().toString().equals("")){
                    finish();
                }
                else{
                    url = Ylink.getText().toString();
                    finish();
                }

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {

                //the selected audio.
                audioUri = data.getData();
                done.setVisibility(View.VISIBLE);
                finish();
                //tic.setVisibility(View.VISIBLE);
                //pointerthree.setVisibility(View.INVISIBLE);
                //pointerthree.setAlpha((float) 0.00000001);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public static Uri getaudiouri(){
        return audioUri;
    }
    public static String getUrl(){
        return url;
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Ylink.setVisibility(View.VISIBLE);
        done.setVisibility(View.VISIBLE);
        audioUri = null;
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(this, "Purchase was NOT successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}
