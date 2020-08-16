package fbvd.ma7moud3ly.com;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;


public class HowToActivity extends AppCompatActivity {
    private ViewPager slider;
    private Integer[] imgs;
    private String[] descriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howto);
        imgs = new Integer[]{R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4, R.drawable.img5};
        descriptions = new String[]{"1 - Find a video on Facebook App", "2 - Copy the video link"
                , "3 - Open FB Downloader and Do nothing..\nLink will be copied from clipboard."
                , "4 - It requires login in first time."
                , "5 - Watch or Download your video..."};

        init_slider();
    }

    private void init_slider() {
        try {
            slider = findViewById(R.id.slider);
            slider.setAdapter(new SliderAdapter(this, imgs, descriptions));
            CircleIndicator indicator = findViewById(R.id.indicator);
            indicator.setViewPager(slider);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SliderAdapter extends PagerAdapter {
    private Integer[] images;
    private String[] descriptions;
    private LayoutInflater inflater;
    private Context context;

    public SliderAdapter(Context context, Integer[] images, String[] descriptions) {
        this.context = context;
        this.images = images;
        this.descriptions = descriptions;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        final ImageView myImage = myImageLayout.findViewById(R.id.slider_image);
        final TextView mydesc = myImageLayout.findViewById(R.id.slider_description);
        myImage.setImageResource(images[position]);
        mydesc.setText(descriptions[position]);
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}


