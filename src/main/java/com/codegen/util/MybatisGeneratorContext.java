package com.codegen.util;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.codegen.ibatis2.IntrospectedTableIbatis2Java2Impl;

public class MybatisGeneratorContext {
    private IntrospectedTable introspectedTable;

    private static MybatisGeneratorContext sl;
    private MybatisGeneratorContext(){
       System.out.println("");
    }
 
    public static MybatisGeneratorContext getInstance(){
    if(sl==null)
         sl=new MybatisGeneratorContext();
     return sl;
    }

    public IntrospectedTable getIntrospectedTable() {
        return introspectedTable;
    }

    public void setIntrospectedTable(IntrospectedTable introspectedTable) {
        this.introspectedTable = introspectedTable;
    }

    public static void main(String[] args){
        MybatisGeneratorContext sl= MybatisGeneratorContext.getInstance();
        sl.setIntrospectedTable(new IntrospectedTableIbatis2Java2Impl());
        MybatisGeneratorContext sl2= MybatisGeneratorContext.getInstance();

        IntrospectedTable introspectedTable = sl2.getIntrospectedTable();

        System.out.println(sl.getIntrospectedTable()+"=="+introspectedTable);
    }
}