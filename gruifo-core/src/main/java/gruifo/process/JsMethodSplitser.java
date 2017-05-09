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
package gruifo.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gruifo.lang.js.JsElement;
import gruifo.lang.js.JsFile;
import gruifo.lang.js.JsMethod;
import gruifo.lang.js.JsParam;
import gruifo.lang.js.JsType;
import gruifo.lang.js.JsTypeList;
import gruifo.lang.js.JsTypeObject;
import gruifo.output.util.PrintUtil;

/**
 * Class to splits methods with choice or optional parameters into separate
 * methods.
 */
class JsMethodSplitser {

  private static final Logger LOG =
      LoggerFactory.getLogger(JsMethodSplitser.class);

  public Collection<JsFile> splitFiles(final Collection<JsFile> files) {
    for (final JsFile jsFile : files) {
      try {
        splitMethodsInClass(jsFile);
      } catch (final Exception e) {
        LOG.error("Error splitting file:{}", jsFile, e);
      }
    }
    return files;
  }

  /**
   *
   * @param jsFile
   */
  public void splitMethodsInClass(final JsFile jsFile) {
    final List<JsMethod> allMethods = new ArrayList<>();
    for (final JsMethod jsMethod : jsFile.getMethods()) {
      final List<List<JsParam>> list =
          splitMethodParamsOptional(jsMethod.getParams());
      final List<List<JsParam>> jParamList = new ArrayList<>();
      for (final List<JsParam> innerList : list) {
        jParamList.addAll(split2MethodParamsMulti(innerList));
      }
      for (final List<JsParam> params : jParamList) {
        final JsMethod splitMethod = jsMethod.clone();
        splitMethod.setParams(params);
        allMethods.add(splitMethod);
      }
    }
    jsFile.getMethods().clear();
    jsFile.getMethods().addAll(splitMultipleReturns(allMethods));
  }

  /**
   * Creates multiple parameter lists if a parameter is optional. If a parameter
   * is optional a parameter list if added without this parameter.
   *
   * @param jsParams List of parameters
   * @return List of List of parameters
   */
  private List<List<JsParam>> splitMethodParamsOptional(
      final List<JsParam> jsParams) {
    final List<List<JsParam>> params = new ArrayList<>();
    List<JsParam> current = new ArrayList<>();
    int last = jsParams.size();
    boolean addMore = true;
    while (addMore) {
      addMore = false;
      params.add(current);
      final int loopLast = last;
      for (int i = 0; i < loopLast; i++) {
        final JsParam jsParam = jsParams.get(i);
        if (jsParam.getType().isOptional()) {
          last = i;
          addMore = true;
        }
        current.add(jsParam);
      }
      current = new ArrayList<>();
    }
    return params;
  }

  /**
   * Creates multiple parameters lists if a parameter type contains multiple
   * types.
   *
   * @param jsParams
   * @return
   */
  private List<List<JsParam>> split2MethodParamsMulti(
      final List<JsParam> jsParams) {
    final List<List<JsParam>> params = new ArrayList<>();
    params.add(new ArrayList<JsParam>());
    for (int i = 0; i < jsParams.size(); i++) {
      final JsParam jsParam = jsParams.get(i);
      if (isMultiTypeParam(jsParam.getType())) {
        expandMultiParam(params, jsParam);
      } else {
        addSingleParam(params, jsParam);
      }
    }
    return params;
  }

