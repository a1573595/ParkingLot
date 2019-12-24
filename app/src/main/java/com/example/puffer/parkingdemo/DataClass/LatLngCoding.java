package com.example.puffer.parkingdemo.DataClass;

public class LatLngCoding {
    private static final double a = 6378137.0;
    private static final  double b = 6356752.314245;
    private static final  double lon0 = 121 * Math.PI / 180;
    private static final  double k0 = 0.9999;
    private static final  int dx = 250000;

    //給WGS84經緯度度分秒轉成TWD97坐標
    public String lonlat_To_twd97(int lonD,int lonM,int lonS,int latD,int latM,int latS) {
        double RadianLon = (double)(lonD) + (double)lonM / 60 + (double)lonS / 3600;
        double RadianLat = (double)(latD) + (double)latM / 60 + (double)latS / 3600;
        return Cal_lonlat_To_twd97(RadianLon, RadianLat);
    }

    //給WGS84經緯度弧度轉成TWD97坐標
    public String lonlat_To_twd97(double RadianLon, double RadianLat) {
        return Cal_lonlat_To_twd97(RadianLon, RadianLat);
    }

    //給TWD97坐標 轉成 WGS84 度分秒字串  (type1傳度分秒   2傳弧度)
    public String TWD97_To_lonlat(double XValue, double YValue, int Type) {
        String lonlat = "";

        if (Type == 1) {
            String[] Answer = Cal_TWD97_To_lonlat(XValue, YValue).split(",");
            int LonDValue = Integer.valueOf(Answer[0]);
            int LonMValue = (int)((Double.valueOf(Answer[0]) - LonDValue) * 60);
            int LonSValue = (int)((((Double.valueOf(Answer[0]) - LonDValue) * 60) - LonMValue) * 60);

            int LatDValue = Integer.valueOf(Answer[1]);
            int LatMValue = (int)((Double.valueOf(Answer[1]) - LatDValue) * 60);
            int LatSValue = (int)((((Double.valueOf(Answer[1]) - LatDValue) * 60) - LatMValue) * 60);

            lonlat = LonDValue + "度" + LonMValue + "分" + LonSValue + "秒," + LatDValue + "度" + LatMValue + "分" + LatSValue + "秒,";
        } else if (Type == 2) {
            lonlat = Cal_TWD97_To_lonlat(XValue, YValue);
        }
        return lonlat;
    }

    private String Cal_lonlat_To_twd97(double lon ,double lat) {
        String TWD97 = "";

        lon = (lon/180) * Math.PI;
        lat = (lat/180) * Math.PI;

        //---------------------------------------------------------
        double e = Math.pow((1 - Math.pow(b,2) / Math.pow(a,2)), 0.5);
        double e2 = Math.pow(e,2)/(1-Math.pow(e,2));
        double n = ( a - b ) / ( a + b );
        double nu = a / Math.pow((1-(Math.pow(e,2)) * (Math.pow(Math.sin(lat), 2) ) ) , 0.5);
        double p = lon - lon0;
        double A = a * (1 - n + (5/4) * (Math.pow(n,2) - Math.pow(n,3)) + (81/64) * (Math.pow(n, 4)  - Math.pow(n, 5)));
        double B = (3 * a * n/2.0) * (1 - n + (7/8.0)*(Math.pow(n,2) - Math.pow(n,3)) + (55/64.0)*(Math.pow(n,4) - Math.pow(n,5)));
        double C = (15 * a * (Math.pow(n,2))/16.0)*(1 - n + (3/4.0)*(Math.pow(n,2) - Math.pow(n,3)));
        double D = (35 * a * (Math.pow(n,3))/48.0)*(1 - n + (11/16.0)*(Math.pow(n,2) - Math.pow(n,3)));
        double E = (315 * a * (Math.pow(n,4))/51.0)*(1 - n);

        double S = A * lat - B * Math.sin(2 * lat) +C * Math.sin(4 * lat) - D * Math.sin(6 * lat) + E * Math.sin(8 * lat);

        //計算Y值
        double K1 = S*k0;
        double K2 = k0*nu*Math.sin(2*lat)/4.0;
        double K3 = (k0*nu*Math.sin(lat)*(Math.pow(Math.cos(lat),3))/24.0) * (5 - Math.pow(Math.tan(lat),2) + 9*e2*Math.pow((Math.cos(lat)),2) + 4*(Math.pow(e2,2))*(Math.pow(Math.cos(lat),4)));
        double y = K1 + K2*(Math.pow(p,2)) + K3*(Math.pow(p,4));

        //計算X值
        double K4 = k0*nu*Math.cos(lat);
        double K5 = (k0*nu*(Math.pow(Math.cos(lat),3))/6.0) * (1 - Math.pow(Math.tan(lat),2) + e2*(Math.pow(Math.cos(lat),2)));
        double x = K4 * p + K5 * (Math.pow(p, 3)) + dx;

        TWD97 = x + "," + y;
        return TWD97;
    }

