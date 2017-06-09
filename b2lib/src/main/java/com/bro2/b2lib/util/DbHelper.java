package com.bro2.b2lib.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
    public @interface Column {
        String name();

        boolean key() default false;
    }

    private static class Statement {
        ArrayList<String> projection = new ArrayList<>();
        ArrayList<Class> projectionType = new ArrayList<>();
        ArrayList<Field> projectionField = new ArrayList<>();
        ArrayList<Field> keyField = new ArrayList<>();

        Statement(Class<?> clazz) {
            for (Field f : clazz.getDeclaredFields()) {
                Column col = f.getAnnotation(Column.class);
                if (col != null) {
                    projection.add(col.name());
                    projectionType.add(f.getType());
                    projectionField.add(f);
                    if (col.key()) {
                        keyField.add(f);
                    }
                }
            }
        }

        String getEntityQuery(Object entity) throws Exception {
            int keyCount = keyField.size();
            if (keyCount < 1) {
                return "1=0";
            }

            StringBuilder where = new StringBuilder();
            for (int i = 0; i < keyCount; ++i) {
                Field field = keyField.get(i);
                int index = projectionField.indexOf(field);
                if (i > 0) {
                    where.append(" AND ");
                }
                where.append(projection.get(index));
                where.append("=");
                Class type = projectionType.get(index);
                boolean accessible = field.isAccessible();
                if (!accessible) {
                    field.setAccessible(true);
                }
                if (type == boolean.class) {
                    where.append(field.getBoolean(entity));
                } else if (type == int.class) {
                    where.append(field.getInt(entity));
                } else if (type == long.class) {
                    where.append(field.getLong(entity));
                } else {
                    where.append("'");
                    where.append(field.get(entity));
                    where.append("'");
                }
                if (!accessible) {
                    field.setAccessible(false);
                }
            }
            return where.toString();
        }

        ContentValues getContentValues(Object entity) throws Exception {
            ContentValues values = new ContentValues();
            int count = projectionField.size();
            for (int i = 0; i < count; ++i) {
                Field field = projectionField.get(i);
                String col = projection.get(i);
                Class type = field.getType();
                if (type == boolean.class) {
                    values.put(col, field.getBoolean(entity));
                } else if (type == int.class) {
                    values.put(col, field.getInt(entity));
                } else if (type == long.class) {
                    values.put(col, field.getLong(entity));
                } else {
                    values.put(col, field.get(entity).toString());
                }
            }
            return values;
        }
    }

    public static <T> boolean insertEntity(Context ctx, Uri uri, T entity) {
        boolean flag = true;
        try {
            Statement statement = new Statement(entity.getClass());
            ctx.getContentResolver().insert(uri, statement.getContentValues(entity));
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, null, e);
            }
            flag = false;
        }
        return flag;
    }

    public static <T> boolean delEntity(Context ctx, Uri uri, T entity) {
        int count = 0;
        try {
            Statement statement = new Statement(entity.getClass());
            count = ctx.getContentResolver().delete(uri, statement.getEntityQuery(entity), null);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, null, e);
            }
        }
        return count > 0;
    }

    public static <T> boolean updateEntity(Context ctx, Uri uri, T entity) {
        int count = 0;
        try {
            Statement statement = new Statement(entity.getClass());
            count = ctx.getContentResolver().update(uri, statement.getContentValues(entity),
                    statement.getEntityQuery(entity), null);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, null, e);
            }
        }
        return count > 0;
    }

    public static <T> List<T> fetchEntities(Context ctx, Uri uri, String selection, Class<T> clazz) {
        return fetchEntities(ctx, uri, selection, null, null, clazz);
    }

    public static <T> List<T> fetchEntities(Context ctx, Uri uri, String selection,
                                            String[] selectionArgs, String order,
                                            Class<T> clazz) {
        ArrayList<T> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            Statement statement = new Statement(clazz);
            ArrayList<String> projection = statement.projection;
            ArrayList<Class> projectionType = statement.projectionType;
            ArrayList<Field> projectionField = statement.projectionField;
            String[] arr = projection.toArray(new String[projection.size()]);

            cursor = ctx.getContentResolver().query(uri, arr, selection, selectionArgs, order);
            if (cursor == null) {
                if (DEBUG) {
                    Log.d(TAG, "[DbHelper.fetchEntities] cursor is null");
                }
                return list;
            }

            int projectionCount = projection.size();
            int[] index = new int[projectionCount];
            for (int i = 0; i < projectionCount; ++i) {
                index[i] = cursor.getColumnIndex(projection.get(i));
            }
            int count = cursor.getCount();
            for (int i = 0; i < count; ++i) {
                cursor.moveToNext();
                T entity = clazz.newInstance();
                for (int j = 0; j < projectionCount; ++j) {
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
                    } else if (c == long.class) {
                        field.setLong(entity, cursor.getLong(index[j]));
                    } else {
                        field.set(entity, cursor.getString(index[j]));
                    }
                    if (!accessible) {
                        field.setAccessible(false);
                    }
                }
                list.add(entity);
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, null, e);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

}
