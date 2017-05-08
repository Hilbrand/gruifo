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
package gruifo.process;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import gruifo.lang.js.JsElement;
import gruifo.lang.js.JsFile;
import gruifo.lang.js.JsMethod;
import gruifo.lang.js.JsParam;
import gruifo.lang.js.JsType;
import gruifo.lang.js.JsTypeObject;
import gruifo.parser.JsTypeParser;

/**
 * Test class for {@link JsMethodSplitser}.
 */
public class JsMethodSplitserTest {

  private final JsMethodSplitser splitser = new JsMethodSplitser();

  /**
   * Tests if methods with optional parameters are correctly split into
   * multiple methods.
   */
  @Test
  public void testOptionalSplit() {
    final JsFile jsFile = createJsF();
    final List<JsParam> params = addMethodForParams(jsFile);
    addDoubleParam(params, "param1", false);
    addDoubleParam(params, "param2", true);
    addDoubleParam(params, "param3", true);
    splitser.splitMethodsInClass(jsFile);
    assertEquals("Should have 3 methods after splits", 3,
        jsFile.getMethods().size());
    assertEquals("Method 0 should have 3 params", 3,
        jsFile.getMethods().get(0).getParams().size());
    assertEquals("Method 1 should have 2 params", 2,
        jsFile.getMethods().get(1).getParams().size());
    assertEquals("Method 2 should have 1 params", 1,
        jsFile.getMethods().get(2).getParams().size());
  }

  @Test
  public void testMultiMethodParam() {
    final JsFile jsFile = createJsF();
    final List<JsParam> params = addMethodForParams(jsFile);
    final JsTypeParser parser = new JsTypeParser();
    addParam(params, parser.parseType("double|int"), "param1", false);
    splitser.splitMethodsInClass(jsFile);
    assertEquals("Should have 2 methods after splits", 2,
        jsFile.getMethods().size());
  }

  @Test
  public void testMultiGenericMethodParam() {
    final JsFile jsFile = createJsF();
    final List<JsParam> params = addMethodForParams(jsFile);
    final JsTypeParser parser = new JsTypeParser();
    addParam(params, parser.parseType("Array.<double|int>"), "param1", false);
    addParam(params, parser.parseType("Array.<double|int>"), "param2", false);
    splitser.splitMethodsInClass(jsFile);
    assertEquals("Should have 4 methods after splits", 4,
        jsFile.getMethods().size());
  }

  private JsFile createJsF() {
    final JsFile jsFile = new JsFile("test.js", "nl.test", "Test", false);
    return jsFile;
  }

  private List<JsParam> addMethodForParams(final JsFile jsFile) {
    final JsMethod method = new JsMethod("nl.test", "method");
    jsFile.addMethod(method);
    method.setElement(new JsElement());
    final List<JsParam> params = new ArrayList<>();
    method.setParams(params);
    return params;
  }
  private void addDoubleParam(final List<JsParam> params, final String name,
      final boolean optional) {
    addParam(params, new JsType("double"), name, optional);
  }

  private void addParam(final List<JsParam> params, final JsTypeObject type,
      final String name, final boolean optional) {
    type.setOptional(optional);
    params.add(new JsParam(name, type));
  }

}
