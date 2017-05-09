/*
 * Copyright Hilbrand Bouwkamp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gruifo.output.jsni;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.TypeName;

import gruifo.lang.java.JClass;
import gruifo.lang.java.JMethod;
import gruifo.lang.java.JVar;
import gruifo.lang.js.JsElement;
import gruifo.lang.js.JsEnum;
import gruifo.lang.js.JsField;
import gruifo.lang.js.JsFile;
import gruifo.lang.js.JsMethod;
import gruifo.lang.js.JsParam;
import gruifo.lang.js.JsType;
import gruifo.lang.js.JsTypeList;
import gruifo.lang.js.JsTypeObject;
import gruifo.output.util.TypeMapper;

/**
 * Transforms JavaScript into Java.
 */
class Transformer {

  private static final Logger LOG = LoggerFactory.getLogger(Transformer.class);
  private static final TypeMapper TYPE_MAPPER = new TypeMapper();

  private final Set<String> ignoreMethods = new HashSet<>();

  public Transformer() {
    ignoreMethods.add("toString");
  }

  public JClass transform(final JsFile jsFile) {
    final JClass jFile =
        new JClass(jsFile.getPackageName(), jsFile.getClassOrInterfaceName());
    jFile.setInterface(jsFile.isInterface());
    addHeader(jFile, jsFile.getOriginalFileName());
    jFile.setClassDescription(jsFile.getElement().getJsDoc());
    jFile.setAbstract(jsFile.getElement().isAbstract());
    for (final JsFile subFile: jsFile.getInnerJFiles()) {
      jFile.addInnerJFile(transform(subFile));
    }
    if (jsFile.getElement().isTypeDef()) {
      transformFields(jFile, jsFile.getElement().getTypeDef());
      jFile.setDataClass(true);
      jFile.setSuperclass(null); // FIXME: setExtends(null) needed?
    }
    setExtends(jFile, jsFile);
    setImplements(jFile, jsFile);
    //FIXME    transformEnumFields(jFile, jsFile.getElement().getEnumType(),
    //        jsFile.getEnumValues());
    transformFields(jFile, jsFile.getFields());
    transformMethods(jsFile, jFile);
    return jFile;
  }

  private void addHeader(final JClass jFile, final String orgFilename) {
    String canonicalPath;
    try {
      canonicalPath = new File(orgFilename).getCanonicalPath();
    } catch (final IOException e) {
      canonicalPath = orgFilename;
    }
    jFile.setHeaderComment(
        " This file was generated with gruifo.\n"
            + " You probably don't want to edit this file.\n"
            + " Generated from: " + canonicalPath.replace('\\', '/') + "\n");
  }

  private void transformEnumFields(final JClass jFile, final JsType enumType,
      final List<JsEnum> list) {
    if (!list.isEmpty()) {
      jFile.setDataClass(true);
      jFile.setSuperclass(null); //FIXME why doesn't set dataclass alone not work?
    }
    for (final JsEnum enumValue: list) {
      //FIXME      jFile.addEnumValue(enumValue.getFieldName(),
      //          transformSingleType(enumType), enumValue.getJsDoc());
    }
  }

  private void transformMethods(final JsFile jsFile, final JClass jFile) {
    for(final JsMethod jsMethod: jsFile.getMethods()) {
      if (!ignoreMethod(jFile.getFullClassName(), jsMethod)) {
        final JMethod method = transformMethod(jFile, jsMethod);
        if (jsMethod.getElement().isClassDescription()) {
          jFile.setClassDescription(jsMethod.getElement().getJsDoc());
        }
        if (jsMethod.getElement().isConstructor()) {
          jFile.addConstructor(method);
        } else {
          jFile.addMethod(method);
        }
      }
    }
  }

  private void setExtends(final JClass jFile, final JsFile jsFile) {
    final JsTypeObject extendsType = jsFile.getElement().getExtends();
    if (jsFile.getElement().getGenericType() != null) {
      //FIXME      jFile.setClassGeneric(
      //          TYPE_MAPPER.mapType(jsFile.getElement().getGenericType()));
    }
    if (jFile.isDataClass()) {
      jFile.setSuperclass(null);
    } else {
      jFile.setSuperclass(extendsType == null
          ? TypeMapper2.GWT_JAVA_SCRIPT_OBJECT
              : transformSingleType((JsType) extendsType));
    }
  }

