package com.codegen.service;

/**
 * 配置信息变量
 * Created by zhh on 2017/09/27.
 */
public class CodeGeneratorConfig {
	// JDBC 相关配置信息
	protected static String JDBC_URL;
	protected static String JDBC_USERNAME;
	protected static String JDBC_PASSWORD;
	protected static String JDBC_DRIVER_CLASS_NAME;
	protected static String PROJECT_NAME;
	
	// 项目在硬盘上的基础路径
	protected static final String PROJECT_PATH = System.getProperty("user.dir")+"/../"+ "p-generator";

	// java文件路径
	protected static String JAVA_PATH;
	// 资源文件路径
	protected static String RESOURCES_PATH;
	/*
	* 刘春春修改：增加实体类文件路径和xml文件路径
	* */
	// 实体类文件路径
	protected static String MODEL_PATH;
	// mapper xml文件路径
	protected static String MAPPER_PATH;
	// 模板存放位置
	protected static String TEMPLATE_FILE_PATH;
	
	// 项目基础包
	protected static String BASE_PACKAGE;
	// 项目 Model 所在包
	protected static String MODEL_PACKAGE;
	// 项目 Mapper 所在包
	protected static String MAPPER_PACKAGE;
	//增强 Mapper所在包
	protected static String BASE_MAPPER_PACKAGE;
	// 项目 Service 所在包
	protected static String SERVICE_PACKAGE;
	// 项目 Service 实现类所在包
	protected static String SERVICE_IMPL_PACKAGE;
	// 项目 Controller 所在包
	protected static String CONTROLLER_PACKAGE;
	
	// 生成的 Service 存放路径
	protected static String PACKAGE_PATH_SERVICE;

	protected static String PACKAGE_PATH_MODEL;

	protected static String PACKAGE_PATH_MAPPER;

	protected static String PACKAGE_PATH_BASE_MAPPER;
	// 生成的 Service 实现存放路径
	protected static String PACKAGE_PATH_SERVICE_IMPL;
	// 生成的 Controller 存放路径
	protected static String PACKAGE_PATH_CONTROLLER;
	
	// MyMapper 插件基础接口的完全限定名
	protected static String MAPPER_INTERFACE_REFERENCE;
	// 通用 Service 层 基础接口完全限定名
	protected static String SERVICE_INTERFACE_REFERENCE;
	// 基于通用 MyBatis Mapper 插件的 Service 接口的实现
	protected static String ABSTRACT_SERVICE_CLASS_REFERENCE;
	
	// 模板注释中 @author
	protected static String AUTHOR;
	// 模板注释中 @date
	protected static String DATE;
	
}
