package com.example.mr_chen.yotuface;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;


public class SpaceMemberActivity extends AppCompatActivity {
    final String TAG="wang";
    private String time;
    private ListView listView;
    private static String[] data;
    private static ArrayList<String> arrayList=new ArrayList();
    private static ArrayList<String> arrayList1=new ArrayList();
    String[] to;
    private static String  phonespacename;
    private IntentFilter sendFilter;
    private SendStatusReceiver sendStatusReceiver;
    Handler handle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Toast.makeText(SpaceMemberActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(SpaceMemberActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                    break;
                default:break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_member);
        listView= (ListView) findViewById(R.id.space_member_listview);
        sendFilter = new IntentFilter();
        sendFilter.addAction("SENT_SMS_ACTION");
        sendStatusReceiver = new SendStatusReceiver();
        registerReceiver(sendStatusReceiver, sendFilter);
        sendRequestWithHttpURLConnection();
        data=new String[arrayList.size()];
        arrayList.toArray(data);
        arrayList.clear();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(SpaceMemberActivity.this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.returnmsg, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理动作按钮的点击事件
        switch (item.getItemId()) {
            case R.id.returnmsg:
                 showthesolution();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void showthesolution(){
        Intent intent=new Intent(SpaceMemberActivity.this,ReturnMsg.class);
        startActivity(intent);
    }
    @Override
    protected void onStart(){
        super.onStart();
        //arrayList.clear();
//        sendRequestWithHttpURLConnection();
//        data=new String[arrayList.size()];
//        arrayList.toArray(data);
//        arrayList.clear();
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(SpaceMemberActivity.this,android.R.layout.simple_list_item_1,data);
//        listView.setAdapter(adapter);
    }
    public void showmember(View view)
    {
        sendRequestWithHttpURLConnection();
        data=new String[arrayList.size()];
        arrayList.toArray(data);
        arrayList.clear();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(SpaceMemberActivity.this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
    }
    public  void sendRequestWithHttpURLConnection() {
        Log.i("sendR","succeed");
        SharedPreferences pref = getSharedPreferences("listview", MODE_PRIVATE);
        final  String spacename = pref.getString("spacename", "");
        Log.i("MemberSpacename",spacename);
        Log.i("MemberSpacename","空间名");
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                HttpURLConnection php_connection = null;
                try {
                    URL php_url = new URL("http://15km440210.iok.la/spacemember.php");
                    php_connection = (HttpURLConnection) php_url.openConnection();

                    php_connection.setRequestMethod("POST");
                    php_connection.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    php_connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");




                    String data = "spacename="+spacename; //    需要spacename

                    php_connection.setRequestProperty("Content-Length", data.length()+"");//数据的长度
                    //区别4、记得设置把数据写给服务器
                    php_connection.setDoOutput(true);//设置向服务器写数据
                    byte[] bytes = data.getBytes();
                    php_connection.getOutputStream().write(bytes);//把数据以流的方式写给服务器
                    InputStream is =  php_connection.getInputStream();
                    int php_code=php_connection.getResponseCode();
                    Log.i("php_code", String.valueOf(php_code));
                    if(php_code==200) {
//                        String  result = StreamTools.readStream(is);
//                        Message mas= Message.obtain();
//                        //  mas.what = SUCCESS;
//                        mas.obj = result;
//                        Log.i("mss.obj", (String) mas.obj);
//                        Log.i("王",result);
//                        if((((String)mas.obj).equals("成功创建xml")))
//                        {

                        URL url = new URL("http://15km440210.iok.la/"+spacename+".xml");
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        InputStream in = connection.getInputStream();
                        // 下面对获取到的输入流进行读取
                        int code = connection.getResponseCode();
                        Log.i("code", String.valueOf(code));
                        if (code == 200) {
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(in));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            Message message = new Message();
                            message.what = 1;
                            // 将服务器返回的结果存放到Message中
                            message.obj = response.toString();
                            parseXMLWithPull((String) message.obj);
                        }
                    }

//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
    }
}).start();
    }
    public static void parseXMLWithPull(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String name = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    // 开始解析某个结点
                    case XmlPullParser.START_TAG: {
                        if ("spacename".equals(nodeName)) {
                            name = xmlPullParser.nextText();

                            arrayList.add(name);
                            Log.i("SpaceMemberActivity", "spacename is " + name);


                        }
                        else if ("phone".equals(nodeName)) {
                            name = xmlPullParser.nextText();

                            arrayList1.add(name);
                            Log.i("SpaceMemberActivity", "phonenumber is " + name);


                        }
                        else if("phonespacename".equals(nodeName)){
                            name = xmlPullParser.nextText();
                            phonespacename=name;
                        }
                        break;
                    }
                    // 完成解析某个结点
                    case XmlPullParser.END_TAG: {
                        if ("spacename".equals(nodeName)) {
                            Log.d("SpaceMemberActivity", "spacename is " +  name);
                        }
                        else if("phone".equals(nodeName))
                        {
                            Log.d("SpaceMemberActivity", "phonenumber is " +  name);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void XML(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;

                try {

                        URL url = new URL("http://15km440210.iok.la/phonenumber.xml");
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        InputStream in = connection.getInputStream();
                        // 下面对获取到的输入流进行读取
                        int code = connection.getResponseCode();
                        Log.i("code", String.valueOf(code));
                        if (code == 200) {
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(in));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            Message message = new Message();
                            message.what = 1;
                            // 将服务器返回的结果存放到Message中
                            message.obj = response.toString();
                            parseXMLWithPull((String) message.obj);
                            to=new String[arrayList1.size()];
                            arrayList1.toArray(to);
                            arrayList1.clear();

                            String content="请打开人脸识别开始考勤(命令发自空间："+phonespacename+")";

//                            for(int i=1;i<to.length;++i)
//                            {
//                                SmsManager smsManager = SmsManager.getDefault();
//                                Intent sentIntent = new Intent("SENT_SMS_ACTION");
//                                PendingIntent pi = PendingIntent.getBroadcast(
//                                        SpaceMemberActivity.this, 0, sentIntent, 0);
//                                smsManager.sendTextMessage(to[i], null,
//                                        content, pi, null);
//
//                            }
                            MainActivity.getLocation();


                        }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    public void sendSMS(View view){
        XML();




    }

    class SendStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() == RESULT_OK) {
                Toast.makeText(context, "Send succeeded", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(context, "Send failed", Toast.LENGTH_LONG)
                        .show();
            }
        }

    }
}
