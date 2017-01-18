package com.example.mr_chen.yotuface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.ParseException;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class LoginActivity extends AppCompatActivity {
    protected static final int ERROR = 2;
    protected static final int SUCCESS = 1;
    private Button button_register;
    private Button button_login;
    private EditText login_username;
    private EditText login_password;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what)
            {
                case    SUCCESS:

                    Toast.makeText(LoginActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();
                    MainActivity.is_login=true;
                    //Toast.makeText(LoginActivity.this,String.valueOf(MainActivity.is_login),Toast.LENGTH_SHORT).show();
                    Log.i("is_login","成功");
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case ERROR:
                    Toast.makeText(LoginActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                default:break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button_register= (Button) findViewById(R.id.register);
        button_login= (Button) findViewById(R.id.login);
        login_username= (EditText) findViewById(R.id.username);
        login_password= (EditText) findViewById(R.id.password);
        SharedPreferences preferences=getSharedPreferences("register",MODE_PRIVATE);
        login_username.setText(preferences.getString("username",""));
        login_password.setText(preferences.getString("password",""));
    }
    public void register(View view)
    {
        //when click the register botton ,turn to the register's screen
        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    public void login(View view)
    {

        final String  name=login_username.getText().toString().trim();
        final String  pwd=login_password.getText().toString().trim();
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(pwd))
        {
            Toast.makeText(LoginActivity.this,"账号和密码均不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String path="http://15km440210.iok.la/login.php";
                    URL url=new URL(path);
                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    String data = "username="+name+"&password="+pwd+"&button=";
                    connection.setRequestProperty("Content-Length", data.length()+"");//数据的长度
                    //区别4、记得设置把数据写给服务器
                    connection.setDoOutput(true);//设置向服务器写数据
                    byte[] bytes = data.getBytes();
                    connection.getOutputStream().write(bytes);//把数据以流的方式写给服务器
                    InputStream is = connection.getInputStream();
                    int code=connection.getResponseCode();
                    if(code == 200){
                        String  result = StreamTools.readStream(is);
                        Message mas= Message.obtain();
                      //  mas.what = SUCCESS;
                        mas.obj = result;
                        if((((String)mas.obj).equals("登录成功！")))
                        {
                            SharedPreferences.Editor editor=getSharedPreferences("register",MODE_PRIVATE).edit();
                            editor.putString("username",name);
                            editor.putString("password",pwd);
                            editor.commit();
                            Log.i("commit","commit成功");
                            mas.what=SUCCESS;
                        }else{
                            mas.what=ERROR;
                        }
                        handler.sendMessage(mas);
                    }else{
                        Message mas = Message.obtain();
                        mas.what = ERROR;
                        handler.sendMessage(mas);
                        Log.i("访问网络","失败1");
                    }

                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    Message mas = Message.obtain();
                    mas.what = ERROR;
                    handler.sendMessage(mas);
                    Log.i("访问网络","失败2");
                }
            }
        }).start();
    }
}
