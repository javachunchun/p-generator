package com.codegen.service.impl;

import com.codegen.service.CodeGenerator;
import com.codegen.service.CodeGeneratorManager;
import com.codegen.util.MybatisGeneratorContext;
import com.codegen.util.StringUtils;
import com.google.common.base.CaseFormat;
import freemarker.template.Configuration;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller层 代码生成器
 * Created by liuchunchun on 2019/02/14.
 */
public class ControllerGenerator extends CodeGeneratorManager implements CodeGenerator {

	public void genCode(String appAlias, String tableName, String modelName, String sign, boolean reBuildController) {
		Configuration cfg = getFreemarkerConfiguration();
		/*
		 * 刘春春修改：暂时不用表名做路径判断
		 * */
//		String customMapping = "/" + sign + "/";
		String modelNameUpperCamel = StringUtils.isNullOrEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;

		Map<String, Object> data = getDataMapInit(appAlias, tableName, modelName, sign, modelNameUpperCamel);
		try {
			if (reBuildController) {
				File controllerFile = new File(PROJECT_PATH + PACKAGE_PATH + CONTROLLER_PACKAGE.replace(".", "/") +
						"/" + modelNameUpperCamel + "Controller.java");
				if (!controllerFile.getParentFile().exists()) {
					controllerFile.getParentFile().mkdirs();
				}
				cfg.getTemplate("controller.ftl").process(data, new FileWriter(controllerFile));
				logger.info(modelNameUpperCamel + "Controller.java 生成成功!");
			}
		} catch (Exception e) {
			throw new RuntimeException("Controller 生成失败!", e);
		}
	}
	
	/**
	 * 预置页面所需数据
	 *
	 * @param appAlias
	 * @param tableName 表名
	 * @param modelName 自定义实体类名, 为null则默认将表名下划线转成大驼峰形式
	 * @param sign 区分字段, 规定如表 gen_test_demo, 则 test 即为区分字段
	 * @param modelNameUpperCamel 首字为大写的实体类名
	 * @return
	 */
	private Map<String, Object> getDataMapInit(String appAlias, String tableName, String modelName, String sign, String modelNameUpperCamel) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("date", DATE);
        data.put("author", AUTHOR);
        data.put("sign", sign);
		String[] split = CONTROLLER_PACKAGE.split("\\.");
		String leafRequest = split[split.length - 1];
		String baseRequestMapping = "/pf/"+leafRequest+"/"+StringUtils.toLowerCaseFirstOne(modelNameUpperCamel)+"/";
		data.put("baseRequestMapping", baseRequestMapping);
		data.put("modelName", modelName);
        data.put("modelNameUpperCamel", modelNameUpperCamel);
        data.put("modelNameLowerCamel", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelNameUpperCamel));
//        data.put("basePackage", BASE_PACKAGE);
        data.put("controllerPackage", CONTROLLER_PACKAGE);
		data.put("controllerMapping", tableName.replace("_", "-"));
		data.put("modelPackage", MODEL_PACKAGE);
		data.put("mapperPackage", MAPPER_PACKAGE);
		data.put("servicePackage", SERVICE_PACKAGE);

//		获取全局变量
		MybatisGeneratorContext instance = MybatisGeneratorContext.getInstance();
		IntrospectedTable introspectedTable = instance.getIntrospectedTable();
		String remarks = introspectedTable.getRemarks();//表注释
		data.put("modelAlias", StringUtils.isNullOrEmpty(appAlias) ? remarks : appAlias);
		IntrospectedColumn introspectedColumn = introspectedTable.getPrimaryKeyColumns().get(0);
		String shortName = introspectedColumn.getFullyQualifiedJavaType().getShortName();
		String javaProperty = introspectedColumn.getJavaProperty();
		List<IntrospectedColumn> nonPrimaryKeyColumns = introspectedTable.getNonPrimaryKeyColumns();
		String selectName = "";
		for (IntrospectedColumn nonPrimaryKeyColumn : nonPrimaryKeyColumns) {
			String naProperty = nonPrimaryKeyColumn.getJavaProperty();
			if (naProperty.indexOf("na") != -1) {
				selectName = naProperty;
			}
		}

		data.put("nonPrimaryKeyColumns", nonPrimaryKeyColumns);
		data.put("primaryKeyType", shortName);
		String[] split1 = StringUtils.camelCase2UnderScoreCase(modelNameUpperCamel).split("_");
		String leafName = split1[split1.length - 1];
		leafName = StringUtils.toUpperCaseFirstOne(leafName);
		data.put("primaryKeyName", javaProperty);
		data.put("leafName", leafName);
		data.put("selectName", selectName);



		return data;
	}

	@Override
	public void genCode(String appAlias, String tableName, String modelName, String sign) {
	}
}
