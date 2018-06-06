package com.hydbest.iocandroid.util;

import android.app.Activity;
import android.view.View;

import com.hydbest.iocandroid.annotation.ContentView;
import com.hydbest.iocandroid.annotation.EventBase;
import com.hydbest.iocandroid.annotation.ViewInject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by csz on 2018/6/6.
 */

public class ViewInjectUtils {
    private static final String METHOD_SET_CONTENT_VIEW = "setContentView";
    private static final String METHOD_FIND_VIEW_BY_ID = "findViewById";

    public static void inject(Activity activity) {
        injectContentView(activity);
        injectViews(activity);
        injectEvents(activity);
    }

    /**
     * 往act注入界面布局
     *
     * @param activity
     */
    private static void injectContentView(Activity activity) {
        Class<? extends Activity> actClass = activity.getClass();
        ContentView contentView = actClass.getAnnotation(ContentView.class);
        if (contentView != null) {
            int layoutId = contentView.value();
            try {
                Method setContentViewM = actClass.getMethod(METHOD_SET_CONTENT_VIEW, int.class);
                setContentViewM.setAccessible(true);
                setContentViewM.invoke(activity, layoutId);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 往act注入子view
     *
     * @param activity
     */
    private static void injectViews(Activity activity) {
        Class<? extends Activity> actClass = activity.getClass();
        Field[] fields = actClass.getDeclaredFields();
        Field fieldView;
        for (int i = 0; fields != null && i < fields.length; i++) {
            fieldView = fields[i];
            ViewInject viewInject = fieldView.getAnnotation(ViewInject.class);
            if (viewInject != null) {
                int id = viewInject.value();
                try {
                    Method findViewbyIdM = actClass.getMethod(METHOD_FIND_VIEW_BY_ID, int.class);
                    findViewbyIdM.setAccessible(true);
                    View view = (View) findViewbyIdM.invoke(activity, id);
                    fieldView.setAccessible(true);
                    fieldView.set(activity, view);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 往act注入所有act
     *
     * @param activity
     */
    private static void injectEvents(Activity activity) {
        Class<? extends Activity> actClass = activity.getClass();
        Method[] methods = actClass.getDeclaredMethods();
        if (methods == null) {
            return;
        }
        for (Method method : methods) {
            method.setAccessible(true);
            Annotation[] annotations = method.getAnnotations();
            if (annotations != null) {
                //遍历方法上的注解
                for (Annotation annotation : annotations) {
                    //获取注解的类型 , 相当于对象getClass()
                    Class<? extends Annotation> type = annotation.annotationType();
                    EventBase eventBaseAnnotation = type.getAnnotation(EventBase.class);
                    if (eventBaseAnnotation != null) {
                        String listenerSetter = eventBaseAnnotation.listenerSetter();
                        Class<?> listenerType = eventBaseAnnotation.listenerType();
                        String methodName = eventBaseAnnotation.methodName();

                        try {
                            //获取@Onclick的value方法
                            Method clickM = type.getDeclaredMethod("value");
                            int[] ids = (int[]) clickM.invoke(annotation);

                            //通过InvocationHandler设置代理
                            DynamicHandler handler = new DynamicHandler(activity);
                            handler.addMethod(methodName, method);
                            Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class<?>[]{listenerType}, handler);
                            for (int viewId : ids) {
                                View view = activity.findViewById(viewId);
                                Method setEventListenerMethod = view.getClass().getMethod(listenerSetter, listenerType);
                                setEventListenerMethod.invoke(view, listener);
                            }
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
