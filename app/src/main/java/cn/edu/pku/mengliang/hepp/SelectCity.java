package cn.edu.pku.mengliang.hepp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    private ListView mListView;
    private TextView select_city_Tv;



//    private String[] data1 = {"City1","City2"};
//    private int[] c_code = {123,234};

    MyApplication App;
    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> cityId = new ArrayList<String>();
    List<City> data = new ArrayList<City>();
    String SelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);


        mListView = (ListView)findViewById(R.id.city_list_view);

        App = (MyApplication)getApplication();
        data = App.getCityList();
        int i = 0;
        while(i <data.size()){
            city.add(data.get(i).getCity().toString());
            cityId.add(data.get(i).getNumber().toString());
            i++;
        }

        //SimpleAdapter simpleAdapter = new SimpleAdapter(SelectCity.this,data,R.layout.support_simple_spinner_dropdown_item,data1,c_code);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,city);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int i,long I){
                Toast.makeText(SelectCity.this,"选择城市为："+city.get(i),Toast.LENGTH_SHORT).show();
                SelectedId = cityId.get(i);

                select_city_Tv = (TextView)findViewById(R.id.title_name);
                select_city_Tv.setText("选择城市：" + city.get(i));
            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode",SelectedId);
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }
}
