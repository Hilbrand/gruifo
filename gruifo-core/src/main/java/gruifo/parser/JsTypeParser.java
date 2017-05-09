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
import java.util.concurrent.atomic.AtomicInteger;

import gruifo.lang.js.JsType;
import gruifo.lang.js.JsTypeList;
import gruifo.lang.js.JsTypeObject;

/**
 * Parse the types of @param, @return and @type elements.
 */
public class JsTypeParser {

  private static final String FUNCTION = "function(";

  public JsTypeObject parseType(final String rawType) {
    final String stripped = stripParentheses(rawType);
    final boolean optional = isOptional(stripped);
    final JsTypeObject type = typeParser(optional
        ? stripped.substring(0, stripped.length() - 1): stripped);
    type.setOptional(optional);
    return type;
  }

  private String stripParentheses(final String type) {
    String strippedType;
    if (type.isEmpty()) {
      strippedType = type;
    } else {
      final int beginIndex = type.charAt(0) == '(' ? 1 : 0;
      final int length = type.length();
      final int endIndex = length - (type.charAt(length - 1) == ')' ? 1 : 0);
      strippedType = type.substring(beginIndex, endIndex);
    }
    return strippedType;
  }

  private boolean isOptional(final String stripped) {
    if (stripped.isEmpty()) {
      return false;
    }
    return '=' == stripped.charAt(stripped.length() - 1);
  }

  private JsTypeObject typeParser(final String rawType) {
    final char[] chars = rawType.toCharArray();
    final JsTypeObject root;
    final List<JsTypeObject> types =
        typeParser(rawType, chars, new AtomicInteger());
    if (types.size() == 1) {
      root = types.get(0);
    } else {
      root = new JsTypeList(rawType);
      ((JsTypeList) root).addAll(types);
    }
    return root;
  }

  private List<JsTypeObject> typeParser(final String rawType,
      final char[] chars, final AtomicInteger idx) {
    final ParseState parseState = new ParseState(idx.get());
    final List<JsTypeObject> types = new ArrayList<>();
    final List<JsTypeObject> choices = new ArrayList<>();
    List<JsTypeObject> subTypes = null;
    for (; idx.get() < chars.length; idx.incrementAndGet()) {
      final int i = idx.get();
      parseState.endPos = i;
      switch (chars[i]) {
      case 'f':
        if (rawType.substring(i).startsWith(FUNCTION)) {
          parseState.inFunction = true;
          parseState.parentheses++;
        }
        break;
      case '(':
        parseState.parentheses++;
        if (!parseState.inFunction) {
          parseState.startPos++;
        }
        break;
      case ')':
        parseState.parentheses--;
        parseState.endFunction = parseState.parentheses == 0;
        break;
      case ' ':
        if (parseState.startPos == parseState.endPos) {
          parseState.startPos++;
        } else {
          parseState.newType = true;
          parseState.endPos--;
        }
        break;
      case '.': // generic or varargs .... varargs before
        if (chars[i+1] == '<') {
          idx.incrementAndGet(); // skip past .
          subTypes = parseStartGeneric(rawType, chars, idx, parseState);
        } else if (chars[i+1] == '.' && chars[i+2] == '.') {
          parseState.varArgs = true;
          idx.addAndGet(2); // skip ...
          parseState.startPos = i + 3;
        }
        break;
      case ',':
        // set inFunction to false  when we passed the end of the function,
        // But ONLY when inFunction is already true
        parseState.inFunction =
        parseState.inFunction && !parseState.endFunction;
        parseState.param = true;
        parseState.newType = true;
        parseState.endPos--;
        break;
      case '<':
        subTypes = parseStartGeneric(rawType, chars, idx, parseState);
        // should not happen...
        //        throw new IllegalArgumentException("Unexpected '<' in " + rawType);
      case '>':
        parseState.decreaseDepth = true;
        parseState.newType = true;
        parseState.endPos--;
        break;
      case '|': // new choice argument
        parseState.newType = true;
        parseState.endPos--;
        break;
      case '!': // argument can't be null. !  is positioned before type
        parseState.notNull = true;
        parseState.startPos++;
        break;
      case '?': // argument can be null. ? is positioned before type
        if (parseState.startPos == parseState.endPos) {
          parseState.canNull = true;
        }
        break;
      case '=': // optional argument. = is positioned after type
        parseState.optional = true;
        parseState.endPos--;
        break;
        //        throw new IllegalArgumentException("Unexpected '=' in " + rawType);
      default:
        break;
      }
      if (subTypes == null) {
        parseState.nameEndPos = parseState.endPos;
      }
      boolean lastToken = idx.get() == chars.length - 1;
      if (!parseState.inFunction && parseState.newType || lastToken) {
        final int offset =
            rawType.charAt(0) == '?' && rawType.length() > 1 ? 1 : 0;
        final String sType = rawType.substring(
            parseState.startPos + offset, parseState.endPos + 1);
        final String name = rawType.substring(
            parseState.startPos + offset, parseState.nameEndPos + 1);
        boolean withNull = false;
        //        if (sType.isEmpty() && !parseState.canNull) {
        //          // ignore: space after type, but before new -> number |
        //        } else
        if ("undefined".equals(sType) || "null".equals(sType)) {
          withNull = true;
        } else {
          final JsType jsType = new JsType(name, sType);
          jsType.setFunction(sType.startsWith(FUNCTION));
          jsType.setVarArgs(parseState.varArgs);
          jsType.setNotNull(parseState.notNull);
          jsType.setNull(parseState.canNull);
          jsType.setOptional(parseState.optional);
          jsType.addGenericTypes(subTypes);
          choices.add(jsType);
        }
        lastToken = idx.get() == chars.length - 1;
        if (parseState.param || parseState.decreaseDepth || lastToken) {
          if (choices.size() == 1) {
            choices.get(0).setNull(withNull);
            types.add(choices.get(0));
          } else {
            final JsTypeList typeList = new JsTypeList(rawType.substring(
                parseState.startPosChoices, parseState.endPos + 1));
            typeList.setNull(withNull);
            typeList.addAll(choices);
            types.add(typeList);
          }
          choices.clear();
          if (parseState.decreaseDepth || lastToken) {
            return types;
          }
          if (parseState.param) {
            parseState.startPosChoices = i + 1;
          }
        }
        parseState.startPos = idx.get() + 1;
        parseState.resetAll();
        subTypes = null;
      }
    }
    return types;
  }

  private List<JsTypeObject> parseStartGeneric(final String rawType,
      final char[] chars, final AtomicInteger idx, final ParseState ps) {
    List<JsTypeObject> subTypes;
    ps.endPos--;
    ps.nameEndPos = ps.endPos;
    idx.incrementAndGet(); // skip past '<'
    subTypes = typeParser(rawType, chars, idx);
    ps.endPos = idx.get();
    return subTypes;
  }

  private static class ParseState {
    boolean notNull, canNull, optional, varArgs, newType, decreaseDepth,
    inFunction, endFunction, param;
    int startPos, startPosChoices, nameEndPos, parentheses, endPos;

    public ParseState(final int startPos) {
      this.startPos = startPos;
      startPosChoices = startPos;
      nameEndPos = startPos;
    }

    void resetAll() {
      newType = param = varArgs = notNull = canNull = optional = inFunction
          = endFunction = decreaseDepth = false;
    }
  }
}
