package com.example.saintyun.websocketclient.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.saintyun.websocketclient.R;
import com.pusher.java_websocket.client.WebSocketClient;
import com.pusher.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String  tag = "MAINACTIVITY";
    private Button btn_sendMsg;
    private EditText et_msg;
    private EditText et_textarea;
    private String initmsg = "";
    //WebSocketClient 和 address
    private WebSocketClient mWebSocketClient;
    private String address = "ws://192.168.10.102:8889";

    Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    et_textarea.setText(initmsg);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            initSocketClient();
            connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        initViews();
        initEvents();
    }

    private void initEvents() {
        btn_sendMsg.setOnClickListener(this);
    }

    private void initViews() {
        btn_sendMsg = (Button) findViewById(R.id.btn_sendmsg);
        et_msg = (EditText) findViewById(R.id.et_msg);
        et_textarea = (EditText) findViewById(R.id.et_textarea);
        et_textarea.setText(initmsg);
    }




//初始化WebSocketClient
    /**
     *
     * @throws URISyntaxException
     */
    private void initSocketClient() throws URISyntaxException {
        if(mWebSocketClient == null) {
            mWebSocketClient = new WebSocketClient(new URI(address)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
//连接成功
                    Log.d(tag,"opened connection");
                }


                @Override
                public void onMessage(String s) {
//服务端消息
                    initmsg += s + "\n";
                    Message msg = new Message();
                    msg.what = 1;
                    myhandler.sendMessage(msg);
                    Log.d(tag,"received:" + s);
                }


                @Override
                public void onClose(int i, String s, boolean remote) {
//连接断开，remote判定是客户端断开还是服务端断开
                    Log.d(tag,"Connection closed by " + ( remote ? "remote peer" : "us" ) + ", info=" + s);
                    //
                    closeConnect();
                }


                @Override
                public void onError(Exception e) {
                    Log.d(tag,"error:" + e);
                }
            };
        }
    }


    //连接
    private void connect() {
        new Thread(){
            @Override
            public void run() {
                mWebSocketClient.connect();
            }
        }.start();
    }


    //断开连接
    private void closeConnect() {
        try {
            mWebSocketClient.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            mWebSocketClient = null;
        }
    }


//发送消息
    /**
     *
     */
    private void sendMsg(String msg) {
        mWebSocketClient.send(msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sendmsg:
                String msg = et_msg.getText().toString();
                sendMsg(msg);
                break;
        }
    }
}
