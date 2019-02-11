package com.codegen.util;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
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

        // 添加sql_code_name_like
        XmlElement sqlCodeNameLike = new XmlElement("sql");
        sqlCodeNameLike.addAttribute(new Attribute("id", "sql_code_name_like"));
        XmlElement sqlCodeNameLikeWhere = new XmlElement("where");
        XmlElement sqlCodeNameLikeTrim = new XmlElement("trim");
        sqlCodeNameLikeTrim.addAttribute(new Attribute("prefix","("));
        sqlCodeNameLikeTrim.addAttribute(new Attribute("suffix",")"));
        sqlCodeNameLikeTrim.addAttribute(new Attribute("prefixOverrides","or"));
        StringBuilder sqlCodeNameLikeSb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
            String javaProperty = introspectedColumn.getJavaProperty();
            if ("sdOrg".equals(javaProperty) || "idTet".equals(javaProperty)) {
                continue;
            }
            XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            sqlCodeNameLikeSb.setLength(0);
            sqlCodeNameLikeSb.append(javaProperty);
            sqlCodeNameLikeSb.append(" != null"); //$NON-NLS-1$
            sqlCodeNameLikeSb.append(" and "); //$NON-NLS-1$
            sqlCodeNameLikeSb.append(javaProperty);
            sqlCodeNameLikeSb.append(" != ''"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("test", sqlCodeNameLikeSb.toString())); //$NON-NLS-1$

            sqlCodeNameLikeSb.setLength(0);
            sqlCodeNameLikeSb.append(" or ");
            sqlCodeNameLikeSb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sqlCodeNameLikeSb.append(" like \"%\""); //$NON-NLS-1$
            sqlCodeNameLikeSb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            sqlCodeNameLikeSb.append("\"%\"");
            isNotNullElement.addElement(new TextElement(sqlCodeNameLikeSb.toString()));

            sqlCodeNameLikeTrim.addElement(isNotNullElement);
        }
        sqlCodeNameLikeWhere.addElement(sqlCodeNameLikeTrim);
        sqlCodeNameLike.addElement(sqlCodeNameLikeWhere);
        parentElement.addElement(sqlCodeNameLike);

        //添加getList
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", "findByModel"));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        select.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        select.addElement(new TextElement(" select * from "+ introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid", "sql_where"));

        select.addElement(include);
        parentElement.addElement(select);

        //添加getList
        XmlElement delete = new XmlElement("delete");
        delete.addAttribute(new Attribute("id", "deleteByModel"));
        delete.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        delete.addElement(new TextElement(" delete from "+ introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        XmlElement deleteInclude = new XmlElement("include");
        deleteInclude.addAttribute(new Attribute("refid", "sql_where"));

        delete.addElement(deleteInclude);
        parentElement.addElement(delete);

        //添加findByLike
        XmlElement findByLike = new XmlElement("select");
        findByLike.addAttribute(new Attribute("id", "findByLike"));
        findByLike.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        findByLike.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        findByLike.addElement(new TextElement(" select * from "+ introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        XmlElement findByLikeInclude = new XmlElement("include");
        findByLikeInclude.addAttribute(new Attribute("refid", "sql_code_name_like"));

        findByLike.addElement(findByLikeInclude);
        parentElement.addElement(findByLike);

        //添加findAll
        XmlElement findAll = new XmlElement("select");
        findAll.addAttribute(new Attribute("id", "findAll"));
        findAll.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        findAll.addElement(new TextElement(" select * from "+ introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        parentElement.addElement(findAll);

        //添加isExist
        XmlElement isExistSelect = new XmlElement("select");
        isExistSelect.addAttribute(new Attribute("id", "isExist"));
        isExistSelect.addAttribute(new Attribute("resultType", "java.lang.Integer"));
        isExistSelect.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        isExistSelect.addElement(new TextElement(" select count(*) from "+ introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        isExistSelect.addElement(include);
        parentElement.addElement(isExistSelect);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        MybatisGeneratorContext instance = MybatisGeneratorContext.getInstance();
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"+instance);
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
                            expandDaoTargetPackage + "." + shortName.replace("Mapper", "ExpandMapper"));
                    mapperInterface.setVisibility(JavaVisibility.PUBLIC);
                    mapperInterface.addJavaDocLine("/**");
                    mapperInterface.addJavaDocLine(" * " + shortName + "扩展");
                    mapperInterface.addJavaDocLine(" */");
 
                    FullyQualifiedJavaType daoSuperType = new FullyQualifiedJavaType(expandDaoSuperClass);
                    mapperInterface.addImportedType(daoSuperType);
                    mapperInterface.addSuperInterface(daoSuperType);

                    Method method = new Method();
                    method.setName("findByModel");
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
                Interface mapperInterface = new Interface(daoTargetPackage + "." + shortName + "Mapper");

 
                mapperInterface.setVisibility(JavaVisibility.PUBLIC);
                mapperInterface.addJavaDocLine("/**");
                mapperInterface.addJavaDocLine(" * "+shortName+"数据接口");
                mapperInterface.addJavaDocLine(" */");
                mapperInterface.addAnnotation("@Mapper");
                mapperInterface.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));

                String lowerFirstName = StringUtils.toLowerCaseFirstOne(shortName);
                /*
                * findByModel方法
                * */
                //设置方法参数
                Parameter findByModelParam = new Parameter(baseModelJavaType,lowerFirstName);
                //设置方法返回值
                FullyQualifiedJavaType findByModelReturnType = new FullyQualifiedJavaType("List");
                findByModelReturnType.addTypeArgument(baseModelJavaType);
                //设置引入此方法所需要导入的包
                List<FullyQualifiedJavaType> findByModelImports = new ArrayList<>();
                findByModelImports.add(new FullyQualifiedJavaType("java.util.List"));
                findByModelImports.add(baseModelJavaType);

                Method findByModel = buildMethod(mapperInterface, baseModelJavaType, "findByModel", findByModelParam, findByModelReturnType, findByModelImports);

                /*
                * findByLike方法
                * */
                Method findByLike = buildMethod(mapperInterface, baseModelJavaType, "findByLike", findByModelParam, findByModelReturnType, null);

                IntrospectedColumn primaryKey = introspectedTable.getPrimaryKeyColumns().get(0);
                FullyQualifiedJavaType primaryType = primaryKey.getFullyQualifiedJavaType();
                String javaProperty = primaryKey.getJavaProperty();

                /*
                * findAll方法
                * */
                Method findAll = buildMethod(mapperInterface, baseModelJavaType, "findAll", null
                        , baseModelJavaType, null);

                /*
                * deleteByPrimaryKey方法
                * */
                //设置方法参数
                Parameter deleteByPrimaryKeyParam = new Parameter(/*参数类型*/primaryType,/*形参*/javaProperty);
                //设置方法返回值
                FullyQualifiedJavaType deleteByPrimaryKeyReturnType = new FullyQualifiedJavaType("int");
                Method deleteByPrimaryKey = buildMethod(mapperInterface, baseModelJavaType, "deleteByPrimaryKey", deleteByPrimaryKeyParam, deleteByPrimaryKeyReturnType, null);

                /*
                * deleteByModel方法
                * */
                //设置方法参数
                Parameter deleteByModelParam = new Parameter(baseModelJavaType,lowerFirstName);
                //设置方法返回值
                FullyQualifiedJavaType deleteByModelReturnType = new FullyQualifiedJavaType("int");
                Method deleteByModel = buildMethod(mapperInterface, baseModelJavaType, "deleteByModel", deleteByModelParam, deleteByModelReturnType, null);

                /*
                * insert方法
                * */
                //设置方法参数
                Parameter insertParam = new Parameter(/*参数类型*/baseModelJavaType,/*形参*/lowerFirstName);
                //设置方法返回值
                FullyQualifiedJavaType insertReturnType = new FullyQualifiedJavaType("int");

                Method insert = buildMethod(mapperInterface, baseModelJavaType, "insert", insertParam, insertReturnType, null);


                /*
                * insertSelective方法
                * */
                //设置方法参数
                Parameter insertSelectiveParam = new Parameter(/*参数类型*/baseModelJavaType,/*形参*/lowerFirstName);
                //设置方法返回值
                FullyQualifiedJavaType insertSelectiveReturnType = new FullyQualifiedJavaType("int");

                Method insertSelective = buildMethod(mapperInterface, baseModelJavaType, "insertSelective", insertSelectiveParam, insertSelectiveReturnType, null);

                /*
                * selectByPrimaryKey方法
                * */
                //设置方法参数
                Parameter selectByPrimaryKeyParam = new Parameter(/*参数类型*/primaryType,/*形参*/javaProperty);
                //设置方法返回值
                FullyQualifiedJavaType selectByPrimaryKeyReturnType = baseModelJavaType;

                Method selectByPrimaryKey = buildMethod(mapperInterface, baseModelJavaType, "selectByPrimaryKey", selectByPrimaryKeyParam, selectByPrimaryKeyReturnType, null);

                /*
                * updateByPrimaryKeySelective方法
                * */
                //设置方法参数
                Parameter updateByPrimaryKeySelectiveParam = new Parameter(/*参数类型*/baseModelJavaType,/*形参*/lowerFirstName);
                //设置方法返回值
                FullyQualifiedJavaType updateByPrimaryKeySelectiveReturnType = new FullyQualifiedJavaType("int");

                Method updateByPrimaryKeySelective = buildMethod(mapperInterface, baseModelJavaType, "updateByPrimaryKeySelective", updateByPrimaryKeySelectiveParam, updateByPrimaryKeySelectiveReturnType, null);

                /*
                * updateByPrimaryKey方法
                * */
                //设置方法参数
                Parameter updateByPrimaryKeyParam = new Parameter(/*参数类型*/baseModelJavaType,/*形参*/lowerFirstName);
                //设置方法返回值
                FullyQualifiedJavaType updateByPrimaryKeyReturnType = new FullyQualifiedJavaType("int");

                Method updateByPrimaryKey = buildMethod(mapperInterface, baseModelJavaType, "updateByPrimaryKey", updateByPrimaryKeyParam, updateByPrimaryKeyReturnType, null);

                /*
                * isExist方法
                * */
                //设置方法参数
                Parameter isExistParam = new Parameter(/*参数类型*/primaryType,/*形参*/javaProperty);
                //设置方法返回值
                FullyQualifiedJavaType isExistReturnType = new FullyQualifiedJavaType("int");

                Method isExist = buildMethod(mapperInterface, baseModelJavaType, "isExist", updateByPrimaryKeyParam, updateByPrimaryKeyReturnType, null);

                mapperInterface.addMethod(findByModel);
                mapperInterface.addMethod(findByLike);
                mapperInterface.addMethod(findAll);
                mapperInterface.addMethod(deleteByPrimaryKey);
                mapperInterface.addMethod(insert);
                mapperInterface.addMethod(insertSelective);
                mapperInterface.addMethod(selectByPrimaryKey);
                mapperInterface.addMethod(updateByPrimaryKeySelective);
                mapperInterface.addMethod(updateByPrimaryKey);
                mapperInterface.addMethod(isExist);
                mapperInterface.addMethod(deleteByModel);

                mapperJavafile = new GeneratedJavaFile(mapperInterface, daoTargetDir, "UTF-8", javaFormatter,true);
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
