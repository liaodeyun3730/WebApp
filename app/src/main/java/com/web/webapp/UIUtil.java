package com.web.webapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UIUtil {
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int px2sp(Context context, float pxValue) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return (int) (pxValue / displayMetrics.scaledDensity + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return (int) (spValue * displayMetrics.scaledDensity + 0.5f);
    }

    public static int dp2px(Context context, float dp) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return (int) (dp * displayMetrics.density + 0.5);
    }

    public static int px2dp(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        return (int) (dp / displayMetrics.density + 0.5);
    }

    /**
     * 获取通知栏高度
     *
     * @Title: getBarHeight
     * @param @param context
     * @param @return 设定文件
     * @return int 返回类型
     */
    private static int sStatusBarHeight = 0;

    public static int getStatusBarHeight(Context context) {
        if (sStatusBarHeight <= 0) {
            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                sStatusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                sStatusBarHeight = UIUtil.dp2px(context, 25);//大部分手机都是25dp
            }
        }
        return sStatusBarHeight;
    }

    /**
     * @param root 根视图
     * @param size 字体大小
     */
    // 修改整个界面所有控件的字体大小
    public static void changeTextSize(ViewGroup root, int size) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTextSize(size);
            } else if (v instanceof ViewGroup) {
                changeTextSize((ViewGroup) v, size);
            }
        }
    }

    public static void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public static boolean isTouchView(View view, MotionEvent event) {
        if (view != null) {
            int[] leftTop = {0, 0};
            view.getLocationInWindow(leftTop);
            int top = leftTop[1], bottom = top + view.getHeight();
            if (event.getY() > top && event.getY() < bottom) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 5.0以上版本手机动态设置通知栏颜色的方法
     *
     * @param activity 需要设置颜色的Activity
     * @param color 设置的颜色的
     */
    public static void setStatusBarColor(Activity activity, int color) {
        setStatusBarRealColor(activity, color);
    }
    public static void setStatusBarColor(Activity activity) {
        setStatusBarRealColor(activity, 0xffcccccc);
    }
    public static void setStatusBarRealColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(color);
        }
    }


    public static void setStatusBarLightMode(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            MIUISetStatusBarLightMode(activity, dark);
            FlymeSetStatusBarLightMode(activity, dark);
        }
    }

    /**
     * 设置小米状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            Class clazz = activity.getWindow().getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(activity.getWindow(), darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(activity.getWindow(), 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置沉浸式状态栏
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity, boolean on) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }
    }

    /**
     * 检测系统是否有底部虚拟按键
     */
    @SuppressWarnings("unchecked")
    @TargetApi(19)
    public static boolean hasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
            try {
                Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
                Method m = systemPropertiesClass.getMethod("get", String.class);
                String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
                if ("1".equals(navBarOverride)) {
                    hasNavigationBar = false;
                } else if ("0".equals(navBarOverride)) {
                    hasNavigationBar = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return hasNavigationBar;
    }

    public static void setContextNull(View view) {
        Class clazz = View.class;
        try {
            Field fieldContext = clazz.getDeclaredField("mContext");
            fieldContext.setAccessible(true);
            fieldContext.set(view, null);

            Field fieldParent = clazz.getDeclaredField("mParent");
            fieldParent.setAccessible(true);
            fieldParent.set(view, null);
        } catch (Exception e) {
        }

    }


}
