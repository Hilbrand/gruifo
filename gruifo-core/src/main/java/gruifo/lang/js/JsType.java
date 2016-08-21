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
public class JsType {

  private final List<JsType> choices = new ArrayList<>();
  private final List<JsType> typeList = new ArrayList<>();
  private final String name;
  private final String rawType;
  private boolean _function;
  private boolean optional;
  private boolean notNull;
  private boolean canNull;
  private boolean varArgs;

  public JsType(final String rawType) {
    this(rawType, null);
  }

  public JsType(final String name, final String rawType) {
    this.rawType = rawType;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void addChoice(final JsType type) {
    choices.add(type);
  }

  public void addChoices(final List<JsType> choices) {
    this.choices.addAll(choices);
  }

  public JsType getChoice(final int idx) {
    return choices.get(idx);
  }

  public List<JsType> getChoices() {
    return choices;
  }

  public void addGenericType(final JsType type) {
    typeList.add(type);
  }

  public void addGenericTypes(final List<JsType> types) {
    if (types != null) {
      typeList.addAll(types);
    }
  }

  public JsType getGenericType(final int idx) {
    return typeList.get(idx);
  }

  public boolean isGeneric() {
    return !typeList.isEmpty();
  }

  public String getRawType() {
    return rawType;
  }

  public List<JsType> getTypeList() {
    return typeList;
  }

  public boolean isCanNull() {
    return canNull;
  }

  public boolean isFunction() {
    return _function;
  }

  public boolean isOptional() {
    return optional;
  }

  public boolean isNotNull() {
    return notNull;
  }

  public boolean isVarArgs() {
    return varArgs;
  }

  public void setFunction(final boolean _function) {
    this._function = _function;
  }

  /**
   * Type is optional.
   * @param optional
   */
  public void setOptional(final boolean optional) {
    this.optional = optional;
  }

  /**
   * Value may not be null.
   * @param notNull
   */
  public void setNotNull(final boolean notNull) {
    this.notNull = notNull;
  }

  /**
   * Value may be null.
   * @param canNull
   */
  public void setNull(final boolean canNull) {
    this.canNull = canNull;
  }

  /**
   * Type is a var args type.
   * @param varArgs
   */
  public void setVarArgs(final boolean varArgs) {
    this.varArgs = varArgs;
  }

  @Override
  public String toString() {
    return getRawType();
  }
}