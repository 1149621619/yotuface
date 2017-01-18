package com.example.mr_chen.yotuface;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamTools {
    /*
     * 把一个流里面的内容转换成一个字符串
     * return 流的字符串 null 解析失败
     * */
    public static String readStream(InputStream is){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = is.read(buffer))!=-1) {
                baos.write(buffer,0,len);
            }
           // baos.close();
           if(!baos.toString().equals("加入成功"))
            {
                Log.i("baos","成功");
            }
          //  return new String(baos.toByteArray());
            return new String(baos.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
}