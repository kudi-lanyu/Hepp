package cn.edu.pku.mengliang.hepp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.mengliang.app.MyApplication;
import cn.edu.pku.mengliang.bean.City;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/**
 * Created by rwxn on 2016/10/18.
 */
public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mBackBtn;
    private ListView mListView;
    private TextView select_city_Tv;
    private EditText editText_city;

    MyApplication App;
    //用于接收App 传过来的城市数据
    List<City> data = new ArrayList<City>();
    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> cityId = new ArrayList<String>();
    //城市名称拼音首字母 北京---> bj
    ArrayList<String> cityNameHeadChar = new ArrayList<String>();
    //城市名称拼音全字母 北京---> beijing
    ArrayList<String> cityNameStr = new ArrayList<String>();
    //用于更新Listview的数据
    ArrayList<String> searchCity = new ArrayList<String>();

    String SelectedId;

    //字符串结果
    public static StringBuffer sb = new StringBuffer();

    //Listview适配器
    ArrayAdapter<String> adapter;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        //返回键
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.city_list_view);


        //初始化数据
        initCityData();

        set_editText_TextChanged();

        //设置ListView的Adapter
        set_ListView_Adapter();

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long I) {
                Toast.makeText(SelectCity.this, "选择城市为：" + searchCity.get(i), Toast.LENGTH_SHORT).show();
                //更新顶部标题栏的信息
                select_city_Tv = (TextView) findViewById(R.id.title_name);
                select_city_Tv.setText("选择城市：" + searchCity.get(i));

                //传递所选择城市的cityCode
                int j = 0;
                while (j < city.size()) {
                    if (city.get(j).equals(searchCity.get(i))) {
                        SelectedId = cityId.get(j);
                    }
                    j++;
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode", SelectedId);
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

                if (SelectedId != null) {
                    sharedPreferences.edit().putString("main_city_code", SelectedId).commit();
                }

                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }

    private void initCityData() {
        App = (MyApplication) getApplication();
        data = App.getCityList();
        int i = 0;
        while (i < data.size()) {
            city.add(data.get(i).getCity().toString());
            cityId.add(data.get(i).getNumber().toString());
            cityNameHeadChar.add(getPinYinHeadChar(data.get(i).getCity().toString()));
            cityNameStr.add(getPinYin(data.get(i).getCity().toString()));
            i++;
        }
    }

    private void set_editText_TextChanged() {
        editText_city = (EditText) findViewById(R.id.search_edit);
        editText_city.addTextChangedListener(new TextWatcher() {
            //private CharSequence tmp;
            private int editStart;
            private int editEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("hepp", "beforeTextChanged:" + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("hepp", "onTextChanged:" + s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //文本输入字数限制
                editStart = editText_city.getSelectionStart();
                editEnd = editText_city.getSelectionEnd();
                if (s.length() > 10) {
                    Toast.makeText(SelectCity.this, "你输入的字数已经超过了限制! ", Toast.LENGTH_SHORT).show();
                    s.delete(editStart - 1, editEnd);
                    int tmpSelection = editStart;
                    editText_city.setText(s);
                    editText_city.setSelection(tmpSelection);
                }
                Log.d("hepp", "afterTextChanged:" + s);
                //这里要通知一下文本改变，以便更新数据
                mHandler.post(Have_changed);

            }
        });
    }

    Runnable Have_changed = new Runnable() {
        @Override
        public void run() {
            get_Search_Data();
            adapter.notifyDataSetChanged();
        }
    };

    private void get_Search_Data() {
        int i = 0;
        searchCity.clear();
        while (i < data.size()) {
            if (cityNameHeadChar.get(i).toString().equals(editText_city.getText().toString()) || cityNameStr.get(i).toString().equals(editText_city.getText().toString()) || city.get(i).toString().equals(editText_city.getText().toString())) {
                searchCity.add(city.get(i));
            }
            i++;
        }
    }

    private void set_ListView_Adapter() {
        adapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1, searchCity);
    }



    /**
     * 获取汉字字符串的首字母，英文字符不变
     * 例如：阿飞→af
     */
    public static String getPinYinHeadChar(String chines) {
        sb.setLength(0);
        char[] chars = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(chars[i], defaultFormat)[0].charAt(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 获取汉字字符串的第一个字母
     */
    public static String getPinYinFirstLetter(String str) {
        sb.setLength(0);
        char c = str.charAt(0);
        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyinArray != null) {
            sb.append(pinyinArray[0].charAt(0));
        } else {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 获取汉字字符串的汉语拼音，英文字符不变
     */
    public static String getPinYin(String chines) {
        sb.setLength(0);
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(nameChar[i]);
            }
        }
        return sb.toString();
    }
}
