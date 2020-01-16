package com.payne.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.payne.customview.util.DateTime;
import com.payne.customview.widget.AnimLineProgressView;
import com.payne.customview.widget.AnimNumProgressView;
import com.payne.customview.widget.LineGraphicView;
import com.payne.customview.widget.RingProgressView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RingProgressView progressView;
    private LineGraphicView graphicView;
    private AnimNumProgressView numProgressView;
    private AnimLineProgressView lineProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressView = findViewById(R.id.ring_progress);
        progressView.startAnim(0.8f, true, 1000);

        graphicView = findViewById(R.id.view_graphic);

        ArrayList<Double> scoreList = new ArrayList<>();
        ArrayList<String> timeList = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            timeList.add(DateTime.getDayStr(i));
            scoreList.add(Math.random() * 100);
        }


        graphicView.setData(scoreList, timeList, 100, 20);
        graphicView.startAnim(1000);

        numProgressView = findViewById(R.id.num_progress);
        lineProgressView = findViewById(R.id.line_progress);

        numProgressView.startAnim(0.8f, true, 1000);
        lineProgressView.startAnim(0.8f, true, 1000);
    }
}
