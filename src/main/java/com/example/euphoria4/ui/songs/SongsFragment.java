package com.example.euphoria4.ui.songs;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.euphoria4.ListAdapterBitmap;
import com.example.euphoria4.NotificationIntentService;
import com.example.euphoria4.R;
import com.example.euphoria4.MainActivity;
import com.example.euphoria4.ui.notifications.NotificationsViewModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class SongsFragment extends Fragment implements Runnable {

    private static final String CHANNEL_ID = "channel";
    public static String[] arr_songs;
    public static Bitmap[] arr_art;
    public static String[] arr_dir;
    public static RemoteViews collapsedView;
    public static NotificationCompat.Builder mBuilder;
    public static NotificationManager nm;

    public static ListAdapterBitmap listAdapter;

    ListView listView;
    public static ImageView imageView;

    ProgressBar pb;
    public static MediaPlayer player = new MediaPlayer();
    public static ImageButton play, forw, backw;
    public static SeekBar sb;
    Thread t = new Thread(this);
    Handler seekHandler = new Handler();
    boolean wasPlaying = false;
    public static int song_pos = 0;
    public static TextView seekBarHint, duration_text;
    public static TextView songTitle, songArtist;
    public static TreeMap<String, Bitmap> songsList = new TreeMap<>();
    public static TreeMap<String, String> songsDir = new TreeMap<>();
    public static TreeMap<String, String> songsArtist = new TreeMap<>();
    Bitmap global_test;

    private SongsViewModel mViewModel;
    public SongsFragment(){

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_songs, container, false);
        mViewModel =
                ViewModelProviders.of(this).get(SongsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_songs, container, false);

        listView = (ListView) root.findViewById(R.id.listView1);
        registerForContextMenu(listView);
        play = root.findViewById(R.id.button_play);
        play.setImageResource(R.drawable.play_button);
        sb = root.findViewById(R.id.seekB);
        sb.setProgress(0);

        pb = root.findViewById(R.id.progressbar1);
        imageView = root.findViewById(R.id.iv1);
        seekBarHint = root.findViewById(R.id.seekbhint);
        forw = root.findViewById(R.id.button_right);
        backw = root.findViewById(R.id.button_left);
        duration_text = root.findViewById(R.id.dur);
        Handler seekHandler = new Handler();
        songTitle = root.findViewById(R.id.songtitle);
        songArtist = root.findViewById(R.id.songartist);

        sb.setVisibility(View.INVISIBLE);
        duration_text.setVisibility(View.INVISIBLE);
        seekBarHint.setVisibility(View.INVISIBLE);
        seekUpdation();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (player.isPlaying()) {
                        try {
                            play.setImageResource(R.drawable.play_button);
                            player.pause();
                            createNotificationChannel();
                            makeNotifPaused(arr_songs[song_pos], songsArtist.get(arr_songs[song_pos]), arr_art[song_pos]);
                            sb.setProgress(player.getCurrentPosition());
                            Log.i("currpos", "curr pos is :" + player.getCurrentPosition());
                            Log.i("seekB", "curr seeb B pos is :" + sb.getProgress());
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Go on.. Play something.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (!player.isPlaying()) {
                        try {
                            player.start();
                            createNotificationChannel();
                            makeNotif(arr_songs[song_pos], songsArtist.get(arr_songs[song_pos]), arr_art[song_pos]);
                            play.setImageResource(R.drawable.pause_button);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Go on.. Play something.", Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Go on.. Play something.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        forw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (song_pos <= arr_dir.length - 1) {
                        song_pos++;
                        songTitle.setText(arr_songs[song_pos]);
                        songArtist.setText(songsArtist.get(arr_songs[song_pos]));
                        imageView.setImageBitmap(arr_art[song_pos]);
                        createNotificationChannel();
                        makeNotif(arr_songs[song_pos], songsArtist.get(arr_songs[song_pos]), arr_art[song_pos]);
                        playMusic(arr_dir[song_pos]);
                        Log.i("SongPos", "songpos: >>" + song_pos);         //FORWARD PLAYER
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("SongPos", "songpos: >>" + song_pos);
                }
            }
        });

        backw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (song_pos >= 0) {
                        song_pos--;
                        imageView.setImageBitmap(arr_art[song_pos]);
                        songTitle.setText(arr_songs[song_pos]);
                        songArtist.setText(songsArtist.get(arr_songs[song_pos]));
                        createNotificationChannel();
                        makeNotif(arr_songs[song_pos], songsArtist.get(arr_songs[song_pos]), arr_art[song_pos]);
                        playMusic(arr_dir[song_pos]);
                        Log.i("SongPos", "songpos: >>" + song_pos);           //BACKWARD PLAYER
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("SongPos", "songpos: >>" + song_pos);
                }
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f); //IN SECONDS
                int sec = x;
                int min = 0;
                int hour = 0;
                if (sec < 10) {
                    seekBarHint.setText("0:0" + sec);
                } else if (sec > 9 && sec < 60) {
                    seekBarHint.setText("0:" + sec);
                } else if (x >= 60) {
                    min = x / 60;
                    sec = x % 60;
                    if (sec < 10) {
                        seekBarHint.setText("" + min + ":0" + sec);
                    } else if (sec > 9 && sec < 60)
                        seekBarHint.setText("" + min + ":" + sec);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player != null) {
                    player.seekTo(seekBar.getProgress());
                }
            }
        });

        try {
            if (arr_dir == null)
                getMusic();
            listAdapter = new ListAdapterBitmap(getActivity(), arr_songs, arr_art);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        sb.setVisibility(View.VISIBLE);
                        duration_text.setVisibility(View.VISIBLE);
                        seekBarHint.setVisibility(View.VISIBLE);
                        song_pos = position;
                        songTitle.setText(arr_songs[position]);
                        songArtist.setText(songsArtist.get(arr_songs[position]));
                        Log.i("SongPos", "songpos: >>" + song_pos);
                        createNotificationChannel();
                        makeNotif(arr_songs[position], songsArtist.get(arr_songs[position]), arr_art[position]);
                        playMusic(arr_dir[position]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("data", "song is >> " + arr_dir[position]);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            new DoAsync().execute();
        } catch (Exception e) {

        }
        return root;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.song_context_menu, menu);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    public void seekUpdation() {
        try {
            sb.setProgress(player.getCurrentPosition());
            seekHandler.postDelayed(run, 1000);
        } catch (Exception e) {
            sb.setProgress(0);
        }
    }

    //###################### NOTIFICATIONS ###########################################################

    //    private void makeNotif(String song,String artist){
