package com.github.zhangjinxu.mbgplugins.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * 在实体类上添加lombok的@Data注解
 */
public class LombokDataPlugin extends PluginAdapter {

    // private List<String> lombokAnnotations;

    // private final String propertySeparator = ",";

    // @Override
    // public void setProperties(Properties properties) {
    //     String annotationsString = properties.getProperty("lombokAnnotations");
    //     if (StringUtils.isBlank(annotationsString)) {
    //         return;
    //     }
    //     String[] annotations = annotationsString.split(propertySeparator);
    //     lombokAnnotations = Arrays.asList(annotations);
    // }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        addAnnotation(topLevelClass);
        return true;
    }



    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        addAnnotation(topLevelClass);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addAnnotation(topLevelClass);
        return true;
    }



    @Override
    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              Plugin.ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              Plugin.ModelClassType modelClassType) {
        return false;
    }

    private void addAnnotation(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addAnnotation("@Data");
    }

    // private void addAnnotation(TopLevelClass topLevelClass) {
    //     if (lombokAnnotations == null || lombokAnnotations.isEmpty()) {
    //         return;
    //     }
    //     for (String annotation : lombokAnnotations) {
    //         topLevelClass.addImportedType(annotation);
    //         topLevelClass.addAnnotation(getAnnotationShortName(annotation));
    //     }
    // }

    // private String getAnnotationShortName(String annotation) {
    //     if (StringUtils.isBlank(annotation)) {
    //         return "";
    //     }
    //     annotation = annotation.substring(annotation.indexOf(".") + 1);
    //     if (annotation.length() < 2) {
    //         return annotation.toUpperCase();
    //     }
    //     char firstChar = annotation.charAt(0);
    //     char upperCaseFirstChar = Character.toUpperCase(firstChar);
    //     return upperCaseFirstChar + annotation.substring(1);
    // }


}
