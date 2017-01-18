package com.example.mr_chen.yotuface;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;





public  class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    public static ArrayList arrayList=new ArrayList();
    private ListView listView;
    private String[] data;
  //  private static ;    //搜索listview里的空间名
    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what)
            {
                case 0:
                    Toast.makeText(SearchActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    Log.i("SearchActivity",(String)msg.obj);
                    break;
                //加入成功后将进入空间内，可查看空间成员    暂时先跳转到主界面，等将其他内容改好后再完善此处
                case 1:
                    Intent intent=new Intent(SearchActivity.this,SpaceMemberActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(SearchActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    Log.i("SearchActivity",(String)msg.obj);
                    break;
                case 2:
                    Toast.makeText(SearchActivity.this,"访问网络失败",Toast.LENGTH_SHORT).show();
                    Log.i("SearchActivity",(String)msg.obj);
                    break;
                case 3:
                    Toast.makeText(SearchActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    Log.i("SearchActivity",(String)msg.obj);
                default:
                    Log.i("SearchActivity",(String)msg.obj);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listView= (ListView) findViewById(R.id.listview);
        searchView= (SearchView) findViewById(R.id.SearchView);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithHttpURLConnection();
                data=new String[arrayList.size()];
                arrayList.toArray(data);
                arrayList.clear();
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(SearchActivity.this,android.R.layout.simple_list_item_1,data);
                listView.setAdapter(adapter);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            EditText editText=new EditText(SearchActivity.this);

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(SearchActivity.this).setTitle("请输入空间加入密码")
                        .setView(editText)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                 String str= (String) arrayList.get(position);//获得listview子菜单名

                                SharedPreferences.Editor editor=getSharedPreferences("listview",MODE_PRIVATE).edit();
                                editor.putString("spacename",str);
                                editor.commit();





                              //  Log.i("positon:",str);//测试看子菜单是否正确
                                    SharedPreferences pref = getSharedPreferences("register", MODE_PRIVATE);

                                    final String username = pref.getString("username", "");

                                    final String space_name = str;
                                    final String space_password = editText.getText().toString().trim();

                                    Log.i("username:", username);
                                    Log.i("space_name", space_name);
                                    Log.i("space_password", space_password);

                                if(TextUtils.isEmpty(space_password)){
                                    Toast.makeText(SearchActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try{
                                            String path="http://15km440210.iok.la/join_space.php";
                                            URL url=new URL(path);
                                            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
                                            httpURLConnection.setRequestMethod("POST");
                                            httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0(compatible;MSIE 9.0; Windows NT  6.1;Trident/5.0)");
                                            //httpuURLonnection.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                                            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                                           // httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                            String data="username="+username+"&space_name="+space_name+"&space_password="+space_password;
                                            httpURLConnection.setRequestProperty("Content-Length",String.valueOf(data.length()) );
                                            //connection.setRequestProperty("Content-Length", data.length()+"");//数据的长度
                                            httpURLConnection.setDoOutput(true);
                                            byte[] bytes=data.getBytes();
                                            httpURLConnection.getOutputStream().write(bytes);
                                            int code=httpURLConnection.getResponseCode();
                                            Log.i("code",String.valueOf(code));
                                            if(code==200){
                                                InputStream is=httpURLConnection.getInputStream();
                                                String result=StreamTools.readStream(is);
                                                Message msg=Message.obtain();


                                                String join_success="加入成功";
                                                result=result.trim();
                                               result=result.substring(1,5);
                                                msg.obj=result;
                                               // Log.i("result",result);
                                              //  msg.what=1;
//                                                if(result.equals(join_success))
//                                                {
//
//                                                    msg.what=1;
//                                                    Log.i("result",result);
//                                                }
                                                if((((String)msg.obj).equals("进入成功")))
                                                {
                                                    msg.what=1;
                                                    Log.i("msg.what",String.valueOf(msg.what));

                                                }
                                                else if(((String)msg.obj).equals("密码错误"))
                                                {
                                                    msg.what=3;

                                                }
                                                else {
                                                    Log.i("msg.obj", (String) msg.obj);
                                                    Log.i("msg.what", String.valueOf(msg.what));
                                                }

                                                handler.sendMessage(msg);
                                            }else{
                                                Message msg=Message.obtain();
                                                msg.what=2;
                                                handler.sendMessage(msg);
                                            }


                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }



                                    }
                                }).start();
                            }




                        })
                        .setNegativeButton("取消",null).show();
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        //arrayList.clear();
       sendRequestWithHttpURLConnection();
        data=new String[arrayList.size()];
        arrayList.toArray(data);
        arrayList.clear();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(SearchActivity.this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
    }
    public static void sendRequestWithHttpURLConnection() {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                HttpURLConnection php_connection = null;
                try {
                    URL php_url = new URL("http://15km440210.iok.la/xml.php");
                    php_connection = (HttpURLConnection) php_url.openConnection();
                    php_connection.setRequestMethod("GET");
                    php_connection.setConnectTimeout(8000);
                    php_connection.setReadTimeout(8000);
                    php_connection.setDoInput(true);
                    php_connection.setDoOutput(true);
                    int php_code=php_connection.getResponseCode();
                    Log.i("code", String.valueOf(php_code));
                    if(php_code==200) {
                        URL url = new URL("http://15km440210.iok.la/allspace.xml");
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