package android.text;

/**
 * Created on 2017/7/12.
 *
 * @author Bro2
 * @version 1.0
 */

public class TextUtils {

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }
}
