package com.firstgame.menu;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firstgame.game.Game;
import com.firstgame.R;

public class MainActivity extends AppCompatActivity {

    private boolean server;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(game==null) {
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
            return;
        }
        game.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void chooseServerListener(View v){
        server = true;
        setContentView(R.layout.server_launcher);
    }

    public void chooseClientListener(View v){
        server = false;
        setContentView(R.layout.wait_room);
        new ClientManager(this);
    }

    public void launchServerListener(View view){
        RadioButton rb2P = findViewById(R.id.radioButton2P);
        RadioButton rb3P = findViewById(R.id.radioButton3P);
        RadioButton rb4P = findViewById(R.id.radioButton4P);

        setContentView(R.layout.wait_room);

        if(rb2P.isChecked()){
            new ServerManager(2,this);
        }else if(rb3P.isChecked()){
            new ServerManager(3,this);
        }else if(rb4P.isChecked()){
            new ServerManager(4,this);
        }
    }

    public TextView getWaitRoomLog(){
        return findViewById(R.id.statusLog);
    }

    public boolean isServer() {
        return server;
    }

}
