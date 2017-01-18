package com.example.mr_chen.yotuface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
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

/**
 * Created by wangjie on 2016/8/11.
 */
public class MySpaceActivity extends AppCompatActivity {
    public static ArrayList arrayList=new ArrayList();
    private ListView listView;
    private String[] data;
    static String username1;
//    private Handler handler=new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//            }
//        }
//    };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myspace);
        listView= (ListView) findViewById(R.id.myspace_listview);
        SharedPreferences pref=getSharedPreferences("register",MODE_PRIVATE);
        username1=pref.getString("username","");
        Log.i("username",username1);
        sendRequestWithHttpURLConnection();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str= (String) arrayList.get(position);//获得listview子菜单名

                SharedPreferences.Editor editor=getSharedPreferences("listview",MODE_PRIVATE).edit();
                editor.putString("spacename",str);
                editor.commit();
                Intent intent=new Intent(MySpaceActivity.this,SpaceMemberActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        arrayList.clear();
//        sendRequestWithHttpURLConnection();
//        data=new String[arrayList.size()];
//        arrayList.toArray(data);
//        arrayList.clear();
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(MySpaceActivity.this,android.R.layout.simple_list_item_1,data);
//        listView.setAdapter(adapter);

    }

    public void show(View v)
    {

        sendRequestWithHttpURLConnection();
        data=new String[arrayList.size()];
        arrayList.toArray(data);
        arrayList.clear();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(MySpaceActivity.this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
    }

    public static void sendRequestWithHttpURLConnection() {
        Log.i("sendR","succeed");
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                HttpURLConnection php_connection = null;
                try {
                    URL php_url = new URL("http://15km440210.iok.la/myspace1.php");
                    php_connection = (HttpURLConnection) php_url.openConnection();

                    php_connection.setRequestMethod("POST");
                    php_connection.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    php_connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    String data = "username="+username1;

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

                            URL url = new URL("http://15km440210.iok.la/"+username1+".xml");
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
                        if ("name".equals(nodeName)) {
                            name = xmlPullParser.nextText();

                            arrayList.add(name);
                            Log.i("MainActivity", "name is " + name);


                        }
                        break;
                    }
                    // 完成解析某个结点
                    case XmlPullParser.END_TAG: {
                        if ("name".equals(nodeName)) {
                            Log.d("MainActivity", "name is " +  name);
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
