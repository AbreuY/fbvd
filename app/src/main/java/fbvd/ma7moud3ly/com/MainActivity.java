package fbvd.ma7moud3ly.com;

import android.Manifest;
import android.app.DownloadManager;
import android.app.TabActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;


@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

    private TabHost tab;
    private LinearLayout tab_section, video_section;
    private SearchView url;
    private WebView web;
    private SwipeRefreshLayout refresh;
    private TextView title;
    private String video_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tab_section = findViewById(R.id.tabs_section);
        video_section = findViewById(R.id.video_section);


        tab_section.setVisibility(View.VISIBLE);
        video_section.setVisibility(View.GONE);

        init_video_section();
        init_tabs_section();
        //init_ads();

    }
/*
    private void init_ads() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3016789990656088~6690814863");
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("7229BDFA1A49F3AE5DE9BD6276416790").build();
        AdView banner = findViewById(R.id.adView);
        banner.loadAd(adRequest);
    }
*/
    private void init_tabs_section() {
        tab = getTabHost();
        TabHost.TabSpec a1 = tab.newTabSpec("HowTo");
        a1.setIndicator("How To");
        Intent i1 = new Intent(this, HowTo.class);
        a1.setContent(i1);
        TabHost.TabSpec a2 = tab.newTabSpec("About");
        a2.setIndicator("About");
        Intent i2 = new Intent(this, About.class);
        a2.setContent(i2);
        tab.addTab(a1);
        tab.addTab(a2);
    }

    private void init_video_section() {
        url = findViewById(R.id.url);
        title = findViewById(R.id.title);
        web = findViewById(R.id.web);
        refresh = findViewById(R.id.refresh);
        refresh.setEnabled(false);
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

        web.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                Download(s);
            }
        });
        web.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            web.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.1.1; en-gb; Build/KLP) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30");
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
            web.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
        else
            web.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36");

        url.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String u) {
                if (isFbVideo(u))
                    fb(u);
                else
                    Toast.makeText(getApplicationContext(), "Invalid video link", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void Download(String url) {
        if (isStoragePermissionGranted())
            try {
                DownloadManager.Request r = new DownloadManager.Request(Uri.parse(url));
                r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, video_name);
                r.allowScanningByMediaScanner();
                r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(r);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to download the video", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
    }

    private boolean isStoragePermissionGranted() {
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

    private boolean isFbVideo(String link) {
        return link.contains("facebook.com") &&
                (
                        link.contains("/videos/") ||
                                link.contains("/posts/") ||
                                link.contains("/groups/") ||
                                link.contains("?story_fbid=")

                );
    }

    private String editFbVideo(String link) {
        link = link.replace("www.facebook", "m.facebook");
        return link;
    }

    private void fb(String u) {
        if (!MyRequests.isConnected(this)) {
            Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_LONG).show();
            return;
        }
        title.setText("");
        url.setQuery(u, false);
        tab_section.setVisibility(View.GONE);
        video_section.setVisibility(View.VISIBLE);
        refresh.setRefreshing(true);
        u = editFbVideo(u);
        new httpTask(u).execute();
    }

    private String getVideo(String src) {
        int i = src.indexOf("<title>");
        if (i != -1) {
            String t = src.substring(i + 7, src.indexOf("</title>"));
            video_name = t;
            title.setText(t);
            title.setVisibility(View.VISIBLE);
        }
        String t = "href=\"/video_redirect/?src=";
        i = src.indexOf(t);
        if (i == -1) return "";
        src = src.substring(i + 6);
        String video = src.substring(0, src.indexOf("target=") - 2);
        t = ".mp4";
        if (video.contains(t)) {
            i = video.indexOf(t) + t.length();
            String name = video.substring(0, i);
            i = name.lastIndexOf("%") + 1;
            name = name.substring(i);
            video_name = name;
        }
        return video;
    }

    private String clipboard() {
        String textToPaste = "";
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            ClipData clip = clipboard.getPrimaryClip();
            if (clip.getDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))
                textToPaste = clip.getItemAt(0).getText().toString();
            textToPaste = clip.getItemAt(0).coerceToText(this).toString();
        }
        return textToPaste;
    }

    private class httpTask extends AsyncTask<Void, Void, String> {
        private final String url;

        httpTask(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = new MyRequests().sendGet(url);
            return result;
        }

        @Override
        protected void onPostExecute(final String s) {
            String video = getVideo(s);
            web.loadUrl("http://m.facebook.com/" + video);
        }

        @Override
        protected void onCancelled() {
        }
    }

    @Override
    public void onBackPressed() {
        if (video_section.getVisibility() == View.VISIBLE) {
            title.setText("");
            url.setQuery("", false);
            web.loadData("<h1>Loading..</h1>", "text/html", "utf-8");
            video_section.setVisibility(View.GONE);
            tab_section.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        String paste = clipboard();
        if (paste.equals(url.getQuery().toString()) == false && isFbVideo(paste))
            fb(paste);
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(getApplicationContext(), "You can download the video now", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), "You must enable the external storage permission" +
                    " to download the video", Toast.LENGTH_LONG).show();

    }
}