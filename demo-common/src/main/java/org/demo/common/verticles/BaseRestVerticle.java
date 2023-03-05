package org.demo.common.verticles;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.demo.common.Launcher;
import org.demo.common.annotation.RestVerticle;
import org.demo.common.redis.RedisApiClient;
import org.demo.common.redis.RedisKeyStore;
import org.demo.common.spring.SpringOperateConst;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class BaseRestVerticle extends AbstractVerticle {

    private static final Logger logger = LogManager.getLogger();

    private Router router;

    public BaseRestVerticle(Router router) {
        this.router = router;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start() throws Exception {

        Set<Class<?>> annotatedRestClass = null;
        //springFirst
        JsonObject deployOpts = config();
        String mainVerticle = null;
        if(deployOpts != null && !deployOpts.isEmpty()) {
            mainVerticle = config().getString(SpringOperateConst.PROP_MAIN_VERTICLE);

            if(mainVerticle != null && !"".equals(mainVerticle)) {
//                MainVerticleConfigData verticleConfig = Launcher.getMainVerticleConfigDataMap().get(mainVerticle);
//                annotatedRestClass = verticleConfig.getAnnotatedRestClass();
            }

        } else {
            annotatedRestClass = Launcher.annotatedRestClass;
        }

        StringBuffer strb = new StringBuffer();
        strb.append("\n############### "+this.getClass().getSimpleName()+".start ###############");
        strb.append("\n# mainVerticle : " + mainVerticle);
        strb.append("\n# Thread.currentThread.name : " + Thread.currentThread().getName());
        strb.append("\n# vertx hashCode : " + vertx.hashCode());
        strb.append("\n# vertx toString : " + vertx.toString());

        logger.debug("- [{}:tid:{}] mainVerticle : {}, annotatedRestClass size : {}", this.getClass().getSimpleName(), Thread.currentThread().getId(), mainVerticle, (annotatedRestClass != null ? annotatedRestClass.size() : 0));
        logger.debug(strb.toString());

        for (Class<?> clazz : annotatedRestClass) {
            try {
                Annotation[] annotations = clazz.getAnnotations();

                for (Annotation a : annotations) {
                    if (a.annotationType() == RestVerticle.class) {
                        RestVerticle verticleAnnotation = (RestVerticle) a;
                        String[] url = verticleAnnotation.url();
                        boolean isAuth = verticleAnnotation.isAuth();

                        logger.debug("*RestVerticle [{}] url : {}, isAuth : {}", clazz.getName(), url, isAuth);

                        Handler<RoutingContext> handler = (Handler<RoutingContext>) clazz.getConstructor().newInstance();

                        Method m = handler.getClass().getMethod("setIsAuth", boolean.class);
                        m.invoke(handler, isAuth);

                        Method m2 = handler.getClass().getMethod("setVertx", Vertx.class);

                        //springFirst code ( AbstractVerticle의 vertx는  deployVerticle 과정에서 세팅된다.)
                        m2.invoke(handler, vertx);

                        //magnachip code ( BaseRestVerticle만 Launcher에서 getVertx()하고있는데.. 이유가? )
                        //m2.invoke(handler, Launcher.getVertx());

                        for(String u : url) {
                            if(!u.isEmpty()) {
                                router.route(u).handler(handler);
                                RedisApiClient.LPUSH(0, RedisKeyStore.TASK_RESTAPI.getMaster(), new JsonObject().put("URL", u).toString());
                            }   
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
