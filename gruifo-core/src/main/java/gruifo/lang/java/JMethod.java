package gruifo.lang.java;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.lang.model.element.Modifier;

public class JMethod extends JVar {
  private final String classPath;
  private final List<JVar> params = new ArrayList<>();
  private String genericType;

  public JMethod(final String classPath, final String methodName,
      final EnumSet<Modifier> modifiers) {
    super(methodName, null, EnumSet.copyOf(modifiers));
    this.classPath = classPath;
  }

  public void addParam(final JVar param) {
    params.add(param);
  }

  public String getClassPath() {
    return classPath;
  }

  public String getGenericType() {
    return genericType;
  }

  public List<JVar> getParams() {
    return params;
  }

  public void setGenericType(final String genericType) {
    this.genericType = genericType;
  }
}