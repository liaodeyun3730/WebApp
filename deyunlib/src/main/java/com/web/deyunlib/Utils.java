package com.web.deyunlib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import java.io.EOFException;
import java.security.MessageDigest;
import java.text.DecimalFormat;


public class Utils {
    private static final String TAG = "Utils";

    /**
     * 验证有效的电话
     * 不少于3位的纯数字
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        String regex = "^\\d{3,}$";
        return mobiles.matches(regex);
    }
    // 验证邮箱
    public static boolean isValidEmail(String str) {
        if (str == null || str.length() == 0)
            return false;
        return str.matches("^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$");
    }

    public static boolean isValidPwd(String str) {
        if (str == null || str.length() < 8)
            return false;
        return true;
    }
    /**
     * 模糊手机号
     */
    public static String garblePhoneNum(String phone) {
        if (isEmpty(phone))
            return "";
        if (phone.length() < 11)
            return phone;
        return phone.substring(0, 3) + "******" + phone.substring(9);
    }

    /**
     * str为空或者空字符串则返回true。否则返回false
     */
    public static boolean isEmpty(String string) {
        return null == string || "".equals(string.trim()) || "null".equals(string.trim());
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    /**
     * 获取MD5值
     *
     * @param content
     * @return
     */
    public static String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取hash字符串
     *
     * @param digest
     * @return
     */
    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }

    public static void showProgressView(ImageView imageProgress) {
        try {
            if (imageProgress == null) {
                return;
            }
            imageProgress.setVisibility(View.VISIBLE);
            AnimationDrawable drawable = (AnimationDrawable) imageProgress.getDrawable();
            drawable.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取手机号码
     *
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
        if (!SystemUtil.isCanUseSim(context)) {
            return "";
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            String phoneNumber = tm.getLine1Number();// 获取本机号码 有些手机这里会出异常
            if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.contains("+86")) {
                String replace = phoneNumber.replace("+86", "");
                return replace;
            }
            return phoneNumber;
        } catch (Throwable e) {
            return "";
        }

    }




    /**
     * @param source
     * @param decimalNumber 精确到几位小数
     * @return
     */
    public static String formatNumber(double source, int decimalNumber) {
        if (decimalNumber <= 0) {
            return "" + (int) source;
        } else {
            StringBuilder sb = new StringBuilder("#0.");
            for (int i = 1; i <= decimalNumber; i++) {
                sb.append("0");
            }
            DecimalFormat df = new DecimalFormat(sb.toString());
            String str = df.format(source);
            return str;
        }
    }


    /**
     * 获取硬盘缓存的路径地址。
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 检查输入字符中是否有非法字符
     */
    public static boolean checkInvalidFace(String content, String toast) {
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if ((int) c == 55357) {
                //ToastUtil.showToastLong(MyApplication.getInstance(), "昵称不能包含特殊符号及表情");
                return true;
            }
        }
        return false;
    }
    /**
     * 检查输入字符中是否有中文或表情
     */
    public static boolean checkInvalidChineseFace(String content, String toast) {
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            int cint = (int) c;
            //LogUtil.writeDebug("checkValidContext", c + " = " + (int) cint);
            if (cint == 55357||(cint>0x4e00&&cint<0x9fa5)) {
                //ToastUtil.showToastLong(MyApplication.getInstance(), "昵称不能包含特殊符号及表情");
                return true;
            }
        }
        return false;
    }

    public static int getMax(int[] arr) {
        int max = arr[0];

        for (int x = 1; x < arr.length; x++) {
            if (arr[x] > max) {
                max = arr[x];
            }
        }

        return max;
    }

    public static int getMin(int[] arr) {
        int min = arr[0];

        for (int x = 1; x < arr.length; x++) {
            if (arr[x] < min) {
                min = arr[x];
            }
        }

        return min;
    }

    public static float getMax(float[] arr) {
        float max = arr[0];

        for (int x = 1; x < arr.length; x++) {
            if (arr[x] > max) {
                max = arr[x];
            }
        }

        return max;
    }

    public static float getMin(float[] arr) {
        float min = arr[0];

        for (int x = 1; x < arr.length; x++) {
            if (arr[x] < min) {
                min = arr[x];
            }
        }

        return min;
    }

}
