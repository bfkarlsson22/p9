package com.example.brand.p9;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by EmilSiegenfeldt on 01/12/2016.
 */

public class WatchFaceService extends CanvasWatchFaceService {
    @Override
    public Engine onCreateEngine(){
        return new Engine();
    }
    private class Engine extends CanvasWatchFaceService.Engine{
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
            SQLite sqLite = new SQLite(getApplicationContext());
            Cursor data = sqLite.getData();

            String cursorData = DatabaseUtils.dumpCursorToString(data);
            Log.d("CURSOR DATA",cursorData);
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            /* the wearable switched between modes */
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            /* the watch face became visible or invisible */
        }
    }
}
