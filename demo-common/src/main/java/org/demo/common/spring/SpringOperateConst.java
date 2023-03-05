package org.demo.common.spring;

public class SpringOperateConst {


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
