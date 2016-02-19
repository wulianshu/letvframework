package com.letv.framework.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;


public class NetWorkUtil {

    //走流量
    public static final int NETTYPE_GSM = 0;

    public static final int NETTYPE_4G = 4;

    public static final int NETTYPE_NO = -1;
    public static final int NETTYPE_WIFI = 1;
    public static final int NETTYPE_2G = 2;
    public static final int NETTYPE_3G = 3;

    /**
     * 获得网络信息
     */
    public static NetworkInfo getAvailableNetWorkInfo(Context context) {
        try {
        	context = context.getApplicationContext(); 
        	ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
        	NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        	
        	return activeNetInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 网络是否可用
     */
    public static boolean isNetAvailable(Context context) {
        boolean isAvailable = false;
        NetworkInfo info = getAvailableNetWorkInfo(context);
        if (info != null && info.isAvailable()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    /**
     * 判断是否是wifi
     *
     * @return
     */
    public static boolean isWifi(Context context) {
        NetworkInfo networkInfo = getAvailableNetWorkInfo(context);
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得网络类型：wifi/2G/3G/4G 1 2 3 4
     *
     * @return
     */
    public static int getNetType(Context context) {
    	context = context.getApplicationContext();
        NetworkInfo networkInfo = getAvailableNetWorkInfo(context);

        if (networkInfo == null || !networkInfo.isAvailable()) {
        	return NETTYPE_NO;
        }
        if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
            return NETTYPE_WIFI;
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);

            switch (telephonyManager.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return NETTYPE_2G;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return NETTYPE_3G;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NETTYPE_4G;
                default:
                    return NETTYPE_4G;
            }
        }
    }

    public static String getNetInfo(Context context) {
        String netType = null;
        int nt = NetWorkUtil.getNetType(context);
        switch (nt) {
            case NETTYPE_WIFI:
                netType = "wifi";
                break;
            case NETTYPE_4G:
                netType = "4g";
                break;
            case NETTYPE_3G:
                netType = "3g";
                break;
            case NETTYPE_2G:
                netType = "2g";
                break;
            case NETTYPE_NO:
                netType = "";
                break;
            default:
                netType = "";
                break;
        }
        return netType;
    }
}
