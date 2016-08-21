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

import javax.lang.model.element.Modifier;

/**
 * Represents JavaScript element.
 */
public class JsElement {

  public static enum ElementType {
    CLASS,
    CONST,
    CONSTRUCTOR,
    DEFINE,
    ENUM,
    INTERFACE,
    METHOD,
    TYPEDEF;
  }

  private Modifier modifier = Modifier.PUBLIC;
  private ElementType elementType = ElementType.METHOD;
  private boolean classDesc;
  private String jsDoc;
  private JsType extendsType;
  private final List<JsType> implementsTypes = new ArrayList<>();
  private final List<JsParam> params = new ArrayList<>();
  private List<JsParam> typeDef;
  private JsType type;
  private JsType returnType;
  private boolean override;
  private String genericType;
  private JsType define;
  private JsType enumType;

  public void addImplements(final JsType parseType) {
    implementsTypes.add(parseType);
  }

  public String getJsDoc() {
    return jsDoc;
  }

  public Modifier getModifier() {
    return modifier;
  }

  public JsType getDefine() {
    return define;
  }

  public JsType getEnumType() {
    return enumType;
  }

  public JsType getExtends() {
    return extendsType;
  }

  public String getGenericType() {
    return genericType;
  }

  public List<JsType> getImplements() {
    return implementsTypes;
  }

  public JsParam getParam(final int idx) {
    return params.get(idx);
  }

  public List<JsParam> getParams() {
    return params;
  }

  public JsType getType() {
    return type;
  }

  public List<JsParam> getTypeDef() {
    return typeDef;
  }

  public JsType getReturn() {
    return returnType;
  }

  public boolean isClass() {
    return elementType == ElementType.CONSTRUCTOR;
  }

  public boolean isClassDescription() {
    return classDesc;
  }

  public boolean isConst() {
    return elementType == ElementType.CONST;
  }

  public boolean isConstructor() {
    return elementType == ElementType.CONSTRUCTOR;
  }

  public boolean isDefine() {
    return elementType == ElementType.DEFINE;
  }

  public boolean isEnum() {
    return elementType == ElementType.ENUM;
  }

  public boolean isOverride() {
    return override;
  }

  public boolean isInterface() {
    return elementType == ElementType.INTERFACE;
  }

  public boolean isMethod() {
    return elementType == ElementType.METHOD;
  }

  public boolean isPrivate() {
    return modifier == Modifier.PRIVATE;
  }

  public boolean isProtected() {
    return modifier == Modifier.PROTECTED;
  }

  public boolean isTypeDef() {
    return typeDef != null;
  }

  public void setClassDesc() {
    classDesc = true;
  }

  public void setConst() {
    elementType = ElementType.CONST;
  }

  public void setConstructor() {
    elementType = ElementType.CONSTRUCTOR;
  }

  public void setDefine(final JsType define) {
    this.define = define;
    elementType = ElementType.DEFINE;
  }

  public void setEnum(final JsType jsType) {
    elementType = ElementType.ENUM;
    enumType = jsType;
  }

  public void setExtends(final JsType _extends) {
    this.extendsType = _extends;
  }

  public void setGenericType(final String genericType) {
    this.genericType = genericType;
  }

  public void setJsDoc(final String jsDoc) {
    this.jsDoc = jsDoc;
  }

  public void setOverride() {
    this.override = true;
  }

  public void setInterface() {
    elementType = ElementType.INTERFACE;
  }

  public void setMethod() {
    elementType = ElementType.METHOD;
  }

  public void setPrivate() {
    modifier = Modifier.PRIVATE;
  }

  public void setProtected() {
    modifier = Modifier.PROTECTED;
  }

  public void setReturn(final JsType _return) {
    this.returnType = _return;
  }

  public void setType(final JsType type) {
    this.type = type;
  }

  public void setTypeDef(final List<JsParam> typeDef) {
    elementType = ElementType.TYPEDEF;
    this.typeDef = typeDef;
  }

  @Override
  public String toString() {
    return "JsElement [accessType=" + modifier + ", elementType="
        + elementType + ", classDesc=" + classDesc + ", _extends="
        + extendsType + ", params=" + params + ", typeDef=" + typeDef
        + ", type=" + type + ", _return=" + returnType + ", override="
        + override + ", genericType=" + genericType + ", define=" + define
        + ", comment=" + jsDoc + "]";
  }
}
