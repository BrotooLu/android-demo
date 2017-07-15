package android.text;

/**
 * Created by Bro2 on 2017/7/12
 *
 */

public class TextUtils {

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }
}
