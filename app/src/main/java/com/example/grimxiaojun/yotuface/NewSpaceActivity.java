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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewSpaceActivity extends AppCompatActivity {
    private EditText edit_name;
    private EditText edit_purpose;
    private EditText edit_spacenum;
    private EditText password1;
    private EditText password2;
    private Button btn_newspace;
    private Handler handle=new Handler(){

        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    Intent intent=new Intent(NewSpaceActivity.this,MySpaceActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(NewSpaceActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();
                     Log.i("运行",(String)msg.obj);
                    break;
                case 2:
                    Toast.makeText(NewSpaceActivity.this,"注册人脸空间失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_space);
        edit_name= (EditText) findViewById(R.id.edit_name);
        edit_purpose= (EditText) findViewById(R.id.edit_purpose);
        edit_spacenum= (EditText) findViewById(R.id.edit_spacenum);
        password1= (EditText) findViewById(R.id.password1);
        password2= (EditText) findViewById(R.id.password2);
        btn_newspace= (Button) findViewById(R.id.btn_newspace);
    }
    public void newspace(View view)
    {
        final String name = edit_name.getText().toString();
        final String purpose = edit_purpose.getText().toString();
        final String spacenum = edit_spacenum.getText().toString();
        final String pwd1 = password1.getText().toString();
        final String pwd2 = password2.getText().toString();
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(purpose)||TextUtils.isEmpty(spacenum)||TextUtils.isEmpty(pwd1)||TextUtils.isEmpty(pwd2)){
            Toast.makeText(this, "带*号的均不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        else if((pwd1.equals(pwd2))!=true)
        {
            Toast.makeText(this, "所设密码与确认密码不匹配", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor=getSharedPreferences("newspace",MODE_PRIVATE).edit();
        editor.putString("spacename",name);
        editor.putString("spacepassword",pwd1);
        editor.putString("spacepurpose",purpose);
        editor.putString("spacenum",spacenum);
        editor.commit();
        SharedPreferences pref=getSharedPreferences("register",MODE_PRIVATE);
        final String username=pref.getString("username","");
        Log.i("username",username); //判断是否已获得
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    //区别1、url的路径不同
                    String path = "http://15km440210.iok.la/newspace3.php";
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();


                    //区别2、请求方式post
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    //区别3、必须指定两个请求的参数
                    //conn.setRequestProperty("Content-Type", "text/html; charset=utf-8");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//请求的类型  表单数据
                    conn.setRequestProperty("charset","UTF-8");//设置UTF 格式
                    String data = "username="+username+"&name="+name+"&purpose="+purpose+"&spacenum="+spacenum+"&pwd1="+pwd1+"&button=";
                    conn.setRequestProperty("Content-Length", data.length()+"");//数据的长度
                    //区别4、记得设置把数据写给服务器
                    conn.setDoOutput(true);//设置向服务器写数据

                    byte[] bytes = data.getBytes();
                    DataOutputStream out=new DataOutputStream(conn.getOutputStream());
                    out.write(bytes);

                   // conn.getOutputStream().write(bytes);//把数据以流的方式写给服务器
                    Log.i("数据","成功");
                    int code = conn.getResponseCode();

                    System.out.println(code);
                    if(code == 200){
                        InputStream is = conn.getInputStream();
                        String  result =StreamTools.readStream(is);
                        Message mas= Message.obtain();
                        mas.what = 1;
                        mas.obj = result;
                        handle.sendMessage(mas);

                    }else{
                        Message mas = Message.obtain();
                        mas.what = 2;
                        handle.sendMessage(mas);
                        Log.i("访问网络","失败1");
                    }

                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    Message mas = Message.obtain();
                    mas.what = 2;
                    handle.sendMessage(mas);
                    Log.i("访问网络","失败2");
                }

            }
        }).start();

    }
}