  private boolean isMultiTypeParam(final JsTypeObject jsTypeObject) {
    if (jsTypeObject instanceof JsTypeList) {
      return true;
    }
    if (jsTypeObject instanceof JsType) {
      for (final JsTypeObject jso : ((JsType) jsTypeObject).getTypeList()) {
        if (isMultiTypeParam(jso)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Add new lists for each type. The lists are alternating replicated and
   * then sequential added:
   *
   * <pre>
   *   A => A C
   *   B    B C
   *        A D
   *        B D
   * </pre>
   *
   * @param params List of combinations of List of parameters.
   * @param jsParam multi parameters to add.
   */
  private void expandMultiParam(final List<List<JsParam>> params,
      final JsParam jsParam) {
    final List<JsParam> splitParams = splitParam2List(jsParam);
    final int currentSize = params.size();
    // Add new lists matching the number of choices.
    for (int k = 1; k < splitParams.size(); k++) {
      for (int j = 0; j < currentSize; j++) {
        params.add(new ArrayList<>(params.get(j)));
      }
    }
    for (int j = 0; j < splitParams.size(); j++) {
      for (int k = 0; k < currentSize; k++) {
        params.get(k + j * currentSize).add(splitParams.get(j));
      }
    }
  }

  private List<JsParam> splitParam2List(final JsParam jsParam) {
    final List<JsParam> splitParams = new ArrayList<>();
    for (final JsTypeObject innerJsParam : getTypes(jsParam.getType())) {
      if (!isDuplicate(splitParams, innerJsParam)) {
        splitParams.add(new JsParam(jsParam.getName(), innerJsParam));
      }
    }
    return splitParams;
  }

  private List<JsTypeObject> getTypes(final JsTypeObject jsParamType) {
    final List<JsTypeObject> types = new ArrayList<>();
    if (jsParamType instanceof JsType) {
      types.addAll(getJsType((JsType) jsParamType));
    } else if (jsParamType instanceof JsTypeList) {
      types.addAll(getJsTypeList(((JsTypeList) jsParamType).getTypes()));
    } else if (jsParamType == null) {
      throw new NullPointerException("jsParamType may not be null.");
    } else {
      throw new IllegalArgumentException("Instance of JsTypeObject '"
          + jsParamType.getClass().getName() + "' not supported");
    }
    return types;
  }

  private List<JsTypeObject> getJsType(final JsType jsType) {
    final List<JsTypeObject> types = new ArrayList<>();
    if (jsType.getTypeList().isEmpty()) {
      types.add(jsType);
    } else {
      splitJsTypesList(jsType, types);
    }
    return types;
  }

  private void splitJsTypesList(final JsType jsType,
      final List<JsTypeObject> types) {
    for (final JsTypeObject jso : getJsTypeList(jsType.getTypeList())) {
      final String name = jsType.getName();
      final JsType type = new JsType(name,
          name + ".<" + jso.getRawType() + '>');
      type.addGenericType(jso);
      types.add(type);
    }
  }

  private List<JsTypeObject> getJsTypeList(final List<JsTypeObject> list) {
    final List<JsTypeObject> types = new ArrayList<>();
    for (final JsTypeObject jsTypeObject : list) {
      types.addAll(getTypes(jsTypeObject));
    }
    return types;
  }

  private boolean isDuplicate(final List<JsParam> splitParams,
      final JsTypeObject typeObject) {
    boolean duplicate = false;
    for (final JsParam jParam : splitParams) {
      if (typeObject.equals(jParam.getType())) {
        duplicate = true;
      }
    }
    return duplicate;
  }

  /**
   * Add the given jsParam to each list in params.
   *
   * @param params List of combinations of List of parameters.
   * @param jsParam parameter to add.
   */
  private void addSingleParam(final List<List<JsParam>> params,
      final JsParam jsParam) {
    for (final List<JsParam> list : params) {
      list.add(jsParam);
    }
  }

  private List<JsMethod> splitMultipleReturns(final List<JsMethod> methods) {
    final List<JsMethod> allMethods = new ArrayList<>();
    for (final JsMethod jsMethod : methods) {
      final JsElement element = jsMethod.getElement();
      final JsTypeObject returnValue = element.getReturn();
      if (returnValue instanceof JsTypeList) {
        allMethods.addAll(splitMultiReturn(jsMethod, returnValue));
      } else {
        allMethods.add(jsMethod);
      }
    }
    return allMethods;
  }

  private List<JsMethod> splitMultiReturn(final JsMethod jsMethod,
      final JsTypeObject returnValue) {
    final List<JsMethod> methods = new ArrayList<>();
    for (final JsTypeObject type : ((JsTypeList) returnValue).getTypes()) {
      final JsMethod method = jsMethod.clone();
      method.getElement().setReturn(type);
      try {
        method.setMethodName(method.getMethodName()
            + PrintUtil.firstCharUpper(((JsType)type).getName()));
      } catch (final StringIndexOutOfBoundsException e) {
        LOG.error("What? {}", returnValue, e);
        throw e;
      }
      methods.add(method);
    }
    return methods;
  }
}
