package com.github.zhangjinxu.mbgplugins.plugin;

import com.github.zhangjinxu.mbgplugins.model.NotMarkContentHolder;
import com.github.zhangjinxu.mbgplugins.util.Util;
import org.apache.commons.io.IOUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * 保护自定义的内容不会被mbg自动生成覆盖
 */
public class OverridePlugin extends PluginAdapter {


    private String javaModelTargetPackage;

    private String javaModelTargetProject;

    private String mapperTargetPackage;

    private String mapperTargetProject;

    private boolean backup;

    private String backupDirectory;

    private String javaModelName;

    private NotMarkContentHolder mapperNotMarkContentHolder;

    private NotMarkContentHolder modelNotMarkContentHolder;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        this.javaModelTargetPackage = super.context.getJavaModelGeneratorConfiguration().getTargetPackage();
        this.javaModelTargetPackage = this.javaModelTargetPackage.replace(".", File.separator);

        this.javaModelTargetProject = super.context.getJavaModelGeneratorConfiguration().getTargetProject();


        this.mapperTargetPackage = super.context.getJavaClientGeneratorConfiguration().getTargetPackage();
        this.mapperTargetPackage = this.mapperTargetPackage.replace(".", File.separator);

        this.mapperTargetProject = super.context.getJavaClientGeneratorConfiguration().getTargetProject();

        this.backup = Boolean.valueOf(super.properties.getProperty("backup", "true"));
        if (backup) {
            String backupDirectory = super.properties.getProperty("backupDirectory", "");
            if ("".equals(backupDirectory)) {
                throw new RuntimeException("指定备份目录不能为空");
            }
            if (!backupDirectory.endsWith(File.separator)) {
                backupDirectory += File.separator;
            }
            File file = new File(backupDirectory);
            if (!file.exists() || !file.isDirectory()) {
                throw new RuntimeException("制定备份目录不存在");
            }

            this.backupDirectory = backupDirectory;
        }

    }

    @Override
    public void initialized(IntrospectedTable table) {
        this.javaModelName = Util.getJavaModelName(table.getAliasedFullyQualifiedTableNameAtRuntime());
        backupFiles();
    }


    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        mapperNotMarkContentHolder.addNotMarkContent(interfaze);
        return true;
    }


    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        modelNotMarkContentHolder.addNotMarkContent(topLevelClass);
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        modelNotMarkContentHolder.addNotMarkContent(topLevelClass);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        modelNotMarkContentHolder.addNotMarkContent(topLevelClass);
        return true;
    }

    private void backupFiles() {

        mapperNotMarkContentHolder = new NotMarkContentHolder(getMapperFilePath());
        modelNotMarkContentHolder = new NotMarkContentHolder(getModelFilePath());

        if (mapperNotMarkContentHolder.exists() && mapperNotMarkContentHolder.isFile() && backup) {
            doBackupFiles(mapperNotMarkContentHolder.getJavaFile());
        }

        if (modelNotMarkContentHolder.exists() && modelNotMarkContentHolder.isFile() && backup) {
            doBackupFiles(modelNotMarkContentHolder.getJavaFile());
        }

    }

    private void doBackupFiles(File file) {
        try {
            copyFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private void copyFile(File file) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(file);
//            String backupFilePath = getBackupFilePath(file.getName());
            String backupFilePath = getBackupFilePath(file.getPath());
            File backupFile = new File(backupFilePath);
            if (!backupFile.exists() && backupFile.canWrite()) {
                backupFile.createNewFile();
            }
            if (!backupFile.exists() && !backupFile.canWrite()) {
                throw new IOException("文件不存在且不可写:"+backupFile.getAbsolutePath());
            }
                out = new FileOutputStream(backupFile);
                IOUtils.copy(in, out);
            } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private String getBackupFilePath(String filePath) {
        StringBuilder sb = new StringBuilder();
        sb.append(backupDirectory).append(filePath);
        return sb.toString();
    }



    private String getMapperFilePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(mapperTargetProject);
        if (!mapperTargetProject.endsWith(File.separator)) {
            sb.append(File.separator);
        }
        sb.append(mapperTargetPackage);
        if (!mapperTargetPackage.endsWith(File.separator)) {
            sb.append(File.separator);
        }

        sb.append(javaModelName).append("Mapper").append(".java");

        return sb.toString();
    }

    private String getModelFilePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(javaModelTargetProject);
        if (!javaModelTargetProject.endsWith(File.separator)) {
            sb.append(File.separator);
        }
        sb.append(javaModelTargetPackage);
        if (!javaModelTargetPackage.endsWith(File.separator)) {
            sb.append(File.separator);
        }

        sb.append(javaModelName).append(".java");

        return sb.toString();
    }

}
