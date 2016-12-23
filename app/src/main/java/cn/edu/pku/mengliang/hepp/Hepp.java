package cn.edu.pku.mengliang.hepp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.mengliang.Listener.MyLocationListener;
import cn.edu.pku.mengliang.MyAdapter.ViewPagerAdapter_forecastWeather;
import cn.edu.pku.mengliang.bean.ForecastWeather;
import cn.edu.pku.mengliang.bean.TodayWeather;
import cn.edu.pku.mengliang.guide.guide;
import cn.edu.pku.mengliang.util.NetUtil;

/**
 * Created by Administrator on 2016/9/25 0025.
 */
public class Hepp extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final int UPDATE_TODAY_WEATHER = 1;

    private static boolean is_First_use = true;

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

    private ImageView mUpdateBtn;
    private ImageView shareBtn;
    private ImageView locationBtn;
    private ProgressBar mUpdateBtnProgress;

    private ImageView mCitySelect;

    private TextView cityTv, timeTv, humidityTv, weekTv, wenduTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    //forecast
    private TextView week_date1, week_temperture1, week_weather1, week_windTv1;
    private TextView week_date2, week_temperture2, week_weather2, week_windTv2;
    private TextView week_date3, week_temperture3, week_weather3, week_windTv3;
    //forecast
    private TextView week_date4, week_temperture4, week_weather4, week_windTv4;
    private TextView week_date5, week_temperture5, week_weather5, week_windTv5;
    private TextView week_date6, week_temperture6, week_weather6, week_windTv6;

    //adapter for forecastWeather
    private ViewPager vp;
    private ViewPagerAdapter_forecastWeather vpAdapter;
    private List<View> views;
    private ImageView[] dots;
    private int[] ids = {R.id.ui_iv1, R.id.ui_iv2};


    //ForecastWeather
    List<ForecastWeather> forecastWeathers = new ArrayList<ForecastWeather>();

    //location
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    updateForecastWeather(forecastWeathers);
                    //添加更新预测天气

                    mUpdateBtn.setVisibility(View.VISIBLE);
                    mUpdateBtnProgress.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
//        setTitle("myweather");

        SharedPreferences sharedPreferences = getSharedPreferences("first_about_guide", Hepp.MODE_PRIVATE);

        is_First_use = sharedPreferences.getBoolean("is_First_use", true);//获取is_First_use的值，若没有这个key的内容则返回一个默认的boolean
        if (is_First_use) {
            sharedPreferences.edit().putBoolean("is_First_use", false).commit();
            Intent intent = new Intent(this, guide.class);
            startActivity(intent);
        }

        shareBtn = (ImageView) findViewById(R.id.title_share);
        shareBtn.setOnClickListener(this);


        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        mUpdateBtnProgress = (ProgressBar) findViewById(R.id.title_update_btn1);

        locationBtn = (ImageView) findViewById(R.id.title_location);
        locationBtn.setOnClickListener(this);


        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK！");
            Toast.makeText(Hepp.this, "网络OK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了！");
            Toast.makeText(Hepp.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        initView();

        initWeekViews();

        initForecastDate();

        initdots();


        //initLocation();
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //开始定位
        mLocationClient.start();
    }

