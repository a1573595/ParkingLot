package com.example.puffer.parkingdemo.DataClass;

public class Taipei_Parking_Info {
    public Result[] parkings;

    public class Result{
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
        public String Pregnancy_First; //孕婦優先車位
        public String Handicap_First; //身障優先車位
//        public String TOTALLARGEMOTOR; //
        public String ChargingStation; //充電站
    }
}
