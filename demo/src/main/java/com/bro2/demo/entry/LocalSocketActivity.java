package com.bro2.demo.entry;

import android.app.Activity;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bro2.demo.R;
import com.bro2.demo.localsocket.ClientService;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.bro2.demo.DemoEnv.DEBUG;
import static com.bro2.demo.DemoEnv.TAG_PREFIX;

public class LocalSocketActivity extends Activity implements View.OnClickListener {
    public static final String SERVER_NAME = "sock_server";

    private static final String TAG = TAG_PREFIX + "socket_server";

    private static final String WHO_SERVER = "server";
    private static final String WHO_CLIENT = "client";

    private TextView mStatusTv;

    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    LocalServerSocket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_socket);

        mStatusTv = (TextView) findViewById(R.id.tv_status);
        findViewById(R.id.bt_start_server).setOnClickListener(this);
        findViewById(R.id.bt_stop_server).setOnClickListener(this);
        findViewById(R.id.bt_start_client).setOnClickListener(this);
        findViewById(R.id.bt_stop_client).setOnClickListener(this);
        findViewById(R.id.bt_clear_status).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start_server:
                startServer();
                break;
            case R.id.bt_stop_server:
                stopServer();
                break;
            case R.id.bt_start_client:
                startClient();
                break;
            case R.id.bt_stop_client:
                stopClient();
                break;
            case R.id.bt_clear_status:
                mStatusTv.setText("");
                break;
        }
    }

    private void appendStatus(final String who, final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusTv.append("\n" + who + ": " + status);
            }
        });
    }

    private void startServer() {
        try {
            mSocket = new LocalServerSocket(SERVER_NAME);
        } catch (Exception e) {
            appendStatus(WHO_SERVER, e.toString());
            return;
        }

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean flag = Thread.currentThread().isInterrupted();
                while (!flag) {
                    LocalSocket client = null;
                    InputStream mIn = null;
                    try {
                        appendStatus(WHO_SERVER, "start accept");
                        client = mSocket.accept();
                        mIn = client.getInputStream();
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = mIn.read(buffer, 0, buffer.length)) != -1) {
                            appendStatus(WHO_CLIENT, new String(buffer, 0, read));
                        }
                        flag = Thread.currentThread().isInterrupted();
                        appendStatus(WHO_SERVER, "closed client");
                    } catch (Exception e) {
                        appendStatus(WHO_SERVER, e.toString());
                    } finally {
                        close(mIn);
                        close(client);
                    }
                }

                appendStatus(WHO_SERVER, "start close server");
                try {
                    mSocket.close();
                } catch (Exception e) {
                    appendStatus(WHO_SERVER, e.toString());
                }
            }
        });
    }

    private void stopServer() {
        System.exit(0);
    }

    private void startClient() {
        Intent service = new Intent(this, ClientService.class);
        service.putExtra(ClientService.KEY_SERVER_NAME, SERVER_NAME);
        startService(service);
    }

    private void stopClient() {
        Intent service = new Intent(this, ClientService.class);
        service.putExtra(ClientService.KEY_COMMAND, ClientService.CMD_STOP);
        startService(service);
    }

    private void close(Closeable close) {
        if (close != null) {
            try {
                close.close();
            } catch (IOException e) {
                if (DEBUG) {
                    Log.e(TAG, "[LocalSocketActivity.close] ", e);
                }
            }
        }
    }

}
