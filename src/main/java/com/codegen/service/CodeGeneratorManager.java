package com.codegen.service;

import com.codegen.main.CodeGeneratorMain;
import com.codegen.service.impl.ControllerGenerator;
import com.codegen.service.impl.ModelAndMapperGenerator;
import com.codegen.service.impl.ServiceGenerator;
import com.codegen.util.StringUtils;
import com.google.common.base.CaseFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.mybatis.generator.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * 代码生成器基础项 (常量信息 & 通用方法)
 * Created by liuchunchun on 2019/02/14.
 */
public class CodeGeneratorManager extends CodeGeneratorConfig {
	
	protected static final Logger logger = LoggerFactory.getLogger(CodeGeneratorManager.class);

	private static Configuration configuration = null;
	
	private Context myContext;
	
	static {
		// 初始化配置信息
		init();
	}
	
	/**
	 * 获取 Freemarker 模板环境配置
	 * @return
	 */
	public Configuration getFreemarkerConfiguration() {
		if (configuration == null) {
			configuration = initFreemarkerConfiguration();
		}
		return configuration;
	}
	
	/**
	 * Mybatis 代码自动生成基本配置
	 * @return
	 */
	public Context initMybatisGeneratorContext(String sign) {
		Context context = new Context(ModelType.FLAT);
		context.setId("Mysql");
		context.setTargetRuntime("MyBatis3Simple");
		context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
        context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");
        context.addProperty("xmlMergeable", "true");
        context.addProperty("javaMergeable", "true");

		JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setConnectionURL(JDBC_URL);
        jdbcConnectionConfiguration.setUserId(JDBC_USERNAME);
        jdbcConnectionConfiguration.setPassword(JDBC_PASSWORD);
        jdbcConnectionConfiguration.setDriverClass(JDBC_DRIVER_CLASS_NAME);
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

		JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
		javaModelGeneratorConfiguration.setTargetProject(PROJECT_PATH+PACKAGE_PATH);
		javaModelGeneratorConfiguration.setTargetPackage(MODEL_PACKAGE + /*"." +*/ sign);
		context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

		SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
		sqlMapGeneratorConfiguration.setTargetProject(PROJECT_PATH + PACKAGE_PATH);
		sqlMapGeneratorConfiguration.setTargetPackage(XML_MAPPER_PACKAGE + /*"." +*/ sign);
		context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

		// 增加 mapper 插件
		/*
		* 目前取消使用，不需要基础类
		* */
        addMapperPlugin(context);
		/*
		* 去掉注释
		* */
        addMapperGender(context);

		return context;
	}

	private void addMapperGender(Context context) {
		CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
		commentGeneratorConfiguration.setConfigurationType("com.codegen.util.MySQLCommentGenerator");
		context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
	}

	/**
	 * 删除简单名称代码
	 * eg:
	 * 	genCode("gen_test_demo");  gen_test_demo ==> Demo
	 * @param tableNames 表名, 可以多表
	 */
	public void removeCodeWithSimpleName(boolean reMoveController, boolean reMoveService, boolean reMoveServiceImpl, boolean reMoveServiceMock
			, boolean reMoveModelAndMapperAndMapperXML, String... tableNames) {
		removeCodeByTableName(reMoveController,reMoveService,reMoveServiceImpl,reMoveServiceMock
				,reMoveModelAndMapperAndMapperXML,true, tableNames);
	}

	private void removeCodeByTableName(boolean reMoveController, boolean reMoveService, boolean reMoveServiceImpl, boolean reMoveServiceMock, boolean reMoveModelAndMapperAndMapperXML, boolean flag, String[] tableNames) {
		for (String tableName : tableNames) {
			removeByTableName(reMoveController,reMoveService,reMoveServiceImpl,reMoveServiceMock
					,reMoveModelAndMapperAndMapperXML,tableName, null, flag);
		}
	}

	/**
	 * 生成具体名称代码
	 * eg:
	 * 	genCode("gen_test_demo");  gen_test_demo ==> GenTestDemo
	 * @param tableNames 表名, 可以多表
	 */
	public void genCodeWithDetailName(boolean reBuildController,
									  boolean reBuildService,
									  boolean reBuildServiceImpl,
									  boolean reBuildServiceMock,
									  String alias,
									  String appAlias,
									  String ...tableNames) {
		genCodeByTableName(reBuildController, reBuildService, reBuildServiceImpl, reBuildServiceMock
				, false, alias, appAlias, tableNames);
	}

