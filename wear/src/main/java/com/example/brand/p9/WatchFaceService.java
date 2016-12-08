package com.example.brand.p9;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Date;
import java.util.Locale;

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
        private double competitorSteps;
        private double competitorGoal;

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
            Log.d("TIK","TOK");

            DataSenderWear dataSender = new DataSenderWear(getApplicationContext());
            dataSender.sendData();

            LocalStorageWear db = new LocalStorageWear(getApplicationContext());

            Long time = System.currentTimeMillis();
            java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("E-d-M-y");
            String day = simpleDateFormat.format(new Date(time));

            Cursor cursor = db.getDaily(0,"STEP",day);
            cursor.moveToFirst();
            userSteps = cursor.getDouble(cursor.getColumnIndex("VALUE"));
            userGoal = 100;

            competitorSteps = 100;
            competitorGoal = 200;

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
            float competitorSweepAngle = calcSweepAngle(competitorSteps,competitorGoal);

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
            float competitorXOffset = calcXOffset(String.valueOf(competitorSteps),paintStepCounterCompetitor,bounds);

            canvas.drawText(String.valueOf(userSteps),userStepXOffset,bounds.centerY()-80,paintStepCounterUser);
            canvas.drawText(String.valueOf(competitorSteps),competitorXOffset, bounds.centerY()-50,paintStepCounterCompetitor);


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
    }
}