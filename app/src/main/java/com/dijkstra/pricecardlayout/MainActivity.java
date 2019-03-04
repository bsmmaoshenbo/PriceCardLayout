package com.dijkstra.pricecardlayout;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dijkstra.pricecardlayout.widget.PriceCardLayout;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private PriceCardLayout mc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout llBook = findViewById(R.id.ll_book);
        llBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "你看，我弹出来了", Toast.LENGTH_SHORT).show();
                Snackbar.make(view, "你看，我在了", Snackbar.LENGTH_SHORT).show();
                Log.i("book", "进来了");
            }
        });

        mc = findViewById(R.id.multi_card_menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mc.resetCardView();
    }
}
