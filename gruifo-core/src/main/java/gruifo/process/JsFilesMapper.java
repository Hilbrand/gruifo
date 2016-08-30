package gruifo.process;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonSyntaxException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import gruifo.lang.js.JsElement;
import gruifo.lang.js.JsEnum;
import gruifo.lang.js.JsFile;
import gruifo.lang.js.JsMethod;
import gruifo.lang.js.JsParam;
import gruifo.lang.js.JsTypeObject;

/**
 * Util om project specifieke delen om te zetten.
 *
 */
class JsFilesMapper {

  private final Mapper mapper;

  public JsFilesMapper(final String fileName, final Charset charSet)
      throws JsonSyntaxException, IOException {
    mapper = new Mapper(fileName, charSet);
  }

  public Collection<JsFile> mapFiles(final Collection<JsFile> jsFiles) {
    for (final JsFile jsFile : jsFiles) {
      mapFile(jsFile);
    }
    return jsFiles;
  }

  private void mapFile(final JsFile jsFile) {
    mapFiles(jsFile.getInnerJFiles());
    mapElement(jsFile.getElement());
    mapEnums(jsFile.getEnumValues());
    mapParamList(jsFile.getFields());
    mapMethods(jsFile.getMethods());
  }

  private void mapElement(final JsElement element) {
    element.setExtends(mapJsObject(element.getExtends()));
    mapJsObjectList(element.getImplements());
    element.setType(mapJsObject(element.getType()));
    mapParamList(element.getTypeDef());
  }

  private void mapEnums(final List<JsEnum> enumValues) {
    for (final JsEnum jsEnum : enumValues) {
//      mapParam(jsEnum.getFieldName());
    }
  }

  private void mapMethods(final Collection<JsMethod> methods) {
    for (final JsMethod jsMethod : methods) {
      mapElement(jsMethod.getElement());
      mapParamList(jsMethod.getParams());
    }
  }

  private void mapParamList(final Collection<JsParam> list) {
    for (final JsParam jsParam : list) {
      mapParam(jsParam);
    }
  }

  private void mapParam(final JsParam jsParam) {
    jsParam.setType(mapJsObject(jsParam.getType()));
  }

  private void mapJsObjectList(final List<JsTypeObject> list) {
    for (int i = 0; i < list.size(); i++) {
      list.set(i, mapJsObject(list.get(i)));
    }
  }

  private JsTypeObject mapJsObject(final JsTypeObject jsObject) {
    return jsObject;
  }

  private TypeName string2TypeName(final String value) {
    final int idx = value.lastIndexOf('.');
    return ClassName.get(value.substring(0, idx),
        value.substring(idx + 1));
  }
}

