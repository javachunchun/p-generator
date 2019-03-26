package com.codegen.main;

import com.codegen.service.CodeGeneratorManager;

/**
 * @program: p-pub
 * @description: 删除代码
 * @author: liuchunchun
 * @create: 2019-01-26 13:43
 **/
public class CodeDeleteMain {
    private static final String TABLE = "eee";

    private static final boolean reMoveController = true;
    private static final boolean reMoveService = true;
    private static final boolean reMoveServiceImpl = true;
    private static final boolean reMoveServiceMock = true;
    //不可更改！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
    private static final boolean reMoveModelAndMapperAndMapperXML = true;


    public static void main(String[] args) {
        CodeGeneratorManager cgm = new CodeGeneratorManager();

        cgm.removeCodeWithSimpleName(reMoveController, reMoveService, reMoveServiceImpl, reMoveServiceMock
                , reMoveModelAndMapperAndMapperXML, TABLE);
    }
}
