package com.bro2.b2lib.util;

import android.text.TextUtils;
import android.util.Log;

import com.bro2.b2lib.B2Exception;

import java.lang.reflect.Field;

import static com.bro2.b2lib.B2LibEnv.DEBUG;
import static com.bro2.b2lib.B2LibEnv.TAG_PREFIX;

/**
 * Created by Bro2 on 2017/7/12
 *
 */

public class ReflectUtil {

    public static Field getClassField(Class clazz, String field) {
        if (clazz == null || TextUtils.isEmpty(field)) {
            throw new B2Exception("neither class nor field can be null");
        }

        Field f = null;
        try {
            f = clazz.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            if (DEBUG) {
                Log.e(TAG_PREFIX, "[ReflectUtil.getClassField]", e);
            }
        }

        if (f != null) {
            return f;
        }

        if (clazz == Object.class) {
            return null;
        } else {
            return getClassField(clazz.getSuperclass(), field);
        }
    }

    private static boolean replaceField(Object obj, Class clazz, String field, Object val) {
        Class c = clazz == null ? obj.getClass() : clazz;
        Field f = getClassField(c, field);

        if (f == null) {
            return false;
        }

        boolean accessible = f.isAccessible();
        Class fieldClazz = f.getType();

        if (!accessible) {
            f.setAccessible(true);
        }

        try {
            if (fieldClazz == boolean.class) {
                boolean v;
                if (val instanceof Boolean) {
                    v = (Boolean) val;
                } else {
                    v = Boolean.parseBoolean(String.valueOf(val));
                }
                f.setBoolean(obj, v);
            } else if (fieldClazz == byte.class) {
                byte v;
                if (val instanceof Number) {
                    v = ((Number) val).byteValue();
                } else {
                    v = Byte.parseByte(String.valueOf(val));
                }
                f.setByte(obj, v);
            } else if (fieldClazz == char.class) {
                char v;
                if (val instanceof Character) {
                    v = (Character) val;
                } else if (val instanceof Integer) {
                    v = Character.toChars((int) val)[0];
                } else {
                    v = String.valueOf(val).charAt(0);
                }
                f.setChar(obj, v);
            } else if (fieldClazz == short.class) {
                short v;
                if (val instanceof Number) {
                    v = ((Number) val).shortValue();
                } else {
                    v = Short.parseShort(String.valueOf(val));
                }
                f.setShort(obj, v);
            } else if (fieldClazz == int.class) {
                int v;
                if (val instanceof Number) {
                    v = ((Number) val).intValue();
                } else {
                    v = Integer.parseInt(String.valueOf(val));
                }
                f.setInt(obj, v);
            } else if (fieldClazz == long.class) {
                long v;
                if (val instanceof Number) {
                    v = ((Number) val).longValue();
                } else {
                    v = Long.parseLong(String.valueOf(val));
                }
                f.setLong(obj, v);
            } else if (fieldClazz == float.class) {
                float v;
                if (val instanceof Number) {
                    v = ((Number) val).floatValue();
                } else {
                    v = Float.parseFloat(String.valueOf(val));
                }
                f.setFloat(obj, v);
            } else if (fieldClazz == double.class) {
                double v;
                if (val instanceof Number) {
                    v = ((Number) val).doubleValue();
                } else {
                    v = Double.parseDouble(String.valueOf(val));
                }
                f.setDouble(obj, v);
            } else {
                f.set(obj, val);
            }
            return true;
        } catch (IllegalAccessException | NumberFormatException e) {
            if (DEBUG) {
                Log.e(TAG_PREFIX, "[ReflectUtil.replaceField]", e);
            }
        } finally {
            if (!accessible) {
                f.setAccessible(false);
            }
        }

        return false;
    }

    public static boolean replaceStaticField(Class clazz, String field, Object val) {
        if (clazz == null || TextUtils.isEmpty(field)) {
            throw new B2Exception("illegal args, class: " + clazz + " field: " + field);
        }

        return replaceField(null, clazz, field, val);
    }

    public static Class getClassOrNull(String name) {
        if (TextUtils.isEmpty(name)) {
            throw new B2Exception("class name is empty");
        }

        Class c = null;
        boolean flag = true;

        try {
            c = Class.forName(name);
        } catch (ClassNotFoundException e) {
            flag = false;
            if (DEBUG) {
                Log.e(TAG_PREFIX, "[ReflectUtil.getClassOrNull]", e);
            }
        }

        return flag ? c : null;
    }

    public static boolean replaceStaticField(String clazz, String field, Object val) {
        if (TextUtils.isEmpty(clazz) || TextUtils.isEmpty(field)) {
            throw new B2Exception("illegal args, class: " + clazz + " field: " + field);
        }

        Class c = getClassOrNull(clazz);
        return c != null && replaceField(null, c, field, val);
    }

    public static boolean replaceField(Object obj, String field, Object val) {
        if (obj == null || TextUtils.isEmpty(field)) {
            throw new B2Exception("illegal args, obj: " + obj + " field: " + field);
        }

        return replaceField(obj, null, field, val);
    }

    private static <T> T getField(Object obj, Class clazz, String field) {
        Class c = clazz == null ? obj.getClass() : clazz;
        Field f = getClassField(c, field);

        if (f == null) {
            return null;
        }

        boolean accessible = f.isAccessible();

        if (!accessible) {
            f.setAccessible(true);
        }

        try {
            return (T)(f.get(obj));
        } catch (IllegalAccessException e) {
            if (DEBUG) {
                Log.e(TAG_PREFIX, "[ReflectUtil.getField]", e);
            }
        } finally {
            if (!accessible) {
                f.setAccessible(false);
            }
        }

        return null;
    }

    public static <T> T getField(Object obj, String field) {
        if (obj == null || TextUtils.isEmpty(field)) {
            throw new B2Exception("illegal args, obj: " + obj + " field: " + field);
        }

        return getField(obj, null, field);
    }

    public static Object getStaticField(Class clazz, String field) {
        if (clazz == null || TextUtils.isEmpty(field)) {
            throw new B2Exception("illegal args, clazz: " + clazz + " field: " + field);
        }

        return getField(null, clazz, field);
    }

    public static <T> T getStaticField(String clazz, String field) {
        if (TextUtils.isEmpty(clazz) || TextUtils.isEmpty(field)) {
            throw new B2Exception("illegal args, class: " + clazz + " field: " + field);
        }

        Class c = getClassOrNull(clazz);
        return c == null ? null : (T)getField(null, c, field);
    }
}
