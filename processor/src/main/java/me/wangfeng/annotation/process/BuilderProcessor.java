package me.wangfeng.annotation.process;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import me.wangfeng.annotation.Builder;

public class BuilderProcessor extends AbstractProcessor {

    private Set<String> supportAnnotations;

    private Messager messager;
    private Filer filer;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        if (supportAnnotations == null) {
            supportAnnotations = new HashSet<>();
            supportAnnotations.add(Builder.class.getCanonicalName());
        }
        return supportAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) {
            return true;
        }

        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Builder.class);
        for (Element element : annotatedElements) {
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s注解的%s不是类",
                                Builder.class.getCanonicalName(),
                                element.getSimpleName().toString()),
                        element);
            }
            BuilderGenerator.generate((TypeElement) element, filer);
        }
        return true;
    }
}
