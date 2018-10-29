package com.web.deyunlib;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Camera;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

/**
 * 获取相关系统信息
 * 
 */
public class SystemUtil {

	/**
	 * 获取SDK版本
	 * 
	 * @return
	 * @return int
	 */
	public static int getSdkVersion() {
		return Build.VERSION.SDK_INT;
	}

	public static boolean isCompatible(int version) {
		return Build.VERSION.SDK_INT >= version;
	}

	/*
	 * 判断是否是该签名打包
	 */
	public static boolean isRelease(Context context, String signatureString) {
		final String releaseSignatureString = signatureString;
		if (releaseSignatureString == null
				|| releaseSignatureString.length() == 0) {
			throw new RuntimeException(
					"Release signature string is null or missing.");
		}

		final Signature releaseSignature = new Signature(releaseSignatureString);
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature sig : pi.signatures) {
				if (sig.equals(releaseSignature)) {
					return true;
				}
			}
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是模拟器
	 * 
	 * @return
	 * @return boolean
	 */
	public static boolean isEmulator() {
		return Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk");
	}

	/**
	 * @Title: getMobileInfo
	 * @Description: 获取手机的硬件信息
	 * @param @return 设定文件
	 * @return String 返回类型
	 */
	public static String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		/**
		 * 通过反射获取系统的硬件信息 获取私有的信息
		 */
		try {
			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * 
	 * 设置手机立刻震动
	 * */
	public static void vibrate(Context context, long milliseconds) {
		Vibrator vib = (Vibrator) context.getApplicationContext()
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}
	public static long getSDAvailableSize(){
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			File path =Environment.getExternalStorageDirectory();
			StatFs statfs=new StatFs(path.getPath());
			//获取block的SIZE
			long blockSize=statfs.getBlockSize();
			//可使用的Block的数量
			long availeBlock=statfs.getAvailableBlocks();

			return availeBlock*blockSize;
		}
		return -1;
	}
	public static long getSDTotalSize(){
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			File path =Environment.getExternalStorageDirectory();
			StatFs statfs=new StatFs(path.getPath());
			//获取block的SIZE
			long blockSize=statfs.getBlockSize();
			//获取BLOCK数量
			long totalBlocks=statfs.getBlockCount();

			return totalBlocks*blockSize;
		}
		return -1;
	}

	static long KB_SIZE = 1024;//1KB
	static long MB_SIZE = KB_SIZE * 1024;//1MB
	static long GB_SIZE = MB_SIZE * 1024;//1GB
	static final DecimalFormat df = new DecimalFormat("0.00");
	public static String getSizeString(long size) {
		String sizeString = "";
		if (size < KB_SIZE) {
			sizeString += size + "字节";
		} else if (size >= KB_SIZE && size < MB_SIZE) {
			sizeString += df.format(size / 1024.0) + "KB";
		} else if (size >= MB_SIZE&&size<GB_SIZE) {
			String csize = df.format(size / (1024.0 * 1024.0));
			sizeString += csize + "MB";
		}else{
			String csize = df.format(size / (1024.0 * 1024.0*1024));
			sizeString += csize + "GB";
		}
		return sizeString;
	}
	//手机型号
	public static String getModel(){
		return Build.MODEL;
	}

	public static String getMac(Context context){
		WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}
	public static String getIMEI(Context context){
		TelephonyManager telMgr = (TelephonyManager)context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		return telMgr.getDeviceId();
	}
	public static String getIMSI(Context context){
		TelephonyManager telMgr = (TelephonyManager)context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		if(isCanUseSim(context))
			return telMgr.getSubscriberId();
		return "";
	}

	/**
	 * 生成设备唯一标识：IMEI、AndroidId、macAddress 三者拼接再 MD5
	 * @return
	 */
	public static String generateUniqueDeviceId(Context context){
		String imei = "";
		String androidId = "";
		String macAddress = "";

		TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager != null) {
			imei = telephonyManager.getDeviceId();
		}
		ContentResolver contentResolver = context.getContentResolver();
		if (contentResolver != null) {
			androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
		}
		WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null) {
			macAddress = wifiManager.getConnectionInfo().getMacAddress();
		}

		StringBuilder longIdBuilder = new StringBuilder();
		if (imei != null) {
			longIdBuilder.append(imei);
		}
		if (androidId != null) {
			longIdBuilder.append(androidId);
		}
		if (macAddress != null) {
			longIdBuilder.append(macAddress);
		}
		return Utils.getMD5(longIdBuilder.toString());
	}

	//品牌
	public static String getBrand() {
		return Build.BRAND;
	}
	public static String getUDID2(Context context) {
		String uuid;
		final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		try {
			if (!"9774d56d682e549c".equals(androidId)) {
				uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
			} else {
				final String deviceId = ((TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
				uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return uuid;
	}
	public static boolean isCanUseSim(Context context) {
		boolean ret = true;
		try {
			TelephonyManager mgr = (TelephonyManager) context.getApplicationContext()
					.getSystemService(Context.TELEPHONY_SERVICE);

			ret = TelephonyManager.SIM_STATE_ABSENT != mgr.getSimState();
			//ret = TelephonyManager.SIM_STATE_READY == mgr.getSimState();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	// 调用系统短信界面
	public static void sendSms(Context context,String body,List<String> phones) {
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<phones.size();i++){
			String phone = phones.get(i);
			if(i==0){
				builder.append(phone);
			}else{
				builder.append(";").append(phone);
			}
		}

		Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
		sendIntent.setData(Uri.parse("smsto:" + builder));
		sendIntent.putExtra("sms_body", body);
		context.startActivity(sendIntent);
	}
	//有些手机(比如三星)内置的几十G的大存储也不算是SD卡，有些手机又算是
	public static boolean isCanUseSdCard(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	private static boolean checkCameraFacing(final int facing) {
		if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
			return false;
		}
		final int cameraCount = Camera.getNumberOfCameras();
		Camera.CameraInfo info = new Camera.CameraInfo();
		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, info);
			if (facing == info.facing) {
				return true;
			}
		}
		return false;
	}
	public static boolean hasBackFacingCamera() {
		return checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
	}
	public static boolean hasFrontFacingCamera() {
		return checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT);
	}
	//是否有通知栏权限,如果通知栏权限，那么系统的toast也是弹不出来的
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public static boolean isNotificationEnable(Context context){
		try{
			AppOpsManager appOpsManager = (AppOpsManager) context.getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
			Class appOpsClass = AppOpsManager.class;
			Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
			Field opPostnotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
			opPostnotificationValue.setAccessible(true);
			int value = (int) opPostnotificationValue.get(Integer.class);
			ApplicationInfo applicationInfo = context.getApplicationInfo();
			int mode = (int) checkOpNoThrowMethod.invoke(appOpsManager,value,applicationInfo.uid,context.getPackageName());
			return AppOpsManager.MODE_ALLOWED == mode;
		}catch (Throwable e){
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 有些全面屏手机没有物理按键，但是他的back,home特别窄，也是返回false
	 * @return
	 */
	//是否有下方虚拟栏
	public static boolean isNavigationBarAvailable() {
		boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
		boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
		return (!(hasBackKey && hasHomeKey));
	}

	//获取下方虚拟栏高度
	public static int getNavigationBarHeight(Context context) {
		if (isNavigationBarAvailable()) {
			Resources resources = context.getResources();
			int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
			if (resourceId > 0) {
				return resources.getDimensionPixelSize(resourceId);
			}
		}
		return 0;
	}

	/**
	 * 跳转到设置-允许安装未知来源-页面
	 */
	public static void startInstallPermissionSettingActivity(Activity activity, int requestCode) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && activity.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.O) {
			//注意这个是8.0新API
            try {
                boolean hasInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
                if (!hasInstallPermission) {
                    Uri packageURI = Uri.parse("package:" + activity.getPackageName());
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivityForResult(intent, requestCode);
                }
            }catch (Exception e){

            }

		}
	}
}
