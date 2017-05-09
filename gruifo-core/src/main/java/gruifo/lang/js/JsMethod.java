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

import java.util.EnumSet;
import java.util.List;

import javax.lang.model.element.Modifier;

/**
 * Data class to store the JavaScript method data.
 */
public class JsMethod implements Cloneable {

  private final String packageName;
  private final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

  private JsElement element;
  private String functionName;

  public JsMethod(final String packageName, final String functionName) {
    this.packageName = packageName;
    this.functionName = functionName;
  }

  @Override
  public JsMethod clone() {
    final JsMethod clone = new JsMethod(getPackageName(), getMethodName());
    clone.element = element == null ? null : element.clone();
    clone.modifiers.addAll(modifiers);
    return clone;
  }

  public EnumSet<Modifier> getModifiers() {
    return modifiers;
  }

  public String getPackageName() {
    return packageName;
  }

  public List<JsParam> getParams() {
    return element.getParams();
  }

  public JsElement getElement() {
    return element;
  }

  public String getMethodName() {
    return functionName;
  }

  public boolean isAbstractMethod() {
    return modifiers.contains(Modifier.ABSTRACT) || element.isAbstract();
  }

  public boolean isStaticMethod() {
    return modifiers.contains(Modifier.ABSTRACT);
  }

  public void setAbstract(final boolean abstractMethod) {
    modifiers.add(Modifier.ABSTRACT);
  }

  public void setElement(final JsElement element) {
    this.element = element;
    modifiers.add(element.getModifier());
  }

  public void setMethodName(final String methodName) {
    this.functionName = methodName;
  }

  public void setParams(final List<JsParam> params) {
    element.setParams(params);
  }

  public void setStaticMethod(final boolean staticMethod) {
    modifiers.add(Modifier.STATIC);
  }

  @Override
  public String toString() {
    return "JsMethod [element=" + element + ", methodName=" + functionName
        + ", packageName=" + packageName + ", modifers=" + modifiers.toString()
        + "]";
  }
}
