package fbvd.ma7moud3ly.com;

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.ma7moud3ly.ustore.USon;

import java.util.List;


public class VideoActivity extends AppCompatActivity {

    private WebView web;
    private SwipeRefreshLayout refresh;
    private TextView title;
    private String videoName;
    private USon uson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);
        //init view
        init_video_page();
        uson = new USon(getApplicationContext(), "history");
        //get provided link
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("link")) extract_video(bundle.getString("link"));
    }

    //init ui of layout video_layout
    private void init_video_page() {
        title = findViewById(R.id.title);
        web = findViewById(R.id.web);
        refresh = findViewById(R.id.refresh);
        refresh.setEnabled(false);

        //init browser
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                refresh.setRefreshing(false);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (
                            request.getUrl().toString().contains("login.php") ||
                                    request.getUrl().toString().contains("/reg/") ||
                                    request.getUrl().toString().contains("video_redirect")
                                    || request.getUrl().toString().contains(".mp4")
                    ) {
                        return super.shouldOverrideUrlLoading(view, request);
                    }
                }
                return true;
            }
        });
        //when user click on download call download function
        web.setDownloadListener((s, s1, s2, s3, l) -> Download(s));
        web.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            web.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.1.1; en-gb; Build/KLP) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30");
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
            web.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
        else
            web.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36");

    }

    //extract facebook video from facebook provided link
    private void extract_video(String link) {
        List<String> list = uson.getList();
        if (!list.contains(link)) {
            list.add(link);
            uson.putList(list);
        }
        //return if no internet connection
        if (!GetRequests.isConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        title.setText("");
        refresh.setRefreshing(true);
        //make sure video starts with m.facebook...
        link = FBVideo.editFbLink(link);
        new httpTask(link).execute();
    }

    //fetch video page source in background thread
    private class httpTask extends AsyncTask<Void, Void, String> {
        private final String link;

        httpTask(String url) {
            this.link = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = new GetRequests().get(link);
            return result;
        }

        @Override
        protected void onPostExecute(final String src) {
            //get video link from source
            FacebookVideo fb = FBVideo.getVideoFormSrc(src);
            web.loadUrl("http://m.facebook.com/" + fb.link);
            videoName = fb.name;
            title.setText(fb.name);
        }

        @Override
        protected void onCancelled() {
        }
    }

    //.download video
    private void Download(String url) {
        if (isStoragePermissionGranted())
            try {
                DownloadManager.Request r = new DownloadManager.Request(Uri.parse(url));
                r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, videoName);
                r.allowScanningByMediaScanner();
                r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(r);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to download the video", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        title.setText("");
        web.loadData("<h1>Loading..</h1>", "text/html", "utf-8");
    }

    //check or request storage permission
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.
                        WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.download_enabled), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.need_storage_permission), Toast.LENGTH_LONG).show();

    }
}