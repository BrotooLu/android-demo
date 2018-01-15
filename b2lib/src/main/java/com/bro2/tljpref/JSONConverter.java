package com.bro2.tljpref;

/**
 * Created by Brotoo on 15/01/2018.
 */

public interface JSONConverter {

    <T> T fromJSON(String json, Class<T> clazz);

}
