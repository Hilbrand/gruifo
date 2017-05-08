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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonSyntaxException;

import gruifo.lang.js.JsField;
import gruifo.lang.js.JsFile;

/**
 *
 */
public class Processor {

  private final JsFilesMapper mapper;
  private final JsMethodSplitser splitter = new JsMethodSplitser();

  public Processor(final File mapperFile, final Charset charSet)
      throws JsonSyntaxException, IOException {
    mapper = new JsFilesMapper(mapperFile, charSet);
  }

  /**
   *
   * @param files
   * @return
   */

  public Collection<JsFile> process(final Collection<JsFile> files) {
    return mapper.mapFiles(
        splitter.splitFiles(groupFiles(prepareFields(files))));
  }

  /**
   *
   * @param files
   * @return
   */
  Collection<JsFile> groupFiles(final Collection<JsFile> files) {
    final Map<String, JsFile> filesMap = new HashMap<>();
    for (final JsFile jsFile : files) {
      filesMap.put(jsFile.getClassOrInterfaceName(), jsFile);
    }
    final Collection<JsFile> groupedFiles = new ArrayList<>();
    for (final JsFile jsFile : files) {
      final String[] split = jsFile.getPackageName().split("\\.");
      if (split.length > 0 && filesMap.containsKey(split[split.length - 1])) {
        final JsFile jsFile2 = filesMap.get(split[split.length - 1]);
        jsFile2.addInnerJsFile(jsFile);
      } else {
        groupedFiles.add(jsFile);
      }
    }
    return groupedFiles;
  }

  /**
   * Remove any fields specified in @typedef if the field is also specified as
   * prototype field in the JavaScript file.
   *
   * @param files
   *          JavaScript parsed files
   * @return same list of JavaScript parsed files
   */
  Collection<JsFile> prepareFields(final Collection<JsFile> files) {
    for (final JsFile jsFile : files) {
      if (jsFile.getElement().isTypeDef()) {
        final List<JsField> typeDefs = jsFile.getElement().getTypeDef();
        for (final JsField field : jsFile.getFields()) {
          for (int i = 0; i < typeDefs.size(); i++) {
            if (field.getName().equals(typeDefs.get(i).getName())) {
              typeDefs.remove(i);
              break;
            }
          }
        }
      }
    }
    return files;
  }
}
