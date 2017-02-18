package me.wangfeng.annotation.process;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * 生成为Immutable数据结构生成Builder类
 *
 * Created by wangfeng on 17/2/18.
 */

class BuilderGenerator {

    static void generate(TypeElement typeElement, Filer filer) {
        List<VariableElement> fields = getFieldsOf(typeElement);
        if (fields.isEmpty()) {
            return;
        }
        ClassName className = ClassName.get(typeElement);
        ClassName builderClassName = ClassName.get(className.packageName(), className.simpleName() + "$$Builder");
        List<FieldSpec> builderFields = new ArrayList<>();
        List<MethodSpec> builderMethods = new ArrayList<>();
        for (VariableElement field : fields) {
            builderFields.add(generateBuilderField(field));
            builderMethods.add(generateBuilderMethod(builderClassName, field));
        }

        TypeSpec.Builder builderBuilder = TypeSpec.classBuilder(builderClassName);
        builderBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        for (FieldSpec builderField : builderFields) {
            builderBuilder.addField(builderField);
        }
        for (MethodSpec builderMethod : builderMethods) {
            builderBuilder.addMethod(builderMethod);
        }
        builderBuilder.addMethod(generateBuildMethod(className, fields));
        JavaFile javaFile = JavaFile.builder(className.packageName(), builderBuilder.build()).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<VariableElement> getFieldsOf(TypeElement typeElement) {
        List<? extends Element> enclosedElement = typeElement.getEnclosedElements();
        List<VariableElement> fields = new ArrayList<>();
        for (Element element : enclosedElement) {
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }
            Set<Modifier> modifiers = element.getModifiers();
            if (modifiers.contains(Modifier.STATIC) ||
                    modifiers.contains(Modifier.PUBLIC) ||
                    modifiers.contains(Modifier.FINAL) ||
                    modifiers.contains(Modifier.PRIVATE) ||
                    modifiers.contains(Modifier.PROTECTED)) {
                continue;
            }
            fields.add((VariableElement) element);
        }
        return fields;
    }

    private static FieldSpec generateBuilderField(VariableElement field) {
        String fieldName = field.getSimpleName().toString();
        return FieldSpec.builder(TypeName.get(field.asType()), fieldName, Modifier.PRIVATE).build();
    }

    private static MethodSpec generateBuilderMethod(ClassName builderClassName, VariableElement field) {
        String fieldName = field.getSimpleName().toString();
        return MethodSpec.methodBuilder(field.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .returns(builderClassName)
                .addParameter(TypeName.get(field.asType()), fieldName)
                .addStatement("this.$L = $L", fieldName, fieldName)
                .addStatement("return this")
                .build();
    }

    private static MethodSpec generateBuildMethod(ClassName className, List<VariableElement> fields) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(className)
                .addStatement("$T instance = new $T()", className, className);
        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            methodBuilder.addStatement("instance.$L = $L", fieldName, fieldName);
        }
        methodBuilder.addStatement("return instance");
        return methodBuilder.build();
    }
}
