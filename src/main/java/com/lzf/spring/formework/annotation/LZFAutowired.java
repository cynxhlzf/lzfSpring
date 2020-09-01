package com.lzf.spring.formework.annotation;

import java.lang.annotation.*;


/**
 * 自动注入
 * @author Tom
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LZFAutowired {
	String value() default "";
}
