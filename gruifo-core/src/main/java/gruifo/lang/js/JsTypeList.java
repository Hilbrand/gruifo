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
package gruifo.lang.js;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class JsTypeList extends JsTypeObject {

  private final List<JsTypeObject> types = new ArrayList<>();

  public JsTypeList(final String rawType) {
    super(rawType);
  }

  public void add(final JsTypeObject type) {
    types.add(type);
  }

  public void addAll(final List<JsTypeObject> jsTypes) {
    types.addAll(jsTypes);
  }

  public JsTypeObject get(final int idx) {
    return types.get(idx);
  }

  public List<JsTypeObject> getTypes() {
    return types;
  }
}
