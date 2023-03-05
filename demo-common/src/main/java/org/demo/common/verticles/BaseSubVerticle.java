package org.demo.common.verticles;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.demo.common.Launcher;
import org.demo.common.annotation.Subscribe;
import org.demo.common.spring.SpringOperateConst;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class BaseSubVerticle extends AbstractVerticle {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void start() throws Exception {

        Set<Class<?>> annotatedSubClass = null;
        //springFirst
        JsonObject deployOpts = config();
        String mainVerticle = null;
        if(deployOpts != null && !deployOpts.isEmpty()) {

            mainVerticle = config().getString(SpringOperateConst.PROP_MAIN_VERTICLE);
            if(mainVerticle != null && !"".equals(mainVerticle)) {
//                MainVerticleConfigData verticleConfig = Launcher.getMainVerticleConfigDataMap().get(mainVerticle);
//                annotatedSubClass = verticleConfig.getAnnotatedSubClass();
            }
        }
        else {
            annotatedSubClass = Launcher.annotatedSubClass;
        }

        StringBuffer strb = new StringBuffer();
        strb.append("\n############### "+this.getClass().getSimpleName()+".start ###############");
        strb.append("\n# mainVerticle : " + mainVerticle);
        strb.append("\n# Thread.currentThread.name : " + Thread.currentThread().getName());
        strb.append("\n# vertx hashCode : " + vertx.hashCode());
        strb.append("\n# vertx toString : " + vertx.toString());

        logger.debug("- [{}:tid:{}] mainVerticle : {}, annotatedSubClass size : {}", this.getClass().getSimpleName(), Thread.currentThread().getId(), mainVerticle, (annotatedSubClass != null ? annotatedSubClass.size() : 0));
        logger.debug(strb.toString());

        for (Class<?> clazz : annotatedSubClass) {
            try {
                Annotation[] annotations = clazz.getAnnotations();
                for(Annotation a : annotations) {
                    if ( a.annotationType() == Subscribe.class ) {
                        Subscribe verticleAnnotation = (Subscribe) a;

                        //logger.debug(verticleAnnotation.value() + ":::" + a.getClass());
                        logger.debug("*Subscribe [{}] value : {}, size : {}", clazz.getName(), verticleAnnotation.value(), verticleAnnotation.size());

                        for(int i = 0; i < verticleAnnotation.size() ; i ++){
                            // Lambda Runnable
                            String name = "redis-broker-"+i;
                            Runnable task = () -> {
                                try {
                                    Thread.currentThread().setName(name);
                                    clazz.getConstructor(Vertx.class).newInstance(vertx);
                                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                                    e.printStackTrace();
                                }
                            };

                            Thread thread = new Thread(task);
                            thread.start();

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}