	/**
	 * 生成自定义名称代码
	 * eg:
	 * 	genCode("gen_test_demo", "IDemo");  gen_test_demo ==> IDemo
	 * @param tableName 表名, 只能单表
	 */
	public void genCodeWithCustomName(boolean reBuildController, boolean reBuildService, boolean reBuildServiceImpl, boolean reBuildServiceMock
			, boolean reBuildModelAndMapperAndMapperXML,String tableName, String customModelName) {
		genCodeByTableName(reBuildController, reBuildService, reBuildServiceImpl, reBuildServiceMock
				, tableName, customModelName, "", false);
	}

	/**
	 * 下划线转成驼峰, 首字符为小写
	 * eg: gen_test_demo ==> genTestDemo
	 * @param tableName 表名, eg: gen_test_demo
	 * @return
	 */
	protected String tableNameConvertLowerCamel(String tableName) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableName.toLowerCase());
	}

	/**
	 * 下划线转成驼峰, 首字符为大写
	 * eg: gen_test_demo ==> GenTestDemo
	 * @param tableName 表名, eg: gen_test_demo
	 * @return
	 */
	protected String tableNameConvertUpperCamel(String tableName) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase());
	}

	/**
	 * 表名转成映射路径
	 * eg: gen_test_demo ==> /gen/test/demo
	 * @param tableName 表名
	 * @return
	 */
	protected String tableNameConvertMappingPath(String tableName) {
		tableName = tableName.toLowerCase();
		return File.separator + (tableName.contains("_") ? tableName.replaceAll("_", File.separator) : tableName);
	}

	/**
	 * ModelName转成映射路径
	 * eg: Demo ==> /demo
	 * @param modelName
	 * @return
	 */
	protected String modelNameConvertMappingPath(String modelName) {
		String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName);
		return tableNameConvertMappingPath(tableName);
	}

	/**
	 * 获取表的区分字段
	 * @param tableName 表名, eg: gen_test_demo
	 * @return 区分字段 eg: test
	 */
	protected String getSign(String tableName) {
		/*
		* 刘春春修改：默认不使用表名作为包路径
		* */
		return "";
//		return getTableNameSplit(tableName)[1];
	}

	/**
	 * 获取默认 modelName
	 * @param tableName 表名
	 * @return
	 */
	protected String getDefModelName(String tableName) {
		String[] strs = getTableNameSplit(tableName);
		StringBuilder sb = new StringBuilder();
		for (int i = 2; i < strs.length; i++) {
			sb.append(StringUtils.toUpperCaseFirstOne(strs[i].toLowerCase()));
		}
		return sb.toString();
	}

	/**
	 * 获取表名切割后的数组
	 * @param tableName 表名
	 * @return
	 */
	private String[] getTableNameSplit(String tableName) {
		String[] strs = tableName.split("_");
		if (!tableName.contains("_") || strs.length < 3) {
			throw new RuntimeException("表名格式不正确, 请按规定格式! 例如: gen_test_demo");
		}
		return strs;
	}

	/**
	 * 通过数据库表名, 生成代码
	 * 如表名为 gen_test_demo
	 * 将生成  Demo & DemoMapper & DemoService & DemoServiceImpl & DemoController
	 * @param flag 标志
	 * @param tableNames 表名数组
	 */
	private void genCodeByTableName(boolean reBuildController,
									boolean reBuildService,
									boolean reBuildServiceImpl,
									boolean reBuildServiceMock,
									boolean flag,
									String alias,
									String appAlias,
									String ...tableNames) {
		for (String tableName : tableNames) {
			genCodeByTableName(reBuildController, reBuildService, reBuildServiceImpl, reBuildServiceMock
					, tableName, alias, appAlias, flag);
		}
	}

	/**
	 * 通过数据库表名, 和自定义 modelName 生成代码
	 * 如表名为 gen_test_demo, 自定义 modelName 为 IDemo
	 * 将生成  IDemo & IDemoMapper & IDemoService & IDemoServiceImpl & IDemoController
	 * @param tableName 表名
	 * @param modelName 实体类名
	 * @param flag 标志
	 */
	private void genCodeByTableName(boolean reBuildController,
									boolean reBuildService,
									boolean reBuildServiceImpl,
									boolean reBuildServiceMock,
									String tableName,
									String modelName,
									String appAlias,
									boolean flag) {
		String sign = getSign(tableName);
		if (flag) {
			/*
			* 刘春春修改：实体名即为表名驼峰格式
			* */
			modelName = tableNameConvertUpperCamel(tableName);
		}
		new ModelAndMapperGenerator().genCode(appAlias,tableName, modelName, sign);
		new ServiceGenerator().genCode(appAlias,tableName, modelName, sign,reBuildService,reBuildServiceImpl,reBuildServiceMock);
		new ControllerGenerator().genCode(appAlias,tableName, modelName, sign,reBuildController);
	}

	/**
	 * 通过数据库表名, 和自定义 modelName 生成代码
	 * 如表名为 gen_test_demo, 自定义 modelName 为 IDemo
	 * 将生成  IDemo & IDemoMapper & IDemoService & IDemoServiceImpl & IDemoController
	 * @param tableName 表名
	 * @param modelName 实体类名
	 * @param flag 标志
	 */
	private void removeByTableName(boolean reMoveController, boolean reMoveService, boolean reMoveServiceImpl, boolean reMoveServiceMock
			, boolean reMoveModelAndMapperAndMapperXML,String tableName, String modelName, boolean flag) {
		String sign = getSign(tableName);
		String upperModelName = "";
		if (flag) {
			/*
			* 刘春春修改：实体名即为表名驼峰格式
			* */
			modelName = tableNameConvertUpperCamel(tableName);
		}

		System.out.println("删除文件：" + PROJECT_PATH + PROJECT_NAME);
		delFolder(PROJECT_PATH + PROJECT_NAME);
	}

	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);//再删除空文件夹
				flag = true;
			}
		}
		return flag;

	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); //删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); //删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void delFile(String filePath) {
		try {
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); //删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Freemarker 模板环境配置
	 * @return
	 * @throws IOException
	 */
	private Configuration initFreemarkerConfiguration() {
		Configuration cfg = null;
		try {
			cfg = new Configuration(Configuration.VERSION_2_3_23);
			cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_FILE_PATH));
			cfg.setDefaultEncoding("UTF-8");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
		} catch (IOException e) {
			throw new RuntimeException("Freemarker 模板环境初始化异常!", e);
		}
		return cfg;
	}

	/**
	 * 增加 Mapper 插件
	 * @param context
	 */
	private void addMapperPlugin(Context context) {
//		PluginConfiguration equalsHashCodePlugin = new PluginConfiguration();
//		equalsHashCodePlugin.setConfigurationType("org.mybatis.generator.plugins.EqualsHashCodePlugin");
//		context.addPluginConfiguration(equalsHashCodePlugin);

		PluginConfiguration lombookPlugin = new PluginConfiguration();
		lombookPlugin.setConfigurationType("com.codegen.util.LombokPlugin");
		lombookPlugin.addProperty("hasLombok","true");
		context.addPluginConfiguration(lombookPlugin);

		PluginConfiguration mapperPlugin = new PluginConfiguration();
		mapperPlugin.setConfigurationType("com.codegen.util.MapperPlugin");
		mapperPlugin.addProperty("targetProject",PROJECT_PATH+PACKAGE_PATH);
		mapperPlugin.addProperty("targetPackage",MAPPER_PACKAGE);
		context.addPluginConfiguration(mapperPlugin);

		PluginConfiguration renameSqlMapperPlugin = new PluginConfiguration();
		renameSqlMapperPlugin.setConfigurationType("com.codegen.util.RenameXmlMapperPlugin");
		renameSqlMapperPlugin.addProperty("searchString","Mapper");
		renameSqlMapperPlugin.addProperty("replaceString","Dao");
		context.addPluginConfiguration(renameSqlMapperPlugin);

		PluginConfiguration renameJavaMapperPlugin = new PluginConfiguration();
		renameJavaMapperPlugin.setConfigurationType("com.codegen.util.RenameJavaMapperPlugin");
		renameJavaMapperPlugin.addProperty("searchString","Mapper$");
		renameJavaMapperPlugin.addProperty("replaceString","Dao");
		context.addPluginConfiguration(renameJavaMapperPlugin);

		// 创建 Service 接口
		File modelFile = new File(PROJECT_PATH + PACKAGE_PATH + MODEL_PACKAGE.replace(".", "/"));
		// 查看父级目录是否存在, 不存在则创建
		if (!modelFile.exists()) {
			modelFile.mkdirs();
		}

		// 创建 MAPPER 接口
		File mapperFile = new File(PROJECT_PATH + PACKAGE_PATH_MAPPER);
		// 查看父级目录是否存在, 不存在则创建
		if (!mapperFile.exists()) {
			mapperFile.mkdirs();
		}

//		PluginConfiguration pluginConfiguration = new PluginConfiguration();
//		pluginConfiguration.setConfigurationType("com.codegen.util.SerializablePlugin");
//		pluginConfiguration.addProperty("suppressJavaInterface", "false");
//		context.addPluginConfiguration(pluginConfiguration);
	}

	/**
	 * 包转成路径
	 * eg: com.bigsea.sns ==> com/bigsea/sns
	 * @param packageName
	 * @return
	 */
	private static String packageConvertPath(String packageName) {
		return String.format("/%s/", packageName.contains(".") ? packageName.replaceAll("\\.", "/") : packageName);
	}

	/**
	 * 初始化配置信息
	 */
	private static void init() {
		Properties prop = loadProperties();

		JDBC_URL = prop.getProperty("jdbc.url");
		JDBC_USERNAME = prop.getProperty("jdbc.username");
		JDBC_PASSWORD = prop.getProperty("jdbc.password");
		JDBC_DRIVER_CLASS_NAME = prop.getProperty("jdbc.driver.class.name");

		JAVA_PATH = prop.getProperty("java.path");
		MODEL_PATH = prop.getProperty("model.path");
		MAPPER_PATH = prop.getProperty("mapper.path");
		TEMPLATE_FILE_PATH = PROJECT_PATH + prop.getProperty("template.file.path");

		BASE_PACKAGE = prop.getProperty("base.package");
		PROJECT_NAME = prop.getProperty("project.name");
		PACKAGE_PATH = prop.getProperty("package.path");
		MODEL_PACKAGE = BASE_PACKAGE + "." + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, CodeGeneratorMain.ALIAS) + "." + prop.getProperty("model.package.tail");
		MAPPER_PACKAGE = BASE_PACKAGE + "."+ CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, CodeGeneratorMain.ALIAS) + "." + prop.getProperty("mapper.package.tail");
		XML_MAPPER_PACKAGE = BASE_PACKAGE + "."+ CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, CodeGeneratorMain.ALIAS) + "." + prop.getProperty("xml.mapper.package.tail");
		BASE_MAPPER_PACKAGE = BASE_PACKAGE + "."+ CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, CodeGeneratorMain.ALIAS) + "." + prop.getProperty("base.mapper.package.tail");
		SERVICE_PACKAGE = BASE_PACKAGE + "."+ CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, CodeGeneratorMain.ALIAS) + "." + prop.getProperty("service.package.tail");
		SERVICE_IMPL_PACKAGE = BASE_PACKAGE + "."+ CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, CodeGeneratorMain.ALIAS) + "." + prop.getProperty("service.impl.package.tail");
		CONTROLLER_PACKAGE = BASE_PACKAGE + "."+ CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, CodeGeneratorMain.ALIAS) + "." + prop.getProperty("controller.package.tail");

		MAPPER_INTERFACE_REFERENCE = prop.getProperty("mapper.interface.reference");
		SERVICE_INTERFACE_REFERENCE = prop.getProperty("service.interface.reference");
		ABSTRACT_SERVICE_CLASS_REFERENCE = prop.getProperty("abstract.service.class.reference");

		AUTHOR = prop.getProperty("author");
		String dateFormat = "".equals(prop.getProperty("date-format")) ? "yyyy/MM/dd" : prop.getProperty("date-format");
		DATE = new SimpleDateFormat(dateFormat).format(new Date());
	}

	/**
	 * 加载配置文件
	 * @return
	 */
	private static Properties loadProperties() {
		Properties prop = null;
		try {
			prop = new Properties();
			InputStream in = CodeGeneratorManager.class.getClassLoader().getResourceAsStream("generatorConfig.properties");
			prop.load(in);
		} catch (Exception e) {
			throw new RuntimeException("加载配置文件异常!", e);
		}
		return prop;
	}


}
