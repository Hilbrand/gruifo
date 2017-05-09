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
package gruifo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

import gruifo.lang.js.JsElement;
import gruifo.lang.js.JsFile;
import gruifo.lang.js.JsMethod;
import gruifo.output.FilePrinter;
import gruifo.output.jsinterop.JsInteropPrinter;
import gruifo.output.jsni.JSNIBuilder;
import gruifo.parser.JsParser;
import gruifo.process.Processor;

/**
 * Control flow from parsing to generating output files.
 */
public class Controller {

  private static final String JAVA_SCRIPT_EXT = "js";
  private static final String JAVA_EXT = ".java";

  private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

  private final List<File> srcPaths;
  private final File outputPath;
  private final JsParser jsParser = new JsParser();
  private final Processor processor ;

  public Controller(final List<File> srcPaths, final File outputPath,
      final File mapperFile, final Charset charSet)
          throws JsonSyntaxException, IOException {
    this.srcPaths = srcPaths;
    this.outputPath = outputPath;
    processor = new Processor(mapperFile, charSet);
  }

  public void run(final OutputType outputType) {
    final FilePrinter fp;
    if (outputType == OutputType.JSI) {
      fp = new JsInteropPrinter();
    } else if (outputType == OutputType.JSNI) {
      fp = new JSNIBuilder();
    } else {
      throw new RuntimeException("Output type '" + outputType
          + "' not supported");
    }
    run(fp);
  }

  public void run(final FilePrinter printer) {
    final List<JsFile> jsFiles = new ArrayList<>();
    for (final File srcPath : srcPaths) {
      final List<File> files = new ArrayList<>();
      scanJsFiles(files, srcPath);
      for (final File file : files) {
        try {
          jsFiles.addAll(processor.process(jsParser.parseFile(file.getPath())));
        } catch (final IOException e) {
          LOG.error("Exception parsing file:" + file, e);
        }
      }
    }
    processStaticConsts(jsParser.getStaticFields());
    processStaticMethods(jsParser.getStaticMethods());
    writeFiles(printer, jsFiles, outputPath);
  }

  /**
   * Scans the srcPath for JavaScript files and adds them to the files list.
   *
   * @param files list of JavaScript files found
   * @param srcPath source path to search for files
   */
  void scanJsFiles(final List<File> files, final File srcPath) {
    if (srcPath.isFile()) {
      files.add(srcPath);
    } else {
      for (final File file : srcPath.listFiles()) {
        if (file.isDirectory()) {
          scanJsFiles(files, file);
        } else if (file.getPath().endsWith(JAVA_SCRIPT_EXT)) {
          files.add(file);
        }
      }
    }
  }

  private void processStaticConsts(final Map<String, JsElement> staticConsts) {
    if (!staticConsts.isEmpty()) {
      LOG.error("Missed #{} static fields.", staticConsts.size());
    }
  }
  private void processStaticMethods(final List<JsMethod> staticMethods) {
    if (!staticMethods.isEmpty()) {
      LOG.error("Missed #{} static methods.", staticMethods.size());
    }
  }

  void writeFiles(final FilePrinter printer, final Collection<JsFile> jsFiles,
      final File outputPath) {
    for (final JsFile jsFile : jsFiles) {
      if (!printer.ignored(jsFile)) {
        final String packagePath = jsFile.getPackageName().replace('.', '/');
        final File path = new File(outputPath, packagePath);
        path.mkdirs();
        try (final FileWriter writer = new FileWriter(new File(path,
            jsFile.getClassOrInterfaceName() + JAVA_EXT))) {
          writer.append(printer.printFile(jsFile));
          writer.flush();
        } catch (final IOException | RuntimeException e) {
          LOG.error("Exception parsing file:{}",
              jsFile.getOriginalFileName(), e);
        }
      }
    }
  }
}
