package com.example.euphoria4;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import static com.example.euphoria4.ui.songs.SongsFragment.mBuilder;
import static com.example.euphoria4.ui.songs.SongsFragment.nm;
import static com.example.euphoria4.ui.songs.SongsFragment.player;

public class OnClearFromRecentsService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        //Code here
        if(nm!=null){
            nm.cancelAll();
        }
        stopSelf();
    }
}
