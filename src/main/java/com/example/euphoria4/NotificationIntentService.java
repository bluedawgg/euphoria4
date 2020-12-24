package com.example.euphoria4;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.example.euphoria4.ui.songs.SongsFragment;

import static com.example.euphoria4.ui.songs.SongsFragment.arr_dir;
import static com.example.euphoria4.ui.songs.SongsFragment.arr_songs;
import static com.example.euphoria4.ui.songs.SongsFragment.collapsedView;
import static com.example.euphoria4.ui.songs.SongsFragment.duration_text;
import static com.example.euphoria4.ui.songs.SongsFragment.player;
import static com.example.euphoria4.ui.songs.SongsFragment.sb;
import static com.example.euphoria4.ui.songs.SongsFragment.song_pos;

public class NotificationIntentService extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        switch(intent.getAction()) {
            case "pause_clicked": {
                //Toast.makeText(context, "Pause Clicked", Toast.LENGTH_SHORT).show();
                if(player.isPlaying()) {
                    player.pause();
                    collapsedView.setImageViewResource(R.id.button2, R.drawable.white_play_button);
                    SongsFragment.play.setImageResource(R.drawable.play_button);
                    SongsFragment.nm = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    SongsFragment.nm.notify(0, SongsFragment.mBuilder.build());
                }
                else if(!player.isPlaying()){
                    player.start();
                    collapsedView.setImageViewResource(R.id.button2, R.drawable.white_pause_button);
                    SongsFragment.play.setImageResource(R.drawable.pause_button);
                    SongsFragment.nm = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    SongsFragment.nm.notify(0, SongsFragment.mBuilder.build());
                }
                break;
            }
            case "next_clicked":{
                try {
                    if(SongsFragment.song_pos <= SongsFragment.arr_dir.length-1) {
                        SongsFragment.song_pos++;
                        SongsFragment.songTitle.setText(arr_songs[SongsFragment.song_pos]);
                        SongsFragment.songArtist.setText(SongsFragment.songsArtist.get(arr_songs[SongsFragment.song_pos]));
                        SongsFragment.imageView.setImageBitmap(SongsFragment.arr_art[SongsFragment.song_pos]);

                        collapsedView.setImageViewResource(R.id.button2,R.drawable.white_pause_button);
                        collapsedView.setImageViewBitmap(R.id.albumart,SongsFragment.arr_art[SongsFragment.song_pos]);
                        collapsedView.setTextViewText(R.id.title,arr_songs[SongsFragment.song_pos]);
                        collapsedView.setTextViewText(R.id.artist,SongsFragment.songsArtist.get(arr_songs[SongsFragment.song_pos]));
                        SongsFragment.nm = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        SongsFragment.nm.notify(0, SongsFragment.mBuilder.build());
                        //createNotificationChannel();
                        //makeNotif(arr_songs[song_pos],songsArtist.get(arr_songs[song_pos]),arr_art[song_pos]);
                        //SongsFragment.playMusic(SongsFragment.arr_dir[SongsFragment.song_pos]);
                        if(player.isPlaying()){
                            sb.setProgress(0);
                            player.stop();
                            player.release();
                        }
                        player = new MediaPlayer();
                        player.setDataSource(arr_dir[song_pos]);
                        player.prepare();

                        player.start();
                        sb.setProgress(0);
                        sb.setMax(player.getDuration());
                        int seconds = (int) Math.ceil(player.getDuration() / 1000);
                        int minutes = (int) seconds / 60;
                        seconds = (int) Math.ceil(seconds % 60);
                        seconds += 1;
                        if (seconds < 10) {
                            duration_text.setText(minutes + ":0" + seconds);
                        } else if (seconds >= 10) {
                            duration_text.setText(minutes + ":" + seconds);
                        }
                        Log.i("SongPos", "songpos: >>" + SongsFragment.song_pos);         //FORWARD PLAYER
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("SongPos", "songpos: >>" + SongsFragment.song_pos);
                }
                break;
            }
            case "prev_clicked":{
                try {
                    if(song_pos >= 0) {
                        SongsFragment.song_pos--;
                        SongsFragment.songTitle.setText(arr_songs[SongsFragment.song_pos]);
                        SongsFragment.songArtist.setText(SongsFragment.songsArtist.get(arr_songs[SongsFragment.song_pos]));
                        SongsFragment.imageView.setImageBitmap(SongsFragment.arr_art[SongsFragment.song_pos]);

                        collapsedView.setImageViewResource(R.id.button2,R.drawable.white_pause_button);
                        collapsedView.setImageViewBitmap(R.id.albumart,SongsFragment.arr_art[SongsFragment.song_pos]);
                        collapsedView.setTextViewText(R.id.title,arr_songs[SongsFragment.song_pos]);
                        collapsedView.setTextViewText(R.id.artist,SongsFragment.songsArtist.get(arr_songs[SongsFragment.song_pos]));
                        SongsFragment.nm = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        SongsFragment.nm.notify(0, SongsFragment.mBuilder.build());
                        //createNotificationChannel();
                        //makeNotif(arr_songs[song_pos],songsArtist.get(arr_songs[song_pos]),arr_art[song_pos]);
                        //SongsFragment.playMusic(SongsFragment.arr_dir[SongsFragment.song_pos]);
                        if(player.isPlaying()){
                            sb.setProgress(0);
                            player.stop();
                            player.release();
                        }

                        player = new MediaPlayer();
                        player.setDataSource(arr_dir[song_pos]);
                        player.prepare();
                        player.start();
                        sb.setProgress(0);
                        sb.setMax(player.getDuration());
                        int seconds = (int) Math.ceil(player.getDuration() / 1000);
                        int minutes = (int) seconds / 60;
                        seconds = (int) Math.ceil(seconds % 60);
                        seconds += 1;
                        if (seconds < 10) {
                            duration_text.setText(minutes + ":0" + seconds);
                        } else if (seconds >= 10) {
                            duration_text.setText(minutes + ":" + seconds);
                        }
                        Log.i("SongPos", "songpos: >>" + SongsFragment.song_pos);         //BACKWARD PLAYER
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("SongPos", "songpos: >>" + song_pos);
                }
                break;
            }
        }

    }

}
