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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JClass {

  public static class EnumValue {
    private final String javaDoc;
    private final String name;
    private final String type;

    public EnumValue(final String name, final String type, final String javaDoc) {
      this.name = name;
      this.type = type;
      this.javaDoc = javaDoc;
    }
    public String getJavaDoc() {
      return javaDoc;
    }
    public String getName() {
      return name;
    }
    public String getType() {
      return type;
    }
  }
  private final String packageName;
  private final String classOrInteraceName;
  private final Set<String> imports = new HashSet<>();
  private final List<JParam> fields = new ArrayList<>();;
  private final List<JMethod> methods = new ArrayList<>();
  private final List<EnumValue> enumValues = new ArrayList<>();
  private String headerComment = "";
  private final List<JClass> innerJFil = new ArrayList<>();
  private boolean _static;
  private String classDescription;

  private final List<JMethod> constructors = new ArrayList<>();
  private String _extends;
  private String classGeneric;
  private boolean dataClass;

  public JClass(final String packageName, final String className) {
    this.packageName = packageName;
    this.classOrInteraceName = className;
  }

  public void addConstructor(final JMethod constructor) {
    constructors.add(constructor);
  }

  public void addEnumValue(final String name, final String value,
      final String jsDoc) {
    enumValues.add(new EnumValue(name, value, jsDoc));
  }

  public JParam addField(final String name, final String type) {
    final JParam jParam = new JParam(name, type);
    fields.add(jParam);
    return jParam;
  }

  public void addInnerJFile(final JClass JClass) {
    _static = true;
    innerJFil.add(JClass);
  }

  public void addMethod(final JMethod method) {
    methods.add(method);
  }


  public void setExtends(final String _extends) {
    this._extends = _extends;
  }

  public String getClassOrInterfaceName() {
    return classOrInteraceName;
  }

  public String getClassDescription() {
    return classDescription;
  }

  public List<EnumValue> getEnumValues() {
    return enumValues;
  }

  public List<JParam> getFields() {
    return fields;
  }

  public String getFullClassName() {
    return packageName + "." + classOrInteraceName;
  }

  public String getHeaderComment() {
    return headerComment;
  }

  public Set<String> getImports() {
    return imports;
  }

  public List<JClass> getInnerJFiles() {
    return innerJFil;
  }

  public List<JMethod> getMethods() {
    return methods;
  }

  public String getPackageName() {
    return packageName;
  }

  public boolean isStatic() {
    return _static;
  }

  public void setClassDescription(final String classDescription) {
    this.classDescription = classDescription;
  }

  public void setHeaderComment(final String headerComment) {
    this.headerComment = headerComment;
  }

  public void setStatic(final boolean _static) {
    this._static = _static;
  }

  public String getClassGeneric() {
    return classGeneric;
  }

  public List<JMethod> getConstructors() {
    return constructors;
  }

  public String getExtends() {
    return _extends;
  }

  public boolean isDataClass() {
    return dataClass;
  }

  public void setClassGeneric(final String classGeneric) {
    this.classGeneric = classGeneric;

  }

  public void setDataClass(final boolean dataClass) {
    this.dataClass = dataClass;
  }
}
