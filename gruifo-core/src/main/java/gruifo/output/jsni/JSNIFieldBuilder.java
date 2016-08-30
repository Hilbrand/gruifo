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
package gruifo.output.jsni;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import gruifo.lang.java.JParam;
import gruifo.lang.js.JsFile;
import gruifo.lang.js.JsParam;
import gruifo.output.util.PrintUtil;

/**
 * Builds Field members.
 */
class JSNIFieldBuilder {
  private static final Set<Modifier> MODIFIERS =
      EnumSet.of(Modifier.PUBLIC, Modifier.FINAL, Modifier.NATIVE);
  private static final Set<Modifier> STATIC_MODIFIERS =
      EnumSet.of(Modifier.PUBLIC, Modifier.FINAL, Modifier.NATIVE,
          Modifier.STATIC);

  public void buildFields(final TypeSpec.Builder builder, final JsFile jsFile) {
    if (!jsFile.isInterface()) {
      buildFieldMethods(builder, jsFile.getFields());
    }
  }

  private void buildFieldMethods(final TypeSpec.Builder builder,
      final List<JsParam> fields) {
    for (final JsParam field : fields) {
      builder.addMethod(createGetterMethod(field));
      if (!field.getElement().isDefine()) {
        builder.addMethod(createSetterMethod(field));
      }
    }
  }

  private MethodSpec createGetterMethod(final JsParam field) {
    final String methodName = "get" + field.getName();
    return null;
  }

  private MethodSpec createSetterMethod(final JsParam field) {
    final String methodName = "set" + field.getName();
    return null;
  }

  private MethodSpec buildMethod(final String methodName, final String jsDoc) {
    final Set<Modifier> modfilers;
    return MethodSpec.methodBuilder(methodName)
    .addJavadoc("%s", jsDoc)
    .addModifiers(modfilers)
    .addCode("%s", buildCodeBlock(method, notAnInterface))
    .build();
  }

  private void printGetter(final StringBuffer buffer, final int indent,
      final JParam field) {
    PrintUtil.indent(buffer, field.getJavaDoc(), indent);
    PrintUtil.indent(buffer, indent);
    buffer.append("public ");
    if (field.isStatic()) {
      buffer.append("static ");
    }
    buffer.append("final native ");
    buffer.append(field.getType());
    buffer.append(" get");
    printFieldName(buffer, field);
    if (field.isMultiField()) {
      buffer.append(fixMultiTypeField(field));
    }
    buffer.append("() /*-{");
    PrintUtil.nl(buffer);
    PrintUtil.indent(buffer, indent + 1);
    buffer.append("return ");
    printFieldVariable(buffer, field);
    buffer.append(';');
    PrintUtil.nl(buffer);
    PrintUtil.indent(buffer, indent);
    buffer.append("}-*/;");
    PrintUtil.nl2(buffer);
  }

  private String fixMultiTypeField(final JParam field) {
//    final int genericIdx = field.getType().indexOf('<');
//    final String subString = genericIdx < 0 ? field.getType()
//        : field.getType().substring(0, genericIdx);
//    final int dotIdx = subString.lastIndexOf('.');
//    return  PrintUtil.firstCharUpper(
//        dotIdx < 0 ? subString : subString.substring(dotIdx + 1));
    return null;
  }

  private void printSetter(final StringBuffer buffer, final int indent,
      final JParam field) {
    PrintUtil.indent(buffer, field.getJavaDoc(), indent);
    PrintUtil.indent(buffer, indent);
    buffer.append("public ");
    if (field.isStatic()) {
      buffer.append("static ");
    }
    buffer.append("final native void");
    buffer.append(" set");
    printFieldName(buffer, field);
    buffer.append('(');
    buffer.append(field.getType());
    buffer.append(' ');
    printFieldAsVar(buffer, field);
    buffer.append(") /*-{");
    PrintUtil.nl(buffer);
    PrintUtil.indent(buffer, indent + 1);
    printFieldVariable(buffer, field);
    buffer.append(" = ");
    printFieldAsVar(buffer, field);
    buffer.append(';');
    PrintUtil.nl(buffer);
    PrintUtil.indent(buffer, indent);
    buffer.append("}-*/;");
    PrintUtil.nl2(buffer);
  }

  private void printFieldName(final StringBuffer buffer, final JParam field) {
    buffer.append(PrintUtil.firstCharUpper(getFieldName(field)));
  }

  private void printFieldAsVar(final StringBuffer buffer, final JParam field) {
    buffer.append(PrintUtil.firstCharLower(getFieldName(field)));
  }

  private String getFieldName(final JParam field) {
    String name = field.getName();
    if (field.isStatic()) {
      final int classSep = name.lastIndexOf('.');
      name = name.substring(classSep + 1);
    }
    return name;
  }

  private void printFieldVariable(final StringBuffer buffer, final JParam field) {
    if (field.isStatic()) {
      buffer.append("$wnd.");
      buffer.append(field.getName());
    } else {
      buffer.append("this['");
      buffer.append(field.getName());
      buffer.append("']");
    }
  }
}
