package com.man.faltu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
	
	Button btn1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //mLocMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //mProvider = mLocMan.getBestProvider(new Criteria(), true);
        //LocationListener mListener = new Geocoord();



    //Button 0
        /*
    final Button btn = (Button)findViewById(R.id.btn);
    btn.setOnClickListener(new Button.OnClickListener() {
    public void onClick(View v) {
        ....    
    }
    }); //ends button0
*/
    //Button 1
    btn1 = (Button)findViewById(R.id.btn1);
    btn1.setOnClickListener(new Button.OnClickListener() {
        public void onClick(View v) {
            //btn.performClick();



            Intent intent = new Intent(MainActivity.this, MapView.class);
            startActivity(intent);
        }//ends onClick 

        });

    }//ends onCreate
}