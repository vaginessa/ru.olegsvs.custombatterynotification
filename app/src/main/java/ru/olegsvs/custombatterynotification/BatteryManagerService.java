package ru.olegsvs.custombatterynotification;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * Created by user on 11.05.2017.
 */

public class BatteryManagerService extends Service{
    private final String BAT1 = "BAT1 ";
    private final String BAT2 = "BAT2 ";
    private final int NOTIFICATION_CUSTOM_BATTERY = 444;
    private int iconRes[] = {
            R.drawable.battery_green_0,R.drawable.battery_green_1,
            R.drawable.battery_green_2,R.drawable.battery_green_3,
            R.drawable.battery_green_4,R.drawable.battery_green_5,
            R.drawable.battery_green_6,R.drawable.battery_green_7,
            R.drawable.battery_green_8,R.drawable.battery_green_9,
            R.drawable.battery_green_10,R.drawable.battery_green_11,
            R.drawable.battery_green_12,R.drawable.battery_green_13,
            R.drawable.battery_green_14,R.drawable.battery_green_15,
            R.drawable.battery_green_16,R.drawable.battery_green_17,
            R.drawable.battery_green_18,R.drawable.battery_green_19,
            R.drawable.battery_green_20,R.drawable.battery_green_21,
            R.drawable.battery_green_22,R.drawable.battery_green_23,
            R.drawable.battery_green_24,R.drawable.battery_green_25,
            R.drawable.battery_green_26,R.drawable.battery_green_27,
            R.drawable.battery_green_28,R.drawable.battery_green_29,
            R.drawable.battery_green_30,R.drawable.battery_green_31,
            R.drawable.battery_green_32,R.drawable.battery_green_33,
            R.drawable.battery_green_34,R.drawable.battery_green_35,
            R.drawable.battery_green_36,R.drawable.battery_green_37,
            R.drawable.battery_green_38,R.drawable.battery_green_39,
            R.drawable.battery_green_40,R.drawable.battery_green_41,
            R.drawable.battery_green_42,R.drawable.battery_green_43,
            R.drawable.battery_green_44,R.drawable.battery_green_45,
            R.drawable.battery_green_46,R.drawable.battery_green_47,
            R.drawable.battery_green_48,R.drawable.battery_green_49,
            R.drawable.battery_green_50,R.drawable.battery_green_51,
            R.drawable.battery_green_52,R.drawable.battery_green_53,
            R.drawable.battery_green_54,R.drawable.battery_green_55,
            R.drawable.battery_green_56,R.drawable.battery_green_57,
            R.drawable.battery_green_58,R.drawable.battery_green_59,
            R.drawable.battery_green_60,R.drawable.battery_green_61,
            R.drawable.battery_green_62,R.drawable.battery_green_63,
            R.drawable.battery_green_64,R.drawable.battery_green_65,
            R.drawable.battery_green_66,R.drawable.battery_green_67,
            R.drawable.battery_green_68,R.drawable.battery_green_69,
            R.drawable.battery_green_70,R.drawable.battery_green_71,
            R.drawable.battery_green_72,R.drawable.battery_green_73,
            R.drawable.battery_green_74,R.drawable.battery_green_75,
            R.drawable.battery_green_76,R.drawable.battery_green_77,
            R.drawable.battery_green_78,R.drawable.battery_green_79,
            R.drawable.battery_green_80,R.drawable.battery_green_81,
            R.drawable.battery_green_82,R.drawable.battery_green_83,
            R.drawable.battery_green_84,R.drawable.battery_green_85,
            R.drawable.battery_green_86,R.drawable.battery_green_87,
            R.drawable.battery_green_88,R.drawable.battery_green_89,
            R.drawable.battery_green_90,R.drawable.battery_green_91,
            R.drawable.battery_green_92,R.drawable.battery_green_93,
            R.drawable.battery_green_94,R.drawable.battery_green_95,
            R.drawable.battery_green_96,R.drawable.battery_green_97,
            R.drawable.battery_green_98,R.drawable.battery_green_99,
            R.drawable.battery_green_100

    };
    private int BAT1_CAPACITY = 0;
    private int BAT2_CAPACITY = 0;

    private NotificationCompat.Builder mBuilder = null;

    public static boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(SettingsActivity.TAG, "isMyServiceRunning: Service is Running!");
                return true;
            }
        }
        Log.i(SettingsActivity.TAG, "isMyServiceRunning: Service is NOT Running!");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(SettingsActivity.TAG, "onDestroy: BatteryManagerService destroy!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(SettingsActivity.TAG, "onStartCommand: BatteryManagerService start");
        createNotify();
        Log.i(SettingsActivity.TAG, "onStartCommand: create and show notification");
        return super.onStartCommand(intent, flags, startId);
    }

    private String getResults() {
        if (!BatteryManager.checkJSRSupport()) {
            BAT1_CAPACITY = Integer.parseInt(BatteryManager.getValues(BatteryManager.SYS_BATTERY_CAPACITY));
            return (BAT1 + BAT1_CAPACITY + "% "
                         + BatteryManager.getValues(BatteryManager.SYS_BATTERY_STATUS) + " ");

        } else {
            BAT1_CAPACITY = Integer.parseInt(BatteryManager.getValues(BatteryManager.SYS_BATTERY_CAPACITY));
            BAT2_CAPACITY = Integer.parseInt(BatteryManager.getValues(BatteryManager.SYS_BATTERY_CAPACITY_JSR));
            return (BAT1 + BAT1_CAPACITY + "% "
                    + BatteryManager.getValues(BatteryManager.SYS_BATTERY_STATUS) + " \n"
                    + BAT2 + BAT2_CAPACITY + " "
                    + BatteryManager.getValues(BatteryManager.SYS_BATTERY_STATUS_JSR) + " ");
        }
    }

    private void createNotify() {
        int color = 0xff123456;
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Battery")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(getResults()))
                        .setSmallIcon(iconRes[BAT1_CAPACITY])
                        .setOngoing(true)
                        .setColor(color)
                        .setWhen(0)
                        .setContentText(getResults());
        Intent resultIntent = new Intent(this, SettingsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SettingsActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_CUSTOM_BATTERY, mBuilder.build());

        final Handler myHandler;
        myHandler = new Handler();
        Runnable runnable = new Runnable(){

            @Override
            public void run() {

                if(true) {
                    Log.i(SettingsActivity.TAG, "createNotify: " + getResults());
                    mBuilder.setContentText(getResults());
                    mBuilder.setSmallIcon(iconRes[BAT1_CAPACITY]);
                    mNotificationManager.notify(NOTIFICATION_CUSTOM_BATTERY, mBuilder.build());
                    myHandler.postDelayed(this, 2000);
                }
            }

        };

        myHandler.postDelayed(runnable, 2000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(SettingsActivity.TAG, "onCreate: creating BatteryManagerService");
    }

}