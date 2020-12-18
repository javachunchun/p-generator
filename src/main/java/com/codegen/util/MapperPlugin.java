package com.codegen.util;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**   
 * @Title: MapperPlugin.java 
 * @Package org.mybatis.generator.plugins 
 * @author fendo
 * @date 2017年12月2日 下午5:35:14 
 * @version V1.0   
*/
public class MapperPlugin extends PluginAdapter {
 
    private static final String DEFAULT_DAO_SUPER_CLASS = "com.fendo.mapper.BaseMapper";
    private static final String DEFAULT_EXPAND_DAO_SUPER_CLASS = "com.fendo.mapper.BaseExpandMapper";
    private String daoTargetDir;
    private String daoTargetPackage;
 
    private String daoSuperClass;
 
    // 扩展
    private String expandDaoTargetPackage;
    private String expandDaoSuperClass;
 
    private ShellCallback shellCallback = null;
 
    public MapperPlugin() {
        shellCallback = new DefaultShellCallback(false);
    }
 
    /**
     * 验证参数是否有效
     * @param warnings
     * @return
     */
    public boolean validate(List<String> warnings) {
        daoTargetDir = properties.getProperty("targetProject");
        boolean valid = stringHasValue(daoTargetDir);
 
        daoTargetPackage = properties.getProperty("targetPackage");
        boolean valid2 = stringHasValue(daoTargetPackage);
 
        daoSuperClass = properties.getProperty("daoSuperClass");
        if (!stringHasValue(daoSuperClass)) {
            daoSuperClass = DEFAULT_DAO_SUPER_CLASS;
        }
 
        expandDaoTargetPackage = properties.getProperty("expandTargetPackage");
        expandDaoSuperClass = properties.getProperty("expandDaoSuperClass");
        if (!stringHasValue(expandDaoSuperClass)) {
            expandDaoSuperClass = DEFAULT_EXPAND_DAO_SUPER_CLASS;
        }
        return valid && valid2;
    }
 
    
    /** 
     * 生成mapping 添加自定义sql 
     */ 
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();// 数据库表名
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        XmlElement parentElement = document.getRootElement();

