package com.codegen.util;

import com.codegen.service.CodeGeneratorConfig;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * <p> Author：lzhpo </p>
 * <p> Title：</p>
 * <p> Description：
 * 继承我自定义的MyCommentGenerator方法，获取数据库的表注释以及字段注释。
 * </p>
 */
public class MySQLCommentGenerator extends MyCommentGenerator {

    private Properties properties;

    public MySQLCommentGenerator() {
        properties = new Properties();
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        // 获取自定义的 properties
        this.properties.putAll(properties);
    }

    /**
     * 自定义实体类的主注释
     * @param topLevelClass
     * @param introspectedTable
     */
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String author = CodeGeneratorConfig.AUTHOR;
        String dateFormat = properties.getProperty("dateFormat", "yyyy-MM-dd");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

        // 获取表注释
        String remarks = introspectedTable.getRemarks();

        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * @Author：" + author);
        topLevelClass.addJavaDocLine(" * @Company: 北京万相融通科技股份有限公司");
        topLevelClass.addJavaDocLine(" * @Date：" + CodeGeneratorConfig.DATE);
        topLevelClass.addJavaDocLine(" * @Version: 1.0");
        topLevelClass.addJavaDocLine(" * @Description: " + (remarks == null ? "" : remarks));

//        topLevelClass.addJavaDocLine(" * @Description：" + remarks);
        topLevelClass.addJavaDocLine(" */");
    }

    /**
     * 自定义实体类的列注释
     * @param field
     * @param introspectedTable
     * @param introspectedColumn
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        // 获取列注释
        String remarks = introspectedColumn.getRemarks();
        if (!StringUtils.isNullOrEmpty(remarks)) {
            field.addJavaDocLine("//" + remarks);
            if (introspectedColumn.isIdentity()) {
                field.addAnnotation("@TableId(value = \"id\", type = IdType.INPUT)");
            }
        }
    }
}