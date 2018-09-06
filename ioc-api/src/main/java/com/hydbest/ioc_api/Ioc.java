package com.hydbest.ioc_api;

import android.app.Activity;

/**
 * Created by csz on 2018/9/6.
 */

public class Ioc {
    public static void inject(Activity activity) {
        inject(activity, activity);
    }

    public static void inject(Object host, Object root) {
        ViewInjector viewInjector = null;
        try {
            Class<?> clazz = host.getClass();
            String proxyClassFullName = clazz.getName() + "$$ViewInjector";
            //省略try,catch相关代码
            Class<?> proxyClazz = Class.forName(proxyClassFullName);
            viewInjector = (com.csz.ioc.ViewInjector) proxyClazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        viewInjector.inject(host, root);
    }

    public interface ViewInjector<T> {
        void inject(T t, Object object);
    }
}

