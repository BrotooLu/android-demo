package com.bro2.b2lib.util;

import android.database.Cursor;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.bro2.b2lib.B2LibEnv.DEBUG;
import static com.bro2.b2lib.B2LibEnv.TAG;

/**
 * Created on 2017/6/8.
 *
 * @author Bro2
 * @version 1.0
 */

public class DbHelper {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Column {
        String name();
    }

    public static <T> List<T> fetchEntities(Class<T> clazz, Cursor cursor) throws Exception {
        ArrayList<T> list = new ArrayList<>();
        if (cursor == null) {
            if (DEBUG) {
                Log.d(TAG, "[DbHelper.fetchEntities] cursor is null");
            }
            return list;
        }

        int count = cursor.getCount();
        if (count < 0) {
            if (DEBUG) {
                Log.d(TAG, "[DbHelper.fetchEntities] no content");
            }
            return list;
        }

        ArrayList<String> projection = new ArrayList<>();
        ArrayList<Class> projectionType = new ArrayList<>();
        ArrayList<Field> projectionField = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            Column col = f.getAnnotation(Column.class);
            if (col != null) {
                projection.add(col.name());
                projectionType.add(f.getClass());
                projectionField.add(f);
            }
        }

        int projectionCount = projection.size();
        int[] index = new int[projectionCount];
        for (int i = 0; i < projectionCount; ++i) {
            index[i] = cursor.getColumnIndex(projection.get(i));
        }

        for (int i = 0; i < count; ++i) {
            cursor.moveToNext();
            T entity = clazz.newInstance();
            for (int j = 0; j < projectionCount; ++i) {
                Class c = projectionType.get(j);
                Field field = projectionField.get(j);
                boolean accessible = field.isAccessible();
                if (!accessible) {
                    field.setAccessible(true);
                }
                if (c == boolean.class) {
                    field.setBoolean(entity, cursor.getInt(index[j]) == 0);
                } else if (c == int.class) {
                    field.setInt(entity, cursor.getInt(index[j]));
                } else {
                    field.set(entity, cursor.getString(index[j]));
                }
                if (!accessible) {
                    field.setAccessible(false);
                }
            }
            list.add(entity);
        }

        return list;
    }

}
