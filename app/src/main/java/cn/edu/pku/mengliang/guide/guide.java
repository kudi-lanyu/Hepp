package cn.edu.pku.mengliang.guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.mengliang.MyAdapter.viewPagerAdapter_guide;
import cn.edu.pku.mengliang.hepp.MainActivity;
import cn.edu.pku.mengliang.hepp.R;

/**
 * Created by rwxn on 2016/11/29.
 */

public class guide extends Activity implements ViewPager.OnPageChangeListener {
    private ViewPager vp;
    private viewPagerAdapter_guide vpAdapter;
    private List<View> views;
    private ImageView[] dots;
    private int[] ids = {R.id.iv1, R.id.iv2, R.id.iv3};
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        initViews();
        initdots();
        button = (Button) views.get(2).findViewById(R.id.guide_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(guide.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void initdots() {
        dots = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) findViewById(ids[i]);
        }
    }

    private void initViews() {

        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.guide1, null));
        views.add(inflater.inflate(R.layout.guide2, null));
        views.add(inflater.inflate(R.layout.guide3, null));

        vpAdapter = new viewPagerAdapter_guide(views, this);
        vp = (ViewPager) findViewById(R.id.viewpager_guide);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int j = 0; j < ids.length; j++) {
            if (j == position) {
                dots[j].setImageResource(R.drawable.page_indicator_focused);
            } else {
                dots[j].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
