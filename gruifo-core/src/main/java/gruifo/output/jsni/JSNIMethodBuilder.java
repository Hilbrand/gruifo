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

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec.Builder;

import gruifo.lang.java.JClass;
import gruifo.lang.java.JMethod;
import gruifo.lang.java.JParam;
import gruifo.output.util.PrintUtil;

public class JSNIMethodBuilder {

  public void buildMethods(final Builder builder, final JClass jFile) {
    final boolean notAnInterface = !jFile.isInterface();
    for (final JMethod method : jFile.getMethods()) {
      // ignore abstract methods => FIXME move to transformer
      if (method.isAbstractMethod() && notAnInterface) {
        continue;
      }
      enhanceModifiers(method);
      builder.addMethod(buildMethod(method, notAnInterface));
    }
  }

  private MethodSpec buildMethod(final JMethod method,
      final boolean notAnInterface) {
    return MethodSpec.methodBuilder(method.getMethodName())
    .addJavadoc("%s", method.getJsDoc())
    .addModifiers(method.getModifiers())
    .addParameters(buildParameters(method))
//    .addTypeVariable(method.getReturn())
//    .addParameter(typeVariableName, "t") // you can also add modifiers
    .addCode("%s", buildCodeBlock(method, notAnInterface))
    .build();
  }

  private String buildCodeBlock(final JMethod method,
      final boolean notAnInterface) {
    final StringBuffer buffer = new StringBuffer();
    if (!method.isAbstractMethod() && notAnInterface) {
      buffer.append(" /*-{");
      buffer.append(PrintUtil.NL);
      buildMethodBody(buffer, method);
      buffer.append(PrintUtil.NL);
      buffer.append("}-*/");
    }
    buffer.append(';');
    return buffer.toString();
  }

  private void buildMethodBody(final StringBuffer buffer, final JMethod method) {
    buffer.append(isVoidType(method) ? "" : "return ");
    buffer.append("this.");
    buffer.append(method.getMethodName());
    buffer.append('(');
    printMethodParam(buffer, method, false);
    buffer.append(");");
    PrintUtil.nl(buffer);
  }

  private Iterable<ParameterSpec> buildParameters(final JMethod method) {
    // TODO Auto-generated method stub
    return null;
  }

//  private void printMethods(final StringBuffer buffer, final int indent,
//      final JClass jFile) {
//    for (final JMethod method : jFile.getMethods()) {
//      // ignore abstract methods => FIXME move to transformer
//      if (method.isAbstractMethod() && !jFile.isInterface()) {
//        continue;
//      }
//      PrintUtil.indent(buffer, method.getJsDoc(), indent);
//      PrintUtil.indent(buffer, indent);
////      printModifiers(buffer, jFile, method);
//      if (method.getGenericType() != null) {
//        buffer.append('<');
//        buffer.append(method.getGenericType());
//        buffer.append(" extends ");
//        buffer.append(TypeMapper.GWT_JAVA_SCRIPT_OBJECT); //FIXME not hardcode extends generics
//        buffer.append("> ");
//      }
//      buffer.append(method.getReturn());
//      buffer.append(' ');
//      buffer.append(method.getMethodName());
//      buffer.append('(');
//      printMethodParam(buffer, method, true);
//      buffer.append(')');
//      if (method.isAbstractMethod() || jFile.isInterface()) {
//        buffer.append(';');
//      } else {
//        buffer.append(" /*-{");
//        PrintUtil.nl(buffer);
//        printMethodBody(buffer, indent + 1, method);
//        PrintUtil.indent(buffer, indent);
//        buffer.append("}-*/;");
//      }
//      PrintUtil.nl2(buffer);
//    }
//  }

  // @deprecated TODO move to Transformer, should not run on interfaces
  @Deprecated
  private void enhanceModifiers(final JMethod method) {
    if (!method.contains(Modifier.ABSTRACT)) {
      method.addModifier(Modifier.FINAL);
      method.addModifier(Modifier.NATIVE);
    }
  }

  private void printMethodParam(final StringBuffer buffer,
      final JMethod method, final boolean withType) {
    boolean first = true;
    for (final JParam param : method.getParams()) {
      if (!first) {
        buffer.append(", ");
      }
      if (withType) {
        buffer.append(param.getType());
        buffer.append(' ');
      }
      buffer.append(param.getName());
      first = false;
    }
  }

//  private void printMethodBody(final StringBuffer buffer,
//      final int indent, final JMethod method) {
//    PrintUtil.indent(buffer, indent);
//    buffer.append(isVoidType(method) ? "" : "return ");
//    buffer.append("this.");
//    buffer.append(method.getMethodName());
//    buffer.append('(');
//    printMethodParam(buffer, method, false);
//    buffer.append(");");
//    PrintUtil.nl(buffer);
//  }

  private boolean isVoidType(final JMethod method) {
    return TypeName.VOID.equals(method.getReturn().unbox());
  }
}
