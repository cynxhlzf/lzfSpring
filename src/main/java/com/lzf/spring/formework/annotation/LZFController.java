package com.lzf.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 页面交互
 * @author Tom
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LZFController {
	String value() default "";
}
