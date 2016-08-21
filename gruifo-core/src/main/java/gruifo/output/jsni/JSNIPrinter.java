///*
// * Copyright Hilbrand Bouwkamp.
// *
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// * use this file except in compliance with the License. You may obtain a copy of
// * the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// * License for the specific language governing permissions and limitations under
// * the License.
// */
//package gruifo.output.jsni;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import gruifo.lang.java.JClass;
//import gruifo.lang.java.JMethod;
//import gruifo.lang.js.JsFile;
//import gruifo.output.FilePrinter;
//import gruifo.output.PrintUtil;
//
///**
// *
// */
//public class JSNIPrinter implements FilePrinter {
//  private static final Logger LOG = LoggerFactory.getLogger(JSNIPrinter.class);
//
//  private final Transformer transformer = new Transformer();
//  private final JSNIMethodPrinter mPrinter = new JSNIMethodPrinter();
//  private final JSNIFieldPrinter fPrinter = new JSNIFieldPrinter();
//  private final JSNIEnumPrinter ePrinter = new JSNIEnumPrinter();
//
//  @Override
//  public String printFile(final JsFile jsFile) {
//    return printFile(transformer.transform(jsFile));
//  }
//
//  @Override
//  public boolean ignored(final JsFile jsFile) {
//    return TypeMapper.INSTANCE.ignore(
//        jsFile.getPackageName() + '.' + jsFile.getClassOrInterfaceName());
//  }
//
//  public String printFile(final JClass jFile) {
//    final int indent = 0;
//    final StringBuffer buffer = new StringBuffer();
//    buffer.append(jFile.getHeaderComment());
//    printPackageName(buffer, jFile.getPackageName());
//    printImports(buffer, jFile.getImports());
//    jFile.setStatic(false); //FIXME setting static should not be done here
//    printJClassOrEnum(jFile, indent, buffer);
//    return buffer.toString();
//  }
//
//  private void printJClassOrEnum(final JClass jFile, final int indent,
//      final StringBuffer buffer) {
//    PrintUtil.indent(buffer, jFile.getClassDescription(), indent);
//    if (jFile.getEnumValues().isEmpty()) {
//      printJClass(jFile, indent, buffer);
//    } else {
//      ePrinter.printEnum(buffer, indent, jFile.getPackageName(),
//          jFile.getClassOrInterfaceName(), jFile.isStatic(),
//          jFile.getEnumValues());
//    }
//    PrintUtil.nl(buffer);
//    buffer.append('}'); // close file
//    PrintUtil.nl(buffer);
//  }
//
//  private void printJClass(final JClass jFile, int indent,
//      final StringBuffer buffer) {
//    printClass(buffer, jFile, indent);
//    indent++;
//    printConstructors(indent, buffer, jFile);
//    fPrinter.printFields(buffer, indent, jFile);
//    mPrinter.printMethods(buffer, indent, jFile);
//    for (final JClass innerFile: jFile.getInnerJFiles()) {
//      innerFile.setStatic(true); //FIXME setting static should not be done here
//      printJClassOrEnum(innerFile, indent, buffer);
//    }
//  }
//
//  private void printPackageName(final StringBuffer buffer,
//      final String packageName) {
//    buffer.append("package ");
//    buffer.append(packageName);
//    buffer.append(';');
//    PrintUtil.nl2(buffer);
//  }
//
//  private void printImports(final StringBuffer buffer,
//      final Set<String> imports) {
//    final ArrayList<String> importList = new ArrayList<>(imports);
//    Collections.sort(importList);
//    for (final String imp : importList) {
//      buffer.append("import ");
//      buffer.append(imp);
//      buffer.append(';');
//      PrintUtil.nl(buffer);
//    }
//    PrintUtil.nl(buffer);
//  }
//
//  private void printClass(final StringBuffer buffer, final JClass jFile,
//      final int indent) {
//    PrintUtil.indent(buffer, indent);
//    printClassInterfaceModifier(buffer, jFile);
//    buffer.append(jFile.getClassOrInterfaceName());
//    printClassGeneric(buffer, jFile.getClassGeneric());
//    printClassExtends(buffer, jFile);
//    printImplements(buffer, jFile.getImplements());
//    buffer.append(" {");
//    PrintUtil.nl(buffer);
//  }
//
//  private void printClassInterfaceModifier(final StringBuffer buffer,
//      final JClass jFile) {
//    buffer.append("public ");
//    if (jFile.isInterface()) {
//      buffer.append("interface ");
//    } else {
//      if (jFile.isStatic()) {
//        buffer.append("static ");
//      }
//      if (jFile.hasAbstractMethods()) {
//        buffer.append("abstract ");
//      }
//      buffer.append("class ");
//    }
//  }
//
//  private void printClassGeneric(final StringBuffer buffer,
//      final String classGeneric) {
//    if (classGeneric != null) {
//      buffer.append('<');
//      buffer.append(classGeneric);
//      buffer.append(" extends ");
//      buffer.append(TypeMapper.GWT_JAVA_SCRIPT_OBJECT);
//      buffer.append("> ");
//    }
//  }
//
//  private void printClassExtends(final StringBuffer buffer,
//      final JClass jFile) {
//    if (jFile instanceof JClass && jFile.getSuperClass() != null
//        && !jFile.isInterface()) {
//      buffer.append(" extends ");
//      buffer.append(jFile.getSuperClass());
//    }
//  }
//
//  private void printImplements(final StringBuffer buffer,
//      final List<String> implementss) {
//    if (!implementss.isEmpty()) {
//      buffer.append(" implements");
//      boolean first = true;
//      for (final String implString : implementss) {
//        if (first) {
//          first = false;
//        } else {
//          buffer.append(',');
//        }
//        buffer.append(' ');
//        buffer.append(implString);
//      }
//    }
//  }
//
//  private void printConstructors(final int indent, final StringBuffer buffer,
//      final JClass jFile) {
//    // if class is abstract don't create constructors because the class can't
//    // be used directly.
//    if (!jFile.hasAbstractMethods()) {
//      for (final JMethod constructor : jFile.getConstructors()) {
//        if (jFile.isDataClass()) {
//          printConstructorsDataClass(indent, buffer, jFile);
//        } else {
//          printConstructorCreator(indent, buffer, jFile, constructor);
//        }
//      }
//    }
//    if (!jFile.isDataClass() && !jFile.isInterface()) {
//      printConstructor(
//          buffer, indent, "protected", jFile.getClassOrInterfaceName());
//    }
//  }
//
//  private void printConstructorCreator(int indent, final StringBuffer buffer,
//      final JClass jFile, final JMethod constructor) {
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
//  }
//
//  /**
//   * Generate constructor without arguments.
//   * @param buffer
//   * @param indent
//   * @param accessType
//   * @param name name of the constructor
//   */
//  private void printConstructor(final StringBuffer buffer,
//      final int indent, final String accessType, final String name) {
//    PrintUtil.indent(buffer, indent);
//    buffer.append(accessType);
//    buffer.append(' ');
//    buffer.append(name);
//    buffer.append("() { }");
//    PrintUtil.nl2(buffer);
//  }
//
//  private void printConstructorsDataClass(final int indent,
//      final StringBuffer buffer, final JClass jFile) {
//    PrintUtil.indent(buffer, indent);
//    buffer.append("public ");
//    buffer.append(jFile.getClassOrInterfaceName());
//    buffer.append("(){ }");
//    PrintUtil.nl2(buffer);
//  }
//}
//
