package com.wordpress.zackleaman.materialtablayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Zack on 8/15/2016.
 * This class is used as the MainActivity for the application and will handle home page operations.
 * This includes creating the material tab layout with fragment classes:
 * HomeFragment, ReviewLibrary, SendMessageFragment, and NewMessages
 * These Fragments are labeled as:
 * Home, Library, Create, and Inbox
 */
public class MainActivity extends BaseDemoDriveActivity {

    public static boolean isFirstTimeOpening = true;
    public static boolean isJustOpened = true;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private RelativeLayout mainRelativeLayout;
    static Context myContext;



    private int Cur_State = 1;
    private final int Query_State = 1;
    private final int Retrieve_State = 2;
    private final int Create_State = 3;
    private boolean signedIn;
    public static String EXISTING_FILE_ID = "NONE";
    private ArrayList<String> encouragementList = new ArrayList<>();
    private ArrayList<String> notificationEncouragementList = new ArrayList<>();
    private String homeEncouragement = "";
    private Activity thisActivity;
    private boolean bIsFirstTimeAppOpened;
    public static boolean bNeedToClear = false;
    private int pageIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);

        try {
            Bundle extras = getIntent().getExtras();
            String clearGAC;
            String CState;
            String cantFindDrive;
            String startPage;

            if (extras != null) {
                clearGAC = extras.getString("clearGAC");
                CState = extras.getString("curState");
                cantFindDrive = extras.getString("cantFindDrive");
                startPage = extras.getString("startPage");
                if(clearGAC != null) {
                    if (clearGAC.equals("clearGAC")) {
                        Log.d("onNewIntent", "got clearGAC and equals");
                        signedIn = false;
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("signedIn", true);
                        editor.putBoolean("signedIn_Pressed", true);
                        editor.commit();
                        notificationEncouragementList.clear();
                        saveArray(notificationEncouragementList, "notificationEncouragementList");
                        bNeedToClear = true;
                        Intent intent = new Intent(this, PermissionsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                if(CState != null) {
                    if (CState.equals("3")) {
                        try {
                            Cur_State = Integer.parseInt(CState);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(cantFindDrive != null){
                    if(cantFindDrive.equals("cantFindDrive")){
                        try{
                            DialogPopup dialog = new DialogPopup();
                            Bundle args = new Bundle();
                            args.putString(DialogPopup.DIALOG_TYPE, DialogPopup.CANT_FIND_DRIVE);
                            dialog.setArguments(args);
                            dialog.setCancelable(false);
                            dialog.show(getSupportFragmentManager(), "cant-find-drive");

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                if(startPage != null){
                    if(startPage.equals("Home")){
                        pageIndex = 0;
                    }else if(startPage.equals("Library")){
                        pageIndex = 1;
                    }else if(startPage.equals("Create")){
                        pageIndex = 2;
                    }else if(startPage.equals("Inbox")){
                        pageIndex = 3;
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        thisActivity = this;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        signedIn = sp.getBoolean("signedIn",false);
        EXISTING_FILE_ID = sp.getString("EXISTING_FILE_ID","NONE");
        loadArray(encouragementList,getApplicationContext(),"encouragementList");
        loadArray(notificationEncouragementList,getApplicationContext(),"notificationEncouragementList");
        homeEncouragement = sp.getString("homeEncouragement","/nEncouragement/n /nNo Entries in Encouragement/nnone/n3/nAlarm Off");

        bIsFirstTimeAppOpened = sp.getBoolean("bIsFirstTimeAppOpened",true);


        mTabLayout = (TabLayout)findViewById(R.id.tabLayout);
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mainRelativeLayout = (RelativeLayout)findViewById(R.id.mainRelativeLayout);

        isFirstTimeOpening = loadBoolean("isFirstTimeOpening",getApplicationContext());
        isJustOpened = true;

        int picChoice = -1;
        try {
            picChoice = Integer.parseInt(sp.getString("backgroundSelection", "-1"));
        }catch (Exception e){
            e.printStackTrace();
        }
        if(picChoice == -1) {
            Random r = new Random();
            picChoice = r.nextInt(60);
        }
        if(picChoice == 0){
            mainRelativeLayout.setBackgroundResource(R.drawable.ariedlandscape);
        }else if(picChoice == 1){
            mainRelativeLayout.setBackgroundResource(R.drawable.brickwall);
        }else if(picChoice == 2){
            mainRelativeLayout.setBackgroundResource(R.drawable.city);
        }else if(picChoice == 3){
            mainRelativeLayout.setBackgroundResource(R.drawable.mountains1);
        }else if(picChoice == 4){
            mainRelativeLayout.setBackgroundResource(R.drawable.mountains2);
        }else if(picChoice == 5){
            mainRelativeLayout.setBackgroundResource(R.drawable.roof);
        }else if(picChoice == 6){
            mainRelativeLayout.setBackgroundResource(R.drawable.sea1);
        }else if(picChoice == 7){
            mainRelativeLayout.setBackgroundResource(R.drawable.sea2);
        }else if(picChoice == 8){
            mainRelativeLayout.setBackgroundResource(R.drawable.sea3);
        }else if(picChoice == 9){
            mainRelativeLayout.setBackgroundResource(R.drawable.sky1);
        }else if(picChoice == 10){
            mainRelativeLayout.setBackgroundResource(R.drawable.sky2);
        }else if(picChoice == 11){
            mainRelativeLayout.setBackgroundResource(R.drawable.sunset);
        }else if(picChoice == 12){
            mainRelativeLayout.setBackgroundResource(R.drawable.tarp);
        }else if(picChoice == 13){
            mainRelativeLayout.setBackgroundResource(R.drawable.wheatfield);
        }else if(picChoice == 14){
            mainRelativeLayout.setBackgroundResource(R.drawable.city_2);
        }else if(picChoice == 15){
            mainRelativeLayout.setBackgroundResource(R.drawable.crumbled_paper);
        }else if(picChoice == 16){
            mainRelativeLayout.setBackgroundResource(R.drawable.forest);
        }else if(picChoice == 17){
            mainRelativeLayout.setBackgroundResource(R.drawable.hut_wall);
        }else if(picChoice == 18){
            mainRelativeLayout.setBackgroundResource(R.drawable.sea_4);
        }else if(picChoice == 19){
            mainRelativeLayout.setBackgroundResource(R.drawable.shore);
        }else if(picChoice == 20){
            mainRelativeLayout.setBackgroundResource(R.drawable.train);
        }else if(picChoice == 21){
            mainRelativeLayout.setBackgroundResource(R.drawable.tropic_2);
        }else if(picChoice == 22){
            mainRelativeLayout.setBackgroundResource(R.drawable.wood);
        }else if(picChoice == 23){
            mainRelativeLayout.setBackgroundResource(R.drawable.aridcliff);
        }else if(picChoice == 24){
            mainRelativeLayout.setBackgroundResource(R.drawable.aridroad);
        }else if(picChoice == 25){
            mainRelativeLayout.setBackgroundResource(R.drawable.brickwall2);
        }else if(picChoice == 26){
            mainRelativeLayout.setBackgroundResource(R.drawable.cementwall);
        }else if(picChoice == 27){
            mainRelativeLayout.setBackgroundResource(R.drawable.city3);
        }else if(picChoice == 28){
            mainRelativeLayout.setBackgroundResource(R.drawable.crackedsoil);
        }else if(picChoice == 29){
            mainRelativeLayout.setBackgroundResource(R.drawable.crumbledpaper2);
        }else if(picChoice == 30){
            mainRelativeLayout.setBackgroundResource(R.drawable.elephant);
        }else if(picChoice == 31){
            mainRelativeLayout.setBackgroundResource(R.drawable.emeraldbrick);
        }else if(picChoice == 32){
            mainRelativeLayout.setBackgroundResource(R.drawable.fabric);
        }else if(picChoice == 33){
            mainRelativeLayout.setBackgroundResource(R.drawable.floortiled1);
        }else if(picChoice == 34){
            mainRelativeLayout.setBackgroundResource(R.drawable.floortiled2);
        }else if(picChoice == 35){
            mainRelativeLayout.setBackgroundResource(R.drawable.flowers);
        }else if(picChoice == 36){
            mainRelativeLayout.setBackgroundResource(R.drawable.forestmountain2);
        }else if(picChoice == 37){
            mainRelativeLayout.setBackgroundResource(R.drawable.giraffe);
        }else if(picChoice == 38){
            mainRelativeLayout.setBackgroundResource(R.drawable.grungemap);
        }else if(picChoice == 39){
            mainRelativeLayout.setBackgroundResource(R.drawable.jellyfish);
        }else if(picChoice == 40){
            mainRelativeLayout.setBackgroundResource(R.drawable.metalstripes);
        }else if(picChoice == 41){
            mainRelativeLayout.setBackgroundResource(R.drawable.mountainpeak2);
        }else if(picChoice == 42){
            mainRelativeLayout.setBackgroundResource(R.drawable.ocean);
        }else if(picChoice == 43){
            mainRelativeLayout.setBackgroundResource(R.drawable.owl);
        }else if(picChoice == 44){
            mainRelativeLayout.setBackgroundResource(R.drawable.panda);
        }else if(picChoice == 45){
            mainRelativeLayout.setBackgroundResource(R.drawable.patternedbrick);
        }else if(picChoice == 46){
            mainRelativeLayout.setBackgroundResource(R.drawable.prairiedog);
        }else if(picChoice == 47){
            mainRelativeLayout.setBackgroundResource(R.drawable.redpanda);
        }else if(picChoice == 48){
            mainRelativeLayout.setBackgroundResource(R.drawable.rockformation);
        }else if(picChoice == 49){
            mainRelativeLayout.setBackgroundResource(R.drawable.savannah1);
        }else if(picChoice == 50){
            mainRelativeLayout.setBackgroundResource(R.drawable.savannah2);
        }else if(picChoice == 51){
            mainRelativeLayout.setBackgroundResource(R.drawable.snowymountain1);
        }else if(picChoice == 52){
            mainRelativeLayout.setBackgroundResource(R.drawable.snowymountain2);
        }else if(picChoice == 53){
            mainRelativeLayout.setBackgroundResource(R.drawable.stonedoorway);
        }else if(picChoice == 54){
            mainRelativeLayout.setBackgroundResource(R.drawable.tiger);
        }else if(picChoice == 55){
            mainRelativeLayout.setBackgroundResource(R.drawable.tree);
        }else if(picChoice == 56){
            mainRelativeLayout.setBackgroundResource(R.drawable.tropic4);
        }else if(picChoice == 57){
            mainRelativeLayout.setBackgroundResource(R.drawable.wildwest1);
        }else if(picChoice == 58){
            mainRelativeLayout.setBackgroundResource(R.drawable.wildwest2);
        }else if(picChoice == 59){
            mainRelativeLayout.setBackgroundResource(R.drawable.woodendoors);
        }else if(picChoice == 60){
            mainRelativeLayout.setBackgroundResource(R.drawable.plain);
        }


        mViewPagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager());

        mViewPagerAdapter.addFragments(new HomeFragment(), "Home");
        mViewPagerAdapter.addFragments(new ReviewLibrary(), "Library");
        mViewPagerAdapter.addFragments(new SendMessageFragment(), "Create");
        mViewPagerAdapter.addFragments(new NewMessages(), "Inbox");

        myContext = getApplicationContext();
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_home_white_36dp);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_view_agenda_white_36dp);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_create_white_36dp);
        mTabLayout.getTabAt(3).setIcon(R.drawable.message);

        // Set the page to start on
        if(pageIndex != -1){
            selectPage(pageIndex);
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        thisActivity = this;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        signedIn = sp.getBoolean("signedIn",false);
        EXISTING_FILE_ID = sp.getString("EXISTING_FILE_ID","NONE");
        loadArray(encouragementList,getApplicationContext(),"encouragementList");
        loadArray(notificationEncouragementList,getApplicationContext(),"notificationEncouragementList");
        homeEncouragement = sp.getString("homeEncouragement","/nEncouragement/n /nNo Entries in Encouragement/nnone/n3/nAlarm Off");

        mTabLayout = (TabLayout)findViewById(R.id.tabLayout);
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mainRelativeLayout = (RelativeLayout)findViewById(R.id.mainRelativeLayout);

        isFirstTimeOpening = loadBoolean("isFirstTimeOpening",getApplicationContext());
        isJustOpened = true;

        int picChoice = -1;
        try {
            picChoice = Integer.parseInt(sp.getString("backgroundSelection", "-1"));
        }catch (Exception e){
            e.printStackTrace();
        }
        if(picChoice == -1) {
            Random r = new Random();
            picChoice = r.nextInt(60);
        }
        if(picChoice == 0){
            mainRelativeLayout.setBackgroundResource(R.drawable.ariedlandscape);
        }else if(picChoice == 1){
            mainRelativeLayout.setBackgroundResource(R.drawable.brickwall);
        }else if(picChoice == 2){
            mainRelativeLayout.setBackgroundResource(R.drawable.city);
        }else if(picChoice == 3){
            mainRelativeLayout.setBackgroundResource(R.drawable.mountains1);
        }else if(picChoice == 4){
            mainRelativeLayout.setBackgroundResource(R.drawable.mountains2);
        }else if(picChoice == 5){
            mainRelativeLayout.setBackgroundResource(R.drawable.roof);
        }else if(picChoice == 6){
            mainRelativeLayout.setBackgroundResource(R.drawable.sea1);
        }else if(picChoice == 7){
            mainRelativeLayout.setBackgroundResource(R.drawable.sea2);
        }else if(picChoice == 8){
            mainRelativeLayout.setBackgroundResource(R.drawable.sea3);
        }else if(picChoice == 9){
            mainRelativeLayout.setBackgroundResource(R.drawable.sky1);
        }else if(picChoice == 10){
            mainRelativeLayout.setBackgroundResource(R.drawable.sky2);
        }else if(picChoice == 11){
            mainRelativeLayout.setBackgroundResource(R.drawable.sunset);
        }else if(picChoice == 12){
            mainRelativeLayout.setBackgroundResource(R.drawable.tarp);
        }else if(picChoice == 13){
            mainRelativeLayout.setBackgroundResource(R.drawable.wheatfield);
        }else if(picChoice == 14){
            mainRelativeLayout.setBackgroundResource(R.drawable.city_2);
        }else if(picChoice == 15){
            mainRelativeLayout.setBackgroundResource(R.drawable.crumbled_paper);
        }else if(picChoice == 16){
            mainRelativeLayout.setBackgroundResource(R.drawable.forest);
        }else if(picChoice == 17){
            mainRelativeLayout.setBackgroundResource(R.drawable.hut_wall);
        }else if(picChoice == 18){
            mainRelativeLayout.setBackgroundResource(R.drawable.sea_4);
        }else if(picChoice == 19){
            mainRelativeLayout.setBackgroundResource(R.drawable.shore);
        }else if(picChoice == 20){
            mainRelativeLayout.setBackgroundResource(R.drawable.train);
        }else if(picChoice == 21){
            mainRelativeLayout.setBackgroundResource(R.drawable.tropic_2);
        }else if(picChoice == 22){
            mainRelativeLayout.setBackgroundResource(R.drawable.wood);
        }else if(picChoice == 23){
            mainRelativeLayout.setBackgroundResource(R.drawable.aridcliff);
        }else if(picChoice == 24){
            mainRelativeLayout.setBackgroundResource(R.drawable.aridroad);
        }else if(picChoice == 25){
            mainRelativeLayout.setBackgroundResource(R.drawable.brickwall2);
        }else if(picChoice == 26){
            mainRelativeLayout.setBackgroundResource(R.drawable.cementwall);
        }else if(picChoice == 27){
            mainRelativeLayout.setBackgroundResource(R.drawable.city3);
        }else if(picChoice == 28){
            mainRelativeLayout.setBackgroundResource(R.drawable.crackedsoil);
        }else if(picChoice == 29){
            mainRelativeLayout.setBackgroundResource(R.drawable.crumbledpaper2);
        }else if(picChoice == 30){
            mainRelativeLayout.setBackgroundResource(R.drawable.elephant);
        }else if(picChoice == 31){
            mainRelativeLayout.setBackgroundResource(R.drawable.emeraldbrick);
        }else if(picChoice == 32){
            mainRelativeLayout.setBackgroundResource(R.drawable.fabric);
        }else if(picChoice == 33){
            mainRelativeLayout.setBackgroundResource(R.drawable.floortiled1);
        }else if(picChoice == 34){
            mainRelativeLayout.setBackgroundResource(R.drawable.floortiled2);
        }else if(picChoice == 35){
            mainRelativeLayout.setBackgroundResource(R.drawable.flowers);
        }else if(picChoice == 36){
            mainRelativeLayout.setBackgroundResource(R.drawable.forestmountain2);
        }else if(picChoice == 37){
            mainRelativeLayout.setBackgroundResource(R.drawable.giraffe);
        }else if(picChoice == 38){
            mainRelativeLayout.setBackgroundResource(R.drawable.grungemap);
        }else if(picChoice == 39){
            mainRelativeLayout.setBackgroundResource(R.drawable.jellyfish);
        }else if(picChoice == 40){
            mainRelativeLayout.setBackgroundResource(R.drawable.metalstripes);
        }else if(picChoice == 41){
            mainRelativeLayout.setBackgroundResource(R.drawable.mountainpeak2);
        }else if(picChoice == 42){
            mainRelativeLayout.setBackgroundResource(R.drawable.ocean);
        }else if(picChoice == 43){
            mainRelativeLayout.setBackgroundResource(R.drawable.owl);
        }else if(picChoice == 44){
            mainRelativeLayout.setBackgroundResource(R.drawable.panda);
        }else if(picChoice == 45){
            mainRelativeLayout.setBackgroundResource(R.drawable.patternedbrick);
        }else if(picChoice == 46){
            mainRelativeLayout.setBackgroundResource(R.drawable.prairiedog);
        }else if(picChoice == 47){
            mainRelativeLayout.setBackgroundResource(R.drawable.redpanda);
        }else if(picChoice == 48){
            mainRelativeLayout.setBackgroundResource(R.drawable.rockformation);
        }else if(picChoice == 49){
            mainRelativeLayout.setBackgroundResource(R.drawable.savannah1);
        }else if(picChoice == 50){
            mainRelativeLayout.setBackgroundResource(R.drawable.savannah2);
        }else if(picChoice == 51){
            mainRelativeLayout.setBackgroundResource(R.drawable.snowymountain1);
        }else if(picChoice == 52){
            mainRelativeLayout.setBackgroundResource(R.drawable.snowymountain2);
        }else if(picChoice == 53){
            mainRelativeLayout.setBackgroundResource(R.drawable.stonedoorway);
        }else if(picChoice == 54){
            mainRelativeLayout.setBackgroundResource(R.drawable.tiger);
        }else if(picChoice == 55){
            mainRelativeLayout.setBackgroundResource(R.drawable.tree);
        }else if(picChoice == 56){
            mainRelativeLayout.setBackgroundResource(R.drawable.tropic4);
        }else if(picChoice == 57){
            mainRelativeLayout.setBackgroundResource(R.drawable.wildwest1);
        }else if(picChoice == 58){
            mainRelativeLayout.setBackgroundResource(R.drawable.wildwest2);
        }else if(picChoice == 59){
            mainRelativeLayout.setBackgroundResource(R.drawable.woodendoors);
        }else if(picChoice == 60){
            mainRelativeLayout.setBackgroundResource(R.drawable.plain);
        }


        mViewPagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager());

        mViewPagerAdapter.addFragments(new HomeFragment(), "Home");
        mViewPagerAdapter.addFragments(new ReviewLibrary(), "Library");
        mViewPagerAdapter.addFragments(new SendMessageFragment(), "Create");
        mViewPagerAdapter.addFragments(new NewMessages(), "Inbox");

        myContext = getApplicationContext();
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_home_white_36dp);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_view_agenda_white_36dp);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_create_white_36dp);
        mTabLayout.getTabAt(3).setIcon(R.drawable.message);



    }

    /**
     * Used to select the fragment page index on the material design tab layout
     * @param pageIndex
     */
    void selectPage(int pageIndex){
        mTabLayout.setScrollPosition(pageIndex,0f,true);
        mViewPager.setCurrentItem(pageIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.mainRelativeLayout));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        myOptionMenu = menu;

        if(signedIn){
            menu.getItem(1).setVisible(false);
        }else{
            menu.getItem(2).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivityForResult(intent,0);
            return true;
        }

        if(id == R.id.action_sign_in){
            signedIn = true;
            isFirstTimeOpening = true;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("signedIn", true);
            editor.putBoolean("signedIn_Pressed", true);
            editor.putBoolean("isFirstTimeOpening",true);
            editor.commit();
            clearGoogleApiClient();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            inAccountPicker = true;
            finish();
            return true;
        }

        if (id == R.id.action_sign_out) {

            DialogPopup dialog = new DialogPopup();
            Bundle args = new Bundle();
            args.putString(DialogPopup.DIALOG_TYPE, DialogPopup.SIGN_OUT);
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "sign-out");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Boolean loadBoolean(String stringName, Context mContext){
        SharedPreferences mSharedPreference1=PreferenceManager.getDefaultSharedPreferences(mContext);
        Boolean sKey = mSharedPreference1.getBoolean("SMS_" + stringName,true);
        return sKey;
    }

    private boolean saveArray(List<String> sKey, String arrayName){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor mEdit1=sp.edit();
        mEdit1.putInt("SMS_"+arrayName+"_size",sKey.size());

        for(int i=0;i<sKey.size();i++){
            mEdit1.remove("SMS_"+arrayName+i);
            mEdit1.putString("SMS_"+arrayName+i,sKey.get(i));
        }

        return mEdit1.commit();
    }

    private static void loadArray(List<String>sKey, Context mContext, String arrayName){
        SharedPreferences mSharedPreference1=PreferenceManager.getDefaultSharedPreferences(mContext);
        sKey.clear();
        int size=mSharedPreference1.getInt("SMS_"+arrayName+"_size",0);

        for(int i=0;i<size;i++){
            sKey.add(mSharedPreference1.getString("SMS_"+arrayName+i,null));
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        if(getGoogleApiClient() != null) {
            if (bNeedToClear) {
                clearGoogleApiClient();

            }
            if (isJustOpened) {

                isJustOpened = false;
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                signedIn = sp.getBoolean("signedIn", false);
                if (signedIn) {

                    if (Cur_State == Query_State) {

                        Query query = new Query.Builder()
                                .addFilter(Filters.contains(SearchableField.TITLE, ""))
                                .build();
                        Drive.DriveApi.query(getGoogleApiClient(), query)
                                .setResultCallback(metadataCallback);

                    }
                    if (Cur_State == Retrieve_State) {

                        Drive.DriveApi.fetchDriveId(getGoogleApiClient(), MainActivity.EXISTING_FILE_ID)
                                .setResultCallback(idCallback);
                    }
                    if (Cur_State == Create_State) {

                        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                                .setResultCallback(driveContentsCallback);
                    }
                }
            }
        }

    }

    // Query Google Drive for File ID
    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback =
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if(getGoogleApiClient() != null) {
                        if (result != null) {
                            if (!result.getStatus().isSuccess()) {

                                myOptionMenu.getItem(1).setVisible(true);
                                myOptionMenu.getItem(2).setVisible(false);
                                return;
                            }

                            boolean foundFile = false;
                            int count = result.getMetadataBuffer().getCount() - 1;
                            for (int i = 0; i < count; i++) {
                                Metadata metadata = result.getMetadataBuffer().get(i);
                                if (metadata.getTitle().equals("DailyEncList")) {
                                    MainActivity.EXISTING_FILE_ID = metadata.getDriveId().getResourceId();
                                    foundFile = true;
                                    if (bIsFirstTimeAppOpened) {

                                        Cur_State = Retrieve_State;
                                        if(getGoogleApiClient() != null && MainActivity.EXISTING_FILE_ID != null) {
                                            Drive.DriveApi.fetchDriveId(getGoogleApiClient(), MainActivity.EXISTING_FILE_ID)
                                                    .setResultCallback(idCallback);
                                        }

                                    } else {
                                        Cur_State = Create_State;
                                        if(getGoogleApiClient() != null && MainActivity.EXISTING_FILE_ID != null) {
                                            Drive.DriveApi.fetchDriveId(getGoogleApiClient(), MainActivity.EXISTING_FILE_ID)
                                                    .setResultCallback(idCallbackEdit);
                                        }

                                    }

                                    i = count;

                                }

                            }

                            if (!foundFile) {

                                signedIn = false;
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean("signedIn", false);
                                editor.putBoolean("signedIn_Pressed", false);
                                editor.commit();
                                notificationEncouragementList.clear();
                                saveArray(notificationEncouragementList, "notificationEncouragementList");
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("cantFindDrive", "cantFindDrive");
                                startActivity(intent);
                                finish();

                            }

                            myOptionMenu.getItem(1).setVisible(false);
                            myOptionMenu.getItem(2).setVisible(true);
                        }else{
                            Log.d("MainActivity","Query Result is null");
                        }
                    }

                }
            };

    // Retrieve File Contents from Google Drive
    final private ResultCallback<DriveApi.DriveIdResult> idCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            new RetrieveDriveFileContentsAsyncTask(
                    MainActivity.this).execute(result.getDriveId());
        }
    };

    final private class RetrieveDriveFileContentsAsyncTask
            extends ApiClientAsyncTask<DriveId, Boolean, String> {

        public RetrieveDriveFileContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackgroundConnected(DriveId... params) {
            String contents = null;
            DriveFile file = params[0].asDriveFile();
            DriveApi.DriveContentsResult driveContentsResult =
                    file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(driveContents.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                contents = builder.toString();
            } catch (IOException e) {
                Log.e("Retrieve", "IOException while reading from the stream", e);
            }

            driveContents.discard(getGoogleApiClient());
            return contents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                return;
            }
            try {
                String[] totalResult = result.split("/t/");
                String[] encouragementResult = totalResult[0].split("/m/");
                String[] prayerResult = totalResult[1].split("/m/");
                String[] scriptureResult = totalResult[2].split("/m/");
                boolean rWantsStarterEncouragement = Boolean.parseBoolean(totalResult[3]);

                ArrayList<String> categoriesListPrayer = new ArrayList<String>();
                ArrayList<String> categoriesListScripture = new ArrayList<String>();


                encouragementList.clear();
                for (int z = 0; z < encouragementResult.length; z++) {

                    // If alarm is not off make it off when retrieving
                    try{
                        String[] entry = encouragementResult[z].split("/n");
                        String entryNotifString = entry[6];
                        if(!entryNotifString.equals("Alarm Off")){
                            entryNotifString = "Alarm Off";
                            encouragementResult[z] = "/n" + entry[1] + "/n" + entry[2] + "/n" + entry[3] + "/n" +
                                    entry[4] + "/n" + entry[5] + "/n" + entryNotifString;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    encouragementList.add(encouragementResult[z]);
                }

                for (int z = 0; z < prayerResult.length; z++) {
                    categoriesListPrayer.add(prayerResult[z]);
                }

                for (int z = 0; z < scriptureResult.length; z++) {
                    categoriesListScripture.add(scriptureResult[z]);
                }

                notificationEncouragementList.clear();
                if(!encouragementList.isEmpty()) {
                    for(int i = 0; i < encouragementList.size(); i++){
                        try{
                            String[] entry = encouragementList.get(i).split("/n");
                            if(entry[1].equals("Encouragement")){
                                homeEncouragement = encouragementList.get(i);
                                i = encouragementList.size();
                            }else{
                                homeEncouragement = "/nEncouragement/n /nNo Entries in Encouragement/nnone/n3/nAlarm Off";
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }

                saveArray(encouragementList,"encouragementList");
                saveArray(categoriesListPrayer,"categoriesListPrayer");
                saveArray(categoriesListScripture,"categoriesListScripture");
                saveArray(notificationEncouragementList,"notificationEncouragementList");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("homeEncouragement");
                editor.putString("homeEncouragement",homeEncouragement);
                editor.putBoolean("bIsFirstTimeAppOpened",false);
                editor.putBoolean("wantsStarterEncouragement",rWantsStarterEncouragement);
                editor.commit();

                Cur_State = 0;

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                thisActivity.finish();
                startActivity(intent);

                showMessage("Restoration Successful!\nPlease note alarms have been turned off.");

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // Create new file onto Google Drive
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                ArrayList<String> categoriesListPrayer = new ArrayList<String>();
                                ArrayList<String> categoriesListScripture = new ArrayList<String>();
                                loadArray(categoriesListPrayer,getApplicationContext(),"categoriesListPrayer");
                                loadArray(categoriesListScripture,getApplicationContext(),"categoriesListScripture");


                                // write encouragementList
                                for(int x = 0; x < encouragementList.size(); x++){
                                    writer.write(encouragementList.get(x) + "/m/");
                                }
                                writer.write("/t/");

                                // write categoriesListPrayer
                                for(int y = 0; y < categoriesListPrayer.size(); y++){
                                    writer.write(categoriesListPrayer.get(y) + "/m/");
                                }
                                writer.write("/t/");

                                // write categoriesListScripture
                                for(int y = 0; y < categoriesListScripture.size(); y++){
                                    writer.write(categoriesListScripture.get(y) + "/m/");
                                }
                                writer.write("/t/");

                                // write wantsStarterEncouragement
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                boolean wantsStarterEncouragement = sp.getBoolean("wantsStarterEncouragement",true);
                                writer.write(Boolean.toString(wantsStarterEncouragement));

                                writer.close();

                            } catch (IOException e) {
                                Log.e("Create_File", e.getMessage());
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("DailyEncList")
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                    .createFile(getGoogleApiClient(), changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("bIsFirstTimeAppOpened",false);
                    editor.commit();
                }
            };


    // Edit existing file in Google Drive
    final ResultCallback<DriveApi.DriveIdResult> idCallbackEdit = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            if (!result.getStatus().isSuccess()) {
                return;
            }

            DriveId driveId = result.getDriveId();
            final DriveFile file = driveId.asDriveFile();
            file.delete(getGoogleApiClient());

            Drive.DriveApi.newDriveContents(getGoogleApiClient())
                    .setResultCallback(driveContentsCallback);

        }
    };
}
