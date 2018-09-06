package com.hydbest.ioc_compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static java.lang.reflect.Modifier.PRIVATE;

/**
 * Created by csz on 2018/9/6.
 */

public class ClassValidator {

    static boolean isPrivate(Element annotatedClass)
    {
        return annotatedClass.getModifiers().contains(Modifier.PRIVATE);
    }

    static String getClassName(TypeElement type, String packageName)
    {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }
}
