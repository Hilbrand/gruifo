package gruifo.lang.java;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.TypeName;

public class JMethod {
  private String methodName;
  private final String classPath;
  private final EnumSet<Modifier> modifiers;
  private TypeName returnType;
  private final List<JParam> params = new ArrayList<>();
  private String jsDoc;
  private String genericType;

  public JMethod(final String classPath, final String methodName,
      final EnumSet<Modifier> modifiers) {
    this.classPath = classPath;
    this.methodName = methodName;
    this.modifiers = EnumSet.copyOf(modifiers);
  }

  public void addParam(final JParam param) {
    params.add(param);
  }

  public void addModifier(final Modifier modifier) {
    modifiers.add(modifier);
  }

  public boolean contains(final Modifier modifier) {
    return modifiers.contains(modifier);
  }

  public EnumSet<Modifier> getModifiers() {
    return modifiers;
//    return modifiers.toArray(new Modifier[0]);
  }

  public String getClassPath() {
    return classPath;
  }

  public String getJsDoc() {
    return jsDoc;
  }

  public String getGenericType() {
    return genericType;
  }

  public String getMethodName() {
    return methodName;
  }

  public List<JParam> getParams() {
    return params;
  }

  public TypeName getReturn() {
    return returnType;
  }

  public boolean isAbstractMethod() {
    return modifiers.contains(Modifier.ABSTRACT);
  }

  public void setAbstract(final boolean abstractMethod) {
    setModifier(abstractMethod, Modifier.ABSTRACT);
  }

  public void setJsDoc(final String jsDoc) {
    this.jsDoc = jsDoc;
  }

  public void setGenericType(final String genericType) {
    this.genericType = genericType;
  }

  public void setMethodName(final String methodName) {
    this.methodName = methodName;
  }

  public void setReturn(final TypeName returnType) {
    this.returnType = returnType;
  }

  public void setStatic(final boolean staticMethod) {
    setModifier(staticMethod, Modifier.STATIC);
  }

  private void setModifier(final boolean add, final Modifier modifier) {
    if (add) {
      modifiers.add(modifier);
    } else {
      modifiers.remove(modifier);
    }
  }

  @Override
  public String toString() {
    return "JMethod [methodName=" + methodName + "]";
  }
}