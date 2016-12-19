package com.example.brand.p9;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by EmilSiegenfeldt on 01/12/2016.
 */

public class WatchFaceService extends CanvasWatchFaceService {
    @Override
    public Engine onCreateEngine(){
        return new Engine();
    }
    private class Engine extends CanvasWatchFaceService.Engine{

        private double userSteps;
        private double userGoal;
        private double partnerSteps;
        private double partnerGoal;
        private LocalStorageWear localStorageWear = new LocalStorageWear(getApplicationContext());
        private DataSenderWear dataSender = new DataSenderWear(getApplicationContext());
        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            /* initialize your watch face */
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            /* get device features (burn-in, low-bit ambient) */
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            /* the time changed */

            Log.d("BATTERY LEVEL", String.valueOf(batteryMonitor()));

            HashMap<String, String> settings = localStorageWear.getSettings();
            dataSender.syncData();

            Long time = System.currentTimeMillis();
            java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("E-d-M-y");
            String day = simpleDateFormat.format(new Date(time));

            Cursor cursorUser = localStorageWear.getDailyData(settings.get("UID"),"STEP",day);
            if(cursorUser.moveToFirst()) {
                userSteps = cursorUser.getDouble(cursorUser.getColumnIndex("VALUE"));
            } else {
                userSteps = 0;
            }

            Cursor cursorCompetitor = localStorageWear.getDailyData(settings.get("PARTNER"),"STEP",day);
            if(cursorCompetitor.moveToFirst()){
                partnerSteps = cursorCompetitor.getDouble(cursorCompetitor.getColumnIndex("VALUE"));
            } else {
                partnerSteps = 0;
            }

            userGoal = Double.parseDouble(settings.get("GOAL"));
            if(settings.get("PARTNERGOAL") != null) {
                partnerGoal = Double.parseDouble(settings.get("PARTNERGOAL"));
            } else {
                partnerGoal = 1;
            }


            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            /* the wearable switched between modes */
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            Long time = System.currentTimeMillis();
            java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("HH:mm");
            String formattedTime = simpleDateFormat.format(new Date(time));

            float centerX = bounds.exactCenterX();
            float centerY = bounds.exactCenterY();

            float left = centerX-140;
            float right = centerX+140;
            float top = centerY-140;
            float bottom = centerY+140;

            Paint paintStepsUser = new Paint();
            paintStepsUser.setColor(Color.parseColor("#4CAF50"));
            paintStepsUser.setStyle(Paint.Style.STROKE);
            paintStepsUser.setStrokeWidth(10);

            paintStepsUser.setAntiAlias(true);

            Paint paintStepsCompetitor = new Paint();
            paintStepsCompetitor.setColor(Color.parseColor("#F44336"));
            paintStepsCompetitor.setStyle(Paint.Style.STROKE);
            paintStepsCompetitor.setStrokeWidth(10);
            paintStepsCompetitor.setAntiAlias(true);

            Paint paintActivity = new Paint();
            paintActivity.setColor(Color.parseColor("#2196F3"));
            paintActivity.setStyle(Paint.Style.STROKE);
            paintActivity.setStrokeWidth(10);
            paintActivity.setAntiAlias(true);

            Paint paintTime = new Paint();
            paintTime.setColor(Color.WHITE);
            paintTime.setTextSize(60);
            paintTime.setAntiAlias(true);

            Paint paintStepCounterUser = new Paint();
            paintStepCounterUser.setColor(Color.parseColor("#A6A6A6"));
            paintStepCounterUser.setTextSize(30);
            paintStepCounterUser.setAntiAlias(true);

            Paint paintStepCounterCompetitor = new Paint();
            paintStepCounterCompetitor.setColor(Color.parseColor("#A6A6A6"));
            paintStepCounterCompetitor.setTextSize(25);
            paintStepCounterCompetitor.setAntiAlias(true);

            float userSweepAngle = calcSweepAngle(userSteps,userGoal);
            float competitorSweepAngle = calcSweepAngle(partnerSteps, partnerGoal);

            //DRAW USERS GRAPHS
            RectF rectUser = new RectF(left,top,right,bottom);
            canvas.drawArc (rectUser, 180, userSweepAngle, false, paintStepsUser);
            canvas.drawArc(rectUser,180,-120,false,paintActivity);

            //DRAW COMPETITORS GRAPHS
            RectF rectCompetitor = new RectF(left+20,top+20,right-20,bottom-20);
            canvas.drawArc(rectCompetitor,180,competitorSweepAngle,false,paintStepsCompetitor);
            canvas.drawArc(rectCompetitor,180,-90,false,paintActivity);

            //DRAW CLOCK
            float timeXOffset = calcXOffset(formattedTime,paintTime,bounds);
            float timeYOffset = calcYOffset(formattedTime,paintTime,bounds);
            canvas.drawText(formattedTime,timeXOffset,timeYOffset,paintTime);

            //DRAW STEP COUNTERS

            float userStepXOffset = calcXOffset(String.valueOf(userSteps),paintStepCounterUser,bounds);
            float competitorXOffset = calcXOffset(String.valueOf(partnerSteps),paintStepCounterCompetitor,bounds);

            canvas.drawText(String.valueOf(userSteps),userStepXOffset,bounds.centerY()-80,paintStepCounterUser);
            canvas.drawText(String.valueOf(partnerSteps),competitorXOffset, bounds.centerY()-50,paintStepCounterCompetitor);


        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            /* the watch face became visible or invisible */
        }

        public float calcXOffset(String text, Paint paint, Rect bounds){
            float center = bounds.exactCenterX();
            float textLength = paint.measureText(text);
            return center - (textLength/2.0f);
        }

        public float calcYOffset(String text, Paint paint, Rect bounds){
            float center = bounds.exactCenterY();
            Rect textBounds = new Rect();
            paint.getTextBounds(text,0,text.length(),textBounds);
            float textHeight = textBounds.height();
            return center + (textHeight /2.0f);
        }

        public float calcSweepAngle(double steps, double goal){
            float progress = (float) ((steps/goal)*100);
            float sweepAngle = (float)(progress*1.8);
            if(sweepAngle > 180){
                sweepAngle = 180;
            }
            return sweepAngle;
        }

        private float batteryMonitor(){

            Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

            float batteryLevel = ((float)level / (float)scale)*100.0f;

            return batteryLevel;
        }
    }
}
