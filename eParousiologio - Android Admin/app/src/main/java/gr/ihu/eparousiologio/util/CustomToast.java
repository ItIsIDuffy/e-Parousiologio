package gr.ihu.eparousiologio.util;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import com.google.android.material.snackbar.Snackbar;

import gr.ihu.eparousiologio.R;

public class CustomToast {

    public static void showCustomSnackBar(Activity activity,
                                          String message,
                                          @ColorInt int backgroundColor,
                                          @ColorInt int textColor,
                                          @DrawableRes int iconRes) {
        Snackbar snackbar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
        );
        snackbar.setBackgroundTint(backgroundColor);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView tv = layout.findViewById(com.google.android.material.R.id.snackbar_text);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        tv.setLayoutParams(params);

        tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setTextColor(textColor);

        if (iconRes != 0) {
            int padding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8,
                    activity.getResources().getDisplayMetrics()
            );
            tv.setCompoundDrawablePadding(padding);
            tv.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
        }
        snackbar.show();
    }

    public static void showSuccess(Activity activity, String message) {
        showCustomSnackBar(activity, message,
                activity.getColor(R.color.COLOR_SUCCESS_BG), activity.getColor(R.color.COLOR_SUCCESS_TXT),
                R.drawable.ic_success);
    }

    public static void showError(Activity activity, String message) {
        showCustomSnackBar(activity, message,
                activity.getColor(R.color.COLOR_ERROR_BG), activity.getColor(R.color.COLOR_ERROR_TXT),
                R.drawable.ic_error);
    }

    public static void showWarning(Activity activity, String message) {
        showCustomSnackBar(activity, message,
                activity.getColor(R.color.COLOR_WARNING_BG), activity.getColor(R.color.COLOR_WARNING_TXT),
                R.drawable.ic_warning);
    }

    public static void showInfo(Activity activity, String message) {
        showCustomSnackBar(activity, message,
                activity.getColor(R.color.COLOR_INFO_BG), activity.getColor(R.color.COLOR_INFO_TXT),
                R.drawable.ic_info);
    }
}
