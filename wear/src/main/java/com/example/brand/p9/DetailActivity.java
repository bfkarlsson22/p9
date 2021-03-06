package com.example.brand.p9;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.util.Date;
import java.util.HashMap;


public class DetailActivity extends WearableActivity {

    private Context context = this;
    private ImageView backgroundCanvas;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    public SensorEventListener mStepListener;
    int height;
    int width;
    LocalStorageWear localStorageWear = new LocalStorageWear(context);
    int screen = 0;
    String day;
    String UID;
    String partnerID;
    HashMap<String, String> settings;
    DataSenderWear dataSenderWear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stepsdetail);
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        settings = localStorageWear.getSettings();
        dataSenderWear = new DataSenderWear(context);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                settings = localStorageWear.getSettings();
                if(getIntent() != null && getIntent().getStringExtra("DAY") != null){
                    Intent intent = getIntent();
                    day = intent.getStringExtra("DAY");
                } else {
                    Long time = System.currentTimeMillis();
                    java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("E-d-M-y");
                    day = simpleDateFormat.format(new Date(time));
                }

                UID = settings.get("UID");
                partnerID = settings.get("PARTNER");
                setUpStepCounter();

                backgroundCanvas = (ImageView) findViewById(R.id.backgroundCanvas);
                drawSteps();

                stub.setOnTouchListener(new OnSwipeTouchListener(context){
                    @Override
                    public void onSwipeLeft(){
                        Log.d("SWIPE LEFT","TRUE");
                        screen++;
                        Log.d("SCREEN", String.valueOf(screen));
                        if(screen == 1){
                            drawActivity();
                        }
                        if(screen > 1){
                            Intent intent = new Intent(DetailActivity.this,History.class);
                            startActivity(intent);
                        }

                    }
                    @Override
                    public void onSwipeRight(){
                        screen--;
                        Log.d("SCREEN", String.valueOf(screen));
                        if(screen == 0){
                            drawSteps();
                        }
                        if(screen < 0){
                            Intent intent = new Intent(DetailActivity.this, SendMsgActivity.class);
                            startActivity(intent);
                        }

                    }
                });
            }
        });
    }
    private double getData(String day, String user, String unit){
        double value;
        Cursor dataCursor = localStorageWear.getDailyData(user,unit,day);
        if(dataCursor.moveToFirst()){
            value = dataCursor.getDouble(dataCursor.getColumnIndex("VALUE"));
        } else {
            value = 0;
        }

        return value;
    }

    private void drawActivity(){


        Log.d("DAY",day);

        Bitmap bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint activityPaint = new Paint();
        activityPaint.setColor(Color.parseColor("#2196F3"));
        activityPaint.setStyle(Paint.Style.STROKE);
        activityPaint.setStrokeWidth(10);
        activityPaint.setAntiAlias(true);

        Paint headLinePaint = new Paint();
        headLinePaint.setAntiAlias(true);
        headLinePaint.setTextSize(30);
        headLinePaint.setColor(Color.WHITE);

        Paint smallTextPaint = new Paint();
        smallTextPaint.setAntiAlias(true);
        smallTextPaint.setTextSize(20);
        smallTextPaint.setColor(Color.WHITE);

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int centerX = width/2;
        int centerY = height/2;

        int left = centerX-centerX;
        int right = centerX+centerX;
        int top = centerY-centerY;
        int bottom = centerY+centerY;

        float xOffsetHeadLine = calcXOffset("AKTIVE MINUTTER",headLinePaint,centerX);
        canvas.drawText("AKTIVE MINUTTER",xOffsetHeadLine,top+75,headLinePaint);

        String translatedDay = formatDate(day);

        float xOffsetDay = calcXOffset(translatedDay,smallTextPaint,centerX);
        canvas.drawText(translatedDay,xOffsetDay,top+95,smallTextPaint);

        double userMinutes = getData(day,UID,"MINUTE");
        double partnerMinutes = getData(day,partnerID,"MINUTE");
        double userGoal = 30;
        double partnerGoal = 30;

        float startYUser = centerY-40;
        float graphBounds = width-80;
        float graphStartUser = centerX-(graphBounds/2);
        float graphEndUser = centerX+(graphBounds/2);
        float graphLengthUser = graphStartUser+calcGraph(userMinutes,userGoal,graphBounds);
        float zeroOffsetUser = calcYOffset("0",smallTextPaint,startYUser);
        float hundredYOffsetUser = calcYOffset("30",smallTextPaint,startYUser);
        float hundredXOffsetUser = graphEndUser-getTextWidth("30",smallTextPaint)+30;

        float startYPartner = centerY+40;
        float graphStartPartner = centerX-(graphBounds/2);
        float graphEndPartner = centerX+(graphBounds/2);
        float graphLengthPartner = graphStartPartner+calcGraph(partnerMinutes,partnerGoal,graphBounds);
        float zeroOffsetPartner = calcYOffset("0",smallTextPaint,startYPartner);
        float hundredYOffsetPartner = calcYOffset("30",smallTextPaint,startYPartner);
        float hundredXOffsetPartner = graphEndPartner-getTextWidth("30",smallTextPaint)+30;

        canvas.drawLine(graphStartPartner,startYPartner,graphLengthPartner,startYPartner,activityPaint);
        canvas.drawText("0",graphStartPartner-30,zeroOffsetPartner,smallTextPaint);
        canvas.drawText("30",hundredXOffsetPartner,hundredYOffsetPartner,smallTextPaint);


        canvas.drawLine(graphStartUser,startYUser,graphLengthUser,startYUser,activityPaint);
        canvas.drawText("0",graphStartUser-30,zeroOffsetUser,smallTextPaint);
        canvas.drawText("30",hundredXOffsetUser,hundredYOffsetUser,smallTextPaint);


        backgroundCanvas.setImageBitmap(bitmap);
    }

    private void drawSteps(){
        Bitmap bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint userPaint = new Paint();
        userPaint.setAntiAlias(true);
        userPaint.setStrokeWidth(10);
        userPaint.setColor(Color.parseColor("#4CAF50"));
        userPaint.setStyle(Paint.Style.STROKE);

        Paint partnerPaint = new Paint();
        partnerPaint.setAntiAlias(true);
        partnerPaint.setStrokeWidth(10);
        partnerPaint.setColor(Color.parseColor("#F44336"));
        partnerPaint.setStyle(Paint.Style.STROKE);

        Paint headLinePaint = new Paint();
        headLinePaint.setAntiAlias(true);
        headLinePaint.setTextSize(30);
        headLinePaint.setColor(Color.WHITE);

        Paint smallTextPaint = new Paint();
        smallTextPaint.setAntiAlias(true);
        smallTextPaint.setTextSize(20);
        smallTextPaint.setColor(Color.WHITE);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(30);
        textPaint.setColor(Color.WHITE);


        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int centerX = width/2;
        int centerY = height/2;

        int left = centerX-centerX;
        int right = centerX+centerX;
        int top = centerY-centerY;
        int bottom = centerY+centerY;


        float xOffsetHeadLine = calcXOffset("FREMSKRIDT",headLinePaint,centerX);
        canvas.drawText("FREMSKRIDT",xOffsetHeadLine,top+75,headLinePaint);

        String translatedDay = formatDate(day);

        float xOffsetDay = calcXOffset(translatedDay,smallTextPaint,centerX);
        canvas.drawText(translatedDay,xOffsetDay,top+95,smallTextPaint);


        double userSteps = getData(day,UID,"STEP");
        double partnerStep = getData(day,partnerID,"STEP");
        double userGoal = Double.parseDouble(settings.get("GOAL"));
        double partnerGoal = Double.parseDouble(settings.get("PARTNERGOAL"));


        Log.d("USER STEPS", String.valueOf(userSteps));
        Log.d("PARTNER STEPS", String.valueOf(partnerStep));


        float startYUser = centerY-40;
        float graphBounds = width-80;
        float graphStartUser = centerX-(graphBounds/2);
        float graphEndUser = centerX+(graphBounds/2);
        float graphLengthUser = graphStartUser+calcGraph(userSteps,userGoal,graphBounds);
        float zeroOffsetUser = calcYOffset("0%",smallTextPaint,startYUser);
        float hundredYOffsetUser = calcYOffset("100%",smallTextPaint,startYUser);
        float hundredXOffsetUser = graphEndUser-getTextWidth("100%",smallTextPaint)+30;

        float startYPartner = centerY+40;
        float graphStartPartner = centerX-(graphBounds/2);
        float graphEndPartner = centerX+(graphBounds/2);
        float graphLengthPartner = graphStartPartner+calcGraph(partnerStep,partnerGoal,graphBounds);
        float zeroOffsetPartner = calcYOffset("0%",smallTextPaint,startYPartner);
        float hundredYOffsetPartner = calcYOffset("100%",smallTextPaint,startYPartner);
        float hundredXOffsetPartner = graphEndPartner-getTextWidth("100%",smallTextPaint)+30;


        canvas.drawLine(graphStartPartner,startYPartner,graphLengthPartner,startYPartner,partnerPaint);
        canvas.drawText("0%",graphStartPartner-30,zeroOffsetPartner,smallTextPaint);
        canvas.drawText("100%",hundredXOffsetPartner,hundredYOffsetPartner,smallTextPaint);


        canvas.drawLine(graphStartUser,startYUser,graphLengthUser,startYUser,userPaint);
        canvas.drawText("0%",graphStartUser-30,zeroOffsetUser,smallTextPaint);
        canvas.drawText("100%",hundredXOffsetUser,hundredYOffsetUser,smallTextPaint);


        backgroundCanvas.setImageBitmap(bitmap);
    }

    public float calcXOffset(String text, Paint paint, int centerX){
        float textLength = paint.measureText(text);
        return centerX - (textLength/2.0f);
    }
    private float calcYOffset(String text, Paint paint, float y){
        Rect textBounds = new Rect();
        paint.getTextBounds(text,0,text.length(),textBounds);
        float textHeight = textBounds.height();
        return y + (textHeight/2.0f);
    }
    private float getTextWidth(String text, Paint paint){
        float textLenght = paint.measureText(text);
        return textLenght;
    }
    private String formatDate(String day){
        HashMap<String, String> days = new HashMap<>();
        days.put("Mon","Mandag");
        days.put("Tue","Tirsdag");
        days.put("Wed","Onsdag");
        days.put("Thu","Torsdag");
        days.put("Fri","Fredag");
        days.put("Sat","Lørdag");
        days.put("Sun","Søndag");

        String[] splitDay = day.split("-");
        String translatedDay = days.get(splitDay[0]);

        String formattedDay = day.replace(splitDay[0],translatedDay);
        return formattedDay;
    }
    private float calcGraph(double value, double goal, float graphBounds){
        float progress = (float) ((value/goal)*100);
        float graphLength = (graphBounds/100)*progress;
        return graphLength;
    }
    public void setUpStepCounter(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        mStepListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == android.hardware.Sensor.TYPE_STEP_COUNTER) {
                    if (event.values.length > 0) {
                        localStorageWear.addStepData(System.currentTimeMillis());
                        dataSenderWear.syncData();
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        mSensorManager.registerListener(mStepListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

}