  private void setImplements(final JClass jFile, final JsFile jsFile) {
    for (final JsTypeObject jsType : jsFile.getElement().getImplements()) {
      jFile.addSuperinterface(transformSingleType((JsType) jsType));
    }
  }

  private void transformFields(final JClass jFile,
      final List<JsField> jsFields) {
    for (final JsField jsField : jsFields) {
      //      if (!TYPE_MAPPER.ignore(jFile.getFullClassName(), jsParam.getName())) {
      final List<TypeName> types = transformType((JsType) jsField.getType());
      for (final TypeName type: types) {
        final JVar field = jFile.addField(jsField.getName(), type);
        //            filterParam(jFile,
        //            jFile.addField(jsParam.getName(), type));
        //          field.setMultiField(types.size() > 1);
        final JsElement element = jsField.getElement();
        if (element != null) {
          field.setJavaDoc(element.getJsDoc());
          field.setStatic(element.isConst());
          field.setFinal(element.isDefine());
        }
      }
      //      }
    }
  }

  private boolean ignoreMethod(final String clazz, final JsMethod jsMethod) {
    return ignoreMethods.contains(jsMethod.getMethodName())
        || jsMethod.getElement().isOverride()
        || jsMethod.getElement().isPrivate()
        || jsMethod.getElement().isProtected()
        //        || TYPE_MAPPER.ignore(clazz, jsMethod.getMethodName())
        || "clone".equals(jsMethod.getMethodName()); // FIXME clone
  }


  private JMethod transformMethod(final JClass jFile, final JsMethod jsMethod) {
    final JMethod jMethod = new JMethod(jsMethod.getPackageName(),
        jsMethod.getMethodName(), jsMethod.getModifiers());
    jMethod.setJavaDoc(jsMethod.getElement().getJsDoc());
    jMethod.setAbstract(jsMethod.isAbstractMethod());
    jMethod.setStatic(jsMethod.isStaticMethod());
    setReturnType(jsMethod, jMethod);
    for (final JsParam param : jsMethod.getParams()) {
      try {
        jMethod.addParam(filterParam(jFile, jMethod, param));
      } catch (final IllegalArgumentException e) {
        LOG.error("Problem with param:{} in method:{} in file:{}",
            param, jMethod.getName(), jFile.getFullClassName());
        throw e;
      }
    }
    return jMethod;
  }

  /**
   * Replace the type for the parameter if a type is set in the configuration.
   * @param jFile
   * @param jMethod
   * @param param
   * @return
   */
  //  private JParam filterParam(final JClass jFile, final JParam param) {
  //    final TypeName replaceType =
  //        TYPE_MAPPER.map(param.getType());
  //    if (replaceType != null) {
  //      param.setType(replaceType);
  //    }
  //    return param;
  //  }

  /**
   * Replace the type for the parameter if a type is set in the configuration.
   * @param jFile
   * @param jMethod
   * @param param
   * @return
   */
  private JVar filterParam(final JClass jFile, final JMethod jMethod,
      final JsParam param) {
    //    final TypeName replaceType = TYPE_MAPPER.replaceType(
    //        jFile.getFullClassName(), jMethod.getName(), param.getName());
    //    if (replaceType != null) {
    //      param.setType(replaceType);
    //    }
    final JVar jVar = new JVar(param.getName(), transformType((JsType) param.getType()).get(0)); //TYPE_MAPPER.map(param.getType()));
    jVar.setVarArg(param.getType().isVarArgs());
    return jVar;
  }

  private void setReturnType(final JsMethod jsMethod, final JMethod jMethod) {
    jMethod.setGenericType(jsMethod.getElement().getGenericType());
    final boolean voidReturn = jsMethod.getElement().getReturn() == null;
    if (jsMethod.getElement().getReturn() instanceof JsTypeList) {
      throw new IllegalArgumentException(
          "Strange return type for method " + jsMethod.getMethodName()
          + ',' + jsMethod.getElement().getReturn());
    } else {
      jMethod.setType(voidReturn ? TypeName.VOID
          : transformSingleType((JsType) jsMethod.getElement().getReturn()));
    }
  }

