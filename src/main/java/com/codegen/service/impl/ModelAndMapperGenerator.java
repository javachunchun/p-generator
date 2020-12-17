package com.codegen.service.impl;

import com.codegen.service.CodeGenerator;
import com.codegen.service.CodeGeneratorManager;
import com.codegen.util.MyShellCallback;
import com.codegen.util.MybatisGeneratorContext;
import com.codegen.util.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.TableConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Model & Mapper 代码生成器
 * Created by liuchunchun on 2019/02/14.
 */
public class ModelAndMapperGenerator extends CodeGeneratorManager implements CodeGenerator {

	public void genCode(String tableName, String modelName, String sign) {

		Context initConfig = initConfig(tableName, modelName, sign);
		List<String> warnings = null;
		MyBatisGenerator generator = null;
		try {
			Configuration cfg = new Configuration();
			cfg.addContext(initConfig);
			cfg.validate();
			
			MyShellCallback callback = new MyShellCallback(true);
			warnings = new ArrayList<String>();
			generator = new MyBatisGenerator(cfg, callback, warnings);
			generator.generate(null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Model 和  Mapper 生成失败!", e);
		}
		
		if (generator == null || generator.getGeneratedJavaFiles().isEmpty() || generator.getGeneratedXmlFiles().isEmpty()) {
			throw new RuntimeException("Model 和  Mapper 生成失败, warnings: " + warnings);
		}
		
		if (StringUtils.isNullOrEmpty(modelName)) {
			modelName = tableNameConvertUpperCamel(tableName);
		}
		
		logger.info(modelName, "{}.java 生成成功!");
		logger.info(modelName, "{}Dao.java 生成成功!");
		logger.info(modelName, "{}Dao.xml 生成成功!");
	}
	
	/**
	 * 完善初始化环境
	 * @param tableName 表名
	 * @param modelName 自定义实体类名, 为null则默认将表名下划线转成大驼峰形式
	 * @param sign 区分字段, 规定如表 gen_test_demo, 则 test 即为区分字段
	 */
	private Context initConfig(String tableName, String modelName, String sign) {
		Context context = null;
		try {
			context = initMybatisGeneratorContext(sign);
			TableConfiguration tableConfiguration = new TableConfiguration(context);
			tableConfiguration.setSelectByPrimaryKeyStatementEnabled(false);
			tableConfiguration.setUpdateByPrimaryKeyStatementEnabled(false);
			tableConfiguration.setDeleteByPrimaryKeyStatementEnabled(false);
			tableConfiguration.setInsertStatementEnabled(false);
	        tableConfiguration.setTableName(tableName);
	        tableConfiguration.setDomainObjectName(modelName);
	        tableConfiguration.setGeneratedKey(new GeneratedKey("id", "Mysql", true, null));
	        context.addTableConfiguration(tableConfiguration);
		} catch (Exception e) {
			throw new RuntimeException("ModelAndMapperGenerator 初始化环境异常!", e);
		}
		return context;
	}
}
