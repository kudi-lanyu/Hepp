package cn.edu.pku.mengliang.hepp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by rwxn on 2016/12/22.
 */

public class LocationActivity extends Activity implements View.OnClickListener {

    private Button receive_update_btn, refuse_update_btn;
    private TextView local_city;
    String local_cityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        local_city = (TextView) findViewById(R.id.local_city);


        receive_update_btn = (Button) findViewById(R.id.update_local_city_weather);
        receive_update_btn.setOnClickListener(this);
        refuse_update_btn = (Button) findViewById(R.id.refuse_update_local_city_weather);
        refuse_update_btn.setOnClickListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        String local_city_String = sharedPreferences.getString("cityCode_for_local", "101010100");

        local_city.setText(local_city_String);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.update_local_city_weather) {
//            //更新当地天气，返回UI线程
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
//
//            Intent i = new Intent();
//            i.putExtra("cityCode", local_cityCode);
//            if (local_cityCode != null) {
                sharedPreferences.edit().putString("main_city_code", local_cityCode).commit();
//            }
//
//            setResult(RESULT_OK, i);

            finish();
        }
        if (view.getId() == R.id.refuse_update_local_city_weather) {
            //返回UI线程
            finish();
        }

    }
}
