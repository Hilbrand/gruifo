///*
// * Copyright Hilbrand Bouwkamp.
// *
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// * use this file except in compliance with the License. You may obtain a copy of
// * the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// * License for the specific language governing permissions and limitations under
// * the License.
// */
//package gruifo.output.jsni;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.squareup.javapoet.TypeName;
//
//import gruifo.lang.java.JClass;
//import gruifo.lang.java.JMethod;
//import gruifo.lang.java.JVar;
//import gruifo.lang.js.JsEnum;
//import gruifo.lang.js.JsFile;
//import gruifo.lang.js.JsMethod;
//import gruifo.lang.js.JsParam;
//import gruifo.lang.js.JsType;
//
///**
// * Transforms JavaScript into Java.
// * @deprecated kept old implementation for reference in this class.
// */
//@Deprecated
//class Transformer2 {
//
//  private static final Logger LOG = LoggerFactory.getLogger(Transformer.class);
//  private static final TypeMapper2 TYPE_MAPPER = TypeMapper2.INSTANCE;
//
//  private final Set<String> ignoreMethods = new HashSet<>();
//
//  public Transformer2() {
//    ignoreMethods.add("toString");
//  }
//
//  public JClass transform(final JsFile jsFile) {
//    final JClass jFile =
//        new JClass(jsFile.getPackageName(), jsFile.getClassOrInterfaceName());
//    jFile.setInterface(jsFile.isInterface());
//    addHeader(jFile, jsFile.getOriginalFileName());
//    jFile.setClassDescription(jsFile.getElement().getJsDoc());
//    for (final JsFile subFile: jsFile.getInnerJFiles()) {
//      jFile.addInnerJFile(transform(subFile));
//    }
//    if (jsFile.getElement().isTypeDef()) {
//      transformFields(jFile, jsFile.getElement().getTypeDef());
//      jFile.setDataClass(true);
//      jFile.setSuperclass(null); // FIXME: setExtends(null) needed?
//    }
//    setExtends(jFile, jsFile);
//    setImplements(jFile, jsFile);
//    transformEnumFields(jFile, jsFile.getElement().getEnumType(),
//        jsFile.getEnumValues());
//    transformFields(jFile, jsFile.getFields());
//    transformMethods(jsFile, jFile);
//    return jFile;
//  }
//
//  private void addHeader(final JClass jFile, final String orgFilename) {
//    String canonicalPath;
//    try {
//      canonicalPath = new File(orgFilename).getCanonicalPath();
//    } catch (final IOException e) {
//      canonicalPath = orgFilename;
//    }
//    jFile.setHeaderComment(
//        " This file was generated with gruifo.\n"
//            + " You probably don't want to edit this file.\n"
//            + " Generated from: " + canonicalPath.replace('\\', '/') + "\n");
//  }
//
//  private void transformEnumFields(final JClass jFile, final JsType enumType,
//      final List<JsEnum> list) {
//    if (!list.isEmpty()) {
//      jFile.setDataClass(true);
//      jFile.setSuperclass(null); //FIXME why doesn't set dataclass alone not work?
//    }
//    for (final JsEnum enumValue: list) {
//      //FIXME      jFile.addEnumValue(enumValue.getFieldName(),
//      //          transformSingleType(enumType), enumValue.getJsDoc());
//    }
//  }
//
//  private void transformMethods(final JsFile jsFile, final JClass jFile) {
//    for(final JsMethod jsMethod: jsFile.getMethods()) {
//      if (!ignoreMethod(jFile.getFullClassName(), jsMethod)) {
//        final List<List<JsParam>> list =
//            splitMethodParamsOptional(jsMethod.getElement().getParams());
//        final List<List<JVar>> jParamList = new ArrayList<>();
//        for (final List<JsParam> innerList : list) {
//          jParamList.addAll(split2MethodParamsMulti(innerList));
//        }
//        for (final List<JVar> params : jParamList) {
//          final JMethod method = transformMethod(jFile, jsMethod, params);
//          if (jsMethod.getElement().isClassDescription()) {
//            jFile.setClassDescription(jsMethod.getElement().getJsDoc());
//          }
//          if (jsMethod.getElement().isConstructor()) {
//            jFile.addConstructor(method);
//          } else {
//            jFile.addMethod(method);
//          }
//        }
//      }
//    }
//  }
//
//  private void setExtends(final JClass jFile, final JsFile jsFile) {
//    final JsType extendsType = jsFile.getElement().getExtends();
//    if (jsFile.getElement().getGenericType() != null) {
//      //FIXME      jFile.setClassGeneric(
//      //          TYPE_MAPPER.mapType(jsFile.getElement().getGenericType()));
//    }
//    if (jFile.isDataClass()) {
//      jFile.setSuperclass(null);
//    } else {
//      jFile.setSuperclass(extendsType == null
//          ? TypeMapper2.GWT_JAVA_SCRIPT_OBJECT
//              : transformSingleType(extendsType));
//    }
//  }
//
//  private void setImplements(final JClass jFile, final JsFile jsFile) {
//    final List<JsType> implementsTypes = jsFile.getElement().getImplements();
//    for (final JsType jsType : implementsTypes) {
//      jFile.addSuperinterface(transformSingleType(jsType));
//    }
//  }
//
//  private void transformFields(final JClass jFile,
//      final List<JsParam> jsFields) {
//    for (final JsParam jsParam : jsFields) {
//      if (!TYPE_MAPPER.ignore(jFile.getFullClassName(), jsParam.getName())) {
//        final List<TypeName> types = transformType(jsParam.getType());
//        for (final TypeName type: types) {
//          final JVar field = filterParam(jFile,
//              jFile.addField(jsParam.getName(), type));
//          field.setMultiField(types.size() > 1);
//          if (jsParam.getElement() != null) {
//            field.setJavaDoc(jsParam.getElement().getJsDoc());
//            field.setStatic(jsParam.getElement().isConst());
//            field.setFinal(jsParam.getElement().isDefine());
//          }
//        }
//      }
//    }
//  }
//
//  private boolean ignoreMethod(final String clazz, final JsMethod jsMethod) {
//    return ignoreMethods.contains(jsMethod.getMethodName())
//        || jsMethod.getElement().isOverride()
//        || jsMethod.getElement().isPrivate()
//        || jsMethod.getElement().isProtected()
//        || TYPE_MAPPER.ignore(clazz, jsMethod.getMethodName())
//        || "clone".equals(jsMethod.getMethodName()); // FIXME clone
//  }
//
//  /**
//   * Creates multiple parameter lists if a parameter is optional. If a
//   * parameter is optional a parameter list if added without this parameter.
//   * @param jsParams List of parameters
//   * @return List of List of parameters
//   */
//  private List<List<JsParam>> splitMethodParamsOptional(final List<JsParam> jsParams) {
//    final List<List<JsParam>> params = new ArrayList<>();
//    List<JsParam> current = new ArrayList<>();
//    params.add(current);
//    for (int i = 0; i < jsParams.size(); i++) {
//      final JsParam jsParam = jsParams.get(i);
//      if (jsParam.getType().isOptional()) {
//        current = new ArrayList<>(params.get(params.size() - 1));
//        params.add(current);
//      }
//      current.add(jsParam);
//    }
//    return params;
//  }
//
//  /**
//   * Creates multiple parameters lists if a parameter type contains multiple
//   * types.
//   * @param jsParams
//   * @return
//   */
//  private List<List<JVar>> split2MethodParamsMulti(
//      final List<JsParam> jsParams) {
//    final List<List<JVar>> params = new ArrayList<>();
//    params.add(new ArrayList<JVar>());
//    for (int i = 0; i < jsParams.size(); i++) {
//      final JsParam jsParam = jsParams.get(i);
//      if (jsParam.getType().getChoices().size() > 1) {
//        expandChoices(params, jsParam);
//      } else {
//        addSingleParam(params, jsParam);
//      }
//    }
//    return params;
//  }
//
//  /**
//   * Add new lists for each choice.
//   * The lists are alternating replicated and then sequential added:
//   * <pre>
//   *   A => A C
//   *   B    B C
//   *        A D
//   *        B D
//   * </pre>
//   * @param params List of combinations of List of parameters.
//   * @param jsParam choice parameters to add.
//   */
//  private void expandChoices(final List<List<JVar>> params,
//      final JsParam jsParam) {
//    final List<JVar> splitParams = optionParam2List(jsParam);
//    final int currentSize = params.size();
//    // Add new lists matching the number of choices.
//    for (int j = 0; j < currentSize; j++) {
//      for (int k = 1; k < splitParams.size(); k++) {
//        params.add(new ArrayList<>(params.get(j)));
//      }
//    }
//    for (int j = 0; j < splitParams.size();j++) {
//      for (int k = 0; k < currentSize; k++) {
//        params.get(k + j * currentSize).add(splitParams.get(j));
//      }
//    }
//  }
//
//  /**
//   * Add the given jsParam to each list in params.
//   * @param params List of combinations of List of parameters.
//   * @param jsParam parameter to add.
//   */
//  private void addSingleParam(final List<List<JVar>> params,
//      final JsParam jsParam) {
//    for (final TypeName type: transformType(jsParam.getType())) {
//      final JVar jParam = new JVar(jsParam.getName(), type);
//      for (final List<JVar> list : params) {
//        list.add(jParam);
//      }
//    }
//  }
//
//  public List<JVar> optionParam2List(final JsParam jsParam) {
//    final List<JVar> splitParams = new ArrayList<>();
//    for (final JsType innerJsParam : jsParam.getType().getChoices()) {
//      final TypeName transformedType = transformType(innerJsParam, true);
//      if (!isDuplicate(splitParams, transformedType)) {
//        splitParams.add(new JVar(jsParam.getName(), transformedType));
//      }
//    }
//    return splitParams;
//  }
//
//  private void optionParam2List(final JsType jsType, final List<JVar> params) {
//    for (final JsType innerJsParam : jsType.getChoices()) {
//      optionParam2List(innerJsParam, params);
//    }
//  }
//
//  private boolean isDuplicate(final List<JVar> splitParams,
//      final TypeName transformedType) {
//    boolean duplicate = false;
//    if (transformedType != null) {
//      for (final JVar jParam : splitParams) {
//        if (transformedType.equals(jParam.getType())) {
//          duplicate = true;
//        }
//      }
//    }
//    return duplicate;
//  }
//
//  private JMethod transformMethod(final JClass jFile, final JsMethod jsMethod,
//      final List<JVar> params) {
//    final JMethod jMethod = new JMethod(jsMethod.getPackageName(),
//        jsMethod.getMethodName(), jsMethod.getModifier());
//    jMethod.setJsDoc(jsMethod.getElement().getJsDoc());
//    jMethod.setAbstract(jsMethod.isAbstractMethod());
//    jMethod.setStatic(jsMethod.isStaticMethod());
//    setReturnType(jsMethod, jMethod);
//    for (final JVar param : params) {
//      jMethod.addParam(filterParam(jFile, jMethod, param));
//    }
//    return jMethod;
//  }
//
//  /**
//   * Replace the type for the parameter if a type is set in the configuration.
//   * @param jFile
//   * @param jMethod
//   * @param param
//   * @return
//   */
//  private JVar filterParam(final JClass jFile, final JVar param) {
//    final TypeName replaceType =
//        TYPE_MAPPER.replaceType(jFile.getFullClassName(), param.getName());
//    if (replaceType != null) {
//      param.setType(replaceType);
//    }
//    return param;
//  }
//
//  /**
//   * Replace the type for the parameter if a type is set in the configuration.
//   * @param jFile
//   * @param jMethod
//   * @param param
//   * @return
//   */
//  private JVar filterParam(final JClass jFile, final JMethod jMethod,
//      final JVar param) {
//    final TypeName replaceType = TYPE_MAPPER.replaceType(
//        jFile.getFullClassName(), jMethod.getMethodName(), param.getName());
//    if (replaceType != null) {
//      param.setType(replaceType);
//    }
//    return param;
//  }
//
//  private void setReturnType(final JsMethod jsMethod, final JMethod jMethod) {
//    jMethod.setGenericType(jsMethod.getElement().getGenericType());
//    final boolean voidReturn = jsMethod.getElement().getReturn() == null;
//    jMethod.setReturn(voidReturn ? TypeName.VOID
//        : transformSingleType(jsMethod.getElement().getReturn()));
//  }
//
//  private List<TypeName> transformType(final JsType jsType) {
//    final List<TypeName> types = new ArrayList<>();
//    final TypeName mapRawType = mapRawType(jsType.getName());
//    if (mapRawType == null) {
//      if (jsType.getChoices().isEmpty()) {
//        //        LOG.error("Type empty: {}", jsType);
//        //        types.add(TypeMapper.GWT_JAVA_SCRIPT_OBJECT);
//        types.add(tranformVarargs(jsType, transformType(jsType, true)));
//      } else {
//        final List<TypeName> sTypes =
//            tranformTypeList(jsType, jsType.getChoices());
//        for (final TypeName type : sTypes) {
//          types.add(tranformVarargs(jsType, type));
//        }
//        //transformType(jsType.getTypes().get(0), false)
//      }
//    } else {
//      types.add(tranformVarargs(jsType, mapRawType));
//    }
//    return types;
//  }
//
//  private TypeName tranformVarargs(final JsType jsType, final TypeName type) {
//    return type;//type + (jsType.isVarArgs() ? "..." : "");
//  }
//
//  private TypeName transformSingleType(final JsType jsType) {
//    final TypeName type;
//    final TypeName mapRawType = mapRawType(jsType.getRawType());
//    if (mapRawType == null) {
//      if (jsType.getChoices().isEmpty()) {
//        final TypeName transformedType = transformType(jsType, true);
//        if (transformedType == null) {
//          LOG.error("Type for single type conversion empty:{}", jsType);
//          type = TypeMapper2.GWT_JAVA_SCRIPT_OBJECT;
//        } else {
//          type = transformedType;
//        }
//      } else {
//        LOG.debug("More then 1 type: {}", jsType);
//        type = TypeMapper2.GWT_JAVA_SCRIPT_OBJECT;
//      }
//    } else {
//      type = mapRawType;
//    }
//    return type;
//  }
//
//  /**
//   * If the rawType is a predefined type that should be mapped to a specific
//   * other type that other type is returned otherwise null is returned.
//   * @param rawType raw type to map
//   * @return mapped raw type or null
//   */
//  private TypeName mapRawType(final String rawType) {
//    return rawType == null ? null : TYPE_MAPPER.mapType(rawType).equals(rawType)
//        ? null : TYPE_MAPPER.mapType(rawType);
//  }
//
//  private TypeName transformType(final JsType jsType, final boolean generic) {
//    TypeName type;
//    if (jsType.isFunction()) {
//      type = TypeMapper2.GWT_JAVA_SCRIPT_OBJECT;
//    } else if (jsType.isGeneric()) {
//      if (jsType.getChoices().size() > 1) {
//        type = TypeMapper2.GWT_JAVA_SCRIPT_OBJECT;
//      } else {
//        type = mapRawType(jsType.getRawType());
//        if (type == null) {
//          final TypeName mappedType = TYPE_MAPPER.mapType(jsType.getName(), generic);
//          if (TypeMapper2.GWT_JAVA_SCRIPT_OBJECT.equals(mappedType)) {
//            type = TypeMapper2.GWT_JAVA_SCRIPT_OBJECT;
//          } else {
//            type = TypeMapper2.GWT_JAVA_SCRIPT_OBJECT;
//
//            //fixme
//            //            type = mappedType + '<'
//            //                + join(tranformTypeList(jsType, jsType.getTypeList()))
//            //                //              + (sType == null
//            //                //              ? transformType(jsType.getTypeList().get(0), true) : sType)
//            //                + '>';
//          }
//        }
//      }
//    } else {
//      type = TYPE_MAPPER.mapType(jsType.getName(), generic);
//    }
//    return type;
//  }
//
//  private String join(final List<String> list) {
//    final StringBuilder b = new StringBuilder();
//    boolean first = true;
//    for (final String string : list) {
//      if (first) {
//        first = false;
//      } else {
//        b.append(", ");
//      }
//      b.append(string);
//    }
//    return b.toString();
//  }
//
//  private List<TypeName> tranformTypeList(final JsType jsType, final List<JsType> list) {
//    final List<TypeName> types = new ArrayList<>();
//    if (jsType.isFunction()) {
//      types.add(TypeMapper2.GWT_JAVA_SCRIPT_OBJECT);
//    } else {
//      for (final JsType jsTypeSpec : list) {
//        final TypeName mapRawType = mapRawType(jsTypeSpec.getRawType());
//        types.add(
//            mapRawType == null ? transformType(jsTypeSpec, false) : mapRawType);
//      }
//    }
//    return types;
//  }
//
//  //  private String tranformSpecificSubType(final JsType jsTypeSpec) {
//  //    String specific = null;
//  //    if (jsTypeSpec.getChoices().size() == 1
//  //        && "Array".equals(jsTypeSpec.getName())) {
//  //      if ("string".equals(jsTypeSpec.getTypeList().get(0).getName())) {
//  //        specific = "Array.<string>";
//  //      } else if ("number".equals(jsTypeSpec.getTypeList().get(0).getName())) {
//  //        specific = "Array.<number>";
//  //      }
//  //    }
//  //    return specific == null ? null : TYPE_MAPPER.mapType(specific);
//  //  }
//}
