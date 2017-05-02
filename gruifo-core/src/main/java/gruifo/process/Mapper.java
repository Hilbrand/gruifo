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

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

class Mapper {

  private final Gson gson = new Gson();
  private final Mappings mapper;

  public Mapper(final File mapperFile, final Charset charSet)
      throws JsonSyntaxException, IOException {
    mapper = gson.fromJson(
        FileUtils.readFileToString(mapperFile, charSet), Mappings.class);
  }

  public String replace(final String string1, final String string2,
      final String string3) {
    return replace(string1, join(string2, string3));
  }

  public String replace(final String string1, final String string2) {
    return replace(join(string1, string2));
  }

  public String replace(final String string) {
    return mapper.getReplace().get(string);
  }

  public boolean skip(final String fullClassName, final String methodName) {
    return skip(join(fullClassName, methodName));
  }

  public boolean skip(final String string) {
    return mapper.getSkip().contains(string);
  }

  public void replaceType() {
    mapper.getType();
  }

  private String join(final String string1, final String string2) {
    return string1 + '#' + string2;
  }
}
