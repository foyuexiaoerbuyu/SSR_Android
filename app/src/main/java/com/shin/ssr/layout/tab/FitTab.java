package com.shin.ssr.layout.tab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.stepcounter.R;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.shin.ssr.layout.chart.MyMarkerView;
import com.shin.ssr.layout.chart.MyXAxisValueFormatter;
import com.shin.ssr.vo.StepVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ExecutionException;


public class FitTab extends AppCompatActivity  {

    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private Timer mTimer = new Timer();
    private Handler mHandler = new Handler();
    private LineChart lineChart;
    private final LineChart[] charts = new LineChart[1];
    ArrayList<StepVO> stepAry = new ArrayList<StepVO>();
  /*  private MainTimerTask timerTask = new MainTimerTask();*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fit_tab_activity);

        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
            android.util.Log.d("log","in Fitness regist");
        } else {
            android.util.Log.d("log","in Fitness regist");
            subscribe();

        }
        /*mTimer.schedule(timerTask, 5000, 10000);*/
        android.util.Log.d("log","in fit Tab");


        ///////////////기본 라인차트
        /*lineChart = (LineChart)findViewById(R.id.line_chart);

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 0));
        entries.add(new Entry(4, 4));
        entries.add(new Entry(5, 3));

        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setLineWidth(4);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#333743"));
        lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#333743"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        lineChart.setGridBackgroundColor(0);
        lineChart.setDrawBorders(false);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();

        yLAxis.setTextColor(Color.BLACK);
        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        yLAxis.setDrawGridLines(false);
        yLAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);



        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();

        MyMarkerView marker = new MyMarkerView(this,R.layout.markerviewtext);
        marker.setChartView(lineChart);
        lineChart.setMarker(marker);*/

        setTitle("LineChartActivityColored");

