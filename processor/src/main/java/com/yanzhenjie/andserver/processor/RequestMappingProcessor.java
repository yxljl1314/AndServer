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
import com.yanzhenjie.andserver.annotation.DeleteMapping;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PatchMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.PutMapping;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.processor.util.Logger;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by YanZhenjie on 2018/6/7.
 */
@AutoService(Processor.class)
public class RequestMappingProcessor extends AbstractProcessor {

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
      addElementToMap(roundEnvironment, GetMapping.class, typeMap);
      addElementToMap(roundEnvironment, PutMapping.class, typeMap);
      addElementToMap(roundEnvironment, PostMapping.class, typeMap);
      addElementToMap(roundEnvironment, PatchMapping.class, typeMap);
      addElementToMap(roundEnvironment, DeleteMapping.class, typeMap);
      addElementToMap(roundEnvironment, RequestMapping.class, typeMap);
      writeToFile(typeMap);
    }
    return false;
  }

  private void addElementToMap(RoundEnvironment environment, Class<? extends Annotation> clazz,
                               Map<String, TypeElement> typeMap) {
    Set<? extends Element> elements = environment.getElementsAnnotatedWith(clazz);
    for (Element element : elements) {
      TypeElement typeElement = (TypeElement)element;
      String hostClassName = typeElement.getQualifiedName().toString();
      typeMap.put(hostClassName, typeElement);
    }
  }

  private void writeToFile(Map<String, TypeElement> typeMap) {
    // TODO create adapter.
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotations = new LinkedHashSet<>();
    annotations.add(GetMapping.class.getCanonicalName());
    annotations.add(PostMapping.class.getCanonicalName());
    annotations.add(PutMapping.class.getCanonicalName());
    annotations.add(PatchMapping.class.getCanonicalName());
    annotations.add(DeleteMapping.class.getCanonicalName());
    annotations.add(RequestMapping.class.getCanonicalName());
    return annotations;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}