//        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(getActivity(),CHANNEL_ID)
//                .setSmallIcon(R.drawable.play_button)
//                .setContentTitle(song)
//                .setContentText(artist)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
//        int notificationId = 1;
//        notificationManager.notify(notificationId,mBuilder.build());
//    }
    private void makeNotif(String song, String artist, Bitmap bmp) {

        collapsedView = new RemoteViews(getActivity().getPackageName(), R.layout.custom_notif);

        Intent pause_intent = new Intent(getActivity(), NotificationIntentService.class);
        pause_intent.setAction("pause_clicked");
        pause_intent.putExtra("id", 0);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, pause_intent, 0);

        Intent next_intent = new Intent(getActivity(), NotificationIntentService.class);
        next_intent.setAction("next_clicked");
        next_intent.putExtra("id", 0);
        PendingIntent pi_next = PendingIntent.getBroadcast(getActivity(), 0, next_intent, 0);

        Intent prev_intent = new Intent(getActivity(), NotificationIntentService.class);
        prev_intent.setAction("prev_clicked");
        prev_intent.putExtra("id", 0);
        PendingIntent pi_prev = PendingIntent.getBroadcast(getActivity(), 0, prev_intent, 0);

        collapsedView.setOnClickPendingIntent(R.id.button2, pi);
        collapsedView.setOnClickPendingIntent(R.id.button3, pi_next);
        collapsedView.setOnClickPendingIntent(R.id.button4, pi_prev);
        ;
        collapsedView.setImageViewResource(R.id.button2, R.drawable.white_pause_button);
        collapsedView.setImageViewBitmap(R.id.albumart, bmp);
        collapsedView.setTextViewText(R.id.title, song);
        collapsedView.setTextViewText(R.id.artist, artist);
        Intent notInt =  new Intent(getActivity(), SongsFragment.class).setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP );
        mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setCustomContentView(collapsedView)
                .setContentIntent(PendingIntent.getActivity(getActivity(), 0, notInt, 0))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        nm = (android.app.NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, mBuilder.build());

    }

    private void makeNotifPaused(String song, String artist, Bitmap bmp) {

        collapsedView = new RemoteViews(getActivity().getPackageName(), R.layout.custom_notif);

        Intent pause_intent = new Intent(getActivity(), NotificationIntentService.class);
        pause_intent.setAction("pause_clicked");
        pause_intent.putExtra("id", 0);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, pause_intent, 0);

        Intent next_intent = new Intent(getActivity(), NotificationIntentService.class);
        next_intent.setAction("next_clicked");
        next_intent.putExtra("id", 0);
        PendingIntent pi_next = PendingIntent.getBroadcast(getActivity(), 0, next_intent, 0);

        Intent prev_intent = new Intent(getActivity(), NotificationIntentService.class);
        prev_intent.setAction("prev_clicked");
        prev_intent.putExtra("id", 0);
        PendingIntent pi_prev = PendingIntent.getBroadcast(getActivity(), 0, prev_intent, 0);

        collapsedView.setOnClickPendingIntent(R.id.button2, pi);
        collapsedView.setOnClickPendingIntent(R.id.button3, pi_next);
        collapsedView.setOnClickPendingIntent(R.id.button4, pi_prev);
        collapsedView.setImageViewResource(R.id.button2, R.drawable.white_play_button);
        collapsedView.setImageViewBitmap(R.id.albumart, bmp);
        collapsedView.setTextViewText(R.id.title, song);
        collapsedView.setTextViewText(R.id.artist, artist);
        Intent notInt =  new Intent(getActivity(), SongsFragment.class).setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP);
        mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setCustomContentView(collapsedView)
                .setContentIntent(PendingIntent.getActivity(getActivity(), 0,notInt, 0))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        nm = (android.app.NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, mBuilder.build());

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MusicPlayerNotif";
            String description = "channelONE";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //###################### NOTIFICATIONS ####################################################################################

    public void getMusic() throws IOException {

        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
        };
        final Cursor cursor = getActivity().getContentResolver().query(uri,
                cursor_cols, null, null, null);
        int counter = 0;
        while (cursor.moveToNext()) {
            String artist = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            String track = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String data = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            Long albumId = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            int duration = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            //String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART));
            //Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            //Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            Bitmap bitmap = null;
            try {
                //bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), albumArtUri);
                //bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300,true);
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_audiotrack_png);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                    global_test = bitmap;
                }
                songsList.put(track, bitmap);
                songsDir.put(track, data);
                songsArtist.put(track, artist);

            } catch (Exception exception) {
            }
            counter++;
            if (counter == 100)
                break;
        }

        arr_songs = songsList.keySet().toArray(new String[songsList.size()]);
        arr_art = songsList.values().toArray(new Bitmap[songsList.size()]);
        arr_dir = songsDir.values().toArray(new String[songsList.size()]);
        Log.i("total", "total songs are: " + counter);
    }

    private class DoAsync extends AsyncTask<Void, Integer, String> {
        @Override
        protected String doInBackground(Void... voids) {
            if(arr_art[0].sameAs(global_test)) {
                final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                final String[] cursor_cols = {MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DURATION,
                };
                final Cursor cursor = getActivity().getContentResolver().query(uri,
                        cursor_cols, null, null, null);
                int counter = 0;
                while (cursor.moveToNext()) {
                    String artist = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String album = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String track = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String data = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    Long albumId = cursor.getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    int duration = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    //String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART));
                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
                    Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_audiotrack_black_24dp);
                    Bitmap audi_symb = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_audiotrack_black_24dp);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), albumArtUri);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                        songsList.put(track, bitmap);
                        songsDir.put(track, data);
                        songsArtist.put(track, artist);
                    } catch (Exception exception) {
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_audiotrack_black_24dp);
                    }
                    counter++;
                    if (counter == 100)
                        break;
                    publishProgress(counter);
                }
            }
            return "Done";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pb.setProgress(values[0] % 100);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pb.setVisibility(View.INVISIBLE);
            arr_songs = songsList.keySet().toArray(new String[songsList.size()]);
            arr_art = songsList.values().toArray(new Bitmap[songsList.size()]);
            listAdapter = new ListAdapterBitmap(getActivity(), arr_songs, arr_art);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        sb.setVisibility(View.VISIBLE);
                        duration_text.setVisibility(View.VISIBLE);
                        seekBarHint.setVisibility(View.VISIBLE);
                        song_pos = position;
                        songTitle.setText(arr_songs[position]);
                        songArtist.setText(songsArtist.get(arr_songs[position]));
                        Log.i("SongPos", "songpos: >>" + song_pos);
                        imageView.setImageBitmap(arr_art[position]);
                        createNotificationChannel();
                        makeNotif(arr_songs[position], songsArtist.get(arr_songs[position]), arr_art[position]);
                        playMusic(arr_dir[position]);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("data", "song is >> " + arr_dir[position]);
                    }
                }
            });
            //Log.i("total","total songs are: " + counter);
        }
    }

    public void playMusic(String path) throws Exception {
        if (player.isPlaying()) {
            sb.setProgress(0);
            player.stop();
            player.release();
        }
        play.setImageResource(R.drawable.pause_button);
        player = new MediaPlayer();
        player.setDataSource(path);
        player.prepare();
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player.pause();
                play.setImageResource(R.drawable.play_button);
                player.reset();
                Toast.makeText(getActivity(), "Song completed. Play another one :D", Toast.LENGTH_SHORT).show();
            }
        });
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
        //new Thread(this).start();
        t.start();
    }

    @Override
    public void run() {
        int currentPosition = player.getCurrentPosition();
        int total = player.getDuration();

        while (player.isPlaying() && currentPosition <= total) {
            try {
                Thread.sleep(1000);
                currentPosition = player.getCurrentPosition();
                sb.setProgress(currentPosition);
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
        }
    }

}
