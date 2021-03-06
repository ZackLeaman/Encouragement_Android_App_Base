package com.wordpress.zackleaman.materialtablayout;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


/**
 * Created by Zack on 8/15/2016.
 * This Fragment class is to display a home page on the main activity layout that shows all the
 * entries that have alarms on them as well as the most recent encouragement pushed to the user.
 */
public class HomeFragment extends Fragment implements PopupMenu.OnMenuItemClickListener, AdapterView.OnItemClickListener{

    private ArrayList<String> encouragementList,starterEncouragements,categoriesList,selectedEncouragement;
    private ArrayList<String> categoriesListPrayer;
    private ArrayList<String> categoriesListScripture;
    private boolean wantsStarterEncouragement = true, addedStarterEncouragements;

    private int positionEncouragement;
    private RelativeLayout relativeLayoutEncouragementEntry;
    private TextView tvEncouragementMessage, tvEncouragementFrom;
    private TextView tvEncouragement;
    private Button btnInfoEncouragement, btnNotificationFreqEncouragement, btnDayWeekEncouragement,btnSaveEncouragement;
    private TimePicker timePickerEncouragement;
    private boolean isEncouragementInfoActive, isEncouragementNotiFreqActive, isEncouragementDayActive;
    private Button btnEncouragementEntry;
    private enum EncouragementNotification{DAILY,WEEKLY,NONE}
    private enum EncouragementDay{SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY}
    private EncouragementNotification mEncouragementNotification;
    private EncouragementDay mEncouragementDay;
    private ArrayList<String> notificationEncouragementList;

    private boolean isPrayerNotiFreqActive, isPrayerDayActive;
    private ArrayList<String> notificationPrayerList;

    private boolean isScriptureNotiFreqActive, isScriptureDayActive;
    private ArrayList<String> notificationScriptureList;

    private boolean isEncouragementDropDownActive, isPrayerDropDownActive, isScriptureDropDownActive;
    private ListView listViewPrayer, listViewScripture;
    private Button btnEncouragementDropdown, btnPrayerDropdown, btnScriptureDropdown;
    private ArrayList<Integer> intPrayerList, intScriptureList;

    private ArrayList<String> alarmList = new ArrayList<>();
    private String encouragementAlarmString = "Alarm Off";
    private TextView tvEncouragementAlarmString;

    private TextView tvPrayerAndScripture;
    private String homeEncouragement = "";


    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        encouragementList = new ArrayList<>();
        starterEncouragements = new ArrayList<>();
        categoriesList = new ArrayList<>();
        selectedEncouragement = new ArrayList<>();
        categoriesListPrayer = new ArrayList<>();
        categoriesListScripture = new ArrayList<>();
        notificationEncouragementList = new ArrayList<>();
        notificationPrayerList = new ArrayList<>();
        notificationScriptureList = new ArrayList<>();
        intPrayerList = new ArrayList<>();
        intScriptureList = new ArrayList<>();
        positionEncouragement = 0;
        isEncouragementDropDownActive = true;
        isPrayerDropDownActive = false;
        isScriptureDropDownActive = false;
        addedStarterEncouragements = false;

        // load all array list
        loadArray(notificationEncouragementList,getActivity().getApplicationContext(),"notificationEncouragementList");
        loadArray(notificationPrayerList,getActivity().getApplicationContext(),"notificationPrayerList");
        loadArray(notificationScriptureList,getActivity().getApplicationContext(),"notificationScriptureList");
        loadArray(encouragementList,getActivity().getApplicationContext(),"encouragementList");




        // Setting up the 40 starter encouragements
        starterEncouragements.clear();
        starterEncouragements.add("/nEncouragement/n" + "Unknown" + "/n" +
                "In essentials unity, in non-essentials liberty, in all things charity." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Lamplighter Theatre ~ The Basket of Flowers" + "/n" +
                "Though sorrow endures for the night, the healing balm of truth brings joy in morning." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Lamplighter Theatre ~ Charlie’s Choice" + "/n" +
                "The most important journey in life is not about what you get, it’s about what you become." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Lamplighter Theatre ~ Charlie’s Choice" + "/n" +
                "The goals you set for tomorrow shape who you are today." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Lamplighter Theatre ~ Charlie’s Choice" + "/n" +
                "It is not until we change that others can follow." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Lamplighter Theatre ~ Sir Malcolm and the Missing Prince" + "/n" +
                "No eating, drinking, or small thinking beyond this point." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Lamplighter Theatre ~ The White Gypsy" + "/n" +
                "It is often when tragedy strikes that we discover our true identity." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Tracy McNair" + "/n" +
                "Accept the past for what it was. Acknowledge the present for what it is. Anticipate the future for what it can become." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Thomas Jefferson" + "/n" +
                "Never put off until tomorrow what you can do today." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Thomas Jefferson" + "/n" +
                "Never trouble another for what you can do yourself." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Thomas Jefferson" + "/n" +
                "Never spend money before you have earned it." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Thomas Jefferson" + "/n" +
                "Never buy what you don’t want because it is cheap." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Thomas Jefferson" + "/n" +
                "Pride costs more than hunger, thirst and cold." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Thomas Jefferson" + "/n" +
                "We seldom repent of having eaten too little." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Thomas Jefferson" + "/n" +
                "Nothing is troublesome that we do willingly." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Thomas Jefferson" + "/n" +
                "When angry, count ten before you speak; if very angry, count a hundred. " + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Unknown" + "/n" +
                "Sin will take you farther than you want to go, keep you longer than you want to stay, and cost you more than you want to pay." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "George Washington" + "/n" +
                "It is better to offer no excuse than a bad one." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "George Washington" + "/n" +
                "It is better to be alone than in bad company." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Unknown" + "/n" +
                "Be courteous to all, but intimate with few, and let those few be well tried before you give them your confidence. True friendship is a plant of slow growth, and must undergo and withstand the shocks of adversity before it is entitled to appellation." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Edmund Burke" + "/n" +
                "The only thing necessary for the triumph of evil is that good men do nothing. " + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Aesop" + "/n" +
                "Do not say anything at any time that you would not say at all times." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Aesop" + "/n" +
                "It is one thing to say that something should be done, but quite a different matter to do it." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Aesop" + "/n" +
                "Expect no reward for serving the wicked." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Aesop" + "/n" +
                "A kindness is never wasted." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Aesop" + "/n" +
                "Our best blessings are often the least appreciated." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Aesop" + "/n" +
                "Precious things are without value to those who cannot prize them." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Aesop" + "/n" +
                "Do not stop to argue with temptation." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Aesop" + "/n" +
                "Goodwill is worth nothing unless it is accompanied by good acts." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Benjamin Franklin" + "/n" +
                "Tell me and I forget. Teach me and I remember. Involve me and I learn. " + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Benjamin Franklin" + "/n" +
                "Money has never made man happy, nor will it, there is nothing in its nature to produce happiness. The more of it one has the more one wants." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Benjamin Franklin" + "/n" +
                "It takes many good deeds to build a good reputation, and only one bad one to lose it." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Benjamin Franklin" + "/n" +
                "They who can give up essential liberty to obtain a little temporary safety deserve neither liberty nor safety." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Benjamin Franklin" + "/n" +
                "The Constitution only gives people the right to pursue happiness. You have to catch it yourself." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Benjamin Franklin" + "/n" +
                "Lost time is never found again." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Benjamin Franklin" + "/n" +
                "Anger is never without a reason, but seldom with a good one." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Benjamin Franklin" + "/n" +
                "When you are good to others you are best to yourself." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Benjamin Franklin" + "/n" +
                "Would you live with ease, do what you ought and not what you please. " + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Unknown" + "/n" +
                "By failing to prepare, you are preparing to fail." + "/nnone/n3/nAlarm Off");
        starterEncouragements.add("/nEncouragement/n" + "Charles R. Swindoll" + "/n" +
                "We are all faced with a series of great opportunities brilliantly disguised as impossible situations." + "/nnone/n3/nAlarm Off");