//    //设置定位参数包括：定位模式（高精度定位模式、低功耗定位模式和仅用设备定位模式），返回坐标类型，是否打开GPS，是否返回地址信息、位置语义化信息、POI信息等等。
//    private void initLocation() {
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
//        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span = 1000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
//        mLocationClient.setLocOption(option);
//    }


    private void initForecastDate() {
        week_date1 = (TextView) views.get(0).findViewById(R.id.week_date);
        week_temperture1 = (TextView) views.get(0).findViewById(R.id.week_temperature);
        week_weather1 = (TextView) views.get(0).findViewById(R.id.week_climate);
        week_windTv1 = (TextView) views.get(0).findViewById(R.id.week_wind);

        week_date2 = (TextView) views.get(0).findViewById(R.id.week_date2);
        week_temperture2 = (TextView) views.get(0).findViewById(R.id.week_temperature2);
        week_weather2 = (TextView) views.get(0).findViewById(R.id.week_climate2);
        week_windTv2 = (TextView) views.get(0).findViewById(R.id.week_wind2);

        week_date3 = (TextView) views.get(0).findViewById(R.id.week_date3);
        week_temperture3 = (TextView) views.get(0).findViewById(R.id.week_temperature3);
        week_weather3 = (TextView) views.get(0).findViewById(R.id.week_climate3);
        week_windTv3 = (TextView) views.get(0).findViewById(R.id.week_wind3);

        week_date4 = (TextView) views.get(1).findViewById(R.id.week_date4);
        week_temperture4 = (TextView) views.get(1).findViewById(R.id.week_temperature4);
        week_weather4 = (TextView) views.get(1).findViewById(R.id.week_climate4);
        week_windTv4 = (TextView) views.get(1).findViewById(R.id.week_wind4);

        week_date5 = (TextView) views.get(1).findViewById(R.id.week_date5);
        week_temperture5 = (TextView) views.get(1).findViewById(R.id.week_temperature5);
        week_weather5 = (TextView) views.get(1).findViewById(R.id.week_climate5);
        week_windTv5 = (TextView) views.get(1).findViewById(R.id.week_wind5);

        week_date6 = (TextView) views.get(1).findViewById(R.id.week_date6);
        week_temperture6 = (TextView) views.get(1).findViewById(R.id.week_temperature6);
        week_weather6 = (TextView) views.get(1).findViewById(R.id.week_climate6);
        week_windTv6 = (TextView) views.get(1).findViewById(R.id.week_wind6);

    }

    private void initdots() {
        dots = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) findViewById(ids[i]);
        }
    }

    private void initWeekViews() {
        //根据已经获取的forecastWeather信息更新布局文件中的值
        updateForecastWeather(forecastWeathers);

        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.forecastweather1, null));
        views.add(inflater.inflate(R.layout.forecastweather2, null));

        vpAdapter = new ViewPagerAdapter_forecastWeather(views, this);
        vp = (ViewPager) findViewById(R.id.week_viewpager);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);

    }

    void initView() {
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        wenduTv = (TextView) findViewById(R.id.temperature_thistime);

        weatherImg = (ImageView) findViewById(R.id.weather_img);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);


        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        wenduTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, SelectCity.class);
            //startActivity(i);
            startActivityForResult(i, 1);
        }
        //分享文本,模拟器中只能支持短信，真机可以多些分享选择
        if (view.getId() == R.id.title_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, "hello world");
            startActivity(Intent.createChooser(intent, "share text to ..."));
        }
        if (view.getId() == R.id.title_location) {
            //更新城市cityCode_for_local
            String cityCode_for_local = mLocationClient.getLastKnownLocation().getCityCode();
            //Log.d("myWeather",cityCode_for_local);
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            if (cityCode_for_local != null) {
                sharedPreferences.edit().putString("cityCode_for_local", cityCode_for_local).commit();
            }
            //询问用户是否要获取新地址的天气
            Intent intent = new Intent(this, LocationActivity.class);
            startActivity(intent);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK！");
                queryWeatherCode(sharedPreferences.getString("main_city_code","101010100"));

            } else {
                Log.d("myWeather", "网络挂了！");
                Toast.makeText(Hepp.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }

        if (view.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            //考虑更新后的cityCode
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            sharedPreferences.edit().putString("main_city_code", cityCode).commit();

            mUpdateBtn.setVisibility(View.INVISIBLE);
            mUpdateBtnProgress.setVisibility(View.VISIBLE);

            Log.d("myWeather", cityCode);


            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK！");
                queryWeatherCode(cityCode);

            } else {
                Log.d("myWeather", "网络挂了！");
                Toast.makeText(Hepp.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    //用来处理城市选择界面返回的结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            if (newCityCode == null) {
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                newCityCode = sharedPreferences.getString("main_city_code", "101010100");
                sharedPreferences.edit().putString("main_city_code", newCityCode).commit();
            }
            Log.d("myWeather", "选择的城市代码为" + newCityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK！");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "网络挂了！");
                Toast.makeText(Hepp.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 根据城市编号查询对应的天气信息
     */
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);

                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);

                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
//
//                    parseXML(responseStr);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();

    }

    private TodayWeather parseXML(String xmldata) {

        TodayWeather todayWeather = null;
        ForecastWeather forecastWeather = null;
        forecastWeathers.clear();
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;

        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");


            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xmlPullParser.getName();//节点名称
                switch (eventType) {
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                                //Log.d("myWeather", "city:     " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                                //Log.d("myWeather", "updatetime:      " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                                //Log.d("myWeather", "shidu:      " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                                //Log.d("myWeather", "wendu:      " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                                //Log.d("myWeather", "pm25:      " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                                //Log.d("myWeather", "quality:      " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("weather")) {
                                forecastWeather = new ForecastWeather();
                            } else if (xmlPullParser.getName().equals("fengxiang")) {
                                if (fengxiangCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengxiang(xmlPullParser.getText());
                                    //Log.d("myWeather", "fengxiang:      " + xmlPullParser.getText());
                                    fengxiangCount++;
                                } else if (fengxiangCount == 1) {
                                    fengxiangCount++;
                                } else if (fengxiangCount != 0 && fengxiangCount % 2 == 0) {
                                    //forecastWeather.setDay_fengxiang(xmlPullParser.nextText());
                                    eventType = xmlPullParser.next();
                                    forecastWeather.setDay_fengxiang(xmlPullParser.getText());
                                    fengxiangCount++;
                                } else if (fengxiangCount != 1 && fengxiangCount % 2 == 1) {
                                    //forecastWeather.setDay_fengxiang(xmlPullParser.nextText());
                                    eventType = xmlPullParser.next();
                                    forecastWeather.setNight_fengli(xmlPullParser.getText());
                                    fengxiangCount++;
                                }
                            } else if (xmlPullParser.getName().equals("fengli")) {
                                if (fengliCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengli(xmlPullParser.getText());
                                    //Log.d("myWeather", "fengli:      " + xmlPullParser.getText());
                                    fengliCount++;
                                } else if (fengliCount == 1) {
                                    fengliCount++;
                                } else if (fengliCount != 0 && fengliCount % 2 == 0) {
                                    //forecastWeather.setDay_fengxiang(xmlPullParser.nextText());
                                    eventType = xmlPullParser.next();
                                    forecastWeather.setDay_fengli(xmlPullParser.getText());
                                    fengliCount++;
                                } else if (fengliCount != 1 && fengliCount % 2 == 1) {
                                    //forecastWeather.setDay_fengxiang(xmlPullParser.nextText());
                                    eventType = xmlPullParser.next();
                                    forecastWeather.setNight_fengli(xmlPullParser.getText());
                                    fengliCount++;
                                }
                            } else if (xmlPullParser.getName().equals("date")) {
                                if (dateCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setDate(xmlPullParser.getText());
                                    //Log.d("myWeather", "fengxiang:      " + xmlPullParser.getText());
                                    dateCount++;
                                } else {
                                    //forecastWeather.setDay_fengxiang(xmlPullParser.nextText());
                                    eventType = xmlPullParser.next();
                                    forecastWeather.setDate(xmlPullParser.getText());
                                    dateCount++;
                                }
                            } else if (xmlPullParser.getName().equals("high")) {
                                if (highCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setHigh(xmlPullParser.getText());
                                    //Log.d("myWeather", "fengxiang:      " + xmlPullParser.getText());
                                    highCount++;
                                } else {
                                    //forecastWeather.setDay_fengxiang(xmlPullParser.nextText());
                                    eventType = xmlPullParser.next();
                                    forecastWeather.setHigh_temperture(xmlPullParser.getText());
                                    highCount++;
                                }
                            } else if (xmlPullParser.getName().equals("low")) {
                                if (lowCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setLow(xmlPullParser.getText());
                                    //Log.d("myWeather", "fengxiang:      " + xmlPullParser.getText());
                                    lowCount++;
                                } else {
                                    //forecastWeather.setDay_fengxiang(xmlPullParser.nextText());
                                    eventType = xmlPullParser.next();
                                    forecastWeather.setLow_temperture(xmlPullParser.getText());
                                    lowCount++;
                                }
                            } else if (xmlPullParser.getName().equals("type")) {
                                if (typeCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setType(xmlPullParser.getText());
                                    //Log.d("myWeather", "fengli:      " + xmlPullParser.getText());
                                    typeCount++;
                                } else if (typeCount == 1) {
                                    typeCount++;
                                } else if (typeCount != 0 && typeCount % 2 == 0) {
                                    //forecastWeather.setDay_fengxiang(xmlPullParser.nextText());
                                    eventType = xmlPullParser.next();
                                    forecastWeather.setDay_weather(xmlPullParser.getText());
                                    typeCount++;
                                } else if (typeCount != 1 && typeCount % 2 == 1) {
                                    //forecastWeather.setDay_fengxiang(xmlPullParser.nextText());
                                    eventType = xmlPullParser.next();
                                    forecastWeather.setNight_weather(xmlPullParser.getText());
                                    typeCount++;
                                }
                            }
                        }
                        break;
                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        //第一个为今日数据暂时不放在ForecastWeather对象中
                        if (dateCount != 1 && xmlPullParser.getName().equals("weather")) {
                            forecastWeathers.add(forecastWeather);
                            Log.d("myWeather", forecastWeather.toString());
                        }

                        break;
                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return todayWeather;
    }

    public void updateTodayWeather(TodayWeather todayWeather) {
        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度" + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力" + todayWeather.getFengli());
        wenduTv.setText("温度" + todayWeather.getWendu());


        // pmImg.setImageResource(对应图片id);
        if (todayWeather.getPm25() != null) {
            int pm25_datav_value = Integer.valueOf(todayWeather.getPm25());
            if (pm25_datav_value >= 0 && pm25_datav_value <= 50) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);

            } else if (pm25_datav_value >= 51 && pm25_datav_value <= 100) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            } else if (pm25_datav_value >= 101 && pm25_datav_value <= 150) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            } else if (pm25_datav_value >= 151 && pm25_datav_value <= 200) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            } else if (pm25_datav_value >= 201 && pm25_datav_value <= 300) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }
        } else {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        }


        switch (todayWeather.getType().toString()) {
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;

        }

        Toast.makeText(Hepp.this, "更新成功", Toast.LENGTH_SHORT).show();
    }

    private void updateForecastWeather(List<ForecastWeather> forecastWeatherList) {
//        //forecast
//        private TextView week_date1, week_temperture1, week_weather1, week_windTv1;
//        private TextView week_date2, week_temperture2, week_weather2, week_windTv2;
//        private TextView week_date3, week_temperture3, week_weather3, week_windTv3;
//        //forecast
//        private TextView week_date4, week_temperture4, week_weather4, week_windTv4;
//        private TextView week_date5, week_temperture5, week_weather5, week_windTv5;
//        private TextView week_date6, week_temperture6, week_weather6, week_windTv6;
        if (forecastWeatherList == null) {
            return;
        }

        int length = forecastWeatherList.size();

        if (length >= 1 && (forecastWeatherList.get(0) != null)) {
            Log.d("myWeather", forecastWeatherList.get(0).getDate());
            Log.d("ddd", week_date1.toString());
            week_date1.setText(forecastWeatherList.get(0).getDate());

            week_temperture1.setText(forecastWeatherList.get(0).getHigh_temperture() + "~" + forecastWeatherList.get(0).getLow_temperture());
            week_weather1.setText((forecastWeatherList.get(0).getDay_weather() == forecastWeatherList.get(0).getNight_weather() ? (forecastWeatherList.get(0).getDay_weather()) : (forecastWeatherList.get(0).getDay_weather() + "转" + forecastWeatherList.get(0).getNight_weather())));
            week_windTv1.setText((forecastWeatherList.get(0).getDay_fengli() == forecastWeatherList.get(0).getNight_fengli() ? (forecastWeatherList.get(0).getDay_fengli()) : (forecastWeatherList.get(0).getDay_fengli() + "到" + forecastWeatherList.get(0).getNight_fengli())));
        }
        if (length >= 2) {
            week_date2.setText(forecastWeatherList.get(1).getDate());
            week_temperture2.setText(forecastWeatherList.get(1).getHigh_temperture() + "~" + forecastWeatherList.get(1).getLow_temperture());
            week_weather2.setText((forecastWeatherList.get(1).getDay_weather() == forecastWeatherList.get(1).getNight_weather() ? (forecastWeatherList.get(1).getDay_weather()) : (forecastWeatherList.get(1).getDay_weather() + "转" + forecastWeatherList.get(1).getNight_weather())));
            week_windTv2.setText((forecastWeatherList.get(1).getDay_fengli() == forecastWeatherList.get(1).getNight_fengli() ? (forecastWeatherList.get(1).getDay_fengli()) : (forecastWeatherList.get(1).getDay_fengli() + "到" + forecastWeatherList.get(1).getNight_fengli())));
        }
        if (length >= 3) {
            week_date3.setText(forecastWeatherList.get(2).getDate());
            week_temperture3.setText(forecastWeatherList.get(2).getHigh_temperture() + "~" + forecastWeatherList.get(2).getLow_temperture());
            week_weather3.setText((forecastWeatherList.get(2).getDay_weather() == forecastWeatherList.get(2).getNight_weather() ? (forecastWeatherList.get(2).getDay_weather()) : (forecastWeatherList.get(2).getDay_weather() + "转" + forecastWeatherList.get(2).getNight_weather())));
            week_windTv3.setText((forecastWeatherList.get(2).getDay_fengli() == forecastWeatherList.get(2).getNight_fengli() ? (forecastWeatherList.get(2).getDay_fengli()) : (forecastWeatherList.get(2).getDay_fengli() + "到" + forecastWeatherList.get(2).getNight_fengli())));
        }
        if (length >= 4) {
            week_date4.setText(forecastWeatherList.get(3).getDate());
            week_temperture4.setText(forecastWeatherList.get(3).getHigh_temperture() + "~" + forecastWeatherList.get(3).getLow_temperture());
            week_weather4.setText((forecastWeatherList.get(3).getDay_weather() == forecastWeatherList.get(3).getNight_weather() ? (forecastWeatherList.get(3).getDay_weather()) : (forecastWeatherList.get(3).getDay_weather() + "转" + forecastWeatherList.get(3).getNight_weather())));
            week_windTv4.setText((forecastWeatherList.get(3).getDay_fengli() == forecastWeatherList.get(3).getNight_fengli() ? (forecastWeatherList.get(3).getDay_fengli()) : (forecastWeatherList.get(3).getDay_fengli() + "到" + forecastWeatherList.get(3).getNight_fengli())));
        }
        if (length >= 5) {
            week_date5.setText(forecastWeatherList.get(4).getDate());
            week_temperture5.setText(forecastWeatherList.get(4).getHigh_temperture() + "~" + forecastWeatherList.get(4).getLow_temperture());
            week_weather5.setText((forecastWeatherList.get(4).getDay_weather() == forecastWeatherList.get(4).getNight_weather() ? (forecastWeatherList.get(4).getDay_weather()) : (forecastWeatherList.get(4).getDay_weather() + "转" + forecastWeatherList.get(4).getNight_weather())));
            week_windTv5.setText((forecastWeatherList.get(4).getDay_fengli() == forecastWeatherList.get(4).getNight_fengli() ? (forecastWeatherList.get(4).getDay_fengli()) : (forecastWeatherList.get(4).getDay_fengli() + "到" + forecastWeatherList.get(4).getNight_fengli())));
        }
        if (length >= 6) {
            week_date6.setText(forecastWeatherList.get(5).getDate());
            week_temperture6.setText(forecastWeatherList.get(5).getHigh_temperture() + "~" + forecastWeatherList.get(5).getLow_temperture());
            week_weather6.setText((forecastWeatherList.get(5).getDay_weather() == forecastWeatherList.get(5).getNight_weather() ? (forecastWeatherList.get(5).getDay_weather()) : (forecastWeatherList.get(5).getDay_weather() + "转" + forecastWeatherList.get(5).getNight_weather())));
            week_windTv6.setText((forecastWeatherList.get(5).getDay_fengli() == forecastWeatherList.get(5).getNight_fengli() ? (forecastWeatherList.get(5).getDay_fengli()) : (forecastWeatherList.get(5).getDay_fengli() + "到" + forecastWeatherList.get(5).getNight_fengli())));
        }


    }
}
