package com.sube.movil;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import org.json.JSONArray;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;


public class MainActivity extends FragmentActivity implements View.OnClickListener , ListInterface{

    private static ResideMenu resideMenu;
    private static ResideMenuItem itemHome;
    private ResideMenuItem misube;
    private ResideMenuItem itemProfile;
    private ResideMenuItem saldo;
    private ResideMenuItem contacto;
    private ResideMenuItem compartir;
    fragment2 fragment2;
    LocationManager lm;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppRate.with(this)//Llamo ventana para calificar aplicacion
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(1) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();
        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
        setUpMenu();
        fragment2 = new fragment2();
        if( savedInstanceState == null )
            changeFragment(new fragment1());
        checkLocation();
    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.fondo);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome     = new ResideMenuItem(this, R.drawable.puntosrecarga, "Mapa");
        itemProfile  = new ResideMenuItem(this, R.drawable.cuenta,  "Centros"+"\n"+"Venta"+"\n"+"Recarga");
        misube = new ResideMenuItem(this, R.drawable.horarios, "Mi Sube");
        contacto = new ResideMenuItem(this, R.drawable.contacto,"Contacto");
        compartir = new ResideMenuItem(this, R.drawable.compartir,"Compartí");
        saldo= new ResideMenuItem(this,R.drawable.horarios, "Mi saldo");


        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        misube.setOnClickListener(this);
        saldo.setOnClickListener(this);
        contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("plain/text");
                sendIntent.setData(Uri.parse("marceloespinoza00@gmail.com"));
                sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Sube Movil");
                startActivity(sendIntent);
            }
        });
        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.sube.movil");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(misube, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(contacto, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(compartir,ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(saldo,ResideMenu.DIRECTION_RIGHT);


        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        if (view == itemHome){
            changeFragment(new fragment1());
        }else if (view == itemProfile){
            changeFragment(new fragment2());
        }else if (view == saldo) {
            changeFragment(new fragment3());
        }else if (view == misube) {
            changeFragment(new fragment4());}
        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        //llama cuando el menu esta abierto
        @Override
        public void openMenu() {

        }

        //llama cuando el menu esta cerrado
        @Override
        public void closeMenu() {

        }
    };

    private void changeFragment(Fragment targetFragment){
        if(targetFragment instanceof fragment2){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, fragment2, "fragment2")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }else{

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, targetFragment, "fragment")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

    }

    public void checkLocation(){
        Boolean network_enabled=null;
        lm =(LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!network_enabled){
            new MaterialDialog.Builder(this).title("Advertencia").content("Para visualizar los puntos de venta debe permitir el acceso a su ubicacion mediante Wifi y Redes Moviles")
                    .negativeText("Cancelar").neutralText("Activar").
                    onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(viewIntent);
                        }
                    }).show();
        }

    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){

        return resideMenu;
    }

    @Override
    public void onCreateList(JSONArray list) {

        fragment2.getList(list);
    }
}