        // 添加sql——where
        XmlElement sql = new XmlElement("sql");
        sql.addAttribute(new Attribute("id", "sql_where"));
        XmlElement where = new XmlElement("where");
        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
            XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null"); //$NON-NLS-1$
            FullyQualifiedJavaType javaType = introspectedColumn.getFullyQualifiedJavaType();
            if (javaType.getShortName().contains("String")) {
                sb.append(" and "); //$NON-NLS-1$
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != ''"); //$NON-NLS-1$
            }
            isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
            where.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(" and ");
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            isNotNullElement.addElement(new TextElement(sb.toString()));
        }
        sql.addElement(where);
        parentElement.addElement(sql);

        // 添加Base_Column_List
        XmlElement baseColumnListSql = new XmlElement("sql");
        baseColumnListSql.addAttribute(new Attribute("id", "Base_Column_List"));
        for (int i = 0; i < introspectedTable.getAllColumns().size(); i++) {
            IntrospectedColumn introspectedColumn = introspectedTable.getAllColumns().get(i);
            if (i < introspectedTable.getAllColumns().size() - 1) {
                baseColumnListSql.addElement(new TextElement(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn) + ","));
            } else {
                baseColumnListSql.addElement(new TextElement(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn)));
            }
        }
        parentElement.addElement(baseColumnListSql);

        //添加list
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", "list"));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        select.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        select.addElement(new TextElement("select <include refid=\"Base_Column_List\" /> from "+ introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid", "sql_where"));

        select.addElement(include);
        parentElement.addElement(select);

        //添加selectById
        XmlElement selectById = new XmlElement("select");
        selectById.addAttribute(new Attribute("id", "selectById"));
        selectById.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        selectById.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        selectById.addElement(new TextElement("select <include refid=\"Base_Column_List\" /> from "+ introspectedTable.getFullyQualifiedTableNameAtRuntime()+"" +
                " where id= #{id}"));

        parentElement.addElement(selectById);

        //添加selectByIds
        XmlElement selectByIds = new XmlElement("select");
        selectByIds.addAttribute(new Attribute("id", "selectByIds"));
        selectByIds.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        selectByIds.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        selectByIds.addElement(new TextElement("select <include refid=\"Base_Column_List\" /> from "+ introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        XmlElement selectByIdsIf = new XmlElement("if");
        selectByIdsIf.addAttribute(new Attribute("test", "idList != null and idList.size != 0"));
        selectByIdsIf.addElement(new TextElement("where id in"));

        XmlElement selectByIdsForeach = new XmlElement("foreach");
        selectByIdsForeach.addAttribute(new Attribute("collection", "idList"));
        selectByIdsForeach.addAttribute(new Attribute("item", "id"));
        selectByIdsForeach.addAttribute(new Attribute("open", "("));
        selectByIdsForeach.addAttribute(new Attribute("close", ")"));
        selectByIdsForeach.addAttribute(new Attribute("separator", ","));
        selectByIdsForeach.addElement(new TextElement("#{id}"));

        selectByIdsIf.addElement(selectByIdsForeach);
        selectByIds.addElement(selectByIdsIf);

        parentElement.addElement(selectByIds);

        // 添加add
        XmlElement add = new XmlElement("insert");
        add.addAttribute(new Attribute("id", "add"));
        add.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        add.addElement(new TextElement("insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        add.addElement(new TextElement("("));
        for (int i = 0; i < introspectedTable.getAllColumns().size(); i++) {
            IntrospectedColumn introspectedColumn = introspectedTable.getAllColumns().get(i);
            if (i < introspectedTable.getAllColumns().size() - 1) {
                add.addElement(new TextElement(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn) + ","));
            } else {
                add.addElement(new TextElement(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn)));
            }
        }
        add.addElement(new TextElement(")"));

        add.addElement(new TextElement("values"));
        add.addElement(new TextElement("("));
        for (int i = 0; i < introspectedTable.getAllColumns().size(); i++) {
            IntrospectedColumn introspectedColumn = introspectedTable.getAllColumns().get(i);
            if (i < introspectedTable.getAllColumns().size() - 1) {
                add.addElement(new TextElement(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn) + ","));
            } else {
                add.addElement(new TextElement(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn)));
            }
        }
        add.addElement(new TextElement(")"));

        parentElement.addElement(add);

        // 添加update
        XmlElement update = new XmlElement("update");
        update.addAttribute(new Attribute("id", "update" + introspectedTable.getTableConfiguration().getDomainObjectName() + "ById"));
        update.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        update.addElement(new TextElement("update "+ introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        XmlElement set = new XmlElement("set");
        StringBuilder setSb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
            XmlElement ifElement = new XmlElement("if"); //$NON-NLS-1$
            setSb.setLength(0);
            setSb.append(introspectedColumn.getJavaProperty());
            setSb.append(" != null"); //$NON-NLS-1$
            FullyQualifiedJavaType javaType = introspectedColumn.getFullyQualifiedJavaType();
            if (javaType.getShortName().contains("String")) {
                setSb.append(" and "); //$NON-NLS-1$
                setSb.append(introspectedColumn.getJavaProperty());
                setSb.append(" != ''"); //$NON-NLS-1$
            }
            ifElement.addAttribute(new Attribute("test", setSb.toString())); //$NON-NLS-1$

            setSb.setLength(0);
            setSb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            setSb.append(" = "); //$NON-NLS-1$
            setSb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            setSb.append(",");
            ifElement.addElement(new TextElement(setSb.toString()));
            set.addElement(ifElement);
        }

        update.addElement(set);
        update.addElement(new TextElement(" where id=#{id}"));
        parentElement.addElement(update);

        //添加deleteById
        XmlElement deleteById = new XmlElement("delete");
        deleteById.addAttribute(new Attribute("id", "deleteById"));
        deleteById.addAttribute(new Attribute("parameterType", "int"));
        deleteById.addElement(new TextElement("delete from "+ introspectedTable.getFullyQualifiedTableNameAtRuntime()+"" +
                " where id= #{id}"));

        parentElement.addElement(deleteById);

        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        MybatisGeneratorContext instance = MybatisGeneratorContext.getInstance();
        instance.setIntrospectedTable(introspectedTable);

        JavaFormatter javaFormatter = context.getJavaFormatter();
        List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<GeneratedJavaFile>();
        for (GeneratedJavaFile javaFile : introspectedTable.getGeneratedJavaFiles()) {
            CompilationUnit unit = javaFile.getCompilationUnit();
            FullyQualifiedJavaType baseModelJavaType = unit.getType();
 
            String shortName = baseModelJavaType.getShortName();
 
            GeneratedJavaFile mapperJavafile = null;
 
            if (shortName.endsWith("Mapper")) { // 扩展Mapper
                if (stringHasValue(expandDaoTargetPackage)) {
                    Interface mapperInterface = new Interface(
                            expandDaoTargetPackage + "." + shortName.replace("Mapper", "Dao"));
                    mapperInterface.setVisibility(JavaVisibility.PUBLIC);
                    mapperInterface.addJavaDocLine("/**");
                    mapperInterface.addJavaDocLine(" * " + shortName + "扩展");
                    mapperInterface.addJavaDocLine(" */");
 
                    FullyQualifiedJavaType daoSuperType = new FullyQualifiedJavaType(expandDaoSuperClass);
                    mapperInterface.addImportedType(daoSuperType);
                    mapperInterface.addSuperInterface(daoSuperType);

                    Method method = new Method();
                    method.setName("list");
                    Parameter p = new Parameter(new FullyQualifiedJavaType(StringUtils.toUpperCaseFirstOne(shortName)),shortName);
                    method.addParameter(p);

                    FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("List");
                    // 添加泛型支持
                    returnType.addTypeArgument(new FullyQualifiedJavaType(StringUtils.toUpperCaseFirstOne(shortName)));
                    method.setReturnType(returnType);
                    mapperInterface.addImportedType(new FullyQualifiedJavaType("java.util.List"));
                    mapperInterface.addImportedType(baseModelJavaType);

                    mapperInterface.addMethod(method);
 
                    mapperJavafile = new GeneratedJavaFile(mapperInterface, daoTargetDir, javaFormatter);
                    try {
                        File mapperDir = shellCallback.getDirectory(daoTargetDir, daoTargetPackage);
                        File mapperFile = new File(mapperDir, mapperJavafile.getFileName());
                        // 文件不存在
                        if (!mapperFile.exists()) {
                            mapperJavaFiles.add(mapperJavafile);
                        }
                    } catch (ShellException e) {
                        e.printStackTrace();
                    }
                }
            } else if (!shortName.endsWith("Example")) { // CRUD Mapper
                Interface mapperInterface = new Interface(daoTargetPackage + "." + shortName + "Dao");

 
                mapperInterface.setVisibility(JavaVisibility.PUBLIC);
                mapperInterface.addJavaDocLine("/**");
                mapperInterface.addJavaDocLine(" * "+shortName+"数据接口");
                mapperInterface.addJavaDocLine(" */");
                FullyQualifiedJavaType fullyQualifiedJavaType = new FullyQualifiedJavaType("Mapper");
                fullyQualifiedJavaType.addTypeArgument(new FullyQualifiedJavaType(shortName));
//                mapperInterface.addSuperInterface(fullyQualifiedJavaType);
//                mapperInterface.addImportedType(new FullyQualifiedJavaType("com.bjsdzk.common.core.Mapper"));
                mapperInterface.addImportedType(baseModelJavaType);
//                mapperInterface.addAnnotation("@Mapper");
//                mapperInterface.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));

                String lowerFirstName = StringUtils.toLowerCaseFirstOne(shortName);
                /*
                * list方法
                * */
                //设置方法参数
                Parameter listParam = new Parameter(baseModelJavaType,lowerFirstName);
                //设置方法返回值
                FullyQualifiedJavaType listReturnType = new FullyQualifiedJavaType("List");
                listReturnType.addTypeArgument(baseModelJavaType);
                //设置引入此方法所需要导入的包
                List<FullyQualifiedJavaType> listImports = new ArrayList<>();
                listImports.add(new FullyQualifiedJavaType("java.util.List"));
                listImports.add(baseModelJavaType);

                Method list = buildMethod(mapperInterface, baseModelJavaType, "list", listParam, listReturnType, listImports);

                List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
                if (primaryKeyColumns == null || primaryKeyColumns.size() <= 0) {
                    throw new RuntimeException("主键未定义");
                }
                IntrospectedColumn primaryKey = introspectedTable.getPrimaryKeyColumns().get(0);
                FullyQualifiedJavaType primaryType = primaryKey.getFullyQualifiedJavaType();
                String javaProperty = primaryKey.getJavaProperty();

                /*
                 * selectById方法
                 * */
                //设置方法参数
                Parameter selectByIdParam = new Parameter(/*参数类型*/primaryType,/*形参*/javaProperty);
                //设置方法返回值
                FullyQualifiedJavaType selectByIdReturnType = baseModelJavaType;

                Method selectById = buildMethod(mapperInterface, baseModelJavaType, "selectById", selectByIdParam, selectByIdReturnType, null);

                /*
                 * selectByIds方法
                 * */
                //设置方法参数
                FullyQualifiedJavaType listType = new FullyQualifiedJavaType("List");
                FullyQualifiedJavaType stringType = new FullyQualifiedJavaType("String");
                listType.addTypeArgument(stringType);

                Parameter selectByIdsParam = new Parameter(/*参数类型*/listType,/*形参*/javaProperty + "List");
                //设置方法返回值
                FullyQualifiedJavaType selectByIdsReturnType = baseModelJavaType;

                Method selectByIds = buildMethod(mapperInterface, baseModelJavaType, "selectByIds", selectByIdsParam, selectByIdsReturnType, null);

                /*
                 * add方法
                 * */
                //设置方法参数
                Parameter addParam = new Parameter(/*参数类型*/baseModelJavaType,/*形参*/lowerFirstName);
                //设置方法返回值
                FullyQualifiedJavaType addReturnType = new FullyQualifiedJavaType("int");

                Method add = buildMethod(mapperInterface, baseModelJavaType, "add", addParam, addReturnType, null);

                /*
                 * updateById方法
                 * */
                //设置方法参数
                Parameter updateByIdParam = new Parameter(/*参数类型*/baseModelJavaType,/*形参*/lowerFirstName);
                //设置方法返回值
                FullyQualifiedJavaType updateByIdReturnType = new FullyQualifiedJavaType("int");

                Method updateById = buildMethod(mapperInterface, baseModelJavaType, "update" + shortName + "ById", updateByIdParam, updateByIdReturnType, null);

                /*
                * deleteById方法
                * */
                //设置方法参数
                Parameter deleteByIdParam = new Parameter(/*参数类型*/primaryType,/*形参*/javaProperty);
                //设置方法返回值
                FullyQualifiedJavaType deleteByIdReturnType = new FullyQualifiedJavaType("int");
                Method deleteByPrimaryKey = buildMethod(mapperInterface, baseModelJavaType, "deleteById", deleteByIdParam, deleteByIdReturnType, null);

                list.addJavaDocLine("//查询列表");
                selectById.addJavaDocLine("//查询详情");
                selectByIds.addJavaDocLine("//根据主键查询列表");
                add.addJavaDocLine("//新增");
                updateById.addJavaDocLine("//根据主键更新");
                deleteByPrimaryKey.addJavaDocLine("//根据主键删除");
                mapperInterface.addMethod(list);
                mapperInterface.addMethod(selectById);
                mapperInterface.addMethod(selectByIds);
                mapperInterface.addMethod(add);
                mapperInterface.addMethod(updateById);
                mapperInterface.addMethod(deleteByPrimaryKey);

                mapperJavafile = new GeneratedJavaFile(mapperInterface, daoTargetDir, "UTF-8", javaFormatter);
                mapperJavaFiles.add(mapperJavafile);
 
            }
        }
        return mapperJavaFiles;
    }

    private Method buildMethod(Interface mapperInterface, FullyQualifiedJavaType baseModelJavaType,
                               String methodName, Parameter parameter, FullyQualifiedJavaType returnType,
                                List<FullyQualifiedJavaType> imports) {
        String shortName = baseModelJavaType.getShortName();

        //设置方法名
        Method method = new Method();
        method.setName(methodName);
        if (parameter != null) {
            method.addParameter(parameter);
        }
        //设置方法返回值
        if (returnType != null) {
            method.setReturnType(returnType);
        }

        if (imports != null && imports.size() > 0) {
            for (FullyQualifiedJavaType anImport : imports) {
                //设置引用
                mapperInterface.addImportedType(anImport);
            }
        }


        return method;
    }

}
