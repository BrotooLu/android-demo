package com.bro2.demo.localsocket;

import android.app.Service;
import android.content.Intent;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.bro2.demo.DemoEnv.DEBUG;
import static com.bro2.demo.DemoEnv.TAG_PREFIX;

public class ClientService extends Service {
    public static final String KEY_SERVER_NAME = "server-name";
    public static final String KEY_COMMAND = "cmd";

    public static final int CMD_SEND_MSG = 1;
    public static final int CMD_STOP = 2;

    private static final String TAG = TAG_PREFIX + "socket_client";

    LocalSocket mSocket;
    OutputStream mOs;
    InputStream mIn;

    public ClientService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(null, flags, startId);
        }

        int cmd = intent.getIntExtra(KEY_COMMAND, CMD_SEND_MSG);
        try {
            if (cmd == CMD_SEND_MSG) {
                if (mSocket == null || !mSocket.isConnected()) {
                    if (DEBUG) {
                        Log.d(TAG, "[ClientService.onStartCommand] start connect");
                    }
                    String server = intent.getStringExtra(KEY_SERVER_NAME);
                    mSocket = new LocalSocket();
                    LocalSocketAddress address = new LocalSocketAddress(server);
                    try {
                        mSocket.connect(address);
                        if (DEBUG) {
                            Log.d(TAG, "[ClientService.onStartCommand] connected");
                        }
                        mOs = mSocket.getOutputStream();
                        mIn = mSocket.getInputStream();
                    } catch (Exception e) {
                        if (DEBUG) {
                            Log.e(TAG, "[ClientService.onStartCommand] ", e);
                        }
                    }
                }

                byte[] buffer = new byte[1024];
                int read = mIn.available();
                if (read > 0) {
                    read = mIn.read(buffer, 0, read);
                    if (DEBUG) {
                        if (read > 0) {
                            Log.d(TAG, "server: " + new String(buffer, 0, read));
                        } else {
                            Log.d(TAG, "no msg from server");
                        }
                    }
                }
                mOs.write("hello, server".getBytes());
            } else if (cmd == CMD_STOP) {
                if (DEBUG) {
                    Log.d(TAG, "[ClientService.onStartCommand] close");
                }
                mOs.close();
                mSocket.close();
                stopSelf();
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "[ClientService.onStartCommand] ", e);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
