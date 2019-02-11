package com.codegen.main;

import com.codegen.service.CodeGeneratorManager;

/**
 * 代码生成器启动项
 * Created by liuchunchun on 2018/10/20.
 */
public class CodeGeneratorMain {
	private static final String TABLE = "eee";
	private static final boolean reBuildController = true;
	private static final boolean reBuildService = true;
	private static final boolean reBuildServiceImpl = true;
	private static final boolean reBuildServiceMock = true;

	public static void main(String[] args) {
		CodeGeneratorManager cgm = new CodeGeneratorManager();
		//生成代码入口
		cgm.genCodeWithSimpleName(reBuildController, reBuildService, reBuildServiceImpl, reBuildServiceMock
				, TABLE);
	}
}
