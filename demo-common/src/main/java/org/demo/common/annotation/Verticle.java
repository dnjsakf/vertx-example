package org.demo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.vertx.core.AbstractVerticle;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Verticle {
    Class<? extends AbstractVerticle> value();
    String eventbus();
    int size();
}


