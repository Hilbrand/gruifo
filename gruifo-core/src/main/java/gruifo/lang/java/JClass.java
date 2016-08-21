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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

/**
 * Data class containing the transformed JavaScript class as a Java class.
 */
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
  private String classDescription;

  private final List<JMethod> constructors = new ArrayList<>();
  private final List<TypeVariableName> typeVariables = new ArrayList<>();
  private final List<TypeName> superinterfaces = new ArrayList<>();
  private TypeName superClass;
  private boolean dataClass;
  private boolean _interface;
  private final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

  public JClass(final String packageName, final String className) {
    this.packageName = packageName;
    this.classOrInteraceName = className;
    modifiers.add(Modifier.PUBLIC);
  }

  public void addConstructor(final JMethod constructor) {
    constructors.add(constructor);
  }

  public void addEnumValue(final String name, final String value,
      final String jsDoc) {
    enumValues.add(new EnumValue(name, value, jsDoc));
  }

  public JParam addField(final String name, final TypeName type) {
    final JParam jParam = new JParam(name, type);
    fields.add(jParam);
    return jParam;
  }

  public void addSuperinterface(final TypeName implementsType) {
    superinterfaces.add(implementsType);
  }

  public void addInnerJFile(final JClass JClass) {
    setStatic(true);
    innerJFil.add(JClass);
  }

  public void addMethod(final JMethod method) {
    methods.add(method);
    modifiers.addAll(method.getModifiers());
  }

  public String getClassDescription() {
    return classDescription;
  }

  public String getClassOrInterfaceName() {
    return classOrInteraceName;
  }

  public List<JMethod> getConstructors() {
    return constructors;
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

  public Modifier[] getModifiers() {
    return modifiers.toArray(new Modifier[0]);
  }

  public String getPackageName() {
    return packageName;
  }

  public TypeName getSuperClass() {
    return superClass;
  }

  public Iterable<? extends TypeName> getSuperinterfaces() {
    return superinterfaces;
  }

  public List<TypeVariableName> getTypeVariables() {
    return typeVariables;
  }

  public boolean hasAbstractMethods() {
    for (final JMethod jMethod : methods) {
      if (jMethod.isAbstractMethod()) {
        return true;
      }
    }
    return false;
  }

  public boolean isDataClass() {
    return dataClass;
  }

  public boolean isInterface() {
    return this._interface;
  }

  public boolean isStatic() {
    return modifiers.contains(Modifier.STATIC);
  }

  public void setClassDescription(final String classDescription) {
    this.classDescription = classDescription;
  }

  // true if not a @class annotation but a @typdef annotation
  public void setDataClass(final boolean dataClass) {
    this.dataClass = dataClass;
  }

  public void setHeaderComment(final String headerComment) {
    this.headerComment = headerComment;
  }

  public void setInterface(final boolean _interface) {
    this._interface = _interface;
  }

  public void setSuperclass(final TypeName superClass) {
    this.superClass = superClass;
  }

  public void setStatic(final boolean staticClass) {
    if (staticClass) {
      modifiers.add(Modifier.STATIC);
    } else {
      modifiers.remove(Modifier.STATIC);
    }
  }

  @Override
  public String toString() {
    return "JClass [packageName=" + packageName + ", classOrInteraceName="
        + classOrInteraceName + "]";
  }
}
