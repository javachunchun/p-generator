package com.codegen.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mybatis.generator.api.dom.OutputUtilities.newLine;

/**
 * Created by liuchunchun on 2018/12/6.
 */
public class MyShellCallback extends DefaultShellCallback {

    public MyShellCallback(boolean overwrite) {
        super(overwrite);
    }

    @Override
    public boolean isOverwriteEnabled() {
        return true;
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    @Override
    public String mergeJavaFile(String newFileSource, File existingFile, String[] javadocTags, String fileEncoding) throws ShellException {
        CompilationUnit newCompilationUnit = JavaParser.parse(newFileSource);
        CompilationUnit existingCompilationUnit = null;
        try {
            existingCompilationUnit = JavaParser.parse(existingFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return mergerFile(newCompilationUnit, existingCompilationUnit);
    }

    public String mergerFile(CompilationUnit newCompilationUnit, CompilationUnit existingCompilationUnit) {

        System.out.println("合并java代码");
        StringBuilder sb = new StringBuilder(newCompilationUnit.getPackageDeclaration().get().toString());
        newCompilationUnit.removePackageDeclaration();

        //合并imports
        NodeList<ImportDeclaration> imports = newCompilationUnit.getImports();
        imports.addAll(existingCompilationUnit.getImports());
        Set importSet = new HashSet<ImportDeclaration>();
        importSet.addAll(imports);

        NodeList<ImportDeclaration> newImports = new NodeList<ImportDeclaration>();
        newImports.addAll(importSet);
        newCompilationUnit.setImports(newImports);
        for (ImportDeclaration i : newCompilationUnit.getImports()) {
            sb.append(i.toString());
        }
        newLine(sb);
        NodeList<TypeDeclaration<?>> types = newCompilationUnit.getTypes();
        NodeList<TypeDeclaration<?>> oldTypes = existingCompilationUnit.getTypes();

        for (int i = 0; i < types.size(); i++) {
            //截取Class
            String classNameInfo = types.get(i).toString().substring(0, types.get(i).toString().indexOf("{") + 1);
            sb.append(classNameInfo);
            newLine(sb);
            newLine(sb);
            //合并fields
            List<FieldDeclaration> fields = types.get(i).getFields();
            List<FieldDeclaration> oldFields = oldTypes.get(i).getFields();
            List<FieldDeclaration> newFields = new ArrayList<FieldDeclaration>();
            List<FieldDeclaration> fieldsModifiable = new ArrayList<>(fields);
            List<FieldDeclaration> oldFieldsModifiable = new ArrayList<>(oldFields);
            fieldsModifiable.removeAll(oldFieldsModifiable);
            newFields.addAll(oldFields);
            newFields.addAll(fieldsModifiable);
            for (FieldDeclaration f : newFields) {
                sb.append("\t" + f.toString().replaceAll("\r\n", "\r\n\t"));
                newLine(sb);
                newLine(sb);
            }

            //合并methods
            List<MethodDeclaration> methods = types.get(i).getMethods();
            List<MethodDeclaration> existingMethods = oldTypes.get(i).getMethods();

            for (MethodDeclaration f : methods) {
                String res = f.toString().replaceAll("\r\n", "\r\n\t");
                sb.append("\t" + res);
                newLine(sb);
                newLine(sb);
            }

            List<String> methodList = new ArrayList<String>();
            for (MethodDeclaration m : methods) {
                methodList.add(m.getName().toString());
            }
            methodList.add("toString");
            methodList.add("hashCode");
            methodList.add("equals");

            for (MethodDeclaration m : existingMethods) {
                if (methodList.contains(m.getName().toString())) {
                    continue;
                }

                boolean flag = true;
                for (String tag : MergeConstants.OLD_ELEMENT_TAGS) {
                    if (m.toString().contains(tag)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    String res = m.toString().replaceAll("\r\n", "\r\n\t");
                    sb.append("\t" + res);
                    newLine(sb);
                    newLine(sb);
                }
            }

            //判断是否有内部类
            types.get(i).getChildNodes();
            for (Node n : types.get(i).getChildNodes()) {
                if (n.toString().contains("static class")) {
                    String res = n.toString().replaceAll("\r\n", "\r\n\t");
                    sb.append("\t" + res);
                }
            }

        }

        return sb.append(System.getProperty("line.separator") + "}").toString();
    }

}
