package com.github.zhangjinxu.mbgplugins.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.MergeConstants;

import java.util.List;

/**
 * 会在实体类生成一个方法，将model中不为空的字段转换为对应Example类中的条件
 */
public class ModelToExamplePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        generateModelToExampleMethod(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        generateModelToExampleMethod(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateModelToExampleMethod(topLevelClass, introspectedTable);
        return true;
    }

    private boolean generateModelToExampleMethod(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {


        List<Field> fields = topLevelClass.getFields();
        if (fields == null || fields.isEmpty()) {
            return true;
        }

        Method method = new Method();
        method.setName("toExample");
        method.setConstructor(false);
        method.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType topLevelClassType = topLevelClass.getType();
        FullyQualifiedJavaType modelExampleType = new FullyQualifiedJavaType(topLevelClassType.getFullyQualifiedName()+"Example");
        method.setReturnType(modelExampleType);

        addBodyLine(method,topLevelClass,modelExampleType);
        addJavaDocLine(method,topLevelClass);

        topLevelClass.addMethod(method);
        return true;
    }
    private void addBodyLine(Method method, TopLevelClass topLevelClass,FullyQualifiedJavaType modelExampleType) {

//        ProjectExample example = new ProjectExample();
//        ProjectExample.Criteria criteria = example.createCriteria();
//        if (id != null) {
//            criteria.andIdEqualTo(id);
//        }

        List<Field> fields = topLevelClass.getFields();
        List<String> bodyLines = method.getBodyLines();
        StringBuilder sb = new StringBuilder();

        sb.append(modelExampleType.getShortName())
            .append(" example = new ")
            .append(modelExampleType.getShortName())
            .append("();");
        bodyLines.add(sb.toString());
        sb.setLength(0);


        sb.append(modelExampleType.getShortName())
                .append(".Criteria")
                .append(" criteria = example.createCriteria();");
        bodyLines.add(sb.toString());
        sb.setLength(0);

        for (Field field : fields) {
            String fieldName = field.getName();
            if ("serialVersionUID".equals(fieldName)) {
                continue;
            }

            sb.append("if (").append(fieldName).append(" != null) {");
            bodyLines.add(sb.toString());
            sb.setLength(0);

            String tail = fieldName.substring(1);
            String head = fieldName.substring(0, 1).toUpperCase();
            sb.append("criteria.and").append(head).append(tail).append("EqualTo(").append(fieldName).append(");");
            bodyLines.add(sb.toString());
            sb.setLength(0);

            sb.append("}");
            bodyLines.add(sb.toString());
            sb.setLength(0);
        }

        bodyLines.add("return example;");
    }


    private void addJavaDocLine(Method method, TopLevelClass topLevelClass) {
        List<String> javaDocLines = method.getJavaDocLines();
        javaDocLines.add("/** ");
        javaDocLines.add("* " + "将model类中的非null字段转化为Example中的条件");
        javaDocLines.add("* " + MergeConstants.NEW_ELEMENT_TAG);
        javaDocLines.add("*/");
    }


}
