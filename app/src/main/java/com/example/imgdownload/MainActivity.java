package com.example.imgdownload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements HandlerListener {
    private ImageView iv_main;
    private Button btn_main;
    private String imageurl = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2117149999,2003327935&fm=26&gp=0.jpg";
    private Bitmap bitmap;
    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_main = findViewById(R.id.iv_main);
        btn_main = findViewById(R.id.btn_main);
        btn_main.setText("MainActivity下载");

        //初始化
        handler = new MyHandler(Looper.getMainLooper());
        //回调接收
        handler.setListener(this);

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(runnable).start();
            }
        });
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(imageurl);
                // 打开连接
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                /**
                 * 当我们要采用非get请求给一个http网络地址传参
                 * 就使用connection.getOutputStream().write() 方法时
                 * 我们就需要setDoOutput(true),当
                 *
                 * 我们要获取我们请求的http地址访问的数据时
                 * 就是使用connection.getInputStream().read()方式时
                 * 我们就需要 setDoInput(true)，
                 * 根据api文档我们可知doInput默认就是为true。
                 */
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(false);
                //请求方式
                httpURLConnection.setRequestMethod("GET");
                //超时时间
                httpURLConnection.setConnectTimeout(3000);
                //http缓存
                httpURLConnection.setUseCaches(true);
                httpURLConnection.connect();
                //  请求的code参数
                int code = httpURLConnection.getResponseCode();
                Log.e("TAG", "getMessageData_InputStream: " + code);
                InputStream is = httpURLConnection.getInputStream();
                byte[] bytesInputStream = getByteInputStream(is);
                bitmap = BitmapFactory.decodeByteArray(bytesInputStream, 0, bytesInputStream.length);
                //message  what obj  发送消息
                handler.obtainMessage(101, bitmap).sendToTarget();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private byte[] getByteInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[512];
        int len;
        while ((len = is.read(buff)) != -1) {
            arrayOutputStream.write(buff, 0, len);
        }
        is.close();
        arrayOutputStream.close();
        return arrayOutputStream.toByteArray();
    }

    @Override
    public void handleResult(Message msg) {
        if (msg.what == 101) {
            bitmap = (Bitmap) msg.obj;
            iv_main.setImageBitmap(bitmap);
        }
    }
}