package com.example.coolweather.gson;

import android.app.Fragment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/5/29.
 */

public class Weather {
   /*
    服务器请求的数据格式：
    {
        "HeWeather" : [
            {
                "status" : "ok",
                "basic" : {
                         "city" : "宿州",
                        "id" : "CN101190401",
                        "update" : {
                                "loc" : "2016-08-01 21:58"
                         }

                "aqi" : {
                        "city" :{
                                "aqi" : "44",
                                "pm25" : "24"
                         }
                },
                "now" : {
                        "tmp" : "29",
                        "cond" : {
                                "txt" : "阵雨"
                         }
                 },
                "suggesstion" : {
                        "comf" : {
                                "txt" : "白天天气较热。。。。"
                        }
                        "cw" : {
                            "txt" : "白天天气较热。。。。"
                        }
                        "sport" : {
                            "txt" : "白天天气较热。。。。"
                        }

                 },
                "daily_forecast" : [
                    {
                        "date" : "2016-08-08",
                         "cond" : {
                            "txt_d" : "阵雨"
                        },
                        "tmp" : {
                            "max" : "34",
                            "min" : "27"
                         }

                    },
                    {
                        "date" : "2016-08-09",
                         "cond" : {
                             "txt_d" : "阵雨"
                        },
                        "tmp" : {
                             "max" : "31",
                            "min" : "21"
                        }
                    }
                ]
            }
         }
      ]
    }
*/
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public  Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
