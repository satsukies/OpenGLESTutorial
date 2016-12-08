package net.ddns.satsukies.openglestutorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        GameSurfaceView view = new GameSurfaceView(this);
        setContentView(view);
    }
}
