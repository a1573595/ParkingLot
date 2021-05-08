package com.a1573595.parkingdemo.model.data;

import com.google.gson.annotations.SerializedName;

public class TCMSV_ALLDESC {
    @SerializedName("data")
    public Data data;

    public class Data {
        public String UPDATETIME;
        @SerializedName("park")
        public Result[] park;

        public class Result {
            @SerializedName("id")
            public String id;
            @SerializedName("area")
            public String area; //
            @SerializedName("name")
            public String name; //
            @SerializedName("summary")
            public String summary;  //
            @SerializedName("address")
            public String address;  //
            @SerializedName("tel")
            public String tel;  //
            @SerializedName("payex")
            public String payex;    //
            public String servicetime;  //  營業時間
            @SerializedName("tw97x")
            public double tw97x;    //
            @SerializedName("tw97y")
            public double tw97y;    //
            @SerializedName("totalcar")
            public int totalcar; //轎車停車位
            @SerializedName("totalmotor")
            public int totalmotor;   //機車停車位
            @SerializedName("totalbike")
            public int totalbike;    //自行車停車位
            @SerializedName("totalbus")
            public int totalbus;    //巴士停車位
            public String Pregnancy_First; //孕婦優先車位
            public String Handicap_First; //身障優先車位
            //        public String TOTALLARGEMOTOR; //
            public String ChargingStation; //充電站
        }
    }
}