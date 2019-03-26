package com.codegen.service.impl;

import com.codegen.service.CodeGenerator;
import com.codegen.service.CodeGeneratorManager;
import com.codegen.util.MybatisGeneratorContext;
import com.codegen.util.StringUtils;
import freemarker.template.Configuration;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service层 代码生成器
 * Created by liuchunchun on 2019/02/14.
 */
public class ServiceGenerator extends CodeGeneratorManager implements CodeGenerator {

	public void genCode(String tableName, String modelName, String sign, boolean reBuildService, boolean reBuildServiceImpl, boolean reBuildServiceMock) {
		Configuration cfg = getFreemarkerConfiguration();
		/*
		* 刘春春修改：暂时不用表名作为报名判断
		* */
//		String customMapping = "/" + sign + "/";
		String modelNameUpperCamel = StringUtils.isNullOrEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
		
		Map<String, Object> data = getDataMapInit(modelName, sign, modelNameUpperCamel);
		try {
			if(reBuildService) {
				// 创建 Service 接口
				File serviceFile = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_SERVICE + /*customMapping
					+*/ modelNameUpperCamel + "Service.java");
				// 查看父级目录是否存在, 不存在则创建
				if (!serviceFile.getParentFile().exists()) {
					serviceFile.getParentFile().mkdirs();
				}
				cfg.getTemplate("service.ftl").process(data, new FileWriter(serviceFile));
				logger.info(modelNameUpperCamel + "Service.java 生成成功!");

			}
			if(reBuildServiceMock) {
				// 创建 ServiceMock 降级实现
				File serviceMockFile = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_SERVICE + /*customMapping
					+*/ modelNameUpperCamel + "ServiceMock.java");
				// 查看父级目录是否存在, 不存在则创建
				if (!serviceMockFile.getParentFile().exists()) {
					serviceMockFile.getParentFile().mkdirs();
				}
				cfg.getTemplate("service-mock.ftl").process(data, new FileWriter(serviceMockFile));
				logger.info(modelNameUpperCamel + "ServiceMock.java 生成成功!");
			}
			if(reBuildServiceImpl) {
				// 创建 Service 接口的实现类
				File serviceImplFile = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_SERVICE_IMPL + /*customMapping
					+*/ modelNameUpperCamel + "ServiceImpl.java");
				// 查看父级目录是否存在, 不存在则创建
				if (!serviceImplFile.getParentFile().exists()) {
					serviceImplFile.getParentFile().mkdirs();
				}
				cfg.getTemplate("service-impl.ftl").process(data, new FileWriter(serviceImplFile));
				logger.info(modelNameUpperCamel + "ServiceImpl.java 生成成功!");
			}
		} catch (Exception e) {
			throw new RuntimeException("Service 生成失败!", e);
		}
	}
	
	/**
	 * 预置页面所需数据
	 * @param modelName 自定义实体类名, 为null则默认将表名下划线转成大驼峰形式
	 * @param sign 区分字段, 规定如表 gen_test_demo, 则 test 即为区分字段
	 * @param modelNameUpperCamel 首字为大写的实体类名
	 * @return
	 */
	private Map<String, Object> getDataMapInit(String modelName, String sign, String modelNameUpperCamel) {
		Map<String, Object> data = new HashMap<>();
		data.put("date", DATE);
		data.put("author", AUTHOR);
		data.put("sign", sign);
		data.put("modelNameUpperCamel", modelNameUpperCamel);
		data.put("modelNameLowerCamel", StringUtils.toLowerCaseFirstOne(modelNameUpperCamel));
		data.put("basePackage", BASE_PACKAGE);
		data.put("modelPackage", MODEL_PACKAGE);
		data.put("mapperPackage", MAPPER_PACKAGE);
		data.put("serviceImplPackage", SERVICE_IMPL_PACKAGE);
		data.put("servicePackage", SERVICE_PACKAGE);

		MybatisGeneratorContext instance = MybatisGeneratorContext.getInstance();
		System.out.println("========================================="+instance);
		IntrospectedTable introspectedTable = instance.getIntrospectedTable();
		IntrospectedColumn introspectedColumn = introspectedTable.getPrimaryKeyColumns().get(0);
		String shortName = introspectedColumn.getFullyQualifiedJavaType().getShortName();
		String javaProperty = introspectedColumn.getJavaProperty();

		String orgUpper = "";

		List<String> codeList = new ArrayList<>();
		List<String> upCdNaList = new ArrayList<>();
		List<IntrospectedColumn> nonPrimaryKeyColumns = introspectedTable.getNonPrimaryKeyColumns();
		for (IntrospectedColumn nonPrimaryKeyColumn : nonPrimaryKeyColumns) {
			String classType = nonPrimaryKeyColumn.getFullyQualifiedJavaType().getShortName();
			String property = nonPrimaryKeyColumn.getJavaProperty();

			if ("String".equals(classType)) {
				if (property != null) {
					if (property.startsWith("cd") || property.startsWith("code") || property.endsWith("code")
							|| property.startsWith("na") || property.startsWith("name") || property.endsWith("na")
							|| property.endsWith("name")) {
						String code = StringUtils.toLowerCaseFirstOne(modelNameUpperCamel)+".set"+
								StringUtils.toUpperCaseFirstOne(property)+"(requestVO.getCd());";
						String up_cd_na = StringUtils.toUpperCaseFirstOne(property);

						upCdNaList.add(up_cd_na);
						codeList.add(code);
					} else if (property.endsWith("Org")) {
						orgUpper = StringUtils.toUpperCaseFirstOne(property);
					}
				}
			}
		}
		data.put("orgUpper", orgUpper);
		data.put("codeList", codeList);
		data.put("upCdNaList", upCdNaList);
		data.put("primaryKeyType", shortName);
		data.put("primaryKeyName", javaProperty);
		data.put("primaryKeyNameUpperFirst", StringUtils.toUpperCaseFirstOne(javaProperty));


		return data;
	}

	@Override
	public void genCode(String tableName, String modelName, String sign) {

	}
}
