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
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import gruifo.lang.java.JClass;
import gruifo.lang.java.JVar;
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

  public void buildFields(final TypeSpec.Builder builder, final JClass jFile) {
    if (!jFile.isInterface()) {
      buildFieldMethods(builder, jFile.getFields());
    }
  }

  private void buildFieldMethods(final TypeSpec.Builder builder,
      final List<JVar> list) {
    for (final JVar field : list) {
      builder.addMethod(createGetterMethod(field));
      if (!field.isFinal()) {
        builder.addMethod(createSetterMethod(field));
      }
    }
  }

  private MethodSpec createGetterMethod(final JVar field) {
    final String methodName = getPrefix(field) + getFieldAsMethodName(field);
    return buildMethod(methodName, field.getType(), field.isStatic(),
        buildGetterCodeBlock(field), "Getter for " + field.getName()).build();
  }

  private String getPrefix(final JVar field) {
    return field.getType() == TypeName.BOOLEAN ? "is" : "get";
  }

  private MethodSpec createSetterMethod(final JVar field) {
    final String methodName = "set" + getFieldAsMethodName(field);
    final Builder builder = buildMethod(methodName, TypeName.VOID,
        field.isStatic(), buildSetterCodeBlock(field),
        "Setter for " + field.getName());
    builder.addParameter(field.getType(), getFieldName(field));
    return builder.build();
  }

  private Builder buildMethod(final String methodName,
      final TypeName returnType, final boolean isStatic, final String codeBlock,
      final String jsDoc) {
    return MethodSpec.methodBuilder(methodName)
        .addJavadoc(jsDoc + "\n")
        .addModifiers(isStatic ? STATIC_MODIFIERS : MODIFIERS)
        .returns(returnType)
        .addCode(codeBlock);
  }

  private String buildGetterCodeBlock(final JVar field) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append(" /*-{");
    PrintUtil.nl(buffer);
    buffer.append("$>return ");
    printFieldVariable(buffer, field);
    buffer.append(';');
    PrintUtil.nl(buffer);
    buffer.append("$<}-*/");
    return buffer.toString();
  }

  private String buildSetterCodeBlock(final JVar field) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append(" /*-{");
    PrintUtil.nl(buffer);
    buffer.append("$>");
    printFieldVariable(buffer, field);
    buffer.append(" = ");
    buffer.append(getFieldName(field));
    buffer.append(';');
    PrintUtil.nl(buffer);
    buffer.append("$<}-*/");
    return buffer.toString();
  }

  private String getFieldAsMethodName(final JVar field) {
    return PrintUtil.firstCharUpper(getFieldName(field));
  }

  private String getFieldName(final JVar field) {
    String name = field.getName();
    if (field.isStatic()) {
      final int classSep = name.lastIndexOf('.');
      name = name.substring(classSep + 1);
    }
    return name;
  }

  private void printFieldVariable(final StringBuffer buffer, final JVar field) {
    if (field.isStatic()) {
      buffer.append("$$wnd.");
      buffer.append(field.getName());
    } else {
      buffer.append("this['");
      buffer.append(field.getName());
      buffer.append("']");
    }
  }
}
