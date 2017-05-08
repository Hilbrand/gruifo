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
public class JsElement implements Cloneable {

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
  private JsTypeObject extendsType;
  private final List<JsTypeObject> implementsTypes = new ArrayList<>();
  private List<JsParam> params = new ArrayList<>();
  private List<JsParam> typeDef = new ArrayList<>();
  private JsTypeObject type;
  private JsTypeObject returnType;
  private boolean override;
  private String genericType;
  private JsTypeObject define;
  private JsTypeObject enumType;

  public void addImplements(final JsTypeObject jsTypeObject) {
    implementsTypes.add(jsTypeObject);
  }

  @Override
  public JsElement clone() {
    final JsElement clone = new JsElement();
    clone.modifier = modifier;
    clone.elementType = elementType;
    clone.classDesc = classDesc;
    clone.jsDoc = jsDoc;
    clone.extendsType = extendsType;
    clone.implementsTypes.addAll(implementsTypes);
    clone.params.addAll(params);
    clone.typeDef = new ArrayList<>(typeDef);
    clone.type = type;
    clone.returnType = returnType;
    clone.override = override;
    clone.genericType = genericType;
    clone.define = define;
    clone.enumType = enumType;
    return clone;
  }

  public String getJsDoc() {
    return jsDoc;
  }

  public Modifier getModifier() {
    return modifier;
  }

  public JsTypeObject getDefine() {
    return define;
  }

  public JsTypeObject getEnumType() {
    return enumType;
  }

  public JsTypeObject getExtends() {
    return extendsType;
  }

  public String getGenericType() {
    return genericType;
  }

  public List<JsTypeObject> getImplements() {
    return implementsTypes;
  }

  public JsParam getParam(final int idx) {
    return params.get(idx);
  }

  public List<JsParam> getParams() {
    return params;
  }

  public JsTypeObject getType() {
    return type;
  }

  public List<JsParam> getTypeDef() {
    return typeDef;
  }

  public JsTypeObject getReturn() {
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

  public void setDefine(final JsTypeObject jsTypeObject) {
    this.define = jsTypeObject;
    elementType = ElementType.DEFINE;
  }

  public void setEnum(final JsTypeObject jsTypeObject) {
    elementType = ElementType.ENUM;
    enumType = jsTypeObject;
  }

  public void setExtends(final JsTypeObject jsTypeObject) {
    this.extendsType = jsTypeObject;
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

  public void setParams(final List<JsParam> params) {
    this.params = params;
  }

  public void setReturn(final JsTypeObject jsTypeObject) {
    this.returnType = jsTypeObject;
  }

  public void setType(final JsTypeObject jsTypeObject) {
    this.type = jsTypeObject;
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
