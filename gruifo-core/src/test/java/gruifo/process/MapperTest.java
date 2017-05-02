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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.google.gson.JsonSyntaxException;

/**
 * Test class for {@link Mapper}.
 */
public class MapperTest {

  private static final Charset DEFAULT =
      Charset.forName(StandardCharsets.UTF_8.name());

  @Test
  public void testMapper() throws JsonSyntaxException, IOException {
    final Mapper mapper = new Mapper(new File(
        getClass().getResource("type_mapper.json").getFile()), DEFAULT);
    assertEquals("",
        "com.google.gwt.core.client.JavaScriptObject",
        mapper.replace("ArrayBuffer"));
  }

}
