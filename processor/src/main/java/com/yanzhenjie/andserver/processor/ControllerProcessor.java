/*
 * Copyright 2018 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.andserver.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yanzhenjie.andserver.annotation.Controller;
import com.yanzhenjie.andserver.processor.util.Constant;
import com.yanzhenjie.andserver.processor.util.Logger;

import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by YanZhenjie on 2018/6/6.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.yanzhenjie.andserver.annotation.Controller"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ControllerProcessor extends AbstractProcessor {

  private Filer mFiler;
  private Elements mElementUtils;

  private Logger mLog;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    mFiler = processingEnvironment.getFiler();
    mElementUtils = processingEnvironment.getElementUtils();

    mLog = new Logger(processingEnvironment.getMessager());
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    if (CollectionUtils.isNotEmpty(set)) {
      Map<String, TypeElement> typeMap = new HashMap<>();
      obtainType(roundEnvironment, typeMap);
      writeToFile(typeMap);
    }
    return false;
  }

  private void obtainType(RoundEnvironment roundEnvironment, Map<String, TypeElement> typeMap) {
    Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(Controller.class);
    for (Element element : elementSet) {
      TypeElement typeElement = (TypeElement)element;
      String hostClassName = typeElement.getQualifiedName().toString();
      typeMap.put(hostClassName, typeElement);
    }
  }

  private void writeToFile(Map<String, TypeElement> typeMap) {
    for (Map.Entry<String, TypeElement> typeEntry : typeMap.entrySet()) {
      TypeElement typeElement = typeEntry.getValue();

      FieldSpec controller =
        FieldSpec.builder(TypeName.get(typeElement.asType()), "mController", Modifier.PRIVATE,
                          Modifier.FINAL).build();

      MethodSpec constructor = MethodSpec.constructorBuilder()
                                         .addModifiers(Modifier.PUBLIC)
                                         .addParameter(TypeName.get(typeElement.asType()),
                                                       "controller")
                                         .addStatement("this.mController = controller")
                                         .build();

      TypeSpec clazz = TypeSpec.classBuilder(typeElement.getSimpleName() + "$$Controller")
                               .addJavadoc(Constant.DOC_EDIT_WARN)
                               .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                               .superclass(
                                 ClassName.get(Constant.ANDSERVER_PACKAGE, "BaseController"))
                               .addField(controller)
                               .addMethod(constructor)
                               .build();
      String packageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
      JavaFile javaFile = JavaFile.builder(packageName, clazz).build();

      try {
        javaFile.writeTo(mFiler);
      } catch (Exception e) {
        mLog.e(e);
      }
    }
  }
}