package com.firstgame.menu;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firstgame.game.Game;
import com.firstgame.R;
import com.firstgame.game.GameManager;
import com.firstgame.game.client.ClientGameManager;
import com.firstgame.game.server.ServerGameManager;

public class MainActivity extends AppCompatActivity {

    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(gameManager!=null) {
            gameManager.onStop();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(gameManager!=null) {
            gameManager.onDestroy();
        }
    }

    public void chooseServerListener(View v){
        setContentView(R.layout.server_launcher);
    }

    public void chooseClientListener(View v){
        setContentView(R.layout.wait_room);
        gameManager = new ClientGameManager(this);
    }

    public void launchServerListener(View view){
        RadioButton rb2P = findViewById(R.id.radioButton2P);
        RadioButton rb3P = findViewById(R.id.radioButton3P);
        RadioButton rb4P = findViewById(R.id.radioButton4P);

        setContentView(R.layout.wait_room);

        if(rb2P.isChecked()){
            gameManager = new ServerGameManager(2,this);
        }else if(rb3P.isChecked()){
            gameManager = new ServerGameManager(3,this);
        }else if(rb4P.isChecked()){
            gameManager = new ServerGameManager(4,this);
        }
    }

    public TextView getWaitRoomLog(){
        return findViewById(R.id.statusLog);
    }

}
