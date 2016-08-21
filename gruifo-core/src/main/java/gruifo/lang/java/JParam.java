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
package gruifo.lang.java;

import com.squareup.javapoet.TypeName;

public class JParam {

  private TypeName type;
  private final String name;
  private boolean multiField;
  private boolean _static;
  private boolean _final;
  private String javaDoc;

  public JParam(final String name, final TypeName type) {
    this.name = name;
    this.type = type;
  }

  public String getJavaDoc() {
    return javaDoc;
  }

  public String getName() {
    return name;
  }

  public TypeName getType() {
    return type;
  }

  public boolean isFinal() {
    return _final;
  }

  public boolean isMultiField() {
    return multiField;
  }

  public boolean isStatic() {
    return _static;
  }

  public void setFinal(final boolean _final) {
    this._final = _final;
  }

  public void setJavaDoc(final String javaDoc) {
    this.javaDoc = javaDoc;
  }

  public void setMultiField(final boolean multiField) {
    this.multiField = multiField;
  }

  public void setStatic(final boolean _static) {
    this._static = _static;
  }

  public void setType(final TypeName type) {
    this.type = type;
  }
}
