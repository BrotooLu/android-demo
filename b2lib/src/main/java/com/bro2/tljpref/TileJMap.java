package com.bro2.tljpref;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Brotoo on 15/01/2018.
 */

@Target(ElementType.TYPE.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TileJMap {

    boolean isLeaf() default true;

    String tileName() default "";

}
