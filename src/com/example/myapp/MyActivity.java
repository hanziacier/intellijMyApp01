package com.example.myapp;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

public class MyActivity extends Activity
{
    private static final int SELECT_PICTURE = 10;
    private static final int SELECT_CAMER = 20;
    // 要上传的文件路径，理论上可以传输任何文件，实际使用时根据需要处理
    private String uploadFile = "/data/git.png";
    private String srcPath = "/data/git.png";
    // 服务器上接收文件的处理页面，这里根据需要换成自己的
    private String actionUrl = "http://www.weloong.net/upload.php";
    private TextView appTitle;
    private TextView mText1;
    private TextView mText2;
    private TextView mTextResult;
    private Button mButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        MyApp myApp = (MyApp)getApplication();
        appTitle = (TextView) findViewById(R.id.applicationTitle);
        appTitle.setText(myApp.user.getUserName());
        mText1 = (TextView) findViewById(R.id.title);
        mTextResult = (TextView) findViewById(R.id.result);
        mText1.setText("文件路径：\n" + uploadFile);
        mText2 = (TextView) findViewById(R.id.uploadUrl);
        mText2.setText("上传网址：\n" + actionUrl);
    /* 设置mButton的onClick事件处理 */
        mButton = (Button) findViewById(R.id.submit);
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CharSequence[] items = {"相册", "相机"};
                new AlertDialog.Builder(MyActivity.this)
                        .setTitle("选择图片来源")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if( which == 0 ){
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.setType("image/*");
                                    startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_PICTURE);
                                }else{
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, SELECT_CAMER);
                                }
                            }
                        })
                        .create().show();
                //FileUploadTask fileuploadtask = new FileUploadTask();
                //fileuploadtask.execute();
            }
        });

    }

    // show Dialog method
    private void showDialog(String mess) {
        new AlertDialog.Builder(MyActivity.this).setTitle("Message")
                .setMessage(mess)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri uri = data.getData();
            String [] proj={MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery( uri,
                    proj,                 // Which columns to return
                    null,       // WHERE clause; which rows to return (all rows)
                    null,       // WHERE clause selection arguments (none)
                    null);                 // Order-by clause (ascending by name)

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            String path = cursor.getString(column_index);
            Bitmap bmp = BitmapFactory.decodeFile(path);
            mText1.setText("文件路径：\n" + path);
            System.out.println("the path is :" + path);
        }else{
            Toast.makeText(MyActivity.this, "请重新选择图片", Toast.LENGTH_SHORT).show();
        }

    }
    class FileUploadTask extends AsyncTask<Object, Integer, Void> {

        private ProgressDialog dialog = null;
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        //the file path to upload
        String pathToOurFile = "/data/git.png";
        //the server address to process uploaded file
        String urlServer = "http://www.weloong.net/upload.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        File uploadFile = new File(pathToOurFile);
        long totalSize = uploadFile.length(); // Get size of file, bytes

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MyActivity.this);
            dialog.setMessage("正在上传...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Object... arg0) {

            long length = 0;
            int progress;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 256 * 1024;// 256KB

            try {
                FileInputStream fileInputStream = new FileInputStream(new File(
                        pathToOurFile));

                URL url = new URL(urlServer);
                connection = (HttpURLConnection) url.openConnection();

                // Set size of every block for post
                connection.setChunkedStreamingMode(256 * 1024);// 256KB

                // Allow Inputs & Outputs
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                // Enable POST method
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);

                outputStream = new DataOutputStream(
                        connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream
                        .writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                                + pathToOurFile + "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    length += bufferSize;
                    progress = (int) ((length * 100) / totalSize);
                    publishProgress(progress);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                        + lineEnd);
                publishProgress(100);

                // Responses from the server (code and message)
                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                /* 将Response显示于Dialog */
                // Toast toast = Toast.makeText(UploadtestActivity.this, ""
                // + serverResponseMessage.toString().trim(),
                // Toast.LENGTH_LONG);
                // showDialog(serverResponseMessage.toString().trim());
                /* 取得Response内容 */
                // InputStream is = connection.getInputStream();
                // int ch;
                // StringBuffer sbf = new StringBuffer();
                // while ((ch = is.read()) != -1) {
                // sbf.append((char) ch);
                // }
                //
                // showDialog(sbf.toString().trim());

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

            } catch (Exception ex) {
                // Exception handling
                // showDialog("" + ex);
                // Toast toast = Toast.makeText(UploadtestActivity.this, "" +
                // ex,
                // Toast.LENGTH_LONG);

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                dialog.dismiss();
                // TODO Auto-generated method stub
            } catch (Exception e) {
            }
        }

    }
}
