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
 * Data class representing a type as specified in the JsDoc.
 */
public class JsType extends JsTypeObject {

  private final List<JsTypeObject> typeList = new ArrayList<>();
  private String name;

  public JsType(final String rawType) {
    this(null, rawType);
  }

  public JsType(final String name, final String rawType) {
    super(rawType);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void addGenericType(final JsType type) {
    typeList.add(type);
  }

  public void addGenericTypes(final List<JsTypeObject> types) {
    if (types != null) {
      typeList.addAll(types);
    }
  }

  public JsTypeObject getGenericType(final int idx) {
    return typeList.get(idx);
  }

  public boolean isGeneric() {
    return !typeList.isEmpty();
  }

  public List<JsTypeObject> getTypeList() {
    return typeList;
  }
}
