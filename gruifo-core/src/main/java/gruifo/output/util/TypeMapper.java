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
package gruifo.output.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import gruifo.lang.js.JsType;
import gruifo.lang.js.JsTypeObject;

/**
 * Converts JavaScript type to Java type objects.
 * FIXME: probably only primitive types mapper?
 */
public class TypeMapper {

  private final Map<String, TypeName> unboxedMapper = new HashMap<>();
  private final Map<String, TypeName> boxedMapper = new HashMap<>();
  private final Map<String, TypeName> customMapper = new HashMap<>();

  public TypeMapper() {
    unboxedMapper.put("void", TypeName.VOID);
    unboxedMapper.put("string", TypeName.get(String.class));
    unboxedMapper.put("int", TypeName.INT);
    unboxedMapper.put("double", TypeName.DOUBLE);
    unboxedMapper.put("float", TypeName.FLOAT);
    unboxedMapper.put("boolean", TypeName.BOOLEAN);
    unboxedMapper.put("number", TypeName.DOUBLE);

    boxedMapper.put("void", TypeName.VOID.box());
    boxedMapper.put("string", TypeName.get(String.class));
    boxedMapper.put("int", TypeName.INT.box());
    boxedMapper.put("double", TypeName.DOUBLE.box());
    boxedMapper.put("float", TypeName.FLOAT.box());
    boxedMapper.put("boolean", TypeName.BOOLEAN.box());
    boxedMapper.put("number", TypeName.DOUBLE.box());
  }

  public TypeName map(final JsTypeObject jsTypeObject) {
    return map(jsTypeObject, unboxedMapper);
  }

  private TypeName map(final JsTypeObject jsTypeObject,
      final Map<String, TypeName> primitiveMapper) {
    if (jsTypeObject instanceof JsType) {
      return map((JsType) jsTypeObject, unboxedMapper);
    }
    throw new IllegalArgumentException("JsTypeObject not allowed.");
  }

  private TypeName map(final JsType jsTypeObject,
      final Map<String, TypeName> primitiveMapper) {
    final TypeName mappedType;
    final TypeName rawType =
        mapRawType(jsTypeObject.getRawType(), primitiveMapper);
    if (rawType == null) {
      final ClassName className = getClassName(jsTypeObject.getName());
      if (jsTypeObject.getTypeList().isEmpty()) {
        mappedType = className;
      } else {
        mappedType = ParameterizedTypeName.get(className,
            mapTypeList(jsTypeObject.getTypeList()));
      }
    } else {
      mappedType = rawType;
    }
    return mappedType;
  }

  private TypeName mapRawType(final String rawType,
      final Map<String, TypeName> primitiveMapper) {
    final TypeName mappedType;
    if (primitiveMapper.containsKey(rawType)) {
      mappedType = primitiveMapper.get(rawType);
    } else if (customMapper.containsKey(rawType)) {
      mappedType = customMapper.get(rawType);
    } else {
      mappedType = null;
    }
    return mappedType;
  }

  private ClassName getClassName(final String name) {
    final int clzidx = name.lastIndexOf('.');
    return ClassName.get(name.substring(0, clzidx),
        name.substring(clzidx + 1, name.length()));
  }

  private TypeName[] mapTypeList(final List<JsTypeObject> list) {
    final TypeName[] typeArguments = new TypeName[list.size()];
    for (int i = 0; i < list.size(); i++) {
      typeArguments[i] = map(list.get(i), boxedMapper);
    }
    return typeArguments;
  }
}
