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

import javax.lang.model.element.Modifier;

/**
 * Data class to store the JavaScript method data.
 */
public class JsMethod {

  private final String packageName;
  private final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

  private JsElement element;
  private String methodName;

  public JsMethod(final String packageName, final String functionName) {
    this.packageName = packageName;
    methodName = functionName;
  }

  public Modifier getModifier() {
    return element.getModifier();
  }

  public String getPackageName() {
    return packageName;
  }

  public JsElement getElement() {
    return element;
  }

  public String getMethodName() {
    return methodName;
  }

  public boolean isAbstractMethod() {
    return modifiers.contains(Modifier.ABSTRACT);
  }

  public boolean isStaticMethod() {
    return modifiers.contains(Modifier.ABSTRACT);
  }

  public void setAbstract(final boolean abstractMethod) {
    modifiers.add(Modifier.ABSTRACT);
  }

  public void setElement(final JsElement element) {
    this.element = element;
  }

  public void setMethodName(final String methodName) {
    this.methodName = methodName;
  }

  public void setStaticMethod(final boolean staticMethod) {
    modifiers.add(Modifier.STATIC);
  }

  @Override
  public String toString() {
    return "JsMethod [element=" + element + ", methodName=" + methodName
        + ", packageName=" + packageName + ", modifers=" + modifiers.toString()
        + "]";
  }
}
