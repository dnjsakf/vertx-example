package org.demo.common.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan("org.demo.common")
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    private static Environment env;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        context = ac;
        env = context.getBean(Environment.class);
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static <T> Object getBean(Class<T> arg) {
        return context.getBean(arg);
    }

    public static String getEnv(String key) {
        if (env.containsProperty(key)) {
            return env.getProperty(key);
        } else {
            return "";
        }
    }
}

