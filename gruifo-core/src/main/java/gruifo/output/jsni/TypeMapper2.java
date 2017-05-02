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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * This class is replaced by the TypeMapper in util and gson mapper reader.
 * @deprecated old TypeMapper
 */
@Deprecated
public final class TypeMapper2 {
  private static final String DOM_CLIENT = "com.google.gwt.dom.client";
  private static final String CORE_CLIENT = "com.google.gwt.core.client";
  static final TypeName GWT_JAVA_SCRIPT_OBJECT =
      ClassName.get(CORE_CLIENT, "JavaScriptObject");

  public static final TypeMapper2 INSTANCE = new TypeMapper2();

  private final Map<String, TypeName> mapper = new HashMap<>();
  private final Set<String> ignores = new HashSet<>();
  private final Map<String, String> replaceTypes = new HashMap<>();

  private TypeMapper2() {
    mapper.put("*", GWT_JAVA_SCRIPT_OBJECT);
    mapper.put("object", GWT_JAVA_SCRIPT_OBJECT);
    mapper.put("Object", GWT_JAVA_SCRIPT_OBJECT);
    mapper.put("undefined", GWT_JAVA_SCRIPT_OBJECT);
    // JSNI specific
    mapper.put("HTMLDocument", ClassName.get(DOM_CLIENT, "Node"));
    mapper.put("Document", ClassName.get(DOM_CLIENT, "Document"));
    mapper.put("Node", ClassName.get(DOM_CLIENT, "Node"));
    mapper.put("Element", ClassName.get(DOM_CLIENT, "Element"));
    mapper.put("Event", ClassName.get(DOM_CLIENT, "NativeEvent"));
    mapper.put("Touch", ClassName.get(DOM_CLIENT, "Touch"));

    mapper.put("Array", ClassName.get(CORE_CLIENT, "JsArray"));
    mapper.put("Array.<*>", ClassName.get(CORE_CLIENT, "JsArray"));
    mapper.put("Array.<number>", ClassName.get(CORE_CLIENT, "JsArrayNumber"));
    mapper.put("Array.<string>", ClassName.get(CORE_CLIENT, "JsArrayString"));
  }

  public void addMappings(final Properties props) {
    for (final Entry<Object, Object> prop : props.entrySet()) {
      if (((String) prop.getKey()).charAt(0) == '-') {
        ignores.add(((String) prop.getKey()).substring(1));
      } else if (((String) prop.getKey()).charAt(0) == '&') {
        replaceTypes.put(((String) prop.getKey()).substring(1),
            (String) prop.getValue());
      } else {
        mapper.put((String) prop.getKey(),
            string2Class((String) prop.getValue()));
      }
    }
  }

  public boolean ignore(final String clazz) {
    return ignores.contains(clazz);
  }

  public boolean ignore(final String clazz, final String method) {
    return ignores.contains(clazz + "$" + method);
  }

  /**
   * Returns true if typeToCheck is a primitive type.
   * @param typeToCheck type to check
   * @return true if is primitive
   */
  //  public boolean isPrimitive(final String typeToCheck) {
  //    return primitiveMapper.containsKey(typeToCheck);
  //  }
  //
  //  public TypeName mapType(final String typeToMap, final boolean generic) {
  //    return generic ?
  //        genericMapper.containsKey(typeToMap) ? mapGenericType(typeToMap)
  //            : mapOtherType(typeToMap): mapType(typeToMap);
  //  }

  /**
   * Returns the type matching the typeToMap or the value passed if it doesn't
   * map any types.
   * @param typeToMap
   * @return
   */
  //  public TypeName mapType(final String typeToMap) {
  //    return primitiveMapper.containsKey(typeToMap)
  //        ? mapPrimitiveType(typeToMap) : mapOtherType(typeToMap);
  //  }

  private TypeName mapOtherType(final String typeToMap) {
    return mapper.containsKey(typeToMap)
        ? mapper.get(typeToMap) : string2Class(typeToMap);
  }

  //  private TypeName mapGenericType(final String typeToMap) {
  //    return genericMapper.containsKey(typeToMap)
  //        ? genericMapper.get(typeToMap) : string2Class(typeToMap);
  //  }
  //
  //  private TypeName mapPrimitiveType(final String typeToMap) {
  //    return primitiveMapper.containsKey(typeToMap)
  //        ? primitiveMapper.get(typeToMap) : string2Class(typeToMap);
  //  }

  public TypeName replaceType(final String fullClassName,
      final String methodName, final String name) {
    return string2Class(
        replaceTypes.get(fullClassName + '$' + methodName + '$' + name));
  }

  public TypeName replaceType(final String fullClassName, final String name) {
    return string2Class(replaceTypes.get(fullClassName + '$' + name));
  }

  private TypeName string2Class(final String typeToMap) {
    if (typeToMap == null) {
      return null;
    }
    final int idx = typeToMap.lastIndexOf('.');
    return ClassName.get(typeToMap.substring(0, idx),
        typeToMap.substring(idx + 1));
  }
}
