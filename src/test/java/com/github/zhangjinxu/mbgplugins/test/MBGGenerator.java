package com.github.zhangjinxu.mbgplugins.test;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MBGGenerator {

    public static void main(String[] args) throws Exception {
        List<String> warnings = new ArrayList<>(0);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        InputStream configStream = MBGGenerator.class.getClassLoader().getResourceAsStream("mybatis-generator-default.xml");

        Configuration config = cp.parseConfiguration(configStream);
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);

        System.out.println("代码生成完毕>>>>>>>>>>>>");
    }
}