        charts[0] = findViewById(R.id.chart1);





    }

    public void sendToFinance(View view) {
        Intent intent = new Intent(FitTab.this, FinanceTab.class);
        startActivity(intent);
    }


    public void sendToLife(View view) {
        Intent intent = new Intent(FitTab.this, LifeTab.class);
        startActivity(intent);
    }

    public void sendToPay(View view) {
        Intent intent = new Intent(FitTab.this, PaymentTab.class);
        startActivity(intent);
    }

    /*Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            readData();

        }
    };

    class MainTimerTask extends TimerTask {
        public void run() {
            mHandler.post(mUpdateTimeTask);
        }
    }

    @Override
    protected void onResume() {
        MainTimerTask timerTask = new MainTimerTask();
        mTimer.schedule(timerTask, 5000, 10000);
        super.onResume();
    }*/

    private final int[] colors = new int[] {
            /*Color.rgb(217, 77, 50)*/
            Color.rgb(51, 55, 68)
    };

    private void setupChart(LineChart chart, LineData data, int color, ArrayList<String> dayList) {

        // no description text
        chart.getDescription().setEnabled(false);

        // chart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false);
//        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setBackgroundColor(color);



        // add data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setWordWrapEnabled(true);
        l.setTextColor(Color.WHITE);
        l.setXEntrySpace(10f);
        l.setMaxSizePercent(0.5f);



      chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setSpaceTop(40);
        chart.getAxisLeft().setSpaceBottom(40);
        chart.getAxisRight().setEnabled(false);
   /*     chart.getXAxis().setEnabled(false);*/


        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart.setViewPortOffsets(50, 0, 50, 60);

        Log.d("log","inside chart creation");



        XAxis xAxis = chart.getXAxis();

        xAxis.setYOffset(-10f);
        xAxis.setTextSize(10f);
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(15f);

        /*String[] values = new String[] {"월","화","수","목","금","토","일"};*/

        Calendar cal = Calendar.getInstance();
        String today = new SimpleDateFormat("EE").format(cal.getTime());

        dayList.add(today);


        for(int i =0; i < dayList.size(); i++) {
            Log.d("daylist", dayList.get(i));
        }

        xAxis.setValueFormatter(new MyXAxisValueFormatter(dayList.toArray(new String[0])));


        // animate calls invalidate()...
    /*    chart.animateX(700);*/
        chart.animateY(1200,Easing.EasingOption.EaseInOutCirc);
       /* chart.animateX(1200, Easing.EasingOption.EaseInCubic);*/

        MyMarkerView marker = new MyMarkerView(this,R.layout.markerviewtext);
        marker.setChartView(charts[0]);
        charts[0].setMarker(marker);

        LimitLine ll1 = new LimitLine(6000F, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setTextSize(10f);
        ll1.setLineColor(Color.RED);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.setDrawLimitLinesBehindData(false);
    }

    private LineData getData(int count, float range, int total) {
        for(int i =0; i < stepAry.size(); i++ ) {
            Log.d("values", Integer.toString(stepAry.get(i).getWk_am()));
        }

        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            Log.d("result", "are you here");
            float val = (float) stepAry.get(i).getWk_am();
            values2.add(new Entry(i, val));
        }



        for (int i = 7; i < stepAry.size(); i++) {
                Log.d("result", "are you here");
                float val = (float) stepAry.get(i).getWk_am();
                values.add(new Entry(i-7, val));

            }
        values.add(new Entry(6, total));



        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        LineDataSet set2 = new LineDataSet(values2, "DataSet 2");
   /*     set1.setFillAlpha(110);
        set1.setFillColor(Color.RED);*/

        set1.setLineWidth(1.75f);
        set1.setCircleRadius(7f);
        set1.setCircleHoleRadius(2.5f);
        set1.setCircleColorHole(Color.WHITE);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setHighLightColor(Color.rgb(51, 55, 68));
        set1.setDrawValues(false);

        set2.setLineWidth(1.75f);
        set2.setCircleRadius(7f);
        set2.setCircleHoleRadius(2.5f);
        set2.setCircleColorHole(Color.rgb(143, 141, 143));
        set2.setColor(Color.rgb(143, 141, 143));
        set2.setCircleColor(Color.rgb(143, 141, 143));
        set2.setHighLightColor(Color.rgb(51, 55, 68));
        set2.setDrawValues(false);


        // create a data object with the data sets

        return new LineData(set1, set2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("log",Integer.toString(resultCode));
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
            }
        }
    }

    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Log.d("log","in subscribe");
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    /*Log.i(TAG, "Successfully subscribed!");*/
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }

                                readData();
                            }
                        });
    }

    private void readData() {
        Log.d("log","in readdata");
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {

                            @Override
                            public void onSuccess(DataSet dataSet) {

                                int total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();


                            /*    TextView textView = findViewById(R.id.steps_taken);
                                textView.setText("오늘 걸은 걸음 : " + Long.toString(total));*/

                                //////////////http connection
                                String SERVER_URL="http://10.149.179.44:8088/step.do"; // 서버 주소
                                HttpUtil hu = new HttpUtil(FitTab.this);

                                String[] params = {SERVER_URL, "steps:"+total, "userno:"+ 1} ;


                                hu.execute(params);
                                String result;

                                try {
                                    result = hu.get();
                                    JSONArray object = null;
                                    android.util.Log.d("log","result from spring" + result);

                                    try {
                                        object =  new JSONArray(result);

                                        for(int i =0; i < object.length(); i++) {
                                            JSONObject obj = (JSONObject)object.get(i);
                                            android.util.Log.d("log",obj.getString("wk_am"));
                                            android.util.Log.d("log",obj.getString("user_id"));
                                            stepAry.add(new StepVO(obj.optInt("user_id"),obj.optInt("wk_am"),obj.optString("wk_dt")));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                ArrayList<String> dateList = new ArrayList<>();
                                Log.d("stepsize", Integer.toString(stepAry.size()));
                                SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat format2 = new SimpleDateFormat("EE");
                                for(int i =7; i < stepAry.size(); i++) {

                                    try {
                                       Date d1 =  format1.parse(stepAry.get(i).getWk_dt());
                                       dateList.add(format2.format(d1));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                for(int i =0; i<dateList.size(); i++) {
                                    Log.d("getdate1", dateList.get(i));
                                }

                                Log.d("result", "stepvO" + stepAry);

                                LineData data1 = getData(7, 10000, total);

                                Log.d("result","getdata" + data1.getDataSets().toString());

                                // add some transparency to the color with "& 0x90FFFFFF"
                                setupChart(charts[0], data1, colors[0 % colors.length], dateList);

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
    }

    public void httpWeb(){

    }

    public void printToast(String rtn) {
        Toast.makeText(FitTab.this, rtn, Toast.LENGTH_SHORT).show();
    }


}

