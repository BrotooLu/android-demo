package com.bro2.b2lib.util;

import android.text.TextUtils;
import android.util.Log;

import com.bro2.b2lib.B2Exception;

import java.lang.reflect.Field;

import static com.bro2.b2lib.B2LibEnv.DEBUG;
import static com.bro2.b2lib.B2LibEnv.TAG;

/**
 * Created on 2017/7/12.
 * <p>
 * 调用replace方法的时候如果field类型为char，而调用者使用自动装箱会将数字转换为Integer类型，导致出现
 * 当replace传递的值1会被转化为'1'
 *
 * @author Bro2
 * @version 1.0
 */

public class ReflectUtil {

    private static boolean replaceField(Object obj, Class clazz, String field, Object val) {
        boolean flag = true;
        Class c = clazz == null ? obj.getClass() : clazz;
        Field f = null;
        try {
            f = c.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            flag = false;
            if (DEBUG) {
                Log.e(TAG, "[ReflectUtil.replaceField]", e);
            }
        }

        if (!flag) {
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
        } catch (IllegalAccessException | NumberFormatException e) {
            flag = false;
            if (DEBUG) {
                Log.e(TAG, "[ReflectUtil.replaceField]", e);
            }
        } finally {
            if (!accessible) {
                f.setAccessible(false);
            }
        }

        return flag;
    }

    public static boolean replaceStaticField(Class clazz, String field, Object val) {
        if (clazz == null || TextUtils.isEmpty(field)) {
            new B2Exception("illegal args, class: " + clazz + " field: " + field);
        }

        return replaceField(null, clazz, field, val);
    }

    private static Class getClassOrNull(String name) {
        Class c = null;
        boolean flag = true;

        try {
            c = Class.forName(name);
        } catch (ClassNotFoundException e) {
            flag = false;
            if (DEBUG) {
                Log.e(TAG, "[ReflectUtil.getClassOrNull]", e);
            }
        }

        return flag ? c : null;
    }

    public static boolean replaceStaticField(String clazz, String field, Object val) {
        if (TextUtils.isEmpty(clazz) || TextUtils.isEmpty(field)) {
            new B2Exception("illegal args, class: " + clazz + " field: " + field);
        }

        Class c = getClassOrNull(clazz);
        return c != null && replaceField(null, c, field, val);
    }

    public static boolean replaceField(Object obj, String field, Object val) {
        if (obj == null || TextUtils.isEmpty(field)) {
            new B2Exception("illegal args, obj: " + obj + " field: " + field);
        }

        return replaceField(obj, null, field, val);
    }

    private static Object getField(Object obj, Class clazz, String field) {
        boolean flag = true;
        Class c = clazz == null ? obj.getClass() : clazz;
        Field f = null;
        try {
            f = c.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            flag = false;
            if (DEBUG) {
                Log.e(TAG, "[ReflectUtil.getField]", e);
            }
        }

        if (!flag) {
            return null;
        }

        boolean accessible = f.isAccessible();

        if (!accessible) {
            f.setAccessible(true);
        }

        Object ret = null;

        try {
            ret = f.get(obj);
        } catch (IllegalAccessException e) {
            if (DEBUG) {
                Log.e(TAG, "[ReflectUtil.getField]", e);
            }
        } finally {
            if (!accessible) {
                f.setAccessible(false);
            }
        }

        return ret;
    }

    public static Object getField(Object obj, String field) {
        if (obj == null || TextUtils.isEmpty(field)) {
            new B2Exception("illegal args, obj: " + obj + " field: " + field);
        }

        return getField(obj, null, field);
    }

    public static Object getStaticField(Class clazz, String field) {
        if (clazz == null || TextUtils.isEmpty(field)) {
            new B2Exception("illegal args, clazz: " + clazz + " field: " + field);
        }

        return getField(null, clazz, field);
    }

    public static Object getStaticField(String clazz, String field) {
        if (TextUtils.isEmpty(clazz) || TextUtils.isEmpty(field)) {
            new B2Exception("illegal args, class: " + clazz + " field: " + field);
        }

        Class c = getClassOrNull(clazz);
        return c == null ? null : getField(null, c, field);
    }
}
