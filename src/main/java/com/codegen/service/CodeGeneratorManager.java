package com.codegen.service;

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
		context.setId("MysqlContext");
		context.setTargetRuntime("MyBatis3");
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
		javaModelGeneratorConfiguration.setTargetProject(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_MODEL);
		javaModelGeneratorConfiguration.setTargetPackage(MODEL_PACKAGE + /*"." +*/ sign);
		context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

		JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
		javaClientGeneratorConfiguration.setTargetProject(PROJECT_PATH + JAVA_PATH+ PACKAGE_PATH_MAPPER);
		javaClientGeneratorConfiguration.setTargetPackage(MAPPER_PACKAGE + /*"." +*/ sign);
		javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
		context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

		SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
		sqlMapGeneratorConfiguration.setTargetProject(PROJECT_PATH + JAVA_PATH+ RESOURCES_PATH);
		sqlMapGeneratorConfiguration.setTargetPackage(MAPPER_PACKAGE + /*"." +*/ sign);
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
		commentGeneratorConfiguration.setConfigurationType("com.codegen.util.CommentGenerator");
		commentGeneratorConfiguration.addProperty("suppressAllComments","true");
		commentGeneratorConfiguration.addProperty("suppressDate","true");
		context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
	}

	/**
	 * 生成简单名称代码
	 * eg: 
	 * 	genCode("gen_test_demo");  gen_test_demo ==> Demo
	 * @param tableNames 表名, 可以多表
	 */
	public void genCodeWithSimpleName(boolean reBuildController,
									  boolean reBuildService,
									  boolean reBuildServiceImpl,
									  boolean reBuildServiceMock,
									  String... tableNames) {
		genCodeByTableName(reBuildController,reBuildService,reBuildServiceImpl,reBuildServiceMock
				,true, tableNames);
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
	public void genCodeWithDetailName(boolean reBuildController, boolean reBuildService, boolean reBuildServiceImpl, boolean reBuildServiceMock
			, boolean reBuildModelAndMapperAndMapperXML,String ...tableNames) {
		genCodeByTableName(reBuildController,reBuildService,reBuildServiceImpl,reBuildServiceMock
				,false, tableNames);
	}

	/**
	 * 生成自定义名称代码
	 * eg:
	 * 	genCode("gen_test_demo", "IDemo");  gen_test_demo ==> IDemo
	 * @param tableName 表名, 只能单表
	 */
	public void genCodeWithCustomName(boolean reBuildController, boolean reBuildService, boolean reBuildServiceImpl, boolean reBuildServiceMock
			, boolean reBuildModelAndMapperAndMapperXML,String tableName, String customModelName) {
		genCodeByTableName(reBuildController,reBuildService,reBuildServiceImpl,reBuildServiceMock
				,tableName, customModelName, false);
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

	public static void main(String[] args) {
		CodeGeneratorManager cm = new CodeGeneratorManager();
		String hcs_base_tet = cm.getDefModelName("hcs_base_tet");
		System.out.println(hcs_base_tet);
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
									String ...tableNames) {
		for (String tableName : tableNames) {
			genCodeByTableName(reBuildController,reBuildService,reBuildServiceImpl,reBuildServiceMock
					,tableName, null, flag);
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
									boolean flag) {
		String sign = getSign(tableName);
		if (flag) {
			/*
			* 刘春春修改：实体名即为表名驼峰格式
			* */
			modelName = tableNameConvertUpperCamel(tableName);
		}
		new ModelAndMapperGenerator().genCode(tableName, modelName, sign);
		new ServiceGenerator().genCode(tableName, modelName, sign,reBuildService,reBuildServiceImpl,reBuildServiceMock);
		new ControllerGenerator().genCode(tableName, modelName, sign,reBuildController);
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
			upperModelName = StringUtils.toUpperCaseFirstOne(modelName);
//			modelName = getDefModelName(tableName);
		}
		if (reMoveModelAndMapperAndMapperXML) {
			System.out.println("删除文件：" + PROJECT_PATH + PACKAGE_PATH_MODEL + MODEL_PACKAGE.replace(".", "/") + "/" + upperModelName + ".java");
			delFile(PROJECT_PATH + PACKAGE_PATH_MODEL + MODEL_PACKAGE.replace(".", "/") + "/" + upperModelName + ".java");

			System.out.println("删除文件：" + PROJECT_PATH + PACKAGE_PATH_MAPPER + MAPPER_PACKAGE.replace(".", "/") + "/" + upperModelName + "Mapper.java");
			delFile(PROJECT_PATH + PACKAGE_PATH_MAPPER + MAPPER_PACKAGE.replace(".", "/") + "/" + upperModelName + "Mapper.java");

			System.out.println("删除文件：" + PROJECT_PATH + RESOURCES_PATH + "/" + MAPPER_PACKAGE.replace(".", "/") + "/" + upperModelName + "Mapper.xml");
			delFile(PROJECT_PATH + RESOURCES_PATH + "/" + MAPPER_PACKAGE.replace(".", "/") + "/" + upperModelName + "Mapper.xml");
		}
		if(reMoveService) {
			System.out.println("删除文件：" + PROJECT_PATH + PACKAGE_PATH_SERVICE + upperModelName + "Service.java");
			delFile(PROJECT_PATH + PACKAGE_PATH_SERVICE + upperModelName + "Service.java");
		}
		if(reMoveServiceImpl) {
			System.out.println("删除文件：" + PROJECT_PATH + PACKAGE_PATH_SERVICE_IMPL + upperModelName + "ServiceImpl.java");
			delFile(PROJECT_PATH + PACKAGE_PATH_SERVICE_IMPL + upperModelName + "ServiceImpl.java");
		}
		if(reMoveServiceMock) {
			System.out.println("删除文件：" + PROJECT_PATH + PACKAGE_PATH_SERVICE + upperModelName + "ServiceMock.java");
			delFile(PROJECT_PATH + PACKAGE_PATH_SERVICE + upperModelName + "ServiceMock.java");
		}
		if(reMoveController){
			System.out.println("删除文件："+PROJECT_PATH+PACKAGE_PATH_CONTROLLER+upperModelName+"Controller.java");
			delFile(PROJECT_PATH+PACKAGE_PATH_CONTROLLER+upperModelName+"Controller.java");
		}

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
		PluginConfiguration equalsHashCodePlugin = new PluginConfiguration();
		equalsHashCodePlugin.setConfigurationType("org.mybatis.generator.plugins.EqualsHashCodePlugin");
		context.addPluginConfiguration(equalsHashCodePlugin);

		PluginConfiguration mapperPlugin = new PluginConfiguration();
		mapperPlugin.setConfigurationType("com.codegen.util.MapperPlugin");
		mapperPlugin.addProperty("targetProject",PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_BASE_MAPPER);
		mapperPlugin.addProperty("targetPackage",MAPPER_INTERFACE_REFERENCE);

		context.addPluginConfiguration(mapperPlugin);

		// 创建 Service 接口
		File modelFile = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_BASE_MAPPER+"/liuchunchun.txt");
		// 查看父级目录是否存在, 不存在则创建
		if (!modelFile.getParentFile().exists()) {
			modelFile.getParentFile().mkdirs();
		}

		PluginConfiguration pluginConfiguration = new PluginConfiguration();
		pluginConfiguration.setConfigurationType("com.codegen.util.SerializablePlugin");
		pluginConfiguration.addProperty("suppressJavaInterface", "false");
		context.addPluginConfiguration(pluginConfiguration);
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
		RESOURCES_PATH = prop.getProperty("resources.path");
		MODEL_PATH = prop.getProperty("model.path");
		MAPPER_PATH = prop.getProperty("mapper.path");
		TEMPLATE_FILE_PATH = PROJECT_PATH + prop.getProperty("template.file.path");

		BASE_PACKAGE = prop.getProperty("base.package");
		MODEL_PACKAGE = prop.getProperty("model.package");
		MAPPER_PACKAGE = prop.getProperty("mapper.package");
		BASE_MAPPER_PACKAGE = prop.getProperty("base.mapper.package");
		SERVICE_PACKAGE = prop.getProperty("service.package");
		SERVICE_IMPL_PACKAGE = prop.getProperty("service.impl.package");
		CONTROLLER_PACKAGE = prop.getProperty("controller.package");

		MAPPER_INTERFACE_REFERENCE = prop.getProperty("mapper.interface.reference");
		SERVICE_INTERFACE_REFERENCE = prop.getProperty("service.interface.reference");
		ABSTRACT_SERVICE_CLASS_REFERENCE = prop.getProperty("abstract.service.class.reference");

		String servicePackage = prop.getProperty("package.path.service");
		String modelPackage = prop.getProperty("package.path.model");
		String mapperPackage = prop.getProperty("package.path.mapper");
		String baseMapperPackage = prop.getProperty("package.path.base.mapper");
		String serviceImplPackage = prop.getProperty("package.path.service.impl");
		String controllerPackage = prop.getProperty("package.path.controller");

		PACKAGE_PATH_SERVICE = "".equals(servicePackage) ? packageConvertPath(SERVICE_PACKAGE) : servicePackage;
		PACKAGE_PATH_MODEL = "".equals(modelPackage) ? packageConvertPath(MODEL_PACKAGE) : modelPackage;
		PACKAGE_PATH_MAPPER = "".equals(mapperPackage) ? packageConvertPath(MAPPER_PACKAGE) : mapperPackage;
		PACKAGE_PATH_BASE_MAPPER = "".equals(baseMapperPackage) ? packageConvertPath(BASE_MAPPER_PACKAGE) : baseMapperPackage;
		PACKAGE_PATH_SERVICE_IMPL = "".equals(serviceImplPackage) ? packageConvertPath(SERVICE_IMPL_PACKAGE) : serviceImplPackage;
		PACKAGE_PATH_CONTROLLER = "".equals(controllerPackage) ? packageConvertPath(CONTROLLER_PACKAGE) : controllerPackage;

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
