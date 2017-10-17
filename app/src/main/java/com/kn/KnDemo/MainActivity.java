package com.kn.KnDemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.kn.game.Gameactivity;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private TextView te;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        te = (TextView) findViewById(R.id.te);

        te.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent(MainActivity.this, Gameactivity.class);
                startActivity(intent);*/

            }
        });

        init();


    }

    private void init() {


    }

}
