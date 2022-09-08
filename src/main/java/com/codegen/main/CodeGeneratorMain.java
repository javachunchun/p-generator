package com.codegen.main;

import com.codegen.service.CodeGeneratorManager;

/**
 * 代码生成器启动项
 * Created by liuchunchun on 2018/10/20.
 */
public class CodeGeneratorMain {
	private static final String TABLE = "device";
	public static final String ALIAS = "Device";
	public static final String APP_NAME = "设备TVM信息";
	private static final boolean reBuildController = false;
	private static final boolean reBuildService = true;
	private static final boolean reBuildServiceImpl = true;
	private static final boolean reBuildServiceMock = false;

	public static void main(String[] args) {
		CodeGeneratorManager cgm = new CodeGeneratorManager();

		cgm.removeCodeWithSimpleName(reBuildController,
				reBuildService,
				reBuildServiceImpl,
				reBuildServiceMock,
				true,
				TABLE);
		//生成代码入口
		cgm.genCodeWithDetailName(reBuildController,
				reBuildService,
				reBuildServiceImpl,
				reBuildServiceMock,
				ALIAS,
				APP_NAME,
				TABLE);
	}
}
