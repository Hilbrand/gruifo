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

/**
 *
 */
public class JsTypeObject {

  private final String rawType;
  private boolean _function;
  private boolean optional;
  private boolean notNull;
  private boolean canNull;
  private boolean varArgs;

  protected JsTypeObject(final String rawType) {
    this.rawType = rawType;
  }

  public String getRawType() {
    return rawType;
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
