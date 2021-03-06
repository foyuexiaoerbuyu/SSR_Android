package com.shin.ssr.layout.tab;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
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
import com.google.android.gms.fit.samples.backgroundgps.RealService;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.stepcounter.MainActivity;
import com.google.android.gms.fit.samples.stepcounter.R;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shin.ssr.layout.chart.MyMarkerView;
import com.shin.ssr.layout.chart.MyXAxisValueFormatter;
import com.shin.ssr.layout.point.Point;
import com.shin.ssr.vo.StepVO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import at.grabner.circleprogress.CircleProgressView;
import static android.app.PendingIntent.getActivity;
import static android.graphics.Color.rgb;
import static com.google.android.gms.fit.samples.backgroundgps.RealService.insideMall;
import static com.google.android.gms.fit.samples.stepcounter.MainActivity.user_id;


public class FitTab extends AppCompatActivity {

    private PopupWindow mPopupWindow;
    private static double step_percentage;
    private static double mall_percentage;
    private static double ssgpaycon_percentage;
    private static boolean lorddata = false;
    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private final LineChart[] charts = new LineChart[1];
    public static final String SERVER_URL = "http://13.125.183.32:8088/";
    public ImageView help;
    private int total;
    private Handler handler = new Handler();
    private static final int NOTIF_ID = 1234;
    private Context context;
    private Button btnMoney;
    private Button cartimg;
    private String convertedPoint;
    private FrameLayout mBackground;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fit_tab_activity);

        btnMoney = findViewById(R.id.button);
        cartimg = findViewById(R.id.button3);

        if (insideMall == true) {


            cartimg.setBackgroundResource(R.drawable.cart_in_off);
            //cartimg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cart_2_times, 0);
            /* cartimg.setBackgroundResource(R.drawable.cart_y);*/
        } else {
            cartimg.setBackgroundResource(R.drawable.cart_out_off);
            //cartimg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cart_normal, 0);
        }

        cartimg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (insideMall == true) {
                            cartimg.setBackgroundResource(R.drawable.cart_in_on);
                        } else {
                            cartimg.setBackgroundResource(R.drawable.cart_out_on);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (insideMall == true) {
                            cartimg.setBackgroundResource(R.drawable.cart_in_off);
                        } else {
                            cartimg.setBackgroundResource(R.drawable.cart_out_off);
                        }
                        break;
                }
                return false;
            }
        });
        btnMoney.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btnMoney.setBackgroundResource(R.drawable.ssg_money_on);
                        break;
                    case MotionEvent.ACTION_UP:
                        btnMoney.setBackgroundResource(R.drawable.ssg_money_off);
                        break;
                }
                return false;
            }
        });
        Log.d("fit", "after readdata" + Integer.toString(total));

        setTitle("LineChartActivityColored");
        charts[0] = findViewById(R.id.chart1);

        help = findViewById(R.id.helppop);
        help.setOnClickListener(new helpListener());

        btnMoney.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(FitTab.this);

                LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.yes_or_no_popup,null);

                TextView txt = view.findViewById(R.id.password);
                txt.setTypeface(Typeface.createFromAsset(getAssets(), "font/bmhannapro.ttf"));

                AlertDialog dialog = builder.create();
               /* builder.setView(view).setPositiveButton("예", dialogClickListener)
                        .setNegativeButton("아니요", dialogClickListener).show();*/
                dialog.setView(view);
                dialog.setButton(AlertDialog.BUTTON_POSITIVE,"예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                        final ImageView imgMoney = findViewById(R.id.imgMoney);
                        final TextView converted = findViewById(R.id.converted);
                        final TextView pointconverted = findViewById(R.id.pointconverted);


                        HttpUtil_ssgMoney hu = new HttpUtil_ssgMoney(FitTab.this);
                        String[] params = {SERVER_URL + "changeMoney.do", "numPoint:" + 1, "user_id:" + user_id};
                        hu.execute(params);


                        try {
                            convertedPoint = hu.get();
                            while(convertedPoint==null||convertedPoint.equals("")) {
                                Log.d("ssg", "inside while loop");
                                convertedPoint = hu.get();

                            }
                            Log.d("ssg", "SSGPOINT" + convertedPoint + "converted point" + convertedPoint);

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("ssg", "SSGPOINT" + convertedPoint + "after converted point" + convertedPoint);


                        imgMoney.setVisibility(View.VISIBLE);
                        converted.setVisibility(View.VISIBLE);
                        pointconverted.setVisibility(View.VISIBLE);

                        imgMoney.bringToFront();
                        converted.bringToFront();
                        pointconverted.bringToFront();


                        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(imgMoney,1);
                        Glide.with(imgMoney.getContext()).load(R.drawable.medal_pop)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(gifImage);



                        RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
                                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        tParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                        tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                        tParams.addRule(RelativeLayout.ALIGN_TOP, R.id.imgMoney);
                        /*tParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.converted);*/
                        tParams.setMargins(0,170,0,0);

                        pointconverted.setText(convertedPoint);
                        pointconverted.setLayoutParams(tParams);
                        pointconverted.setTypeface(Typeface.createFromAsset(getAssets(), "font/bmhannapro.ttf"));

                        final LinearLayout linear = findViewById(R.id.tabs);
                        final RelativeLayout relative = findViewById(R.id.belowTab);

                        linear.setVisibility(View.GONE);
                        relative.setVisibility(View.GONE);
                        linear.setTouchscreenBlocksFocus(true);
                        linear.clearFocus();
                        relative.setClickable(false);
                        relative.clearFocus();
                        relative.setTouchscreenBlocksFocus(true);
                        imgMoney.setClickable(true);




                        android.util.Log.d("pointy", "onClick: changedMoney");
                        imgMoney.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    imgMoney.setVisibility(View.GONE);
                                    converted.setVisibility(View.GONE);
                                    pointconverted.setVisibility(View.GONE);
                                    linear.setVisibility(View.VISIBLE);
                                    relative.setVisibility(View.VISIBLE);
                                    linear.setClickable(true);
                                    relative.setClickable(true);
                                    linear.findFocus();
                                    relative.findFocus();
                                    linear.setTouchscreenBlocksFocus(false);
                                    relative.setTouchscreenBlocksFocus(false);


                                }
                                return false;
                            }

                        });

                    }

                });

                dialog.setButton(AlertDialog.BUTTON_NEGATIVE,"아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                            return;
                    }

                });
                dialog.show();

                Button yesButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button noButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                yesButton.setGravity(Gravity.CENTER);
                noButton.setGravity(Gravity.CENTER);

                yesButton.setLayoutParams(layoutParams);
                noButton.setLayoutParams(layoutParams);





               /* final ImageView imgMoney = findViewById(R.id.imgMoney);
                final TextView converted = findViewById(R.id.converted);
                final TextView pointconverted = findViewById(R.id.pointconverted);


                HttpUtil_ssgMoney hu = new HttpUtil_ssgMoney(FitTab.this);
                String[] params = {SERVER_URL + "changeMoney.do", "numPoint:" + 1, "user_id:" + user_id};
                hu.execute(params);


                try {
                    convertedPoint = hu.get();
                    while(convertedPoint==null||convertedPoint.equals("")) {
                        Log.d("ssg", "inside while loop");
                        convertedPoint = hu.get();

                    }
                    Log.d("ssg", "SSGPOINT" + convertedPoint + "converted point" + convertedPoint);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("ssg", "SSGPOINT" + convertedPoint + "after converted point" + convertedPoint);


                imgMoney.setVisibility(View.VISIBLE);
                converted.setVisibility(View.VISIBLE);
                pointconverted.setVisibility(View.VISIBLE);

                imgMoney.bringToFront();
                converted.bringToFront();
                pointconverted.bringToFront();


                GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(imgMoney,1);
                Glide.with(imgMoney.getContext()).load(R.drawable.medal_pop)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(gifImage);



                RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
                        (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                tParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                tParams.addRule(RelativeLayout.ALIGN_TOP, R.id.imgMoney);
                *//*tParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.converted);*//*
                tParams.setMargins(0,170,0,0);

                pointconverted.setText(convertedPoint);
                pointconverted.setLayoutParams(tParams);
                pointconverted.setTypeface(Typeface.createFromAsset(getAssets(), "font/bmhannapro.ttf"));






                final LinearLayout linear = findViewById(R.id.tabs);
                final RelativeLayout relative = findViewById(R.id.belowTab);

                linear.setVisibility(View.GONE);
                relative.setVisibility(View.GONE);
                linear.setTouchscreenBlocksFocus(true);
                linear.clearFocus();
                relative.setClickable(false);
                relative.clearFocus();
                relative.setTouchscreenBlocksFocus(true);
                imgMoney.setClickable(true);




                android.util.Log.d("pointy", "onClick: changedMoney");
                imgMoney.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            imgMoney.setVisibility(View.GONE);
                            converted.setVisibility(View.GONE);
                            pointconverted.setVisibility(View.GONE);
                            linear.setVisibility(View.VISIBLE);
                            relative.setVisibility(View.VISIBLE);
                            linear.setClickable(true);
                            relative.setClickable(true);
                            linear.findFocus();
                            relative.findFocus();
                            linear.setTouchscreenBlocksFocus(false);
                            relative.setTouchscreenBlocksFocus(false);


                        }
                        return false;
                    }
                });*/
            }
        });

        mBackground = findViewById(R.id.backmain);
        readData();


        handler.post(new Runnable() {
            @Override
            public void run() {
                updateData();
                TextView txtView = findViewById(R.id.steps_taken);
                TextView txtView2 = findViewById(R.id.todo1_step);
                txtView.setText(" " + total + " / 7000  ");
                txtView2.setText(" " + total + " / 7000  ");
                String text = "<font color='#333743'> <b> " + total + "</b> / 7000 </font>";
                txtView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                handler.postDelayed(this, 5000); // set time here to refresh textView
            }
        });
    }

    private void setViewInvalidate(View... views) {
        for (View v : views) {
            v.invalidate();
        }
    }
    public FitTab(Context context) {
        this.context = context;
    }

    public FitTab() {

    }

    public void sendToFinance(View view) {
        Intent intent = new Intent(FitTab.this, MainActivity.class);
        intent.putExtra("buttonNum", 1);
        startActivity(intent);
        finish();
    }

    public void sendToPay(View view) {
        Intent intent = new Intent(FitTab.this, MainActivity.class);
        intent.putExtra("buttonNum", 2);
        startActivity(intent);
        finish();
    }

    public void sendToLife(View view) {
        Intent intent = new Intent(FitTab.this, MainActivity.class);
        intent.putExtra("buttonNum", 3);
        startActivity(intent);
        finish();
    }

    public void sendToPoint(View view) {
        Intent intent = new Intent(FitTab.this, Point.class);
        android.util.Log.d("CHECK", "sendToPoint: OK");
        startActivity(intent);
    }

    public void eventSSGMONEY(){
        ImageView money = findViewById(R.id.imgMoney);
        money.setVisibility(View.VISIBLE);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(money);
        Glide.with(this).load(R.drawable.grow_money).into(gifImage);
        money.setVisibility(View.GONE);
    }


    CircleProgressView mCircleView;


    @SuppressLint("ClickableViewAccessibility")
    public void getTodoList(double result) {

        this.step_percentage = result;
        System.out.println("getTodoList" + result);

        System.out.println("getTodoList" + this.step_percentage);


        View popupView = getLayoutInflater().inflate(R.layout.popup_window, null);

        mBackground.setVisibility(View.VISIBLE);
        mCircleView = popupView.findViewById(R.id.circleView);
        mCircleView.setFocusable(true);
        mBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mBackground.setVisibility(View.GONE);
                }
                return false;
            }
        });


        mCircleView.setValueAnimated((float) step_percentage);


        mPopupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);


    }


    public void stepgoal2(View v) {
        HttpUtil_Todo hu = new HttpUtil_Todo(FitTab.this);
        String[] params = {SERVER_URL + "todayGoal.do", "wk_am:" + total, "user_id:" + 2};
        hu.execute(params);
    }


    public void stepgoal1(View v) {
        HttpUtil_Todo1 hu = new HttpUtil_Todo1(FitTab.this);

        String[] params = {SERVER_URL + "visitmall.do", "wk_am:" + total, "user_id:" + 2};
        hu.execute(params);

    }


    public void getTodoList2(double result) {

        this.mall_percentage = result;
        System.out.println("getTodoList" + result);

        System.out.println("getTodoList" + this.mall_percentage);


        View popupView = getLayoutInflater().inflate(R.layout.popup_window2, null);

        mBackground.setVisibility(View.VISIBLE);
        mCircleView = popupView.findViewById(R.id.circleView);
        mCircleView.setFocusable(true);


        mCircleView.setValueAnimated(1);


        mPopupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);


    }


    public void stepgoal3(View v) {


        View popupView = getLayoutInflater().inflate(R.layout.popup_window3, null);

        mBackground.setVisibility(View.VISIBLE);
        mCircleView = popupView.findViewById(R.id.circleView);


        mCircleView.setValueAnimated(1);


        mPopupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
    }

    private final int[] colors = new int[]{
            /*Color.rgb(217, 77, 50)*/
            rgb(250, 250, 250)
    };

    private void setupChart(LineChart chart, LineData data, int color) {

        Log.d("fit", "in setup chart");

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
        l.setTextColor(Color.rgb(51, 55, 68));
        l.setXEntrySpace(20f);
        l.setMaxSizePercent(0.5f);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTypeface(Typeface.createFromAsset(getAssets(), "font/applesgothic_regular.ttf"));
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setSpaceTop(40);
        chart.getAxisLeft().setSpaceBottom(40);
        chart.getAxisRight().setEnabled(false);
        /*     chart.getXAxis().setEnabled(false);*/


        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart.setViewPortOffsets(50, 30, 50, 90);

        Log.d("log", "inside chart creation");


        XAxis xAxis = chart.getXAxis();

        xAxis.setYOffset(-10f);
        xAxis.setTextSize(10f);
        xAxis.setTypeface(Typeface.createFromAsset(getAssets(), "font/applesgothic_regular.ttf"));
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setTextColor(Color.rgb(51, 55, 68));
        xAxis.setTextSize(15f);

        /*String[] values = new String[] {"월","화","수","목","금","토","일"};*/


        String[] today = new String[7];

        for (int i = 0; i < today.length; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -i);
            today[today.length - i - 1] = new SimpleDateFormat("EE").format(cal.getTime());
        }


        xAxis.setValueFormatter(new MyXAxisValueFormatter(today));


        // animate calls invalidate()...
        /*    chart.animateX(700);*/
        chart.animateY(1200, Easing.EasingOption.EaseInOutCirc);
        /* chart.animateX(1200, Easing.EasingOption.EaseInCubic);*/

        MyMarkerView marker = new MyMarkerView(this, R.layout.markerviewtext);
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



    private LineData getData(int count, float range, int total, ArrayList<StepVO> stepAry) {


        Log.d("fit", "in getdata");
        for (int i = 0; i < stepAry.size(); i++) {
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
            values.add(new Entry(i - 7, val));

        }
        values.add(new Entry(6, total));


        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "이번 주");
        LineDataSet set2 = new LineDataSet(values2, "지난 주");
   /*     set1.setFillAlpha(110);
        set1.setFillColor(Color.RED);*/


        set2.setLineWidth(1.75f);
        set2.setCircleRadius(7f);
        set2.setCircleHoleRadius(2.5f);
        set2.setCircleColorHole(Color.TRANSPARENT);
        set2.setColor(rgb(227, 179, 196));
        set2.setCircleColor(rgb(227, 179, 196));
        set2.setHighLightColor(rgb(227, 179, 196));
        set2.setDrawValues(false);


        set1.setLineWidth(1.75f);
        set1.setCircleRadius(7f);
        set1.setCircleHoleRadius(2.5f);
        set1.setCircleColorHole(Color.TRANSPARENT);
        set1.setColor(Color.rgb(203, 55, 55));
        set1.setCircleColor(Color.rgb(203, 55, 55));
        set1.setHighLightColor(rgb(203, 55, 55));
        set1.setValueTextColor(Color.rgb(51, 55, 68));
        set1.setValueTextSize(12f);
        set1.setDrawValues(false);


        // create a data object with the data sets

        return new LineData(set2, set1);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("fit", "in activity result");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
            }
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        readData();
    }

    /*public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Log.d("fit","in subscribe");
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    *//*Log.i(TAG, "Successfully subscribed!");*//*
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                                Log.d("readdata","before read data - in subscribe");

                            }
                        });
    }*/

    private void readData() {
        Log.d("fit", "in readdata");
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {

                            @Override
                            public void onSuccess(DataSet dataSet) {
                                ArrayList<StepVO> stepAry = new ArrayList<>();

                                HttpUtil hu = new HttpUtil(FitTab.this);

                                String[] params = {SERVER_URL + "step.do", "wk_am:" + total, "user_id:" + user_id};

                                hu.execute(params);
                                total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                                JSONArray object = null;
                                String result;
                                try {
                                    result = hu.get();
                                    object = new JSONArray(result);

                                    android.util.Log.d("log", "result from spring" + result);

                                    for (int i = 0; i < object.length(); i++) {
                                        JSONObject obj = (JSONObject) object.get(i);
                                        android.util.Log.d("log", obj.getString("wk_am"));
                                        android.util.Log.d("log", obj.getString("user_id"));
                                        stepAry.add(new StepVO(obj.optInt("user_id"), obj.optInt("wk_am"), obj.optString("wk_dt")));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                               /* Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                                getApplicationContext().startService(intent);*/

                                updateNotification(total);


                                Log.d("fit", "todays walk");
                                Log.d("fit", "stepvO" + stepAry);

                                if (stepAry.size() != 0) {
                                    LineData data1 = getData(7, 10000, total, stepAry);

                                    Log.d("fit", "getdata" + data1.getDataSets().toString());

                                    // add some transparency to the color with "& 0x90FFFFFF"
                                    setupChart(charts[0], data1, colors[0 % colors.length]);
                                }
                                TextView txtView = findViewById(R.id.steps_taken);
                                TextView txtView2 = findViewById(R.id.todo1_step);
                                txtView.setText(" " + total + " / 7000  ");

                                String text = "<font color='#333743'> <b> " + total + "</b> / 7000 </font>";
                                txtView2.setText(" " + total + " / 7000  ");
                                txtView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);

                                //////////////http connection
                                // 서버 주소


                                if (total >= 7000) {
                                    Log.d("fit", "inside checkbox");
                                    CheckBox step_checkbox = findViewById(R.id.steps_check);
                                    step_checkbox.setChecked(true);
                                }
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

    public void httpWeb() {

    }

    public void printToast(String rtn) {
        Toast.makeText(FitTab.this, rtn, Toast.LENGTH_SHORT).show();
    }

    PopupWindow helpPopup;
    View popupView;

    class helpListener implements View.OnClickListener {

        @Override
        public void onClick(View helpicon) {

            //Toast.makeText(getApplicationContext(), "are you clicked?", Toast.LENGTH_LONG).show();

            switch (helpicon.getId()) {
                case R.id.helppop:
                    popupView = getLayoutInflater().inflate(R.layout.help_popup_activity, null);
                    helpPopup = new PopupWindow(popupView,
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    helpPopup.setAnimationStyle(-1);
                    helpPopup.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                popupView.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    PopupWindow moneyPopup;
    View popupView_m;
    class moneyListener implements View.OnClickListener {

        @Override
        public void onClick(View moneyicon) {

            //Toast.makeText(getApplicationContext(), "are you clicked?", Toast.LENGTH_LONG).show();

            switch (moneyicon.getId()) {
                case R.id.imgMoney:
                    ImageView imgMoney = findViewById(R.id.imgMoney);
                    popupView = getLayoutInflater().inflate(R.layout.popup_money, null);
                    moneyPopup = new PopupWindow(popupView_m,
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    moneyPopup.setAnimationStyle(-1);
                    moneyPopup.showAtLocation(popupView_m, Gravity.CENTER, 0, 0);
                    GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(imgMoney);
                    Glide.with(imgMoney.getContext()).load(R.drawable.grow_money).into(gifImage);
                    popupView_m.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                popupView_m.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    public void getPastSteps(ArrayList<StepVO> arry) {

    }


    private Notification getMyActivityNotification(int total) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_service);

        Log.d("noti", "inside get my activity");
        // The PendingIntent to launch our activity if the user selects
        // this notification
        Intent notificationIntent = new Intent(this, FitTab.class);
        PendingIntent pendingIntent = getActivity(this, 0, notificationIntent, 0);

        remoteViews.setTextViewText(R.id.notif_content2, Integer.toString(total));
        return new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_ssgpay_launch)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent).getNotification();
    }

    private void updateNotification(int total) {

        Notification notification = getMyActivityNotification(total);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIF_ID, notification);
    }

    private void updateData() {
        Log.d("fit", "in readdata");
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {

                            @Override
                            public void onSuccess(DataSet dataSet) {
                                total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();


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

    public void getConvertedPoint(String result) {
        this.convertedPoint = result;
    }

}

