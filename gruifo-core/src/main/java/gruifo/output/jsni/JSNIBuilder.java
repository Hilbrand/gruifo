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

import java.util.List;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import gruifo.lang.java.JClass;
import gruifo.lang.java.JClass.EnumValue;
import gruifo.lang.java.JMethod;
import gruifo.lang.js.JsFile;
import gruifo.output.FilePrinter;
import gruifo.output.PrintUtil;

public class JSNIBuilder implements FilePrinter {
  private final Transformer transformer = new Transformer();
  private final JSNIMethodBuilder mBuilder = new JSNIMethodBuilder();
  private final JSNIFieldBuilder fBuilder = new JSNIFieldBuilder();
  private final JSNIEnumBuilder eBuilder = new JSNIEnumBuilder();

  @Override
  public String printFile(final JsFile jsFile) {
    return buildFile(transformer.transform(jsFile));
  }

  @Override
  public boolean ignored(final JsFile jsFile) {
    return TypeMapper.INSTANCE.ignore(
        jsFile.getPackageName() + '.' + jsFile.getClassOrInterfaceName());
  }

  public String buildFile(final JClass jFile) {
    jFile.setStatic(false); // FIXME setting static should not be done here
    final TypeSpec clazz = buildJClassOrEnum(jFile);
    return JavaFile.builder(jFile.getPackageName(), clazz)
        .addFileComment(jFile.getHeaderComment()).build().toString();
  }

  private TypeSpec buildJClassOrEnum(final JClass jFile) {
    final TypeSpec.Builder builder;
    if (jFile.getEnumValues().isEmpty()) {
      builder = buildClass(jFile);
    } else {
      builder = buildEnum(jFile.getPackageName(),
          jFile.getClassOrInterfaceName(), jFile.isStatic(),
          jFile.getEnumValues());
    }
    return builder.addJavadoc(jFile.getClassDescription()).build();
  }

  private Builder buildClass(final JClass jFile) {
    final TypeSpec.Builder builder = buildClassName(jFile);
    buildConstructors(builder, jFile);
    fBuilder.buildFields(builder, jFile);
    mBuilder.buildMethods(builder, jFile);
    for (final JClass innerFile: jFile.getInnerJFiles()) {
      innerFile.setStatic(true); //FIXME setting static should not be done here
//      printJClassOrEnum(innerFile, indent, buffer);
    }
    return builder;
  }

  private Builder buildClassName(final JClass jFile) {
    final TypeSpec.Builder builder = buildClassInterfaceModifier(jFile);
    builder.addTypeVariables(jFile.getTypeVariables());
    buildClassExtends(builder, jFile);
    builder.addSuperinterfaces(jFile.getSuperinterfaces());
    return builder;
  }

  private Builder buildClassInterfaceModifier(final JClass jFile) {
    final TypeSpec.Builder builder;
    if (jFile.isInterface()) {
      builder = TypeSpec.interfaceBuilder(jFile.getClassOrInterfaceName());
    } else {
      builder = TypeSpec.classBuilder(jFile.getClassOrInterfaceName());
    }
    builder.addModifiers(jFile.getModifiers());
    return builder;
  }

  private void buildClassExtends(final Builder builder, final JClass jFile) {
    if (jFile instanceof JClass && jFile.getSuperClass() != null
        && !jFile.isInterface()) {
      builder.superclass(jFile.getSuperClass());
    }
  }

  private void buildConstructors(final Builder builder, final JClass jFile) {
    // if class is abstract don't create constructors because the class can't
    // be used directly.
/*    if (!jFile.hasAbstractMethods()) {
      for (final JMethod constructor : jFile.getConstructors()) {
        if (jFile.isDataClass()) {
          printConstructorsDataClass(indent, buffer, jFile);
        } else {
          printConstructorCreator(indent, buffer, jFile, constructor);
        }
      }
    }
    if (!jFile.isDataClass() && !jFile.isInterface()) {
      printConstructor(
          buffer, indent, "protected", jFile.getClassOrInterfaceName());
    }
*/
  }

  private Builder buildEnum(final String packageName, final String classOrInterfaceName, final boolean static1,
      final List<EnumValue> enumValues) {
    // TODO Auto-generated method stub
    return null;
  }

  private void printConstructorCreator(final int indent, final StringBuffer buffer,
      final JClass jFile, final JMethod constructor) {
//    PrintUtil.indent(buffer, indent);
//    buffer.append("public static native ");
//    printClassGeneric(buffer, jFile.getClassGeneric());
//    buffer.append(jFile.getClassOrInterfaceName());
//    if (jFile.getClassGeneric() != null) {
//      buffer.append('<');
//      buffer.append(jFile.getClassGeneric());
//      buffer.append('>');
//    }
//    buffer.append(" new");
//    buffer.append(PrintUtil.firstCharUpper(jFile.getClassOrInterfaceName()));
//    buffer.append('(');
//    JSNIMethodPrinter.printMethodParam(buffer, constructor, true);
//    buffer.append(") /*-{");
//    PrintUtil.nl(buffer);
//    PrintUtil.indent(buffer, ++indent);
//    buffer.append("return new $wnd.");
//    if (!jFile.getPackageName().isEmpty()) {
//      buffer.append(jFile.getPackageName());
//      buffer.append('.');
//    }
//    buffer.append(jFile.getClassOrInterfaceName());
//    buffer.append('(');
//    JSNIMethodPrinter.printMethodParam(buffer, constructor, false);
//    buffer.append(");");
//    PrintUtil.nl(buffer);
//    PrintUtil.indent(buffer, --indent);
//    buffer.append("}-*/;");
//    PrintUtil.nl2(buffer);
  }

  /**
   * Generate constructor without arguments.
   * @param buffer
   * @param indent
   * @param accessType
   * @param name name of the constructor
   */
  private void printConstructor(final StringBuffer buffer,
      final int indent, final String accessType, final String name) {
    PrintUtil.indent(buffer, indent);
    buffer.append(accessType);
    buffer.append(' ');
    buffer.append(name);
    buffer.append("() { }");
    PrintUtil.nl2(buffer);
  }

  private void printConstructorsDataClass(final int indent,
      final StringBuffer buffer, final JClass jFile) {
    PrintUtil.indent(buffer, indent);
    buffer.append("public ");
    buffer.append(jFile.getClassOrInterfaceName());
    buffer.append("(){ }");
    PrintUtil.nl2(buffer);
  }
}