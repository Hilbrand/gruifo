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

public class JsFile {

  private final List<JsEnum> enumValues = new ArrayList<>();
  private final List<JsField> fields = new ArrayList<>();
  private final List<JsMethod> methods = new ArrayList<>();
  private final List<JsFile> innerJsFiles = new ArrayList<>();
  private final String packageName;
  private final String classOrInteraceName;
  private final boolean _interface;
  private JsElement element;
  private final String orginalFileName;

  public JsFile(final String orginalFileName, final String packageName,
      final String className, final boolean _interface) {
    this.orginalFileName = orginalFileName;
    this.packageName = packageName;
    this.classOrInteraceName = className;
    this._interface = _interface;
  }

  public String getOriginalFileName() {
    return orginalFileName;
  }

  public void addEnumValue(final String name, final String jsDoc) {
    enumValues.add(new JsEnum(name, jsDoc));
  }

  public void addField(final JsField field) {
    fields.add(field);
  }

  public void addMethod(final JsMethod method) {
    methods.add(method);
  }

  public void addInnerJsFile(final JsFile jsFile) {
    innerJsFiles.add(jsFile);
  }

  public String getClassOrInterfaceName() {
    return classOrInteraceName;
  }

  public JsElement getElement() {
    return element;
  }

  public List<JsEnum> getEnumValues() {
    return enumValues;
  }

  public List<JsField> getFields() {
    return fields;
  }

  public List<JsMethod> getMethods() {
    return methods;
  }

  public String getPackageName() {
    return packageName;
  }

  public List<JsFile> getInnerJFiles() {
    return innerJsFiles;
  }

  public boolean isInterface() {
    return _interface;
  }

  public void setElement(final JsElement element) {
    this.element = element;
  }
}