        // Get the wantsStarterEncouragement boolean from shared preferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        wantsStarterEncouragement = sp.getBoolean("wantsStarterEncouragement", true);

        // Determine whether starter encouragements has been add already
        String aSE = loadString("addedStarterEncouragements",getContext());
        if(!aSE.isEmpty()){
            try{
                addedStarterEncouragements = Boolean.parseBoolean(aSE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // Add starter encouragements
        if(wantsStarterEncouragement){
            // if want starters add them
            if(!addedStarterEncouragements){
                for(int i = 0; i < starterEncouragements.size(); i++){
                    encouragementList.add(i,starterEncouragements.get(i));
                }

                saveArray(encouragementList,"encouragementList");
                addedStarterEncouragements = true;
                saveString("addedStarterEncouragements",Boolean.toString(addedStarterEncouragements));
            }
        }else{
            // else if don't want starters remove them
            if(addedStarterEncouragements){
                ArrayList<Integer> index = new ArrayList<>();
                for(int i = 0; i < encouragementList.size(); i++){
                    for(int j = 0; j < starterEncouragements.size(); j++) {
                        if (encouragementList.get(i).equals(starterEncouragements.get(j))) {
                            index.add(i);
                        }
                    }
                }

                if(!index.isEmpty()) {
                    for (int l = index.size(); l > 0; l--) {
                        int pos = l - 1;
                        encouragementList.remove(index.get(pos).intValue());
                    }
                }

                saveArray(encouragementList,"encouragementList");
                addedStarterEncouragements = false;
                saveString("addedStarterEncouragements",Boolean.toString(addedStarterEncouragements));
            }
        }

        // Load main categoriesList if null or empty add them
        loadArray(categoriesList,getContext().getApplicationContext(),"categoriesList");
        if(categoriesList == null || categoriesList.isEmpty()) {
            categoriesList.add("/nEncouragement/n");
            categoriesList.add("/nPrayer/n");
            categoriesList.add("/nScripture/n");
            saveArray(categoriesList,"categoriesList");
        }
        // Load prayer categories if null or empty add them
        loadArray(categoriesListPrayer,getContext().getApplicationContext(),"categoriesListPrayer");
        if(categoriesListPrayer == null || categoriesListPrayer.isEmpty()){
            categoriesListPrayer.add("/nChurch");
            categoriesListPrayer.add("/nFamily");
            categoriesListPrayer.add("/nFriends");
            categoriesListPrayer.add("/nMissions");
            categoriesListPrayer.add("/nWork");
            categoriesListPrayer.add("/nAnswered");
            categoriesListPrayer.add("/nOther");
            saveArray(categoriesListPrayer,"categoriesListPrayer");
        }
        // Load scripture categories if null or empty add them
        loadArray(categoriesListScripture,getContext().getApplicationContext(),"categoriesListScripture");
        if(categoriesListScripture == null || categoriesListScripture.isEmpty()){
            categoriesListScripture.add("/nHope");
            categoriesListScripture.add("/nLove");
            categoriesListScripture.add("/nFriendship");
            categoriesListScripture.add("/nForgiveness");
            categoriesListScripture.add("/nStrength");
            categoriesListScripture.add("/nOther");
            saveArray(categoriesListScripture,"categoriesListScripture");
        }


        /// THIS IS THE START OF ENCOURAGEMENT NOTIFICATION INITIALIZATION

        tvEncouragementMessage = (TextView)getView().findViewById(R.id.tvEncouragementMessage);
        tvEncouragementFrom = (TextView)getView().findViewById(R.id.tvEncouragementFrom);

        ArrayList<String> encouragements = new ArrayList<>();
        try{
            // Add the encouragements from encouragementList into their own array encouragements
            if(!encouragementList.isEmpty()){
                for(int w = 0; w < encouragementList.size(); w++){
                    String[] entry = encouragementList.get(w).split("/n");
                    if(entry[1].equals("Encouragement")){
                        encouragements.add(encouragementList.get(w));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // If notificationEncouragementList is empty and encouragements is empty display a blank encouragement on home tab
        if(notificationEncouragementList.isEmpty() && encouragements.isEmpty()){
            notificationEncouragementList.add(0,"/nEncouragement/n /n /nnone/n3/nAlarm Off");
            saveArray(notificationEncouragementList,"notificationEncouragementList");
            SharedPreferences.Editor mEdit1 = sp.edit();
            mEdit1.remove("homeEncouragement");
            mEdit1.putString("homeEncouragement",notificationEncouragementList.get(0));
            mEdit1.commit();
        }
        // If notificationEncouragementList is still displaying a blank encouragement even though encouragements have been added to encouragements remove the blank
        if(notificationEncouragementList.size() == 1 && !encouragements.isEmpty()){
            if(notificationEncouragementList.get(0).equals("/nEncouragement/n /n /nnone/n3/nAlarm Off")){
                notificationEncouragementList.remove(0);
                saveArray(notificationEncouragementList, "notificationEncouragementList");
                SharedPreferences.Editor mEdit1 = sp.edit();
                mEdit1.remove("homeEncouragement");
                mEdit1.commit();
            }
        }
        // Generate a random list of encouragement notifications if its empty, null, or first time opening app
        if(notificationEncouragementList.isEmpty()){
            Random r = new Random();
            ArrayList<String> orderedList = new ArrayList<>();
            for(int i = 0; i < encouragementList.size(); i++){
                try{
                    String[] entry = encouragementList.get(i).split("/n");
                    String category = entry[1];

                    if(category.equals("Encouragement")){
                        orderedList.add(encouragementList.get(i));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            int olSize = orderedList.size();
            int i1;
            while(olSize > 0){
                if(olSize > 1) {
                    i1 = r.nextInt(olSize);
                    notificationEncouragementList.add(0,orderedList.get(i1));
                    orderedList.remove(i1);
                }else{
                    notificationEncouragementList.add(0,orderedList.get(0));
                    orderedList.remove(0);
                }
                olSize--;

            }

            saveArray(notificationEncouragementList,"notificationEncouragementList");
            try{
                for(int j = 0; j < encouragementList.size(); j++){
                    if(notificationEncouragementList.get(0).equals(encouragementList.get(j))){
                        positionEncouragement = j;
                    }
                }

                homeEncouragement = sp.getString("homeEncouragement",notificationEncouragementList.get(0));
                String[] entry = homeEncouragement.split("/n");
                String nameAddressDate = entry[2];
                String message = entry[3];

                tvEncouragementMessage.setText(message);
                tvEncouragementFrom.setText(nameAddressDate);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        // Check to make sure entries from encouragement list match the randomized notification encouragement list
        else{
            try{
                if(!wantsStarterEncouragement) {
                    if(!notificationEncouragementList.isEmpty() && !starterEncouragements.isEmpty()) {
                        ArrayList<Integer> index = new ArrayList<>();
                        for (int k = 0; k < notificationEncouragementList.size(); k++) {
                            for (int r = 0; r < starterEncouragements.size(); r++) {
                                if (notificationEncouragementList.get(k).equals(starterEncouragements.get(r))) {
                                    index.add(k);
                                }
                            }
                        }

                        for (int x = index.size(); x > 0; x--) {
                            int indexPos = x - 1;
                            int notifPos = index.get(indexPos);
                            notificationEncouragementList.remove(notifPos);
                        }

                        saveArray(notificationEncouragementList, "notificationEncouragementList");
                    }

                    if(notificationEncouragementList.isEmpty() && encouragements.isEmpty()){
                        notificationEncouragementList.add(0,"/nEncouragement/n /n /nnone/n3/nAlarm Off");
                        saveArray(notificationEncouragementList,"notificationEncouragementList");
                    }

                    homeEncouragement = sp.getString("homeEncouragement",notificationEncouragementList.get(0));
                    for(int l = 0; l < starterEncouragements.size(); l++){
                        if(homeEncouragement.equals(starterEncouragements.get(l))){
                            homeEncouragement = notificationEncouragementList.get(0);
                            SharedPreferences.Editor mEdit1 = sp.edit();
                            mEdit1.remove("homeEncouragement");
                            mEdit1.putString("homeEncouragement", homeEncouragement);
                            mEdit1.commit();
                        }
                    }

                }

                if(!encouragementList.isEmpty()) {
                    for (int j = 0; j < encouragementList.size(); j++) {
                        if (notificationEncouragementList.get(0).equals(encouragementList.get(j))) {
                            positionEncouragement = j;
                        }
                    }
                }


                homeEncouragement = sp.getString("homeEncouragement",notificationEncouragementList.get(0));
                String[] entry = homeEncouragement.split("/n");
                String nameAddressDate = entry[2];
                String message = entry[3];

                tvEncouragementMessage.setText(message);
                tvEncouragementFrom.setText(nameAddressDate);

            }catch (Exception e){
                e.printStackTrace();
            }


        }

        relativeLayoutEncouragementEntry = (RelativeLayout)getView().findViewById(R.id.relativeLayoutEncouragementEntry);
        relativeLayoutEncouragementEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!notificationEncouragementList.get(0).equals("/nEncouragement/n /n /nnone/n3/nAlarm Off")){
                    if(!homeEncouragement.equals("")) {
                        selectedEncouragement.clear();
                        selectedEncouragement.add(0, homeEncouragement);
                        saveArray(selectedEncouragement, "selectedEncouragement");
                        Intent intent = new Intent(getContext().getApplicationContext(), CurrentEncouragement.class);
                        getActivity().finish();
                        startActivity(intent);
                    }
                }
            }
        });

        btnEncouragementEntry = (Button)getView().findViewById(R.id.btnEncouragementEntry);
        if(!notificationEncouragementList.isEmpty()) {
            if (!homeEncouragement.equals("/nEncouragement/n /n /nnone/n3/nAlarm Off")) {
                btnEncouragementEntry.setVisibility(View.VISIBLE);
            } else {
                btnEncouragementEntry.setVisibility(View.INVISIBLE);
            }
        }else{
            btnEncouragementEntry.setVisibility(View.INVISIBLE);
        }

        btnEncouragementEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!notificationEncouragementList.get(0).equals("/nEncouragement/n /n /nnone/n3/nAlarm Off")){
                    selectedEncouragement.clear();
                    selectedEncouragement.add(0,homeEncouragement);
                    saveArray(selectedEncouragement,"selectedEncouragement");
                    Intent intent = new Intent(getContext().getApplicationContext(),CurrentEncouragement.class);
                    getActivity().finish();
                    startActivity(intent);
                }
            }
        });




        tvEncouragement = (TextView)getView().findViewById(R.id.tvEncouragement);
        btnInfoEncouragement = (Button)getView().findViewById(R.id.btnInfoEncouragement);
        btnNotificationFreqEncouragement = (Button)getView().findViewById(R.id.btnNotificationFreqEncouragement);
        btnDayWeekEncouragement = (Button)getView().findViewById(R.id.btnDayWeekEncouragement);
        timePickerEncouragement = (TimePicker)getView().findViewById(R.id.timePickerEncouragement);
        btnSaveEncouragement = (Button)getView().findViewById(R.id.btnSaveEncouragement);


        mEncouragementNotification = EncouragementNotification.NONE;
        btnNotificationFreqEncouragement.setText("No Notification");
        mEncouragementDay = EncouragementDay.SUNDAY;
        btnDayWeekEncouragement.setText("Sunday");
        String encDay = loadString("EncouragementDay",getActivity().getApplicationContext());
        String encNotFreq = loadString("EncouragementNotiFreq",getActivity().getApplicationContext());

        if(encNotFreq.equals("Daily")){
            mEncouragementNotification = EncouragementNotification.DAILY;
            btnNotificationFreqEncouragement.setText("Daily Notification");
        }else if(encNotFreq.equals("Weekly")){
            mEncouragementNotification = EncouragementNotification.WEEKLY;
            btnNotificationFreqEncouragement.setText("Weekly Notification");
        }else if(encNotFreq.equals("None")){
            mEncouragementNotification = EncouragementNotification.NONE;
            btnNotificationFreqEncouragement.setText("No Notification");
        }

        if(encDay.equals("Sunday")){
            mEncouragementDay = EncouragementDay.SUNDAY;
            btnDayWeekEncouragement.setText(encDay);
        }else if(encDay.equals("Monday")){
            mEncouragementDay = EncouragementDay.MONDAY;
            btnDayWeekEncouragement.setText(encDay);
        }else if(encDay.equals("Tuesday")){
            mEncouragementDay = EncouragementDay.TUESDAY;
            btnDayWeekEncouragement.setText(encDay);
        }else if(encDay.equals("Wednesday")){
            mEncouragementDay = EncouragementDay.WEDNESDAY;
            btnDayWeekEncouragement.setText(encDay);
        }else if(encDay.equals("Thursday")){
            mEncouragementDay = EncouragementDay.THURSDAY;
            btnDayWeekEncouragement.setText(encDay);
        }else if(encDay.equals("Friday")){
            mEncouragementDay = EncouragementDay.FRIDAY;
            btnDayWeekEncouragement.setText(encDay);
        }else if(encDay.equals("Saturday")){
            mEncouragementDay = EncouragementDay.SATURDAY;
            btnDayWeekEncouragement.setText(encDay);
        }


        isEncouragementInfoActive = false;
        isEncouragementNotiFreqActive = false;
        isEncouragementDayActive = false;
        btnNotificationFreqEncouragement.setVisibility(View.GONE);
        btnDayWeekEncouragement.setVisibility(View.GONE);
        timePickerEncouragement.setVisibility(View.GONE);
        btnSaveEncouragement.setVisibility(View.GONE);
        btnInfoEncouragement.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        btnInfoEncouragement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEncouragementInfoActive = !isEncouragementInfoActive;
                if(isEncouragementInfoActive){
                    btnInfoEncouragement.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                    tvEncouragementAlarmString.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                    btnNotificationFreqEncouragement.setVisibility(View.VISIBLE);
                    btnSaveEncouragement.setVisibility(View.VISIBLE);
                    switch (mEncouragementNotification){
                        case DAILY:
                            btnDayWeekEncouragement.setVisibility(View.GONE);
                            timePickerEncouragement.setVisibility(View.VISIBLE);
                            break;
                        case WEEKLY:
                            btnDayWeekEncouragement.setVisibility(View.VISIBLE);
                            timePickerEncouragement.setVisibility(View.VISIBLE);
                            break;
                        case NONE:
                            btnDayWeekEncouragement.setVisibility(View.GONE);
                            timePickerEncouragement.setVisibility(View.GONE);
                            break;
                        default:
                            break;
                    }

                    if (Build.VERSION.SDK_INT >= 23 ) {
                        String hour = loadString("EncouragementTimeHour",getContext());
                        String minute = loadString("EncouragementTimeMinute",getContext());
                        if(!hour.isEmpty()){
                            timePickerEncouragement.setHour(Integer.parseInt(hour));
                        }
                        if(!minute.isEmpty()){
                            timePickerEncouragement.setMinute(Integer.parseInt(minute));
                        }
                    }else {
                        String hour = loadString("EncouragementTimeHour",getContext());
                        String minute = loadString("EncouragementTimeMinute",getContext());
                        if(!hour.isEmpty()){
                            timePickerEncouragement.setCurrentHour(Integer.parseInt(hour));
                        }
                        if(!minute.isEmpty()){
                            timePickerEncouragement.setCurrentMinute(Integer.parseInt(minute));
                        }
                    }


                }else{
                    btnNotificationFreqEncouragement.setVisibility(View.GONE);
                    btnDayWeekEncouragement.setVisibility(View.GONE);
                    timePickerEncouragement.setVisibility(View.GONE);
                    btnSaveEncouragement.setVisibility(View.GONE);
                    btnInfoEncouragement.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    tvEncouragementAlarmString.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                }
            }
        });
        btnNotificationFreqEncouragement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEncouragementNotiFreqActive = true;
                isEncouragementDayActive = false;
                isPrayerNotiFreqActive = false;
                isPrayerDayActive = false;
                isScriptureNotiFreqActive = false;
                isScriptureDayActive = false;
                showPopUp(view);
            }
        });
        btnDayWeekEncouragement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEncouragementNotiFreqActive = false;
                isEncouragementDayActive = true;
                isPrayerNotiFreqActive = false;
                isPrayerDayActive = false;
                isScriptureNotiFreqActive = false;
                isScriptureDayActive = false;
                showPopUp(view);
            }
        });
        tvEncouragementAlarmString = (TextView)getView().findViewById(R.id.tvEncouragementAlarmString);
        encouragementAlarmString = loadString("encouragementAlarmString",getContext());
        if(encouragementAlarmString.isEmpty()){
            encouragementAlarmString = "Alarm Off";
        }
        tvEncouragementAlarmString.setText(encouragementAlarmString);
        tvEncouragementAlarmString.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        btnSaveEncouragement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day = 0;
                int hour = 1;
                int minute = 1;
                String amOrPmHour;
                String amOrPm;
                String stringMinute;
                String dayString = "";



                if (Build.VERSION.SDK_INT >= 23 ) {
                    saveString("EncouragementTimeHour",Integer.toString(timePickerEncouragement.getHour()));
                    saveString("EncouragementTimeMinute",Integer.toString(timePickerEncouragement.getMinute()));
                    hour = timePickerEncouragement.getHour();
                    minute = timePickerEncouragement.getMinute();
                }else {
                    saveString("EncouragementTimeHour",Integer.toString(timePickerEncouragement.getCurrentHour()));
                    saveString("EncouragementTimeMinute",Integer.toString(timePickerEncouragement.getCurrentMinute()));
                    hour = timePickerEncouragement.getCurrentHour();
                    minute = timePickerEncouragement.getCurrentMinute();
                }
                switch (mEncouragementDay){
                    case SUNDAY:
                        saveString("EncouragementDay","Sunday");
                        dayString = "Sunday";
                        day = 1;
                        break;
                    case MONDAY:
                        saveString("EncouragementDay","Monday");
                        dayString = "Monday";
                        day = 2;
                        break;
                    case TUESDAY:
                        saveString("EncouragementDay","Tuesday");
                        dayString = "Tuesday";
                        day = 3;
                        break;
                    case WEDNESDAY:
                        saveString("EncouragementDay","Wednesday");
                        dayString = "Wednesday";
                        day = 4;
                        break;
                    case THURSDAY:
                        saveString("EncouragementDay","Thursday");
                        dayString = "Thursday";
                        day = 5;
                        break;
                    case FRIDAY:
                        saveString("EncouragementDay","Friday");
                        dayString = "Friday";
                        day = 6;
                        break;
                    case SATURDAY:
                        saveString("EncouragementDay","Saturday");
                        dayString = "Saturday";
                        day = 7;
                        break;
                }
                if(hour >= 12 && hour <= 23){
                    amOrPm = "PM";
                    if(hour != 12) {
                        amOrPmHour = Integer.toString(hour - 12);
                    }else{
                        amOrPmHour = Integer.toString(hour);
                    }
                }else{
                    amOrPm = "AM";
                    if(hour == 0) {
                        amOrPmHour = Integer.toString(hour + 12);
                    }else{
                        amOrPmHour = Integer.toString(hour);
                    }
                }
                if(minute < 10){
                    stringMinute = "0"+Integer.toString(minute);
                }else{
                    stringMinute = Integer.toString(minute);
                }

                switch (mEncouragementNotification){
                    case DAILY:
                        saveString("EncouragementNotiFreq","Daily");
                        setDailyAlarm("Daily",day,hour,minute,3,notificationEncouragementList.get(0),Integer.toString(positionEncouragement));
                        encouragementAlarmString = "Alarm Daily at " +
                                amOrPmHour + ":" + stringMinute + " " + amOrPm;
                        tvEncouragement.setText("Daily Encouragement");
                        saveString("encouragementAlarmString", encouragementAlarmString);
                        saveString("encouragementType","Daily");
                        break;
                    case WEEKLY:
                        saveString("EncouragementNotiFreq","Weekly");
                        setDailyAlarm("Weekly",day,hour,minute,3,notificationEncouragementList.get(0),Integer.toString(positionEncouragement));
                        encouragementAlarmString = "Alarm Weekly on " + dayString + " " +
                                amOrPmHour + ":" + stringMinute + " " + amOrPm;
                        tvEncouragement.setText("Weekly Encouragement");
                        saveString("encouragementAlarmString", encouragementAlarmString);
                        saveString("encouragementType","Weekly");
                        break;
                    case NONE:
                        encouragementAlarmString = "Alarm Off";
                        tvEncouragement.setText("Encouragement");
                        saveString("encouragementAlarmString", encouragementAlarmString);
                        saveString("EncouragementNotiFreq","None");
                        saveString("encouragementType","Off");
                        stopNotification(3);
                        break;
                }



                btnInfoEncouragement.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                tvEncouragementAlarmString.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                tvEncouragementAlarmString.setText(encouragementAlarmString);
                isEncouragementInfoActive = false;
                isEncouragementNotiFreqActive = false;
                isEncouragementDayActive = false;
                btnDayWeekEncouragement.setVisibility(View.GONE);
                btnNotificationFreqEncouragement.setVisibility(View.GONE);
                btnSaveEncouragement.setVisibility(View.GONE);
                timePickerEncouragement.setVisibility(View.GONE);

                Toast.makeText(getActivity().getApplicationContext(),
                        "Encouragement Notification Settings Saved",
                        Toast.LENGTH_LONG).show();

            }
        });

        listViewPrayer = (ListView)getView().findViewById(R.id.listViewPrayer);
        listViewScripture = (ListView)getView().findViewById(R.id.listViewScripture);
        listViewPrayer.setOnItemClickListener(this);
        listViewScripture.setOnItemClickListener(this);
        btnPrayerDropdown = (Button)getView().findViewById(R.id.btnPrayerDropdown);
        btnScriptureDropdown = (Button)getView().findViewById(R.id.btnScriptureDropdown);
        btnEncouragementDropdown = (Button)getView().findViewById(R.id.btnEncouragementDropdown);
        btnEncouragementDropdown.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        btnPrayerDropdown.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        btnScriptureDropdown.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        btnEncouragementDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEncouragementDropDownActive = !isEncouragementDropDownActive;
                if(isEncouragementDropDownActive){
                    relativeLayoutEncouragementEntry.setVisibility(View.VISIBLE);
                    btnEncouragementDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_up_float);
                    isPrayerDropDownActive = false;
                    isScriptureDropDownActive = false;
                    btnPrayerDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_down_float);
                    btnScriptureDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_down_float);
                    listViewPrayer.setVisibility(View.GONE);
                    listViewScripture.setVisibility(View.GONE);
                }else{
                    relativeLayoutEncouragementEntry.setVisibility(View.GONE);
                    btnEncouragementDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_down_float);
                }
            }
        });
        btnPrayerDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPrayerDropDownActive = !isPrayerDropDownActive;
                if(isPrayerDropDownActive){
                    listViewPrayer.setVisibility(View.VISIBLE);
                    ArrayList<String> alarmNotifications = new ArrayList<>();
                    try {
                        intPrayerList.clear();
                        for(int i = 0; i < encouragementList.size(); i++){
                            String[] entry = encouragementList.get(i).split("/n");
                            if(entry[1].equals("Prayer") && !entry[6].equals("Alarm Off")){
                                alarmNotifications.add(encouragementList.get(i));
                                intPrayerList.add(i);
                            }
                        }
                        listViewPrayer.setAdapter(new MyListAdapter(getContext(), R.layout.list_options, alarmNotifications));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    btnPrayerDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_up_float);
                    isEncouragementDropDownActive = false;
                    isScriptureDropDownActive = false;
                    btnEncouragementDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_down_float);
                    btnScriptureDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_down_float);
                    relativeLayoutEncouragementEntry.setVisibility(View.GONE);
                    listViewScripture.setVisibility(View.GONE);

                }else{
                    listViewPrayer.setVisibility(View.GONE);
                    btnPrayerDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_down_float);
                }
            }
        });
        btnScriptureDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isScriptureDropDownActive = !isScriptureDropDownActive;
                if(isScriptureDropDownActive){
                    listViewScripture.setVisibility(View.VISIBLE);
                    ArrayList<String> alarmNotifications = new ArrayList<>();
                    try {
                        intScriptureList.clear();
                        for(int i = 0; i < encouragementList.size(); i++){
                            String[] entry = encouragementList.get(i).split("/n");
                            if(entry[1].equals("Scripture") && !entry[6].equals("Alarm Off")){
                                alarmNotifications.add(encouragementList.get(i));
                                intScriptureList.add(i);
                            }
                        }
                        listViewScripture.setAdapter(new MyListAdapter(getContext(), R.layout.list_options, alarmNotifications));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    btnScriptureDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_up_float);
                    isEncouragementDropDownActive = false;
                    isPrayerDropDownActive = false;
                    btnEncouragementDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_down_float);
                    btnPrayerDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_down_float);
                    relativeLayoutEncouragementEntry.setVisibility(View.GONE);
                    listViewPrayer.setVisibility(View.GONE);
                }else{
                    listViewScripture.setVisibility(View.GONE);
                    btnScriptureDropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.arrow_down_float);
                }
            }
        });
        if(homeEncouragement.equals("")){
            btnEncouragementEntry.setVisibility(View.GONE);
        }

        // This is switch that will appear if Prayer and Scripture Categories are turned off
        tvPrayerAndScripture = (TextView) getView().findViewById(R.id.tvPrayerAndScripture);
        boolean wantsPrayerAndScripture = sp.getBoolean("wantsPrayerAndScripture",true);
        if(wantsPrayerAndScripture){
            tvPrayerAndScripture.setVisibility(View.GONE);
        }else{
            RelativeLayout relativeLayoutPrayer, relativeLayoutScripture;
            ListView listViewPrayer, listViewScripture;
            relativeLayoutPrayer = (RelativeLayout)getView().findViewById(R.id.relativeLayoutPrayer);
            relativeLayoutScripture = (RelativeLayout)getView().findViewById(R.id.relativeLayoutScripture);
            listViewPrayer = (ListView)getView().findViewById(R.id.listViewPrayer);
            listViewScripture = (ListView)getView().findViewById(R.id.listViewScripture);

            relativeLayoutPrayer.setVisibility(View.GONE);
            relativeLayoutScripture.setVisibility(View.GONE);
            listViewPrayer.setVisibility(View.GONE);
            listViewScripture.setVisibility(View.GONE);
            btnPrayerDropdown.setVisibility(View.GONE);
            btnScriptureDropdown.setVisibility(View.GONE);

            tvPrayerAndScripture.setVisibility(View.VISIBLE);

        }


        // On first time app opened
        if(MainActivity.isFirstTimeOpening){
            // Save that the app has been opened for the first time
            MainActivity.isFirstTimeOpening = false;
            sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor mEdit1 = sp.edit();
            mEdit1.remove("SMS_isFirstTimeOpening");
            mEdit1.putBoolean("SMS_isFirstTimeOpening",false);
            mEdit1.commit();
            // Popup dialog asking if want prayer and scripture categories
            DialogPopup dialog = new DialogPopup();
            Bundle args = new Bundle();
            args.putString(DialogPopup.DIALOG_TYPE, DialogPopup.OTHER_CATEGORIES);
            args.putInt("FragmentID",this.getId());
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "other-categories");


            starterEncouragements.add("/nEncouragement/n" + "Benjamin Franklin" + "/n" +
                    "They who can give up essential liberty to obtain a little temporary safety deserve neither liberty nor safety." + "/nnone/n3/nAlarm Off");

            // Setup reminder message alarm
            setDailyAlarm("Weekly",7,10,0,4,
                    "/nReminder/nNA/n" +
                            "Have you encouraged someone this week?" +
                            "/n /n4/nAlarm Weekly",
                    "0");

            // Setup daily encouragement alarm
            saveString("EncouragementNotiFreq","Daily");
            setDailyAlarm("Daily",1,10,0,3,notificationEncouragementList.get(0),Integer.toString(positionEncouragement));
            encouragementAlarmString = "Alarm Daily at 10:00 AM";
            tvEncouragement.setText("Daily Encouragement");
            tvEncouragementAlarmString.setText(encouragementAlarmString);
            saveString("encouragementAlarmString", encouragementAlarmString);
            saveString("encouragementType","Daily");
            mEncouragementNotification = EncouragementNotification.DAILY;
            saveString("EncouragementNotiFreq","Daily");
            saveString("EncouragementTimeHour","10");
            saveString("EncouragementTimeMinute","0");
            saveString("encouragementType","Daily");
            btnNotificationFreqEncouragement.setText("Daily Notification");



        }

    }




    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        homeEncouragement = sp.getString("homeEncouragement","/nEncouragement/n /n /nnone/n3/nAlarm Off");
        loadArray(notificationEncouragementList,getContext(),"notificationEncouragementList");
        try {
            String[] entry = homeEncouragement.split("/n");
            String nameAddressDate = entry[2];
            String message = entry[3];
            tvEncouragementMessage.setText(message);
            tvEncouragementFrom.setText(nameAddressDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(!notificationEncouragementList.isEmpty()) {
            if (!homeEncouragement.equals("/nEncouragement/n /n /nnone/n3/nAlarm Off")) {
                btnEncouragementEntry.setVisibility(View.VISIBLE);
            } else {
                btnEncouragementEntry.setVisibility(View.INVISIBLE);
            }
        }else{
            btnEncouragementEntry.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    public void onItemClick(AdapterView<?>parent,View view,int position,long id){
        if(isPrayerDropDownActive){
            selectedEncouragement.add(0,encouragementList.get(intPrayerList.get(position)));
            saveArray(selectedEncouragement,"selectedEncouragement");
            Intent intent = new Intent(getContext(),CurrentEncouragement.class);
            getActivity().finish();
            startActivity(intent);
        }else if(isScriptureDropDownActive){
            selectedEncouragement.add(0,encouragementList.get(intScriptureList.get(position)));
            saveArray(selectedEncouragement,"selectedEncouragement");
            Intent intent = new Intent(getContext(),CurrentEncouragement.class);
            getActivity().finish();
            startActivity(intent);
        }
    }

    /**
     * This sets the alarm for the particular entry or for all encouragements
     * @param notificationType string that represents type of notification
     * @param day int that represents day alarm to be set to
     * @param hour int that is the hour the alarm to be set to
     * @param minute int that is the minute the alarm to be set to
     * @param notifyID int that is the unique notification identifier
     * @param msg string message to send as the notification text
     * @param pos string position of selected entry
     */
    public void setDailyAlarm(String notificationType, int day, int hour, int minute, int notifyID, String msg, String pos){
        String category = "";
        String message = "";
        String subCategory = "";
        try{
            String[] entry = msg.split("/n");
            category = entry[1];
            message = entry[3];
            subCategory = entry[4];
        }catch (Exception e){
            e.printStackTrace();
        }

        if(subCategory.equals("none")){
            subCategory = "";
        }else{
            subCategory = " - " + subCategory;
        }


        Calendar calendar = Calendar.getInstance();
        if(notificationType.equals("Weekly")) {
            calendar.set(Calendar.DAY_OF_WEEK, day);
        }

        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);


        Intent alertIntent = new Intent(getContext(),AlertReceiver.class);
        alertIntent.putExtra("msg",msg);
        alertIntent.putExtra("msgCategory",category);
        alertIntent.putExtra("msgTitle", category + subCategory);
        alertIntent.putExtra("msgText",message);
        alertIntent.putExtra("msgAlert","New " + category + " Notification");
        alertIntent.putExtra("msgPos",pos);
        alertIntent.putExtra("notifyID",notifyID);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(),notifyID,alertIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        try {

            loadArray(alarmList, getContext(), "alarmList");
            boolean newEntry = true;
            String[] mesEntry = msg.split("/n");
            String notIDMes = mesEntry[5];
            for (int j = 0; j < alarmList.size(); j++) {
                String[] entry = alarmList.get(j).split("/n");
                String notIDEnt = entry[5];
                if(notIDMes.equals(notIDEnt)){
                    String newMessage = msg + "/n" + Long.toString(calendar.getTimeInMillis());
                    alarmList.set(j,newMessage);
                    newEntry = false;
                }
            }
            if(newEntry){
                String newMessage = msg + "/n" + Long.toString(calendar.getTimeInMillis());
                alarmList.add(newMessage);
            }
            saveArray(alarmList,"alarmList");
        }catch (Exception e){
            e.printStackTrace();
        }


        AlarmManager alarmManager = (AlarmManager)
                getContext().getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        if(notificationType.equals("Daily")) {
            // if calendar time has already passed then add a day to the time
            if(calendar.getTimeInMillis() < System.currentTimeMillis()){
                calendar.setTimeInMillis(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY);
            }

            // alarm set
            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }else if(notificationType.equals("Weekly")){
            // if calendar time has already passed then add a week to the time
            if(calendar.getTimeInMillis() < System.currentTimeMillis()){
                calendar.setTimeInMillis(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY*7);
            }

            // alarm set
            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }

    }

    public void stopNotification(int notifID){
        Intent intentstop = new Intent(getContext(), AlertReceiver.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(getContext(),
                notifID, intentstop, 0);
        AlarmManager alarmManagerstop = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);

        alarmManagerstop.cancel(senderstop);
    }


    /**
     * Used to display a popup list to select from
     * @param v view to show the popup on
     */
    public void showPopUp(View v){
        PopupMenu popupMenu = new PopupMenu(getActivity(),v,1,0,R.style.PopupMenu);

        popupMenu.setOnMenuItemClickListener(HomeFragment.this);
        MenuInflater inflater = popupMenu.getMenuInflater();

        inflater.inflate(R.menu.popup_home_menu, popupMenu.getMenu());

        if(isEncouragementNotiFreqActive || isPrayerNotiFreqActive || isScriptureNotiFreqActive){
            for(int i = 3; i <= 9; i++) {
                popupMenu.getMenu().getItem(i).setVisible(false);
            }
        }
        if(isEncouragementDayActive || isPrayerDayActive || isScriptureDayActive){
            for(int i = 0; i <= 2; i++) {
                popupMenu.getMenu().getItem(i).setVisible(false);
            }
        }

        popupMenu.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.popup_dailyNotification:
                if(isEncouragementNotiFreqActive) {
                    mEncouragementNotification = EncouragementNotification.DAILY;
                    btnNotificationFreqEncouragement.setText("Daily Notification");
                    btnDayWeekEncouragement.setVisibility(View.GONE);
                    timePickerEncouragement.setVisibility(View.VISIBLE);
                    isEncouragementNotiFreqActive = false;
                }
                return true;
            case R.id.popup_weeklyNotification:
                if(isEncouragementNotiFreqActive) {
                    mEncouragementNotification = EncouragementNotification.WEEKLY;
                    btnNotificationFreqEncouragement.setText("Weekly Notification");
                    btnDayWeekEncouragement.setVisibility(View.VISIBLE);
                    timePickerEncouragement.setVisibility(View.VISIBLE);
                    isEncouragementNotiFreqActive = false;
                }
                return true;
            case R.id.popup_noNotification:
                if(isEncouragementNotiFreqActive) {
                    mEncouragementNotification = EncouragementNotification.NONE;
                    btnNotificationFreqEncouragement.setText("No Notification");
                    btnDayWeekEncouragement.setVisibility(View.GONE);
                    timePickerEncouragement.setVisibility(View.GONE);
                    isEncouragementNotiFreqActive = false;
                }
                return true;
            case R.id.popup_sunday:
                if(isEncouragementDayActive) {
                    btnDayWeekEncouragement.setText("Sunday");
                    mEncouragementDay = EncouragementDay.SUNDAY;
                    isEncouragementDayActive = false;
                }
                return true;
            case R.id.popup_monday:
                if(isEncouragementDayActive) {
                    btnDayWeekEncouragement.setText("Monday");
                    mEncouragementDay = EncouragementDay.MONDAY;
                    isEncouragementDayActive = false;
                }
                return true;
            case R.id.popup_tuesday:
                if(isEncouragementDayActive) {
                    btnDayWeekEncouragement.setText("Tuesday");
                    mEncouragementDay = EncouragementDay.TUESDAY;
                    isEncouragementDayActive = false;
                }
                return true;
            case R.id.popup_wednesday:
                if(isEncouragementDayActive) {
                    btnDayWeekEncouragement.setText("Wednesday");
                    mEncouragementDay = EncouragementDay.WEDNESDAY;
                    isEncouragementDayActive = false;
                }
                return true;
            case R.id.popup_thursday:
                if(isEncouragementDayActive) {
                    btnDayWeekEncouragement.setText("Thursday");
                    mEncouragementDay = EncouragementDay.THURSDAY;
                    isEncouragementDayActive = false;
                }
                return true;
            case R.id.popup_friday:
                if(isEncouragementDayActive) {
                    btnDayWeekEncouragement.setText("Friday");
                    mEncouragementDay = EncouragementDay.FRIDAY;
                    isEncouragementDayActive = false;
                }
                return true;
            case R.id.popup_saturday:
                if(isEncouragementDayActive) {
                    btnDayWeekEncouragement.setText("Saturday");
                    mEncouragementDay = EncouragementDay.SATURDAY;
                    isEncouragementDayActive = false;
                }
                return true;
            default:
                return false;
        }
    }


    private boolean saveArray(List<String> sKey, String arrayName){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
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

    private boolean saveString(String stringName,String sKey){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor mEdit1=sp.edit();
        mEdit1.remove("SMS_" + stringName);
        mEdit1.putString("SMS_" + stringName,sKey);
        return mEdit1.commit();
    }

    private String loadString(String stringName, Context mContext){
        String sKey = "";
        SharedPreferences mSharedPreference1=PreferenceManager.getDefaultSharedPreferences(mContext);
        sKey = mSharedPreference1.getString("SMS_" + stringName,"");
        return sKey;
    }


    /**
     * Holds the view elements for the list adapter
     */
    public class ViewHolder{
        TextView title;
        TextView title2;
        Button button;
    }

    /**
     * Array Adapter class that holds the home page displayed entries
     */
    private class MyListAdapter extends ArrayAdapter<String> {
        private int layout;

        /**
         * constructor for MyListAdapter
         * @param context Context that will be using the ListAdapter
         * @param resource int resource id
         * @param objects List<String> of objects to display
         */
        public MyListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewHolder = null;

            if(convertView == null){
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(layout,parent,false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView)convertView.findViewById(R.id.list_item_textview);
                viewHolder.title2 = (TextView)convertView.findViewById(R.id.list_item_textview2);
                viewHolder.button = (Button)convertView.findViewById(R.id.list_item_btn);
                convertView.setTag(viewHolder);
            }

            mainViewHolder = (ViewHolder)convertView.getTag();
            mainViewHolder.button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.ic_media_play);
            mainViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isPrayerDropDownActive){
                        selectedEncouragement.add(0,encouragementList.get(intPrayerList.get(position)));
                        saveArray(selectedEncouragement,"selectedEncouragement");
                        Intent intent = new Intent(getContext(),CurrentEncouragement.class);
                        getActivity().finish();
                        startActivity(intent);
                    }else if(isScriptureDropDownActive){
                        selectedEncouragement.add(0,encouragementList.get(intScriptureList.get(position)));
                        saveArray(selectedEncouragement,"selectedEncouragement");
                        Intent intent = new Intent(getContext(),CurrentEncouragement.class);
                        getActivity().finish();
                        startActivity(intent);
                    }
                }
            });

            try{
                String[] entry = getItem(position).split("/n");
                String title = entry[3];
                String title2 = entry[2] + "\n" + entry[6];
                mainViewHolder.title.setText(title);
                mainViewHolder.title2.setText(title2);
            }catch (Exception e){
                e.printStackTrace();
            }


            return convertView;
        }
    }

}
