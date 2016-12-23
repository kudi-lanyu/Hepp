package cn.edu.pku.mengliang.hepp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

/**
 * Created by rwxn on 2016/10/18.
 */
public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mBackBtn;
    private ListView mListView;
    private TextView select_city_Tv;
    private EditText editText_city;


//    private String[] data1 = {"City1","City2"};
//    private int[] c_code = {123,234};

    MyApplication App;
    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> cityId = new ArrayList<String>();
    List<City> data = new ArrayList<City>();
    String SelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        editText_city = (EditText) findViewById(R.id.search_edit);
        editText_city.addTextChangedListener(textWatcher);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);


        mListView = (ListView) findViewById(R.id.city_list_view);

        App = (MyApplication) getApplication();
        data = App.getCityList();
        int i = 0;
        while (i < data.size()) {
            city.add(data.get(i).getCity().toString());
            cityId.add(data.get(i).getNumber().toString());
            i++;
        }

        //SimpleAdapter simpleAdapter = new SimpleAdapter(SelectCity.this,data,R.layout.support_simple_spinner_dropdown_item,data1,c_code);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1, city);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long I) {
                Toast.makeText(SelectCity.this, "选择城市为：" + city.get(i), Toast.LENGTH_SHORT).show();
                SelectedId = cityId.get(i);

                select_city_Tv = (TextView) findViewById(R.id.title_name);
                select_city_Tv.setText("选择城市：" + city.get(i));


            }
        });
    }

    TextWatcher textWatcher = new TextWatcher() {
        private CharSequence tmp;
        private int editStart;
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            tmp = s;
            Log.d("hepp", "beforeTextChanged:" + tmp);
        }

        //可以在这里写查找匹配逻辑
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = editText_city.getSelectionStart();
            editEnd = editText_city.getSelectionEnd();
            if (tmp.length() > 10) {
                Toast.makeText(SelectCity.this, "你输入的字数已经超过了限制! ", Toast.LENGTH_SHORT).show();
                s.delete(editStart - 1, editEnd);
                int tmpSelection = editStart;
                editText_city.setText(s);
                editText_city.setSelection(tmpSelection);
            }
            Log.d("hepp", "afterTextChanged:");
        }
    };

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
}
