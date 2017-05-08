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
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec.Builder;

import gruifo.lang.java.JClass;
import gruifo.lang.java.JMethod;
import gruifo.lang.java.JVar;
import gruifo.output.util.PrintUtil;

public class JSNIMethodBuilder {

  public void buildMethods(final Builder builder, final JClass jFile) {
    final boolean notAnInterface = !jFile.isInterface();
    for (final JMethod method : jFile.getMethods()) {
      // ignore abstract methods => FIXME move to transformer
      if (method.isAbstract() && notAnInterface) {
        continue;
      }
      enhanceModifiers(method);
      builder.addMethod(buildMethod(method, notAnInterface));
    }
  }

  private MethodSpec buildMethod(final JMethod method,
      final boolean notAnInterface) {
    final MethodSpec.Builder builder = MethodSpec.methodBuilder(method.getName())
            .addJavadoc(method.getJavaDoc())
            .addModifiers(method.getModifiers())
            .returns(method.getType())
            .addCode(buildCodeBlock(method, notAnInterface));
    builder.varargs(buildParameters(builder, method));
    return builder.build();
  }

  private String buildCodeBlock(final JMethod method,
      final boolean notAnInterface) {
    final StringBuffer buffer = new StringBuffer();
    if (!method.isAbstract() && notAnInterface) {
      buffer.append(" /*-{");
      PrintUtil.nl(buffer);
      buffer.append("$>");
      buildMethodBody(buffer, method);
      buffer.append("$<}-*/");
    }
    return buffer.toString();
  }

  private void buildMethodBody(final StringBuffer buffer, final JMethod method) {
    if (!isVoidType(method)) {
      buffer.append("return ");
    }
    buffer.append("this.");
    buffer.append(method.getName());
    buffer.append('(');
    appendMethodParam(buffer, method, false);
    buffer.append(");");
    PrintUtil.nl(buffer);
  }

  private boolean buildParameters(final MethodSpec.Builder builder, final JMethod method) {
    boolean varargs = false;
    for (final JVar param : method.getParams()) {
      builder.addParameter(param.getType() == null ? TypeName.VOID : param.getType(), param.getName());
      varargs |= param.isVarArg();
    }
    return varargs;
  }

  // @deprecated TODO move to Transformer, should not run on interfaces
  @Deprecated
  private void enhanceModifiers(final JMethod method) {
    if (!method.isAbstract()) {
      method.setFinal(true);
      method.addModifier(Modifier.NATIVE);
    }
  }

  private void appendMethodParam(final StringBuffer buffer,
      final JMethod method, final boolean withType) {
    boolean first = true;
    for (final JVar param : method.getParams()) {
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

  private boolean isVoidType(final JMethod method) {
    final TypeName type = method.getType();
    return TypeName.VOID.equals(type) || TypeName.VOID.box().equals(type);
  }
}
