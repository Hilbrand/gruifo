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
package gengwtjs.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gengwtjs.lang.js.JsElement;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link JavaScriptDocParser}.
 */
public class JavaScriptDocParserTest {

  private static final String COMMENT1 = "comment1";
  private static final String COMMENT2 = "comment2";
  private JsElement docs1;
  private JsElement docs2;

  @Before
  public void parseFiles() throws IOException {
    docs1 = parseFile(COMMENT1);
    docs2 = parseFile(COMMENT2);
  }

  private JsElement parseFile(final String comment) throws IOException {
    final URL commentFile =
        JavaScriptDocParserTest.class.getResource(comment + ".txt");
    final Path file = new File(commentFile.getPath()).toPath();
    final String comment1 = new String(Files.readAllBytes(file));
    final JavaScriptDocParser parser = new JavaScriptDocParser();
    return parser.parse(file.toString(), comment1);
  }

  @Test
  public void testParser() {
    assertTrue("class description", docs1.isClassDescription());
    assertTrue("constructor", docs1.isConstructor());
    assertTrue("protected", docs1.isProtected());
  }

  @Test
  public void testExtends() {
    assertEquals("extends", "nl.Object",
        docs1.getExtends().getTypes().get(0).getName());
  }

  @Test
  public void testParam() {
    assertEquals("params size", 12, docs1.getParams().size());
    assertEquals("params 0 name",
        "options", docs1.getParams().get(0).getName());
    assertEquals("params 0 type",
        "nl.Options", docs1.getParams().get(0).getType().getTypes().get(0).getName());
    assertTrue("params 1",
        docs1.getParams().get(1).getType().isFunction());
  }

  @Test
  public void testReturn() {
    assertEquals("return type 1", "nl.Object",
        docs1.getReturn().getTypes().get(0).getName());
    assertEquals("return type 2", "undefined",
        docs1.getReturn().getTypes().get(1).getName());
  }

  @Test
  public void testTypeDef() {
    assertEquals("Size of typedef fields", 4, ((List) docs2.getTypeDef()).size());
  }
}
