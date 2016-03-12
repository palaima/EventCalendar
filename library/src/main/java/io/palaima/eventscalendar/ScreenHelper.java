package io.palaima.eventscalendar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

final class ScreenHelper {

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public static boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static int getStatusBarHeightResources(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result == 0 ? getStatusBarHeight((Activity) context) : result;
    }

    public static int getStatusBarHeight(Activity context) {
        Rect rect = new Rect();
        Window window = context.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    public static int getScreenWidth(Context context) {
        calculateScreenDimensions(context);
        return mWidth;
    }

    public static int getScreenHeight(Context context) {
        calculateScreenDimensions(context);
        return mHeight;
    }

    public static int dpToPx(@NonNull Context context, float dp) {
        return dpToPx(context.getResources(), dp);
    }

    public static int dpToPx(@NonNull Resources resources, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public static float pxToDp(@NonNull Context context, int px) {
        return pxToDp(context.getResources(), px);
    }

    public static float pxToDp(@NonNull Resources resources, int px) {
        return px / (resources.getDisplayMetrics().densityDpi / 160f);
    }

    private static int mWidth;
    private static int mHeight;

    @SuppressWarnings("deprecation")
    private static void calculateScreenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final Point point = new Point();
            display.getSize(point);
            mWidth = point.x;
            mHeight = point.y;
        } else {
            mWidth = display.getWidth();
            mHeight = display.getHeight();
        }
    }

}
