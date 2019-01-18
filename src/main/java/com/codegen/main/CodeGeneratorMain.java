package com.codegen.main;

import com.codegen.service.CodeGeneratorManager;

/**
 * 代码生成器启动项
 * Created by liuchunchun on 2018/10/20.
 */
public class CodeGeneratorMain {
	private static final String TABLE = "hcs_ci_prosofmod";
	//是否进行批量删除文件，若是生成文件，请修改为true
	private static final boolean toRemove = false;
	private static final String MODEL_NAME = "ITest";
	/*private static final String[] TABLES = {
			"hcs_base_use","hcs_base_tet","hcs_base_soft","hcs_base_rouse","hcs_base_rofu","hcs_base_ro"
	};*/
	//项目路径，必须填写，为保证所有使用自动生成项目的相对路径保持不变
	public static final String PROJECT_NAME = "p-generator";
	/*
	 * TODO 是否重构一下项目：合并代码只能合并至mapper以及model层面
	 * TODO 若编写完controller层和service层且不想删除，请将reBuildController和reBuildServiceImpl设置为false
	 **/
	private static final boolean reBuildController = false;
	private static final boolean reBuildService = false;
	private static final boolean reBuildServiceImpl = false;
	private static final boolean reBuildServiceMock = false;
	//不可更改！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
	private static final boolean reBuildModelAndMapperAndMapperXML = true;
	public static void main(String[] args) {
		if (!reBuildModelAndMapperAndMapperXML) {
			return;
		}
		CodeGeneratorManager cgm = new CodeGeneratorManager();

		if(!toRemove) {
			//生成代码入口
			cgm.genCodeWithSimpleName(reBuildController, reBuildService, reBuildServiceImpl, reBuildServiceMock
					, reBuildModelAndMapperAndMapperXML, TABLE);
		}else {

		/*
		* 删除已生成代码，开关与生成代码逻辑保持一致
		* */
			cgm.removeCodeWithSimpleName(reBuildController, reBuildService, reBuildServiceImpl, reBuildServiceMock
					, reBuildModelAndMapperAndMapperXML, TABLE);
		}
		
//		cgm.genCodeWithDetailName(reBuildController,reBuildService,reBuildServiceImpl,reBuildServiceMock
//				,reBuildModelAndMapperAndMapperXML,TABLES);
		
//		cgm.genCodeWithCustomName(reBuildController,reBuildService,reBuildServiceImpl,reBuildServiceMock
//				,reBuildModelAndMapperAndMapperXML,TABLE, MODEL_NAME);
	}
}
