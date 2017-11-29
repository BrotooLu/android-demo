package com.bro2.crm;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.bro2.b2lib.B2LibEnv.DEBUG;
import static com.bro2.b2lib.B2LibEnv.TAG_PREFIX;

/**
 * Created by Bro2 on 2017/6/10
 *
 */

public class CRM {
    private static class Statement {
        final ArrayList<String> projection = new ArrayList<>();
        final ArrayList<Class> projectionType = new ArrayList<>();
        final ArrayList<Field> projectionField = new ArrayList<>();
        final ArrayList<Field> keyField = new ArrayList<>();
        final Uri uri;

        Statement(Class<?> clazz) {
            Table table = clazz.getAnnotation(Table.class);
            if (table == null) {
                throw new CRMException("not a crm type");
            }

            String authority = table.authority();
            if (TextUtils.isEmpty(authority)) {
                throw new CRMException("illegal authority: " + authority);
            }

            uri = Uri.parse("content://" + authority + "/" + table.path());

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

            if (projection.size() < 1) {
                throw new CRMException("no column specified");
            }
        }

        String getEntityQuery(Object entity) {
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
                try {
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
                } catch (IllegalAccessException e) {
                    throw new CRMException(e);
                }
                if (!accessible) {
                    field.setAccessible(false);
                }
            }
            return where.toString();
        }

        ContentValues getContentValues(Object entity) {
            ContentValues values = new ContentValues();
            int count = projectionField.size();
            for (int i = 0; i < count; ++i) {
                Field field = projectionField.get(i);
                String col = projection.get(i);
                Class type = field.getType();
                boolean accessible = field.isAccessible();
                if (!accessible) {
                    field.setAccessible(true);
                }
                try {
                    if (type == boolean.class) {
                        values.put(col, field.getBoolean(entity));
                    } else if (type == int.class) {
                        values.put(col, field.getInt(entity));
                    } else if (type == long.class) {
                        values.put(col, field.getLong(entity));
                    } else {
                        values.put(col, field.get(entity).toString());
                    }
                } catch (IllegalAccessException e) {
                    throw new CRMException(e);
                }
                if (!accessible) {
                    field.setAccessible(false);
                }
            }
            return values;
        }
    }

    public static <T> boolean insertEntity(Context ctx, T entity, boolean updateIfExists) {
        if (ctx == null || entity == null) {
            throw new CRMException("null parameter");
        }

        Statement statement = new Statement(entity.getClass());
        if (!updateIfExists) {
            ctx.getContentResolver().insert(statement.uri, statement.getContentValues(entity));
            return true;
        }

        List<?> arr = fetchEntities(ctx, statement.getEntityQuery(entity), entity.getClass());
        if (arr.size() == 1) {
            if (entity.equals(arr.get(0))) {
                return true;
            } else {
                int count = updateEntityInner(
                        ctx.getContentResolver(), statement.uri, statement, entity);
                return count > 0;
            }
        }

        ctx.getContentResolver().insert(statement.uri, statement.getContentValues(entity));
        return true;
    }

    public static <T> boolean delEntity(Context ctx, T entity) {
        if (ctx == null || entity == null) {
            throw new CRMException("null parameters");
        }

        Statement statement = new Statement(entity.getClass());
        int count = ctx.getContentResolver()
                .delete(statement.uri, statement.getEntityQuery(entity), null);
        return count > 0;
    }

    private static <T> int updateEntityInner(ContentResolver resolver, Uri uri,
                                             Statement statement, T entity) {
        return resolver.update(uri, statement.getContentValues(entity),
                statement.getEntityQuery(entity), null);
    }

    public static <T> boolean updateEntity(Context ctx, T entity) {
        if (ctx == null || entity == null) {
            throw new CRMException("null parameter");
        }

        Statement statement = new Statement(entity.getClass());
        int count = updateEntityInner(ctx.getContentResolver(), statement.uri, statement, entity);
        return count > 0;
    }

    public static <T> List<T> fetchEntities(Context ctx, String selection, Class<T> clazz) {
        return fetchEntities(ctx, selection, null, null, clazz);
    }

    public static <T> List<T> fetchEntities(Context ctx, String selection,
                                            String[] selectionArgs, String order,
                                            Class<T> clazz) {
        if (ctx == null || clazz == null) {
            throw new CRMException("null parameter");
        }

        Statement statement = new Statement(clazz);
        ArrayList<T> list = new ArrayList<>();
        ArrayList<String> projection = statement.projection;
        ArrayList<Class> projectionType = statement.projectionType;
        ArrayList<Field> projectionField = statement.projectionField;
        String[] arr = projection.toArray(new String[projection.size()]);

        Cursor cursor = ctx.getContentResolver()
                .query(statement.uri, arr, selection, selectionArgs, order);
        if (cursor == null) {
            if (DEBUG) {
                Log.d(TAG_PREFIX, "[CRM.fetchEntities] cursor is null");
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
            T entity;
            try {
                entity = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new CRMException(e);
            }

            for (int j = 0; j < projectionCount; ++j) {
                Class c = projectionType.get(j);
                Field field = projectionField.get(j);
                boolean accessible = field.isAccessible();
                if (!accessible) {
                    field.setAccessible(true);
                }
                try {
                    if (c == boolean.class) {
                        field.setBoolean(entity, cursor.getInt(index[j]) == 0);
                    } else if (c == int.class) {
                        field.setInt(entity, cursor.getInt(index[j]));
                    } else if (c == long.class) {
                        field.setLong(entity, cursor.getLong(index[j]));
                    } else {
                        field.set(entity, cursor.getString(index[j]));
                    }
                } catch (IllegalAccessException e) {
                    throw new CRMException(e);
                }
                if (!accessible) {
                    field.setAccessible(false);
                }
            }
            list.add(entity);
        }
        cursor.close();
        return list;
    }

    public <T> Pair<Boolean, T> fillEntity(Context ctx, T entity) {
        if (ctx == null || entity == null) {
            throw new CRMException("null parameter");
        }

        Class clazz = entity.getClass();
        Statement statement = new Statement(clazz);
        List<?> entities = fetchEntities(ctx, statement.getEntityQuery(entity), clazz);
        if (entities.size() == 1) {
            return new Pair<>(true, (T) entities.get(0));
        }

        if (DEBUG) {
            Log.d(TAG_PREFIX, "[CRM.fillEntity] no unique entity found: " + entities.size());
        }
        return new Pair<>(false, entity);
    }

}
