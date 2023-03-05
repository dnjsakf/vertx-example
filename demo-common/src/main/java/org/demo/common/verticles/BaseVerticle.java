package org.demo.common.verticles;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.demo.common.Launcher;
import org.demo.common.annotation.Verticle;
import org.demo.common.spring.SpringOperateConst;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class BaseVerticle extends AbstractVerticle {

    private static final Logger logger = LogManager.getLogger();

    @SuppressWarnings("unchecked")
    @Override
    public void start() throws Exception {

        Set<Class<?>> annotatedClass = null;
        //springFirst
        JsonObject deployOpts = config();
        String mainVerticle = null;
        if(deployOpts != null && !deployOpts.isEmpty()) {

            mainVerticle = config().getString(SpringOperateConst.PROP_MAIN_VERTICLE);
            if(mainVerticle != null && !"".equals(mainVerticle)) {
//                MainVerticleConfigData verticleConfig = Launcher.getMainVerticleConfigDataMap().get(mainVerticle);
//                annotatedClass = verticleConfig.getAnnotatedClass();
            }
        }
        else {
            annotatedClass = Launcher.annotatedClass;
        }

        StringBuffer strb = new StringBuffer();
        strb.append("\n############### "+this.getClass().getSimpleName()+".start ###############");
        strb.append("\n# mainVerticle : " + mainVerticle);
        strb.append("\n# Thread.currentThread.name : " + Thread.currentThread().getName());
        strb.append("\n# vertx hashCode : " + vertx.hashCode());
        strb.append("\n# vertx toString : " + vertx.toString());

        logger.debug("- [{}:tid:{}] mainVerticle : {}, annotatedClass size : {}", this.getClass().getSimpleName(), Thread.currentThread().getId(), mainVerticle, (annotatedClass != null ? annotatedClass.size() : 0));
        logger.debug(strb.toString());

        for (Class<?> clazz : annotatedClass) {
            try {
                Annotation[] annotations = clazz.getAnnotations();
                for(Annotation a : annotations) {
                    if ( a.annotationType() == Verticle.class ) {
                        Verticle verticleAnnotation = (Verticle)a;

                        //logger.debug(verticleAnnotation.value() + ":::" + this.getClass());
                        logger.debug("*Verticle [{}] value : {}, eventbus : {}, size : {}", clazz.getName(), verticleAnnotation.value(), verticleAnnotation.eventbus(), verticleAnnotation.size());

                        if ( verticleAnnotation.value() == this.getClass() ) {
                            for(int i = 0; i < verticleAnnotation.size() ; i ++)
                            vertx.eventBus().consumer( verticleAnnotation.eventbus())
                                .handler((Handler<Message<Object>>) clazz.getConstructor(Vertx.class).newInstance(vertx));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