    public static String Cal_TWD97_To_lonlat(double x, double y) {
        double dy = 0;
        double e = Math.pow((1- Math.pow(b,2)/Math.pow(a,2)), 0.5);

        x -= dx;
        y -= dy;

        // Calculate the Meridional Arc
        double M = y/k0;

        // Calculate Footprint Latitude
        double mu = M/(a*(1.0 - Math.pow(e, 2)/4.0 - 3*Math.pow(e, 4)/64.0 - 5*Math.pow(e, 6)/256.0));
        double e1 = (1.0 - Math.pow((1.0 - Math.pow(e, 2)), 0.5)) / (1.0 + Math.pow((1.0 - Math.pow(e, 2)), 0.5));

        double J1 = (3*e1/2 - 27*Math.pow(e1, 3)/32.0);
        double J2 = (21*Math.pow(e1, 2)/16 - 55*Math.pow(e1, 4)/32.0);
        double J3 = (151*Math.pow(e1, 3)/96.0);
        double J4 = (1097*Math.pow(e1, 4)/512.0);

        double fp = mu + J1*Math.sin(2*mu) + J2*Math.sin(4*mu) + J3*Math.sin(6*mu) + J4*Math.sin(8*mu);

        // Calculate Latitude and Longitude

        double e2 = Math.pow((e*a/b), 2);
        double C1 = Math.pow(e2*Math.cos(fp), 2);
        double T1 = Math.pow(Math.tan(fp), 2);
        double R1 = a*(1-Math.pow(e, 2))/Math.pow((1-Math.pow(e, 2)*Math.pow(Math.sin(fp), 2)), (3.0/2.0));
        double N1 = a/Math.pow((1-Math.pow(e, 2)*Math.pow(Math.sin(fp), 2)), 0.5);

        double D = x/(N1*k0);

        // 計算緯度
        double Q1 = N1*Math.tan(fp)/R1;
        double Q2 = (Math.pow(D, 2)/2.0);
        double Q3 = (5 + 3*T1 + 10*C1 - 4*Math.pow(C1, 2) - 9*e2)*Math.pow(D, 4)/24.0;
        double Q4 = (61 + 90*T1 + 298*C1 + 45*Math.pow(T1, 2) - 3*Math.pow(C1, 2) - 252*e2)*Math.pow(D, 6)/720.0;
        double lat = fp - Q1*(Q2 - Q3 + Q4);

        // 計算經度
        double Q5 = D;
        double Q6 = (1 + 2*T1 + C1)*Math.pow(D, 3)/6;
        double Q7 = (5 - 2*C1 + 28*T1 - 3*Math.pow(C1, 2) + 8*e2 + 24*Math.pow(T1, 2))*Math.pow(D, 5)/120.0;
        double lon = lon0 + (Q5 - Q6 + Q7)/Math.cos(fp);

        lat = (lat * 180) / Math.PI; //緯
        lon = (lon * 180) / Math.PI; //經


        String lonlat = lat + "," + lon;
        return lonlat;
    }
}
