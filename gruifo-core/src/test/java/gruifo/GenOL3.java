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
package gruifo;

import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.junit.Ignore;
import org.junit.Test;

public class GenOL3 {

  private static final String OL3_PATH = "D:/oss/";
  private static final String GWT_OL3_PATH = "D:/workspace/gen-gwt-wrapper-js/";
  private static final String SINGLE_FILE = "geom/multilinestring";

  @Test
  public void testGenOL3() throws IOException, ParseException {
    final String target = GWT_OL3_PATH + "gwt-ol3-jnsi/src/main/java";
    final String src = OL3_PATH + "ol3/src/ol/;"
        + OL3_PATH + "ol3/externs/olx.js;"
        + OL3_PATH + "ol3/externs/oli.js";
    final String typeMappingFile =
        getClass().getResource("type_mapper.properties").getPath();
    GruifoCli.main(new String[] {"-src", src, "-target", target,
        "-type_mapping", typeMappingFile, });
  }

  @Ignore
  @Test
  public void testSingleOL3() throws IOException, ParseException {
    final String target = GWT_OL3_PATH + "gwt-ol3-jnsi/src/main/java";
    final String src = OL3_PATH + "ol3/src/ol/" + SINGLE_FILE + ".js";
    final String typeMappingFile =
        getClass().getResource("type_mapper.properties").getPath();
    GruifoCli.main(new String[] {"-src", src, "-target", target,
        "-type_mapping", typeMappingFile, });
  }
}
