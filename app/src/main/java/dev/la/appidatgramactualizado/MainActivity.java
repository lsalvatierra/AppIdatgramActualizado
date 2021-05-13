package dev.la.appidatgramactualizado;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent intLogin = new Intent(this,
                LoginActivity.class);

        Thread timer = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(4000);
                }catch (InterruptedException ex){

                }finally {
                    startActivity(intLogin);
                    finish();
                }
            }
        };
        timer.start();
    }
}