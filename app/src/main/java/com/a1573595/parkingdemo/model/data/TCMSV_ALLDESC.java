package com.a1573595.parkingdemo.model.data;

public class TCMSV_ALLDESC {
    public Data data;

    public class Data {
        public String UPDATETIME;
        public Result[] park;

        public class Result {
            public String id;
            public String area; //
            public String name; //
            public String summary;  //
            public String address;  //
            public String tel;  //
            public String payex;    //
            public String servicetime;  //  營業時間
            public double tw97x;    //
            public double tw97y;    //
            public int totalcar; //轎車停車位
            public int totalmotor;   //機車停車位
            public int totalbike;    //自行車停車位
            public int totalbus;    //巴士停車位
            public String Pregnancy_First; //孕婦優先車位
            public String Handicap_First; //身障優先車位
            //        public String TOTALLARGEMOTOR; //
            public String ChargingStation; //充電站
        }
    }
}