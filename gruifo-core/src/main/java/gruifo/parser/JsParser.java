package gruifo.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import gruifo.lang.js.JsElement;
import gruifo.lang.js.JsFile;
import gruifo.lang.js.JsMethod;

public class JsParser {

  /**
  *
  * @param fileName
  * @param staticMethods
  * @param staticFields
  * @return
  * @throws FileNotFoundException
  * @throws IOException
  */
 public Collection<JsFile> parseFile(final String fileName,
     final List<JsMethod> staticMethods,
     final Map<String, JsElement> staticFields)
         throws FileNotFoundException, IOException {
   try (final Reader reader = new BufferedReader(new FileReader(fileName))) {
     final CompilerEnvirons env = new CompilerEnvirons();
     env.setRecordingLocalJsDocComments(true);
     env.setAllowSharpComments(true);
     env.setRecordingComments(true);
     final AstRoot node = new Parser(env).parse(reader, fileName, 1);
     final JavaScriptFileParser parser = new JavaScriptFileParser(fileName);
     node.visitAll(parser);
     staticMethods.addAll(parser.getStaticMethods());
     staticFields.putAll(parser.getConsts());
     return parser.getFiles();
   }
 }
}
