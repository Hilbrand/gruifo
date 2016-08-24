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
package gruifo.parser;

import java.util.ArrayList;
import java.util.List;

import gruifo.lang.js.JsFile;
import gruifo.lang.js.JsMethod;
import gruifo.lang.js.JsParam;
import gruifo.lang.js.JsTypeList;
import gruifo.lang.js.JsTypeObject;

/**
 * Class to splits methods with choice or optional parameters into separate
 * methods.
 */
public class JsMethodSplitser {

  /**
   *
   * @param jsFile
   */
  public void splitMethodsInClass(final JsFile jsFile) {
    final List<JsMethod> allMethods = new ArrayList<>();
    for(final JsMethod jsMethod: jsFile.getMethods()) {
      final List<List<JsParam>> list =
          splitMethodParamsOptional(jsMethod.getParams());
      final List<List<JsParam>> jParamList = new ArrayList<>();
      for (final List<JsParam> innerList : list) {
        jParamList.addAll(split2MethodParamsMulti(innerList));
      }
      for (final List<JsParam> params : jParamList) {
        final JsMethod splitMethod = new JsMethod(jsMethod);
        splitMethod.setParams(params);
      }
    }
    jsFile.getMethods().clear();
    jsFile.getMethods().addAll(allMethods);
  }

  /**
   * Creates multiple parameter lists if a parameter is optional. If a
   * parameter is optional a parameter list if added without this parameter.
   * @param jsParams List of parameters
   * @return List of List of parameters
   */
  private List<List<JsParam>> splitMethodParamsOptional(
      final List<JsParam> jsParams) {
    final List<List<JsParam>> params = new ArrayList<>();
    List<JsParam> current = new ArrayList<JsParam>();
    params.add(current);
    for (int i = 0; i < jsParams.size(); i++) {
      final JsParam jsParam = jsParams.get(i);
      if (jsParam.getType().isOptional()) {
        current = new ArrayList<JsParam>(params.get(params.size() - 1));
        params.add(current);
      }
      current.add(jsParam);
    }
    return params;
  }

  /**
   * Creates multiple parameters lists if a parameter type contains multiple
   * types.
   * @param jsParams
   * @return
   */
  private List<List<JsParam>> split2MethodParamsMulti(
      final List<JsParam> jsParams) {
    final List<List<JsParam>> params = new ArrayList<>();
    params.add(new ArrayList<JsParam>());
    for (int i = 0; i < jsParams.size(); i++) {
      final JsParam jsParam = jsParams.get(i);
      if (jsParam.getType() instanceof JsTypeList) {
        expandChoices(params, jsParam);
      } else {
        addSingleParam(params, jsParam);
      }
    }
    return params;
  }

  /**
   * Add new lists for each choice.
   * The lists are alternating replicated and then sequential added:
   * <pre>
   *   A => A C
   *   B    B C
   *        A D
   *        B D
   * </pre>
   * @param params List of combinations of List of parameters.
   * @param jsParam choice parameters to add.
   */
  private void expandChoices(final List<List<JsParam>> params,
      final JsParam jsParam) {
    final List<JsParam> splitParams = optionParam2List(jsParam);
    final int currentSize = params.size();
    // Add new lists matching the number of choices.
    for (int j = 0; j < currentSize; j++) {
      for (int k = 1; k < splitParams.size(); k++) {
        params.add(new ArrayList<>(params.get(j)));
      }
    }
    for (int j = 0; j < splitParams.size();j++) {
      for (int k = 0; k < currentSize; k++) {
        params.get(k + j * currentSize).add(splitParams.get(j));
      }
    }
  }

  private List<JsParam> optionParam2List(final JsParam jsParam) {
    final List<JsParam> splitParams = new ArrayList<>();
    for (final JsTypeObject innerJsParam :
      ((JsTypeList) jsParam.getType()).getTypes()) {
      if (!isDuplicate(splitParams, innerJsParam)) {
        final JsParam newJsParam =
            new JsParam(jsParam.getName(), jsParam.getElement());
        newJsParam.setType(innerJsParam);
        splitParams.add(newJsParam);
      }
    }
    return splitParams;
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
   * @param params List of combinations of List of parameters.
   * @param jsParam parameter to add.
   */
  private void addSingleParam(final List<List<JsParam>> params,
      final JsParam jsParam) {
    for (final List<JsParam> list : params) {
      list.add(jsParam);
    }
  }
}
