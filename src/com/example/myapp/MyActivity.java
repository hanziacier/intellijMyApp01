package com.example.myapp;

        import java.io.*;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.DialogInterface;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.StrictMode;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;
        import org.json.JSONObject;

public class MyActivity extends Activity
{
    // 要上传的文件路径，理论上可以传输任何文件，实际使用时根据需要处理
    private String uploadFile = "/data/git.png";
    private String srcPath = "/data/git.png";
    // 服务器上接收文件的处理页面，这里根据需要换成自己的
    private String actionUrl = "http://www.weloong.net/upload.php";
    private TextView mText1;
    private TextView mText2;
    private TextView mTextResult;
    private Button mButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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
                FileUploadTask fileuploadtask = new FileUploadTask();
                fileuploadtask.execute();
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
