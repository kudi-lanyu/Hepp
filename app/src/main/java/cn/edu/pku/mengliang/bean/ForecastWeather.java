package cn.edu.pku.mengliang.bean;

/**
 * Created by rwxn on 2016/11/29.
 */

public class ForecastWeather {
    private String date;//日期及星期
    private String high_temperture;//最高气温
    private String low_temperture;//最低气温
    private String day_weather;//白天天气day_type
    private String night_weather;//晚间天气night_type
    private String day_fengxiang;//白天风向
    private String night_fengxiang;//晚上风向
    private String day_fengli;//白天风力day_fengli
    private String night_fengli;//晚间风力night_fengli
    //没有存储白天和黑夜的风向


    public String getDay_fengxiang() {
        return day_fengxiang;
    }

    public void setDay_fengxiang(String day_fengxiang) {
        this.day_fengxiang = day_fengxiang;
    }

    public String getNight_fengxiang() {
        return night_fengxiang;
    }

    public void setNight_fengxiang(String night_fengxiang) {
        this.night_fengxiang = night_fengxiang;
    }


    public String getHigh_temperture() {
        return high_temperture;
    }

    public void setHigh_temperture(String high_temperture) {
        this.high_temperture = high_temperture;
    }

    public String getLow_temperture() {
        return low_temperture;
    }

    public void setLow_temperture(String low_temperture) {
        this.low_temperture = low_temperture;
    }

    public String getDay_weather() {
        return day_weather;
    }

    public void setDay_weather(String day_weather) {
        this.day_weather = day_weather;
    }

    public String getNight_weather() {
        return night_weather;
    }

    public void setNight_weather(String night_weather) {
        this.night_weather = night_weather;
    }

    public String getDay_fengli() {
        return day_fengli;
    }

    public void setDay_fengli(String day_fengli) {
        this.day_fengli = day_fengli;
    }

    public String getNight_fengli() {
        return night_fengli;
    }

    public void setNight_fengli(String night_fengli) {
        this.night_fengli = night_fengli;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override

    public String toString() {
        return "ForecastWeather{" +
                "date:" + date + "/ " +
                "high_temperture:" + high_temperture + "/ " +
                "low_temperture:" + low_temperture + "/ " +
                "day_weather:" + day_weather + "/ " +
                "night_weather:" + night_weather + "/ " +
                "day_fengxiang:" + day_fengxiang + "/ " +
                "night_fengxiang:" + night_fengxiang + "/ " +
                "day_fengli:" + day_fengli + "/ " +
                "night_fengli:" + night_fengli + "/ " +
                "}";
    }
}
