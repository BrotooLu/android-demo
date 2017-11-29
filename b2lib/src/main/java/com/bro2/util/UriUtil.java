package com.bro2.util;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by Bro2 on 2017/9/12
 */

public final class UriUtil {
    private UriUtil() {
    }

    public static void fillQueryParameterInBundle(Uri uri, Bundle bundle) {
        if (uri == null || uri.isOpaque() || bundle == null) {
            return;
        }

        final String query = uri.getEncodedQuery();
        if (TextUtils.isEmpty(query)) {
            return;
        }

        int len = query.length();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = next != -1 ? next : len;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            if (separator > start) {
                String key = query.substring(start, separator);
                String val;
                if (separator == end) {
                    val = "";
                } else {
                    val = query.substring(separator + 1, end);
                }
                bundle.putString(Uri.decode(key), Uri.decode(val));
            }

            if (next != -1) {
                start = next + 1;
            } else {
                break;
            }
        } while (true);
    }

    public static Bundle convertQueryParameterAsBundle(Uri uri) {
        Bundle bundle = new Bundle();
        fillQueryParameterInBundle(uri, bundle);
        return bundle;
    }
}
