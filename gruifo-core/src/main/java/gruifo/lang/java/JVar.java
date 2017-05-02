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

import java.util.EnumSet;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.TypeName;

/**
 *
 */
public class JVar {

  private TypeName type;
  private final String name;
  private final EnumSet<Modifier> modifiers;
  private String javaDoc;

  public JVar(final String name, final TypeName type) {
    this(name, type, EnumSet.noneOf(Modifier.class));
  }

  public JVar(final String name, final TypeName type,
      final EnumSet<Modifier> modifiers) {
    this.name = name;
    this.type = type;
    this.modifiers = modifiers;
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

  public void setJavaDoc(final String javaDoc) {
    this.javaDoc = javaDoc;
  }

  public void setType(final TypeName type) {
    this.type = type;
  }

  public void addModifier(final Modifier modifier) {
    modifiers.add(modifier);
  }

  public boolean contains(final Modifier modifier) {
    return modifiers.contains(modifier);
  }

  public EnumSet<Modifier> getModifiers() {
    return modifiers;
  }

  public boolean isAbstract() {
    return modifiers.contains(Modifier.ABSTRACT);
  }

  public boolean isFinal() {
    return modifiers.contains(Modifier.FINAL);
  }

  public boolean isStatic() {
    return modifiers.contains(Modifier.STATIC);
  }

  public void setAbstract(final boolean abstractModifier) {
    setModifier(abstractModifier, Modifier.ABSTRACT);
  }

  public void setFinal(final boolean finalModifier) {
    setModifier(finalModifier, Modifier.FINAL);
  }

  public void setStatic(final boolean staticModifier) {
    setModifier(staticModifier, Modifier.STATIC);
  }

  private void setModifier(final boolean add, final Modifier modifier) {
    if (add) {
      modifiers.add(modifier);
    } else {
      modifiers.remove(modifier);
    }
  }

  @Override
  public String toString() {
    return "JParam [type=" + type + ", name=" + name + ", modifiers="
        + modifiers + ", javaDoc=" + javaDoc + "]";
  }
}
