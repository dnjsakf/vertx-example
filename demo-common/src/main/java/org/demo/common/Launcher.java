package org.demo.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.common.annotation.RestVerticle;
import org.demo.common.annotation.Subscribe;
import org.demo.common.redis.RedisApiClient;
import org.demo.common.spring.SpringContext;
import org.demo.common.verticles.BaseRestVerticle;
import org.demo.common.verticles.BaseSubVerticle;
import org.reflections.Reflections;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;

public class Launcher extends io.vertx.core.Launcher{

    private static final Logger logger = LogManager.getLogger();
        
    private static Vertx vertx;
    private static String HOST;
    private static final String ACTIVE_MODE;
    private static final String LOCAL;
    private Router ROUTER;

    public static Set<Class<?>> annotatedClass;
    public static Set<Class<?>> annotatedSubClass;
    public static Set<Class<?>> annotatedRestClass;
    
    static {
        HOST = "localhost";
        LOCAL = "local";
        ACTIVE_MODE = System.getProperty("spring.profiles.active", LOCAL);
    }

    public static void setHost(String _host) {
        HOST = _host;
    }
    public static String getHost() {
        return HOST;
    }

    public static void main(String[] args) {
        new Launcher().dispatch(args);
    }
    
    //springFirst Derivation
    public static String getSystemId() {
        UUID u = UUID.randomUUID();
        return GenUtil.toUnsignedString(u.getLeastSignificantBits(), 6).concat(GenUtil.toUnsignedString(u.getMostSignificantBits(), 6));
    }


    public static boolean isStartupSpringApplicationFirst() {
        return SpringOperateConst.BIZMICRO_RUN_EGOVFRAME_VAL.equalsIgnoreCase(System.getProperty(SpringOperateConst.BIZMICRO_RUN_EGOVFRAME_KEY));
    }

    @SuppressWarnings("resource")
    @Override
    public void beforeStartingVertx(VertxOptions options) {
        logger.info("############### beforeStartingVertx ###################\n[VertxOptions] : {}", options.toString());
        logger.debug("isStartupSpringApplicationFirst() : {}", isStartupSpringApplicationFirst());
        //springFirst
        if(isStartupSpringApplicationFirst()) {
            /**
             * core-spring-ext servlet 에서 호출할 경우 SpringContext 초기화를 스킵합니다.
             */
            logger.debug("-> Spring First - The Spring Framework has already been initialized.");
        }
        else {
            /**
             * spring framework integration
             */
            logger.debug("-> Vert.x First - Initialize the Spring Framework.");
            new AnnotationConfigApplicationContext(SpringContext.class);
        }
    }

    @Override
    public void afterStartingVertx(Vertx vertx_) {
        vertx = vertx_;
        ROUTER = Router.router(vertx);

        if(isStartupSpringApplicationFirst()) {
            // mainVerticleConfigDataMap.get(getMainVerticleType()).setVertx(vertx);
        }

        StringBuffer strb = new StringBuffer();
        strb.append("\n############### afterStartingVertx ###############");
        strb.append("\n>>> Thread.currentThread.name : " + Thread.currentThread().getName());
        strb.append("\n>>> vertx deploymentIDs : " + vertx.deploymentIDs());
        strb.append("\n>>> vertx toString : " + vertx.toString());
        strb.append("\n>>> vertx hashCode : " + vertx.hashCode());
        strb.append("\n>>> vertx sharedData : " + vertx.sharedData());
        strb.append("\n>>> ROUTER.route.getPath : " + ROUTER.route().getPath());
        strb.append("\n>>> ROUTER.options : " + ROUTER.options());

        logger.debug(strb.toString());
    }

    @Override
    public void afterConfigParsed(JsonObject config) {
        StringBuffer strb = new StringBuffer();
        strb.append("\n############### afterConfigParsed ###############\n");
        strb.append(config.encodePrettily());

        logger.debug(strb.toString());
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        logger.info("############### beforeDeployingVerticle ###################");

        if (deploymentOptions.getConfig() == null) {
            deploymentOptions.setConfig(new JsonObject());
        }

        deploymentOptions.setWorkerPoolSize(20);

        deploymentOptions.setWorker(true);
        deploymentOptions.setInstances(8);

        //springFirst
        logger.info("- [First option - deploymentOptions]\n{}", deploymentOptions.getConfig().encodePrettily());

        if(isStartupSpringApplicationFirst()) {
            /*
             * //springFirst
             * vert.x verticle deploy option config each service config
             */
            // this.initSpringFirst(deploymentOptions);
        }
        else {
            /*
             * vert.x verticle deploy option config each service config
             */
            this.initVertxFirst(deploymentOptions);
        }

        logger.info("############### [END] beforeDeployingVerticle ###################");

    }

    @Override
    public void beforeStoppingVertx(Vertx vertx) {
        StringBuffer strb = new StringBuffer();
        strb.append("\n############### beforeStoppingVertx ###############");
        strb.append("\n>>> beforeStopping vertx deploymentIDs : " + vertx.deploymentIDs());
        strb.append("\n>>> beforeStopping vertx toString : " + vertx.toString());
        strb.append("\n>>> beforeStopping vertx hashCode : " + vertx.hashCode());
        strb.append("\n>>> beforeStopping vertx sharedData : " + vertx.sharedData());
        logger.debug(strb.toString());
    }

