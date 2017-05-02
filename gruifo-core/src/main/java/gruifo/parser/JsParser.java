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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import gruifo.lang.js.JsElement;
import gruifo.lang.js.JsFile;
import gruifo.lang.js.JsMethod;

/**
 *
 */
public class JsParser {

  private final List<JsMethod> staticMethods = new ArrayList<>();
  private final Map<String, JsElement> staticFields = new HashMap<>();

  /**
   *
   * @param fileName
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   */
  public Collection<JsFile> parseFile(final String fileName)
      throws FileNotFoundException, IOException {
    try (final Reader reader = new BufferedReader(new FileReader(fileName))) {
      final CompilerEnvirons env = new CompilerEnvirons();
      env.setRecordingLocalJsDocComments(true);
      env.setAllowSharpComments(true);
      env.setRecordingComments(true);
      final AstRoot node = new Parser(env).parse(reader, fileName, 1);
      final JavaScriptFileParser parser = new JavaScriptFileParser(fileName);
      node.visitAll(parser);
      staticMethods.addAll(parser.getStaticMethods());
      staticFields.putAll(parser.getConsts());
      return parser.getFiles();
    }
  }

  public Map<String, JsElement> getStaticFields() {
    return staticFields;
  }

  public List<JsMethod> getStaticMethods() {
    return staticMethods;
  }
}
