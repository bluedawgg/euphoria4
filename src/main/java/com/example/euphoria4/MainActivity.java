package com.example.euphoria4;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.euphoria4.ui.playlists.PlaylistFragment;
import com.example.euphoria4.ui.songs.SongsFragment;
import com.example.euphoria4.ui.tools.ToolsFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.transition.FragmentTransitionSupport;

import static com.example.euphoria4.ui.songs.SongsFragment.arr_art;
import static com.example.euphoria4.ui.songs.SongsFragment.arr_dir;
import static com.example.euphoria4.ui.songs.SongsFragment.arr_songs;
import static com.example.euphoria4.ui.songs.SongsFragment.mBuilder;
import static com.example.euphoria4.ui.songs.SongsFragment.nm;
import static com.example.euphoria4.ui.songs.SongsFragment.player;
import com.example.euphoria4.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;
    public static String[] arr_songs_main=new String[10000];
    public static Bitmap[] arr_art_main=new Bitmap[10000];
    public static String[] arr_dir_main=new String[10000];
    public static final Fragment fragment1 = new HomeFragment();
    public static final Fragment fragment2 = new SongsFragment();
    public static final Fragment fragment3 = new PlaylistFragment();
    public static final Fragment fragment4 = new ToolsFragment();
    public final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getBaseContext(), OnClearFromRecentsService.class));
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportActionBar().setTitle("Euphoria");
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home,R.id.navigation_songs,R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);

        //fm.beginTransaction().add(R.id.container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.container, fragment2, "2").hide(fragment2).hide(fragment4).hide(fragment3).commit();
        fm.beginTransaction().add(R.id.container,fragment1, "1").commit();
        fm.beginTransaction().add(R.id.container,fragment3, "3").commit();
        fm.beginTransaction().add(R.id.container,fragment4, "4").commit();
        active = fragment1;
        checkPermissions();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(fragment2).hide(fragment4).hide(fragment3).show(fragment1).commit();
                    getSupportActionBar().setTitle("Euphoria");
                    active = fragment1;
                    return true;

                case R.id.navigation_songs:
                    fm.beginTransaction().hide(fragment1).hide(fragment4).hide(fragment3).show(fragment2).commit();
                    active = fragment2;
                    getSupportActionBar().setTitle("Songs");
                    return true;
                case R.id.navigation_playlist:
                    fm.beginTransaction().hide(fragment1).hide(fragment4).hide(fragment2).show(fragment3).commit();
                    active = fragment3;
                    getSupportActionBar().setTitle("Playlists");
                    return true;
                case R.id.navigation_tools:
                    fm.beginTransaction().hide(fragment1).hide(fragment2).hide(fragment3).show(fragment4).commit();
                    active = fragment4;
                    getSupportActionBar().setTitle("Tools");
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        //Toast.makeText(this, "STOP CALLED", Toast.LENGTH_SHORT).show();
        //SongsFragment.nm.cancelAll();
    }

    @Override
    protected void onDestroy() {
        //Toast.makeText(this, "DESTROY CALLED", Toast.LENGTH_SHORT).show();
        if(nm!=null){
            nm.cancelAll();
        }
        super.onDestroy();
    }

    public void checkPermissions(){
//        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
//        if(result==PackageManager.PERMISSION_GRANTED){
//            return true;
//        }
//        else{
//            return false;
//        }
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);// != PackageManager.PERMISSION_GRANTED;
        if (result != PackageManager.PERMISSION_GRANTED ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // storage-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
