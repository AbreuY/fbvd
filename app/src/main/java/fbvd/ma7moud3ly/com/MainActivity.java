package fbvd.ma7moud3ly.com;

import android.app.TabActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.Toast;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;


@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

    private TabHost tab;
    private SearchView url;
    private Intent videoIntent;
    public static String video_link = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        init_home_page();
        videoIntent = new Intent(this, VideoActivity.class);
    }

    private void init_home_page() {
        tab = getTabHost();
        url = findViewById(R.id.url);
        Intent intent;
        TabHost.TabSpec a1 = tab.newTabSpec("howto").
                setIndicator("How to").
                setContent(new Intent(this, HowToActivity.class));
        TabHost.TabSpec a2 = tab.newTabSpec("history").
                setIndicator("History").
                setContent(new Intent(this, HistoryActivity.class));
        TabHost.TabSpec a3 = tab.newTabSpec("about").
                setIndicator("About").
                setContent(new Intent(this, AboutActivity.class));

        tab.addTab(a1);
        tab.addTab(a2);
        tab.addTab(a3);

        url.setOnQueryTextListener(on_search);
    }

    private void videoActivity(String link) {
        videoIntent.putExtra("link", link);
        startActivity(videoIntent);
    }

    //when user paste link in search box
    private SearchView.OnQueryTextListener on_search = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String link) {
            if (FBVideo.isFbVideo(link)) {
                video_link = link;
                videoActivity(link);
            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_link), Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    //read link from clipboard
    private String readClipboard() {
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

    private void clearClipboard() {
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("", "");
        clipBoard.setPrimaryClip(data);
    }

    //when user navigate activity read url from the clipboard
    @Override
    protected void onResume() {
        String link = readClipboard();
        if (!video_link.equals(link) && FBVideo.isFbVideo(link)) {
            video_link = link;
            videoActivity(link);
        }
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        url.setQuery("", false);
    }

}