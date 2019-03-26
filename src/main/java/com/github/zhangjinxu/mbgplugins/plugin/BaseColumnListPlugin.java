package com.github.zhangjinxu.mbgplugins.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.*;

import java.util.List;

public class BaseColumnListPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable table) {
        List<IntrospectedColumn> columns = table.getAllColumns();
        if (columns.isEmpty()) {
            return true;
        }
        XmlElement rootElement = document.getRootElement();
        XmlElement element = new XmlElement("sql");
        element.addAttribute(new Attribute("id","Base_Column_List"));
        StringBuilder sb = new StringBuilder();
        sb.append("<!--");
        element.addElement(new TextElement(sb.toString()));
        sb.setLength(0);

        sb.append("@mbg.generated");
        element.addElement(new TextElement(sb.toString()));
        sb.setLength(0);

        sb.append("-->");
        element.addElement(new TextElement(sb.toString()));
        sb.setLength(0);

        for (IntrospectedColumn column : columns) {
            sb.append(column.getActualColumnName()).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        element.addElement(new TextElement(sb.toString()));
        rootElement.addElement(1, element);
        return true;
    }


}
