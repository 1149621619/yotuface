package com.example.mr_chen.yotuface;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    public static boolean is_login;
    public static boolean is_myspace;

    /************* 王茜添加的代码 *********************/

    private final int SDK_PERMISSION_REQUEST = 127;
    private static LocationClient locationClient=null;
    private static final int UPDATE_TIME=1000;
    private static int LOCATION_COUNTS=0;

    /************************************************/


    private Handler handler=new Handler(){
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:

                    //Toast.makeText(MainActivity.this,"成功进入空间",Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this,(String) msg.obj,Toast.LENGTH_SHORT).show();
                    Log.i("msg.obj:",(String) msg.obj);
                   // MySpaceActivity.sendRequestWithHttpURLConnection();
                    Intent intent=new Intent(MainActivity.this,MySpaceActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                   // Toast.makeText(MainActivity.this,"进入空间失败",Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this,(String) msg.obj,Toast.LENGTH_SHORT).show();
                    Log.i("msg.obj:",(String) msg.obj);
                    break;
                case 3:
                    Toast.makeText(MainActivity.this,(String) msg.obj,Toast.LENGTH_SHORT).show();
                    Log.i("msg.obj:",(String) msg.obj);
                    break;
                case 4:
                    Toast.makeText(MainActivity.this,(String) msg.obj,Toast.LENGTH_SHORT).show();
                    Log.i("msg.obj:",(String) msg.obj);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /************* 以下是王茜添加的代码 *********************/

        getPermissions();

        locationClient=new LocationClient(this);
        //设置定位条件
        LocationClientOption option=new LocationClientOption();
        option.setOpenGps(true);    //打开gps
        option.setCoorType("bd0911");   //设置返回值的坐标类型
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setProdName("LocationDemo");     //设置产品线名称
        //option.setScanSpan(UPDATE_TIME);        //设置定位的时间间隔，单位毫秒
        locationClient.setLocOption(option);

        //注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if(location==null){
                    return;
                }
                StringBuffer sb=new StringBuffer(256);
                sb.append("Time : ");
                sb.append(location.getTime());
                sb.append("\nLatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nLontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nRadius : ");
                sb.append(location.getRadius());
                if (location.getLocType() == BDLocation.TypeGpsLocation){
                    Log.i("LocType","Gps");
                    sb.append("\nSpeed : ");
                    sb.append(location.getSpeed());
                    sb.append("\nSatellite : ");
                    sb.append(location.getSatelliteNumber());
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                    Log.i("LocType","NetWork");
                    sb.append("\nAddress : ");
                    sb.append(location.getAddrStr());
                }
                LOCATION_COUNTS++;
                sb.append("\n检查位置更新次数: ");
                sb.append(String.valueOf(LOCATION_COUNTS));
                Log.i("Location",sb.toString());

/*

		*/
/*以下为将时间，经纬度，半径传入服务器*/

                final String time=location.getTime();
                final Double latitude=location.getLatitude();
                final Double lontitude=location.getLongitude();

                SharedPreferences pref=getSharedPreferences("register",MODE_PRIVATE);
                final String username=pref.getString("username","");

                final long timeSecond=(System.currentTimeMillis()/1000);
                Log.i("timeSecond", String.valueOf(timeSecond));


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String path = "http://15km440210.iok.la/data.php";
                            URL url = new URL(path);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");

                            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//请求的类型  表单数据
                            String data = "username="+username+"&time="+timeSecond+"&latitude="+latitude+"&longitude="+lontitude;
                            conn.setRequestProperty("Content-Length", data.length()+"");//数据的长度
                            //记得设置把数据写给服务器
                            conn.setDoOutput(true);//设置向服务器写数据

                            byte[] bytes = data.getBytes();
                            conn.getOutputStream().write(bytes);//把数据以流的方式写给服务器


                            //  Log.i("数据","成功");
                            int code = conn.getResponseCode();

                            System.out.println(code);
                            if(code == 200){
                                InputStream is = conn.getInputStream();
                                String  result =StreamTools.readStream(is);
                                Message mas= Message.obtain();
                                mas.what = 3;
                                mas.obj = result;
                                handler.sendMessage(mas);

                            }else{
                                Message mas = Message.obtain();
                                mas.what =4;
                                handler.sendMessage(mas);
                                Log.i("访问网络","失败1");
                            }

                        }catch (IOException e) {
                            // TODO Auto-generated catch block
                            Message mas = Message.obtain();
                            mas.what = 4;
                            handler.sendMessage(mas);
                            Log.i("访问网络","失败2");
                        }

                    }
                }).start();






            }

        });

        /******** 以上是王茜添加的代码 **************************/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理动作按钮的点击事件
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_login:
                if(is_login==false)
                {
                    openLogin();
                    Log.i("is_login","成功");

                   // is_login=true;

                    return true;
                }
                else {Log.i("is_login","失败");
                    Toast.makeText(MainActivity.this,"已登录",Toast.LENGTH_SHORT).show();
                return true;}
            case R.id.action_register:
                openRegister();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*新建人脸库*/
    public void newSpace(View v) {
        if(is_login==true)
        {Log.i("is_login","成功");
            Intent intent=new Intent(this,NewSpaceActivity.class);
            startActivity(intent);
        }
        else {
            Log.i("is_login","失败");
            Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
        }
    }

    /*开启人脸检测*/
    public void startFaceDetection(View v){
        if(is_login==true) {
            Log.i("is_login","成功");
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        }else{
            Log.i("is_login","失败");
            Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
        }
    }

    /* 搜索人脸库 */
    public void openSearch() {
        if(is_login==true) {
            Log.i("is_login","成功");
          //  SearchActivity.sendRequestWithHttpURLConnection();
          //  SearchActivity.arrayList.clear();
            Intent intent=new Intent(this,SearchActivity.class);
            startActivity(intent);
        }else{
            Log.i("is_login","失败");
            Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
        }
    }
    /* 我的人脸空间*/
    public void mySpace(View v){
        if(is_login==true) {
            if (is_myspace == false) {
                final EditText editText=new EditText(MainActivity.this);
            //    LayoutInflater factory = LayoutInflater.from(MainActivity.this);
            //    final View LoginView = factory.inflate(R.layout.dialog, null);
                new AlertDialog.Builder(MainActivity.this).setTitle("请输入你的账户密码进行验证")
                      //  .setView(R.layout.dialog)
                      //  .setPositiveButton("确定",null)
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences pref=getSharedPreferences("newspace",MODE_PRIVATE);
                                SharedPreferences pref1=getSharedPreferences("register",MODE_PRIVATE);
                             //   final String spacename=pref.getString("spacename","");
                                final String username=pref1.getString("username","");
//                                if(spacename.isEmpty()){
//
//                                }
                             //   Log.i("spacename",spacename);
                                Log.i("username",username);
                    //            EditText editText= (EditText)LoginView.findViewById(R.id.edit_diolog_password);

                                final String edt_diolog_password=editText.getText().toString().trim();

                                if(TextUtils.isEmpty(edt_diolog_password))
                                {
                                    Toast.makeText(MainActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                String path="http://15km440210.iok.la/diolog.php";
                                                URL url=new URL(path);
                                                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                                                connection.setRequestMethod("POST");
                                                connection.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                                                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                              //  String data ="username="+username+"&spacename="+spacename+"&userpassword="+edt_diolog_password+"&button=";
                                                String data ="username="+username+"&userpassword="+edt_diolog_password+"&button=";
                                                connection.setRequestProperty("Content-Length", data.length()+"");//数据的长度
                                                //记得设置把数据写给服务器
                                                connection.setDoOutput(true);//设置向服务器写数据
                                                byte[] bytes = data.getBytes();
                                                connection.getOutputStream().write(bytes);//把数据以流的方式写给服务器
                                                InputStream is = connection.getInputStream();
                                                int code=connection.getResponseCode();
                                                if(code == 200){
                                                    String  result = StreamTools.readStream(is);
                                                    Message mas= Message.obtain();
                                                    result=result.trim();
                                                   // result=result.substring(1,5);
                                                    mas.obj=result;

                                                    if((((String)mas.obj).equals("成功进入空间")))
                                                    {
                                                        mas.what=1;
                                                    }else{
                                                        mas.what=2;
                                                    }
                                                    handler.sendMessage(mas);
                                                }else{
                                                    Message mas = Message.obtain();
                                                    mas.what = 2;
                                                    handler.sendMessage(mas);
                                                    Log.i("访问网络","失败1");
                                                }

                                            }catch (IOException e) {
                                                // TODO Auto-generated catch block
                                                Message mas = Message.obtain();
                                                mas.what = 2;
                                                handler.sendMessage(mas);
                                                Log.i("访问网络","失败2");
                                            }
                                        }
                                    }).start();
                                }
                        })
                        .setNegativeButton("取消", null).show();

            }else {
                Intent intent=new Intent(this,MySpaceActivity.class);
                startActivity(intent);
            }
        }else {
            Log.i("is_login","失败");
            Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
        }
    }
    public void openSettings(){
        Toast.makeText(MainActivity.this,"You clicked this",Toast.LENGTH_SHORT).show();
    }

    /*登录*/
    public void openLogin(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    /*注册*/
    public void openRegister(){
        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    protected void onDestroy(){
        super.onDestroy();
        if(locationClient!=null&&locationClient.isStarted()){
            locationClient.stop();
            locationClient=null;
        }

    }


    private void getPermissions(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(MainActivity.this, "进行定位需要权限", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, SDK_PERMISSION_REQUEST);
                }
            }
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(MainActivity.this, "进行定位需要权限", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, SDK_PERMISSION_REQUEST);
                }
            }
        }
    }

    public static void getLocation() {
        if (locationClient == null) {
            Log.i("location","is null");
            return;
        } else {
            locationClient.start();
            Log.i("location","is not null");
                    /*
	                *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
	                *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
	                *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
	                *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
	                *定时定位时，调用一次requestLocation，会定时监听到定位结果。
	                */
            locationClient.requestLocation();
        }
    }

    public void YingJian(View view){
        if(is_login==true) {
            Intent intent = new Intent(MainActivity.this, YingJian.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
        }
    }
}
