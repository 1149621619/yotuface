package com.example.mr_chen.yotuface.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtil {
	private static final String TAG = "FileUtil";
	private static final File parentPath = Environment.getExternalStorageDirectory();
	private static String storagePath = "";
	private static final String DST_FOLDER_NAME = "PlayCamera";
	/**
	 * ��ʼ������·��
	 *
	 * @return
	 */
	private static String initPath() {
		if (storagePath.equals("")) {
			storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
			File f = new File(storagePath);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		return storagePath;
	}

	/**
	 * ����Bitmap��sdcard
	 *
	 * @param b
	 */
	public static void saveBitmap(Bitmap b) {

		String path = initPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + "/" + dataTake + ".jpg";
		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			uploadFile(jpegName,dataTake + ".jpg");
			Log.i(TAG, "saveBitmap:成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "saveBitmap:失败");
			e.printStackTrace();
		}


	}


	private static void uploadFile(final String filePath,final String fileName) {

		final String postUrl="http://15km440210.iok.la/pic1_upload.php";
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String end = "\r\n";
					String twoHyphens = "--";
					String boundary = "*****";
					URL url = new URL(postUrl);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setUseCaches(false);
					con.setRequestMethod("POST");
          /* 设置请求属性 */
					con.setRequestProperty("Connection", "Keep-Alive");
					con.setRequestProperty("Charset", "UTF-8");
					con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
          /*设置StrictMode 否则HTTPURLConnection连接失败，因为这是在主进程中进行网络连接*/
				//	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
          /* 设置DataOutputStream，getOutputStream中默认调用connect()*/
					DataOutputStream ds = new DataOutputStream(con.getOutputStream());  //output to the connection

					ds.writeBytes(twoHyphens + boundary + end);
					ds.writeBytes("Content-Disposition: form-data; " +
							"name=\"file\";filename=\"" +
							fileName + "\"" + end);
					ds.writeBytes(end);
          /* 取得文件的FileInputStream */
					//      FileInputStream fStream = new FileInputStream(uploadFile);
					FileInputStream fStream = new FileInputStream(filePath);





          /* 设置每次写入8192bytes */
					int bufferSize = 8192;
					byte[] buffer = new byte[bufferSize];   //8k
					int length = -1;
          /* 从文件读取数据至缓冲区 */
					while ((length = fStream.read(buffer)) != -1) {
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
					while ((ch = is.read()) != -1) {
						b.append((char) ch);
					}


          /* 显示网页响应内容 */

					Log.i("upload:","成功上传");
				} catch (Exception e) {
            /* 显示异常信息 */
					e.printStackTrace();
					Log.i("excption:","上传失败");

				}
			}

		}).start();

	}
}