    @Override
    public void afterStoppingVertx() {
        StringBuffer strb = new StringBuffer();
        strb.append("\n############### afterStoppingVertx ###############");
        strb.append("\n>>> Thread.currentThread.name : " + Thread.currentThread().getName());
        strb.append("\n>>> vertx deploymentIDs : " + vertx.deploymentIDs());
        strb.append("\n>>> vertx toString : " + vertx.toString());
        strb.append("\n>>> vertx hashCode : " + vertx.hashCode());
        strb.append("\n>>> vertx sharedData : " + vertx.sharedData());
        strb.append("\n>>> ROUTER.route.getPath : " + ROUTER.route().getPath());
        strb.append("\n>>> ROUTER.options : " + ROUTER.options());
        logger.debug(strb.toString());
    }

    /**
     * vertx cli 우선
     * @param deploymentOptions
     * @param activeMode
     */
    private void addDeploymentOptions(DeploymentOptions deploymentOptions) {

        String configCommon = "/conf/".concat(ACTIVE_MODE).concat("/config_common.json");
        String configService = "/conf/".concat(ACTIVE_MODE).concat("/config_service.json");

        InputStream iScommon = JsonParser.class.getResourceAsStream(configCommon);
        InputStream iSservice = JsonParser.class.getResourceAsStream(configService);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonMap = null;
        JsonNode jsonCommon = null;
        try {
            jsonMap = mapper.readTree(iSservice);
            jsonCommon = mapper.readTree(iScommon);
            deploymentOptions.getConfig().mergeIn(new JsonObject(jsonCommon.toString()));
            deploymentOptions.getConfig().mergeIn(new JsonObject(jsonMap.toString()));
        } catch (JsonProcessingException e1) {
            logger.error("CONFIG PARSE ERROR");
        } catch (IOException e1) {
            logger.error("CONFIG PARSE IO ERROR");
        } finally {
            if (iScommon != null) {
                try {
                    iScommon.close();
                } catch (IOException e) {
                    logger.error("COMMON CONFIG PARSE");
                }
            }
            if (iSservice != null) {
                try {
                    iSservice.close();
                } catch (IOException e) {
                    logger.error("SERVICE CONFIG PARSE");
                }
            }
        }
    }


    /**
     * vertx cli 우선
     * @param deploymentOptions
     */
    private void initVertxFirst(DeploymentOptions deploymentOptions) {

        this.addDeploymentOptions(deploymentOptions);

        if (deploymentOptions.getConfig().containsKey("instances"))
            deploymentOptions.setInstances(deploymentOptions.getConfig().getInteger("instances"));

        if (deploymentOptions.getConfig().containsKey("type")) {

            try {

                String systemId = getSystemId();
                Reflections reflections = new Reflections("biz.micro");

                switch (deploymentOptions.getConfig().getString("type")) {
                case "api":
                    new RedisApiClient(
                            deploymentOptions.getConfig().getString("redis.host"),
                            deploymentOptions.getConfig().getInteger("redis.port"),
                            systemId
                    );

                    ROUTER.route().handler(CookieHandler.create());
                    /*
                     * ROUTER SETTING create Body Handler
                     */
                    RestAPIVerticle.setRouter(ROUTER);
                    // ROUTER.route().handler(BodyHandler.create());
                    ROUTER.route().handler(BodyHandler.create().setUploadsDirectory(SpringContext.getEnv("temp.upload.path").trim()));

                    /*
                     * route path from RestVerticle annotated class.
                     */
                    annotatedRestClass = reflections.getTypesAnnotatedWith(RestVerticle.class);
                    vertx.deployVerticle(new BaseRestVerticle(ROUTER), new DeploymentOptions().setWorker(true).setMultiThreaded(true));
                    deploymentOptions.setInstances(10);
                    break;
                }

                annotatedSubClass = reflections.getTypesAnnotatedWith(Subscribe.class);

                vertx.deployVerticle(new BaseSubVerticle(), new DeploymentOptions().setWorker(true).setMultiThreaded(true));

                logger.info(deploymentOptions.getConfig().encodePrettily());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}


class SpringOperateConst {

    public static final String BIZMICRO_RUN_EGOVFRAME_KEY;
    public static final String BIZMICRO_RUN_EGOVFRAME_VAL;

    public static final String PROP_MAIN_VERTICLE;
    public static final String PROP_MAIN_PACKAGE;

    public static final String PROP_MAIN_ANNO_REST_CLASS;
    public static final String PROP_MAIN_ANNO_CLASS;
    public static final String PROP_MAIN_ANNO_SUB_CLASS;
    public static final String SYSTEM_ID;

    static {
        BIZMICRO_RUN_EGOVFRAME_KEY = "bizmicro.run.egovframe";
        BIZMICRO_RUN_EGOVFRAME_VAL = "true";

        PROP_MAIN_VERTICLE = "bizmicro.main.verticle";
        PROP_MAIN_PACKAGE = "bizmicro.main.package";

        PROP_MAIN_ANNO_REST_CLASS = "set.class.annotatedRestClass";
        PROP_MAIN_ANNO_CLASS = "set.class.annotatedClass";
        PROP_MAIN_ANNO_SUB_CLASS = "set.class.annotatedSubClass";

        SYSTEM_ID = "systemId";
    }
}


class GenUtil {
    
    public static String toUnsignedString(long i, int shift) {

        char[] buf = new char[64];
        int charPos = 64;
        int radix = 1 << shift;
        long mask = radix - 1;
        long number = i;
        do {
            buf[--charPos] = digits[(int) (number & mask)];
            number >>>= shift;
        } while (number != 0);

        return new String(buf, charPos, (64 - charPos));
    }

    final static char[] digits = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', '_', '.'
    };
}
