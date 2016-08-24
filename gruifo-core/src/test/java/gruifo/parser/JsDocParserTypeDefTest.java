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
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import gruifo.lang.js.JsType;
import gruifo.lang.js.JsTypeList;
import gruifo.lang.js.JsTypeObject;

/**
 * Test class for {@link JavaScriptDocParser} for typedef annotation.
 */
public class JsDocParserTypeDefTest extends JsDocParserTestBase {

  public JsDocParserTypeDefTest() throws IOException {
    super("jsdoc-typedef");
  }

  @Test
  public void testSingleLineTypeDef() {
    assertEquals("Size of typedef fields", 13, jsElement.getTypeDef().size());
  }

  @Test
  public void testMultiTypeTypedefField() {
    final List<JsTypeObject> choices =
        ((JsTypeList) jsElement.getTypeDef().get(9).getType()).getTypes();
    assertEquals("Typedef field with 1 undefined should have 2 types: "
        + choices, 2, choices.size());
    for (final JsTypeObject jse : choices) {
      assertFalse("Types should not contain '(':" + jse,
          jse.getRawType().contains("("));
      assertFalse("Types should not contain ')':" + jse,
          jse.getRawType().contains(")"));
    }
  }

  @Test
  public void testCorrectlyParseGeneric() {
    final List<JsTypeObject> object =
        ((JsType) jsElement.getTypeDef().get(10).getType()).getTypeList();
    assertEquals("Typedef field has 2 generics: " + object, 2, object.size());
    assertEquals("Second should have 2 choices", 2,
        ((JsTypeList) object.get(1)).getTypes().size());
  }

  @Test
  public void testCorrectlyParseNotNull() {
    final List<JsTypeObject> choices =
        ((JsTypeList) jsElement.getTypeDef().get(11).getType()).getTypes();
    assertEquals("Typedef field has 2 types: " + choices, 2, choices.size());
    for (final JsTypeObject jse : choices) {
      assertFalse("Types should not contain '!':" + jse,
          jse.getRawType().contains("!"));
    }
  }
}
