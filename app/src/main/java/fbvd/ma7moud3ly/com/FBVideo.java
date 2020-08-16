package fbvd.ma7moud3ly.com;

import android.view.View;

public class FBVideo {

    //check if provided link is real facebook video link
    public static boolean isFbVideo(String link) {
        return link.contains("facebook.com") &&
                (
                        link.contains("/videos/") ||
                                link.contains("/posts/") ||
                                link.contains("/groups/") ||
                                link.contains("?story_fbid=")
                );
    }

    //edit video link to open mobile website
    public static String editFbLink(String link) {
        link = link.replace("www.facebook", "m.facebook");
        return link;
    }

    public static FacebookVideo getVideoFormSrc(String src) {
        String name = "", link = "";
        int i = src.indexOf("<title>");
        if (i != -1) {
            name = src.substring(i + 7, src.indexOf("</title>"));
        }
        String t = "href=\"/video_redirect/?src=";
        i = src.indexOf(t);
        if (i == -1) new FacebookVideo("", "");
        src = src.substring(i + 6);
        link = src.substring(0, src.indexOf("target=") - 2);
        t = ".mp4";
        if (link.contains(t)) {
            i = link.indexOf(t) + t.length();
            name = link.substring(0, i);
            i = name.lastIndexOf("%") + 1;
            name = name.substring(i);
        }
        return new FacebookVideo(name, link);
    }


}

class FacebookVideo {
    public final String name;
    public final String link;
    public FacebookVideo(String name, String link) {
        this.name = name;
        this.link = link;
    }
}