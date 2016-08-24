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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import gruifo.lang.js.JsParam;
import gruifo.lang.js.JsType;
import gruifo.lang.js.JsTypeList;

/**
 * Test class for {@link JavaScriptDocParser} for param annotation.
 * Test also {@link JsTypeParser}.
 */
public class JsDocParserParamTest extends JsDocParserTestBase {

  public JsDocParserParamTest() throws IOException {
    super("jsdoc-param");
  }

  @Test
  public void testNumberOfParams() {
    assertEquals("params size", 21, jsElement.getParams().size());
  }

  @Test
  public void testParam() {
    assertEquals("param name", "options", getParamAtRow(0).getName());
    assertEquals("param is object", "nl.Options", getParamNameAtRow(0));
    assertEquals("param is *", "*", getParamNameAtRow(1));
    assertEquals("param is ?", "", getParamNameAtRow(2));
  }

  @Test
  public void testFunction() {
    assertTrue("param is function", getParamTypeAtRow(3).isFunction());
    assertTrue("param is listener function", getParamTypeAtRow(4).isFunction());
  }

  @Test
  public void testArray() {
    assertEquals("param Array", "Array", getParamNameAtRow(5));
    assertArray1Generic(6, "T");
    assertArray1Generic(7, "number");
    assertArray1Generic(8, "nl.SomeObject");
    assertArray1Generic(9, "nl.SomeObject");
    assertEquals("2dn argument of Array", "number",
        ((JsType) getParamTypeAtRow(9).getTypeList().get(1)).getName());
  }

  private void assertArray1Generic(final int idx, final String genericType) {
    assertEquals("param Array", "Array", getParamNameAtRow(idx));
    final JsType type = (JsType) getParamAtRow(idx).getType();
    assertEquals("argument of Array", genericType,
        ((JsType) type.getTypeList().get(0)).getName());
  }

  @Test
  public void testOptional() {
    assertOptional(10, "nl.Object", "nl.Object");
    assertOptional(11, "Array", "Array.<T>");
    assertOptional(12, "S", "S");
  }

  private void assertOptional(final int idx, final String type,
      final String rawType) {
    assertTrue("Type should be optional",
        getParamTypeAtRow(idx).isOptional());
    assertEquals("Type test of optional param", type,
        getParamNameAtRow(idx));
    assertEquals("Raw type test of optional param", rawType,
        getParamTypeAtRow(idx).getRawType());
  }

  public void testState() {
    assertState(13, "nl.StateNull");
    assertTrue("Type should be can null", getParamTypeAtRow(13).isCanNull());
    assertState(14, "nl.StateNotNull");
    assertTrue("Type should be not null", getParamTypeAtRow(14).isNotNull());
  }

  private void assertState(final int idx, final String type) {
    assertEquals("Type test of stateparam", type,
        getParamNameAtRow(idx));
    assertEquals("Raw type test of state param", type,
        getParamTypeAtRow(idx).getRawType());
  }

  @Test
  public void testVarArgs() {
    assertTrue("Param should be var args", getParamTypeAtRow(15).isVarArgs());
    assertEquals("Name should be without dots", "nl.Varargs",
        getParamNameAtRow(15));
    assertEquals("Raw type should be without dots", "nl.Varargs",
        getParamTypeAtRow(15).getRawType());
  }

  @Test
  public void test2LineParam() {
    assertEquals("Param multi line should have 2 params", 3,
        ((JsTypeList) getParamAtRow(16).getType()).getTypes().size());
  }

  @Test
  public void testGenericParams() {
    final JsType paramType = getParamTypeAtRow(17);
    assertEquals("1st type", "Object", getParamNameAtRow(17));
    final JsType genTyp0 = (JsType) paramType.getGenericType(0);
    assertEquals("2dn rawtype", "Object2.<string, nl.Attribution>",
        genTyp0.getRawType());
    assertEquals("2nd type", "Object2", genTyp0.getName());
    assertEquals("3th type", "string",
        ((JsType) genTyp0.getGenericType(0)).getName());
    assertEquals("3th type, 2nd param", "nl.Attribution",
        ((JsType) genTyp0.getGenericType(1)).getName());
    assertEquals("3th type, 2nd param", "nl.Attribution",
        ((JsType) genTyp0.getGenericType(1)).getRawType());
    assertEquals("2nd type, 2dn param", "string2",
        ((JsType) paramType.getGenericType(1)).getName());
  }

  @Test
  public void testChoiceParams1() {
    final JsTypeList paramType = (JsTypeList) getParamAtRow(18).getType();
    assertEquals("2 choices", 2, paramType.getTypes().size());
    assertTrue("Is optional", paramType.isOptional());
    assertEquals("1st choice", "nl.Object",
        ((JsType) paramType.get(0)).getName());
    assertEquals("2nd choice", "Object",
        ((JsType) paramType.get(1)).getName());
    assertEquals("2nd choice rawtype", "Object.<string, *>",
        paramType.get(1).getRawType());
  }

  @Test
  public void testChoiceParams2() {
    assertEquals("param Object", "Object", getParamNameAtRow(19));
//    final JsTypeList paramType = (JsTypeList) getParamTypeAtRow(19).getTypeList()get(19).getType();
//    assertEquals("2 choices", 2, paramType.getTypes().size());
//    assertEquals("1st choice", "nl.Object",
//        ((JsType) paramType.get(0)).getName());
//    assertEquals("2nd choice", "Object", ((JsType) paramType.get(1)).getName());
//    assertEquals("2nd choice rawtype", "Object.<string, *>",
//        paramType.get(1).getRawType());
  }

  private String getParamNameAtRow(final int idx) {
    return getParamTypeAtRow(idx).getName();
  }

  private JsType getParamTypeAtRow(final int idx) {
    return (JsType) getParamAtRow(idx).getType();
  }

  private JsParam getParamAtRow(final int idx) {
    return jsElement.getParam(idx);
  }
}