  private List<TypeName> transformType(final JsType jsType) {
    final List<TypeName> types = new ArrayList<>();
    final TypeName mapRawType = TYPE_MAPPER.map(jsType);
    if (mapRawType == null) {
      //      if (jsType.getChoices().isEmpty()) {
      //        LOG.error("Type empty: {}", jsType);
      //        types.add(TypeMapper.GWT_JAVA_SCRIPT_OBJECT);
      types.add(tranformVarargs(jsType, transformType(jsType, true)));
      //      } else {
      //        final List<TypeName> sTypes =
      //            tranformTypeList(jsType, jsType.getChoices());
      //        for (final TypeName type : sTypes) {
      //          types.add(tranformVarargs(jsType, type));
      //        }
      //        //transformType(jsType.getTypes().get(0), false)
      //      }
    } else {
      types.add(tranformVarargs(jsType, mapRawType));
    }
    return types;
  }

  private TypeName tranformVarargs(final JsType jsType, final TypeName type) {
    if (jsType == null) {
      LOG.error("Called tranformVarargs with null argument");
      return null;
    }
    return jsType.isVarArgs() ? ArrayTypeName.of(type) : type;
  }

  private TypeName transformSingleType(final JsType jsType) {
    final TypeName type;
    final TypeName mapRawType = transformType(jsType, false);
    if (mapRawType == null) {
      final TypeName transformedType = transformType(jsType, true);
      if (transformedType == null) {
        LOG.error("Type for single type conversion empty:{}", jsType);
        type = TypeMapper2.GWT_JAVA_SCRIPT_OBJECT;
      } else {
        type = transformedType;
      }
    } else {
      type = mapRawType;
    }
    return type;
  }

  /**
   * If the rawType is a predefined type that should be mapped to a specific
   * other type that other type is returned otherwise null is returned.
   * @param rawType raw type to map
   * @return mapped raw type or null
   */
  //    private TypeName mapRawType(final String rawType) {
  //      return rawType == null ? null : TYPE_MAPPER.mapType(rawType).equals(rawType)
  //          ? null : TYPE_MAPPER.mapType(rawType);
  //    }

  private TypeName transformType(final JsType jsType, final boolean generic) {
    TypeName type;
    if (jsType == null) {
      LOG.error("Called transformType with null argument");
      return null;
    }
    if (jsType.isFunction()) {
      type = TypeMapper2.GWT_JAVA_SCRIPT_OBJECT;
    } else if (jsType.isGeneric()) {
      type = TYPE_MAPPER.map(jsType);
      if (type == null) {
        //        final TypeName mappedType = TYPE_MAPPER.map;
        //        if (TypeMapper2.GWT_JAVA_SCRIPT_OBJECT.equals(mappedType)) {
        //          type = TypeMapper2.GWT_JAVA_SCRIPT_OBJECT;
        //        } else {
        type = TypeMapper2.GWT_JAVA_SCRIPT_OBJECT;

        //fixme
        //            type = mappedType + '<'
        //                + join(tranformTypeList(jsType, jsType.getTypeList()))
        //                //              + (sType == null
        //                //              ? transformType(jsType.getTypeList().get(0), true) : sType)
        //                + '>';
        //        }
      }
    } else {
      type = TYPE_MAPPER.map(jsType);
    }
    return type;
  }

  private String join(final List<String> list) {
    final StringBuilder b = new StringBuilder();
    boolean first = true;
    for (final String string : list) {
      if (first) {
        first = false;
      } else {
        b.append(", ");
      }
      b.append(string);
    }
    return b.toString();
  }

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

  //  private String tranformSpecificSubType(final JsType jsTypeSpec) {
  //    String specific = null;
  //    if (jsTypeSpec.getChoices().size() == 1
  //        && "Array".equals(jsTypeSpec.getName())) {
  //      if ("string".equals(jsTypeSpec.getTypeList().get(0).getName())) {
  //        specific = "Array.<string>";
  //      } else if ("number".equals(jsTypeSpec.getTypeList().get(0).getName())) {
  //        specific = "Array.<number>";
  //      }
  //    }
  //    return specific == null ? null : TYPE_MAPPER.mapType(specific);
  //  }
}
