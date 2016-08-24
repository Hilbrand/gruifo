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
package gruifo.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import gruifo.lang.js.JsType;
import gruifo.output.util.TypeMapper;

/**
 * Test class for {@link TypeMapper}.
 */
public class TypeMapperTest {

  final TypeMapper mapper = new TypeMapper();

  @Test
  public void testPrimitiveMap() {
    final JsType jsType = new JsType("number", "number");
    final TypeName typeName = mapper.map(jsType);
    assertEquals("primitive typename number:" + jsType,
        "double", typeName.toString());
  }

  @Test
  public void testMap() {
    final JsType jsType = new JsType("nl.test.MyTest", "");
    final ClassName className = (ClassName) mapper.map(jsType);
    assertEquals("Simple classname check:" + jsType,
        "MyTest", className.simpleName());
    assertEquals("Package classname check:" + jsType,
        "nl.test", className.packageName());
  }

  @Test
  public void testMapGeneric() {
    final JsType jsType = new JsType("nl.test.MyTest", "");
    final JsType jsType2 = new JsType("nl.generic.MyGeneric", "");
    jsType.addGenericType(jsType2);
    final ParameterizedTypeName className =
        (ParameterizedTypeName) mapper.map(jsType);
    assertEquals("Generic raw classname check:" + jsType,
        "MyTest", className.rawType.simpleName());
    assertEquals("Generic raw package classname check:" + jsType,
        "nl.test", className.rawType.packageName());
    final ClassName genericClassName = (ClassName) className.typeArguments.get(0);
    assertEquals("Generic classname check:" + jsType,
        "MyGeneric", genericClassName.simpleName());
    assertEquals("Generic Package classname check:" + jsType,
        "nl.generic", genericClassName.packageName());
  }
}
