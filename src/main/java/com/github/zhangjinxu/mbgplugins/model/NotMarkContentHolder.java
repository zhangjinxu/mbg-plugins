package com.github.zhangjinxu.mbgplugins.model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ReferenceType;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.MergeConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotMarkContentHolder {

    private File javaFile;

    private List<ImportDeclaration> imports;

    private List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarations;

    private List<FieldDeclaration> fields;

    private List<MethodDeclaration> methods;

    private List<EnumDeclaration> enums;

    private List<ConstructorDeclaration> constructors;

    public NotMarkContentHolder(File javaFile) {
        this.javaFile = javaFile;
        try {
            getNotMarkContent();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public NotMarkContentHolder(String filePath) {
        this(new File(filePath));
    }

    private void getNotMarkContent() throws FileNotFoundException {
        if (javaFile == null || !javaFile.exists() || !javaFile.isFile()) {
            return;
        }
        CompilationUnit unit = JavaParser.parse(javaFile);

        imports = unit.getImports();

        fields = getNotMarkContentByType(unit, FieldDeclaration.class);

        classOrInterfaceDeclarations = getNotMarkContentByType(unit, ClassOrInterfaceDeclaration.class);

        methods = getNotMarkContentByType(unit, MethodDeclaration.class);

        enums = getNotMarkContentByType(unit, EnumDeclaration.class);

        constructors = getNotMarkContentByType(unit, ConstructorDeclaration.class);

    }


    private <T extends Node> List<T> getNotMarkContentByType(CompilationUnit unit, Class<T> nodeType) {
        List<T> all = unit.findAll(nodeType);
        List<T> filterList = new ArrayList<>();
        for (T t : all) {
            if (!t.toString().contains(MergeConstants.NEW_ELEMENT_TAG)) {
                filterList.add(t);
            }
        }
        return filterList;
    }

    public void addNotMarkContent(org.mybatis.generator.api.dom.java.CompilationUnit unit) {
        if (unit instanceof Interface) {
            addNotMarkContent((Interface) unit);
        }

        if (unit instanceof TopLevelClass) {
            addNotMarkContent((TopLevelClass) unit);
        }

    }

    private void addNotMarkContent(TopLevelClass clazz) {
        addImport(clazz);

        System.out.println();

        addConstructor(clazz);

        addField(clazz);

        addMethod(clazz);

        addClassOrInterface(clazz);

    }

    private void addNotMarkContent(Interface interfaze) {
        addImport(interfaze);

        addField(interfaze);

        addMethod(interfaze);

        addClassOrInterface(interfaze);
    }

    private void addClassOrInterface(TopLevelClass clazz) {
        if (classOrInterfaceDeclarations == null || classOrInterfaceDeclarations.isEmpty()) {
            return;
        }
        for (ClassOrInterfaceDeclaration c : classOrInterfaceDeclarations) {
//            clazz.addInnerClass(transfer(c));
        }
    }

    private void addField(TopLevelClass clazz) {
        if (fields == null || fields.isEmpty()) {
            return;
        }
        for (FieldDeclaration field : fields) {
            clazz.addField(transfer(field));
        }
    }


    private void addConstructor(TopLevelClass clazz) {
        if (constructors == null || constructors.isEmpty()) {
            return;
        }
        for (ConstructorDeclaration constructor : constructors) {
            clazz.addMethod(transfer(constructor));
        }
    }


    private void addMethod(TopLevelClass clazz) {
        if (methods == null || methods.isEmpty()) {
            return;
        }
        for (MethodDeclaration method : methods) {
            clazz.addMethod(transfer(method));
        }
    }
    private void addClassOrInterface(Interface interfaze) {
        //todo 接口增加内部类或内部接口???
        /*for (ClassOrInterfaceDeclaration c : classOrInterfaceDeclarations) {
            interfaze.addInnerInterfaces(transfer(c));
        }*/
    }


    private void addField(Interface interfaze) {
        if (fields == null || fields.isEmpty()) {
            return;
        }
        for (FieldDeclaration field : fields) {
            interfaze.addField(transfer(field));
        }
    }

    private void addMethod(Interface interfaze) {
        if (methods == null || methods.isEmpty()) {
            return;
        }
        for (MethodDeclaration method : methods) {
            interfaze.addMethod(transfer(method));
        }
    }

    private Method transfer(ConstructorDeclaration method) {
        Method m = new Method();
        m.setDefault(false);
        m.setStatic(false);
        m.setFinal(method.isFinal());
        m.setConstructor(true);
        m.setName(method.getNameAsString());

        if (method.isPrivate()) {
            m.setVisibility(JavaVisibility.PRIVATE);
        } else if (!method.isPublic()) {
            if (method.isProtected()) {
                m.setVisibility(JavaVisibility.PROTECTED);
            }
        } else {
            m.setVisibility(JavaVisibility.PUBLIC);
        }
        //todo 访问权限修饰符为默认

        //增加方法体
        BlockStmt bodys = method.getBody();
        NodeList<Statement> statements = bodys.getStatements();
        if (statements.isEmpty()) {
            m.addBodyLine("");
        }
        for (Statement statement : statements) {
            String body = statement.toString();
            StringBuilder sb = new StringBuilder(body);
            if (sb.length() > 0) {
                String[] bodyLines = sb.toString().split(System.getProperty("line.separator"));
                for (String bodyLine : bodyLines) {
                    m.addBodyLine(bodyLine.trim());
                }
            }
        }

        //增加参数
        NodeList<Parameter> parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            org.mybatis.generator.api.dom.java.Parameter p =
                    new org.mybatis.generator.api.dom.java.Parameter(
                            new FullyQualifiedJavaType(parameter.getTypeAsString()),
                            parameter.getNameAsString(),
                            parameter.getAnnotations().toString()
                                    .replace("[","")
                                    .replace("]","")
                                    .replace(","," "),
                            parameter.isVarArgs());
            m.addParameter(p);
        }
        //增加异常
        NodeList<ReferenceType> exceptions = method.getThrownExceptions();
        for (ReferenceType exception : exceptions) {
            m.addException(new FullyQualifiedJavaType(exception.toString()));
        }

        //增加注释
        //todo 行注释
        addJavaDoc(method, m);


        //增加注解
        NodeList<AnnotationExpr> annotations = method.getAnnotations();
        for (AnnotationExpr annotation : annotations) {
            m.addAnnotation(annotation.toString());
        }

        return m;
    }
    private InnerClass transfer(ClassOrInterfaceDeclaration c) {
        return null;
    }

    private Field transfer(FieldDeclaration field) {
        VariableDeclarator variable = field.getVariable(0);
        Field f = new Field();
        f.setName(variable.getNameAsString());
        f.setType(new FullyQualifiedJavaType(variable.getTypeAsString()));
        f.setTransient(field.isTransient());
        f.setVolatile(field.isVolatile());
        f.setFinal(field.isFinal());
        f.setStatic(field.isStatic());

        if (variable.getInitializer().isPresent()) {
            String initValue = variable.getInitializer().get().toString();
            if (!"".equals(initValue)) {
                f.setInitializationString(initValue);
            }
        }

        if (!field.isPublic()) {
            if (field.isPrivate()) {
                f.setVisibility(JavaVisibility.PRIVATE);
            } else if (field.isProtected()) {
                f.setVisibility(JavaVisibility.PROTECTED);
            }
        } else {
            f.setVisibility(JavaVisibility.PUBLIC);
        }

        NodeList<AnnotationExpr> annotations = field.getAnnotations();
        for (AnnotationExpr annotation : annotations) {
            f.addAnnotation(annotation.toString());
        }

        addJavaDoc(field, f);


        return f;
    }

    private void addJavaDoc(NodeWithJavadoc node, JavaElement element) {
        if (node.getJavadocComment().isPresent()) {
            String comment = node.getJavadocComment().get().toString();
            String[] commentBodyLines = comment.split(System.getProperty("line.separator"));
            for (String commentBodyLine : commentBodyLines) {
                element.addJavaDocLine(commentBodyLine.trim());
            }
        }
    }



    private Method transfer(MethodDeclaration method) {
        Method m = new Method();
        m.setName(method.getNameAsString());
        m.setReturnType(new FullyQualifiedJavaType(method.getTypeAsString()));
        m.setSynchronized(method.isSynchronized());
        m.setNative(method.isNative());
        m.setDefault(method.isDefault());
        m.setFinal(method.isFinal());
        m.setStatic(method.isStatic());

        if (method.isPrivate()) {
            m.setVisibility(JavaVisibility.PRIVATE);
        } else if (method.isPublic()) {
            m.setVisibility(JavaVisibility.PUBLIC);
        } else if (method.isProtected()) {
            m.setVisibility(JavaVisibility.PROTECTED);
        }
        //todo 访问权限修饰符为默认

        //增加方法体
        if (method.getBody().isPresent()) {
            String body = method.getBody().get().toString();
            StringBuilder sb = new StringBuilder(body);
            if (sb.length() > 0) {
                if (sb.charAt(0) == '{') {
                    sb.deleteCharAt(0);
                }
                if (sb.charAt(sb.length() - 1) == '}') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                String[] bodyLines = sb.toString().split(System.getProperty("line.separator"));
                for (String bodyLine : bodyLines) {
                    m.addBodyLine(bodyLine.trim());
                }
            }
        }


        //增加参数
        NodeList<Parameter> parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            org.mybatis.generator.api.dom.java.Parameter p =
                    new org.mybatis.generator.api.dom.java.Parameter(
                            new FullyQualifiedJavaType(parameter.getTypeAsString()),
                            parameter.getNameAsString(),
                            parameter.getAnnotations().toString()
                                    .replace("[","")
                                    .replace("]","")
                                    .replace(","," "),
                            parameter.isVarArgs());
            m.addParameter(p);
        }
        //增加异常
        NodeList<ReferenceType> exceptions = method.getThrownExceptions();
        for (ReferenceType exception : exceptions) {
            m.addException(new FullyQualifiedJavaType(exception.toString()));
        }

        //增加注释
        //todo 行注释
        addJavaDoc(method, m);


        //增加注解
        NodeList<AnnotationExpr> annotations = method.getAnnotations();
        for (AnnotationExpr annotation : annotations) {
            m.addAnnotation(annotation.toString());
        }

        return m;
    }

    private void addImport(org.mybatis.generator.api.dom.java.CompilationUnit unit) {
        if (imports == null || imports.isEmpty()) {
            return;
        }
        Set<FullyQualifiedJavaType> importedTypes = unit.getImportedTypes();
        Set<String> newImports = new HashSet<>();
        for (FullyQualifiedJavaType type : importedTypes) {
            newImports.add(type.getFullyQualifiedName());
        }
        List<String> oldImports = new ArrayList<>();
        for (ImportDeclaration i : imports) {
            oldImports.add(i.getName().asString());
        }
        oldImports.removeAll(newImports);

        for (String oldImport : oldImports) {
            unit.addImportedType(new FullyQualifiedJavaType(oldImport));
        }
    }


    public boolean exists() {
        return javaFile.exists();
    }

    public boolean isFile() {
        return javaFile.isFile();
    }

    public File getJavaFile() {
        return javaFile;
    }

}
