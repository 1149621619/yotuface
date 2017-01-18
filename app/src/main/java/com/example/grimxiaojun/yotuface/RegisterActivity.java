package com.example.mr_chen.yotuface;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
* Code by wangjie
* date:2016/8/6
* */

public class RegisterActivity extends AppCompatActivity {

    private File filePath;//文件路径
    private ImageView imageView;  //显示照片
    private  File fileName1 = null;   //拍照所得照片名
    private File filename=null;//上传的照片名
    protected static final int ERROR = 4;
    protected static final int SUCCESS =3;
    private EditText username;
    private EditText password;
    private EditText phonenumber;
    private ImageButton imageButton;
    private TextView textView;
    private String uploadUrl = "http://15km440210.iok.la/upload1.php";
    Handler handle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    textView.setText("上传成功。");
                    Toast.makeText(RegisterActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    textView.setText("无可用网络。");
                    Toast.makeText(RegisterActivity.this, "无可用网络", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    textView.setText("找不到服务器地址");
                    Toast.makeText(RegisterActivity.this, "找不到服务器地址", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(RegisterActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();
                    Log.i("运行",(String)msg.obj);
                    break;
                case ERROR:
                    Toast.makeText(RegisterActivity.this,"注册失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username);
        password= (EditText) findViewById(R.id.password);
        phonenumber= (EditText) findViewById(R.id.phonenumber);
        closeStrictMode();
        imageButton= (ImageButton) findViewById(R.id.take_photo);
        textView= (TextView) findViewById(R.id.textview);
        imageView= (ImageView) findViewById(R.id.imageview);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // MediaStore.ACTION_VIDEO_CAPTURE
//                // 从一个既存的Camera应用中申请视频功能的Intent动作类型
//                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                // MediaStore.EXTRA_VIDEO_QUALITY：这个值的范围是0~1，0的时候质量最差且文件最小，1的时候质量最高且文件最大。
//                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//                startActivityForResult(intent, 1);
                //文件保存路径
                filePath = new File(Environment.getExternalStorageDirectory(), "myCamera");
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }

                //创建新文件，
                fileName1 = new File(filePath,  "register.jpg");

                try {
                    if (!fileName1.exists()) {
                        fileName1.createNewFile();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //打开设备自带照相机
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileName1));
                startActivityForResult(intent,Activity.DEFAULT_KEYS_DIALER);

            }
        });

    }

