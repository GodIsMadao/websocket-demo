package com.example.saintyun.websocketclient.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.pusher.java_websocket.client.WebSocketClient;

public class MyService extends Service {
//  这个可以用单例模式
    public static WebSocketClient client;
//    连接管理器
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
//    NotificationManager通知管理器
    private  NotificationManager manager;

//    private MyBinder mBinder = new MyBinder();
//  NotificationCompat
    private boolean iscon = true;//用于在broadcast中判断是否是需要重新连接的
    private NotificationCompat.Builder notifyBuilder;
//    提醒时震动效果
    private Vibrator vibrator;

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String deviceID = tm.getDeviceId();

        String address = "ws://192.168.3.5:8080/COMM/socket?" +
                "userId=123456ghjgjh789" +
                "&deviceNO=android_" + deviceID;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver,filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (intent.getAction()){
                case ConnectivityManager.CONNECTIVITY_ACTION:
//                    初始化实例
                    connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    networkInfo = connectivityManager.getActiveNetworkInfo();
                    if(networkInfo !=null && networkInfo.isAvailable() && iscon==false){
                        Intent serviceintent = new Intent(context,MyService.class);
                        context.startService(serviceintent);
                        Log.e("wz", "StartService");
                    }
                    break;
            }
        }
    };
}
