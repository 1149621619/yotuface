package com.example.mr_chen.yotuface;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ReturnMsg extends AppCompatActivity {

    private ListView returnmsg;
    private static String[] data;
    private static ArrayList<String> arrayList=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returnmessage);
        returnmsg= (ListView) findViewById(R.id.returnmsg_listview);
        showthesolution();
        data=new String[arrayList.size()];
        arrayList.toArray(data);
        arrayList.clear();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(ReturnMsg.this,android.R.layout.simple_list_item_1,data);
        returnmsg.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.returnmsg, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void showMessage(View view){
        showthesolution();
        data=new String[arrayList.size()];
        arrayList.toArray(data);
        arrayList.clear();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(ReturnMsg.this,android.R.layout.simple_list_item_1,data);
        returnmsg.setAdapter(adapter);
    }

    public void showthesolution(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://15km440210.iok.la/pic/message.xml");
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
                }catch (Exception e){
                    e.printStackTrace();
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
                                        if ("Name".equals(nodeName)) {
                                            name = xmlPullParser.nextText();

                                            arrayList.add(name);
                                            Log.i("ReturnMsgActivity", "name is " + name);
                                        }
                                        break;
                                    }
                                    // 完成解析某个结点
                                    case XmlPullParser.END_TAG: {
                                        if ("/Name".equals(nodeName)) {
                                            Log.d("SpaceMemberActivity", "spacename is " +  name);
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
}