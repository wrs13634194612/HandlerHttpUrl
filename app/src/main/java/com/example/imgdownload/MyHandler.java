package com.example.imgdownload;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

public class MyHandler extends Handler {
    HandlerListener listener;

    public HandlerListener getListener() {
        return listener;
    }

    public void setListener(HandlerListener listener) {
        this.listener = listener;
    }

    public MyHandler(@NonNull Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        Log.e("TAG", "MyHandler: " + msg.what);
        getListener().handleResult(msg);
    }

}