    public static void closeStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll().penaltyLog().build());
    }
    public void register(View view){
        final String qq = username.getText().toString();
        final String psd = password.getText().toString();
        final String phone=phonenumber.getText().toString();
        if(TextUtils.isEmpty(qq)||TextUtils.isEmpty(psd)||TextUtils.isEmpty(phone)){
            Toast.makeText(this, "用户和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!textView.getText().toString().equals("已拍照"))
        {
            Toast.makeText(this,"请上传照片",Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor=getSharedPreferences("register",MODE_PRIVATE).edit();
        editor.putString("username",qq);
        editor.putString("password",psd);
        editor.putString("phonenumber",phone);
        editor.commit();
        Log.i("commit","成功");

        //从设备中重命名所拍照片将其上传到服务器
        //upload the photo to the server from the device
        filename=new File("/storage/emulated/0/myCamera/"+qq+".jpg");
        try {
            if (!filename.exists()) {
                filename.createNewFile();
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        fileName1.renameTo(filename);
        Log.i("filename1",fileName1.toString());
        /*
        *创建文件保存所拍照片
         */
//        File filePath = new File(Environment.getExternalStorageDirectory(), "myCamera");
//        if (!filePath.exists()) {
//            filePath.mkdirs();
//        }
//        fileName1 = new File(filePath,  qq+".jpg");
//
//        try {
//            if (!fileName1.exists()) {
//                fileName1.createNewFile();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }




        /*
        *开启线程将用户名，密码，以照片上传到服务器中。
         */

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {


                    uploadFile(filename);

                    String path = "http://15km440210.iok.la/register.php";
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    Log.i("数据","成功");
                    //请求方式post
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    //必须指定两个请求的参数
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//请求的类型  表单数据
                    String data = "username="+qq+"&password="+psd+"&phonenumber="+phone+"&button=";
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
                        mas.what = SUCCESS;
                        mas.obj = result;
                        handle.sendMessage(mas);

                    }else{
                        Message mas = Message.obtain();
                        mas.what = ERROR;
                        handle.sendMessage(mas);
                        Log.i("访问网络","失败1");
                    }

                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    Message mas = Message.obtain();
                    mas.what = ERROR;
                    handle.sendMessage(mas);
                    Log.i("访问网络","失败2");
                }

            }
        }).start();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("test", "onActivityResult() requestCode:" + requestCode
                + ",resultCode:" + resultCode + ",data:" + data);
        if(requestCode==Activity.DEFAULT_KEYS_DIALER)
        {
            imageView.setImageURI(Uri.fromFile(fileName1));
            Log.i("filename1:",fileName1.toString());
            textView.setText("已拍照");
        }
        /*
        if (resultCode == RESULT_OK) {
            if (null != data) {
                Uri uri = data.getData();
                if (uri == null) {
                    return;
                } else {

                    // 视频捕获并保存到指定的fileUri意图
                    Toast.makeText(this, "Video saved to:\n" + data.getData(),
                            Toast.LENGTH_LONG).show();
                    textView.setText("正在上传中，请稍候");
                    Cursor c = getContentResolver().query(uri,
                            new String[] { MediaStore.MediaColumns.DATA },
                            null, null, null);
                    if (c != null && c.moveToFirst()) {


                        String filPath = c.getString(0);
                        Log.i("test", filPath);
                        new Upload(filPath).start();
                    }

                }
            }
 */
        else if (resultCode == RESULT_CANCELED) {
            // 用户取消了捕捉
            Toast.makeText(RegisterActivity.this, "你取消拍摄", Toast.LENGTH_LONG)
                    .show();
        } else {
            // 捕捉失败,建议用户
            Toast.makeText(RegisterActivity.this, "拍摄失败请重新拍摄",
                    Toast.LENGTH_LONG).show();
        }


    }

/*
*以下代码为上传照片所用
 */
     /* 上传文件至Server的方法 */
private void uploadFile(File qq)
{


    String end = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    try
    {
        URL url = new URL(uploadUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
          /* Output to the connection. Default is false,
             set to true because post method must write something to the connection */
        con.setDoOutput(true);
          /* Read from the connection. Default is true.*/
        con.setDoInput(true);
          /* Post cannot use caches */
        con.setUseCaches(false);
          /* Set the post method. Default is GET*/
        con.setRequestMethod("POST");
          /* 设置请求属性 */
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
          /*设置StrictMode 否则HTTPURLConnection连接失败，因为这是在主进程中进行网络连接*/
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
          /* 设置DataOutputStream，getOutputStream中默认调用connect()*/
        DataOutputStream ds = new DataOutputStream(con.getOutputStream());  //output to the connection
        ds.writeBytes(twoHyphens + boundary + end);
        ds.writeBytes("Content-Disposition: form-data; " +
                "name=\"file\";filename=\"" +
               qq + "\"" + end);
        ds.writeBytes(end);
          /* 取得文件的FileInputStream */
        FileInputStream fStream = new FileInputStream(filename);
          /* 设置每次写入8192bytes */
        int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];   //8k
        int length = -1;
          /* 从文件读取数据至缓冲区 */
        while ((length = fStream.read(buffer)) != -1)
        {
            /* 将资料写入DataOutputStream中 */
            ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
          /* 关闭流，写入的东西自动生成Http正文*/
        fStream.close();
          /* 关闭DataOutputStream */
        ds.close();
          /* 从返回的输入流读取响应信息 */
        InputStream is = con.getInputStream();  //input from the connection 正式建立HTTP连接
        int ch;
        StringBuffer b = new StringBuffer();
        while ((ch = is.read()) != -1)
        {
            b.append((char) ch);
        }
//        textView.setText("照片上传成功");
          /* 显示网页响应内容 */
       // Toast.makeText(MainActivity.this, b.toString().trim(), Toast.LENGTH_SHORT).show();//Post成功
    } catch (Exception e)
    {
        e.printStackTrace();
            /* 显示异常信息 */
       // Toast.makeText(MainActivity.this, "Fail:" + e, Toast.LENGTH_SHORT).show();//Post失败
      //  textView.setText("照片上传失败，请重试");
    }

}





    /*
    以下代码为上传视频所用
     */
    /*
    public class Upload extends Thread {
        String filpath;

        public Upload(String filpath) {
            super();
            this.filpath = filpath;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            ConnectionDetector cd = new ConnectionDetector(RegisterActivity.this);
            if (cd.isConnectingToInternet()) {
                if (cd.checkURL(uploadUrl)) {
                    uploadVideo(RegisterActivity.this, filpath);
                    //uploadFile(filpath);
                    handle.sendEmptyMessage(0);
                } else {
                    handle.sendEmptyMessage(2);
                }
            } else {
                handle.sendEmptyMessage(1);
            }
        }

    }


    public void uploadVideo(Context context, String videoPath) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(uploadUrl);
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            if(!videoPath.isEmpty()){

                FileBody filebodyVideo = new FileBody(new File(videoPath));
                reqEntity.addPart("file", filebodyVideo);
            }
            postRequest.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(postRequest);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }

            Log.e("Response: ", s.toString());

        } catch (Exception e) {
            Log.e(e.getClass().getName(), e.getMessage());
        }
    }
    */

}