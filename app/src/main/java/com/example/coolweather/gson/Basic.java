package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/5/29.
 */

public class Basic {

   /*
   basic字段格式：
   "basic" : {
        "city" : "宿州",
        "id" : "CN101190401",
        "update" : {
            "loc" : "2016-08-01 21:58"
        }
    }

    */

    /*
    * 对应basic字段下的“city”
    * */
    @SerializedName("city")
    public String cityName;

    /*
   * 对应basic字段下的“id”
   * */
    @SerializedName("id") //此注解的方式可以让java字段和json字段简历映射关系
    public String weatherId;

    /*
   * 对应basic字段下的“update”
   * */
    public Update update;
    public class Update{

        /*
        * 对应basic字段下的“update”字段下的“loc”
        * */
        @SerializedName("loc")
        public String updateTime;
    }


}
