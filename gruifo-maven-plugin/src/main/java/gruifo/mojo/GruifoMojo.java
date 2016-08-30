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
package gruifo.mojo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.gson.JsonSyntaxException;

import gruifo.Controller;
import gruifo.OutputType;

/**
 *
 */
@Mojo( name = "java", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true )
@Execute( goal = "java", phase = LifecyclePhase.GENERATE_SOURCES)
public class GruifoMojo extends AbstractMojo {

  private static final String GEN_DIRECTORY = "gruifo";

  /**
   *
   */
  @Parameter(defaultValue = "JSNI")
  private OutputType outputType;

  /**
   *
   */
  @Parameter(required = true)
  private String[] includes;

  /**
   *
   */
  @Parameter
  private String typeMapperFile;

  @Parameter
  private final String charSet = StandardCharsets.UTF_8.name();

  /**
   * The Maven project instance for the executing project.
   */
  @Component
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Generate gwt library");
    final List<File> srcPaths = processInputArguments();
    final File outputPath =
        new File(project.getBuild().getDirectory(), GEN_DIRECTORY);
    final Controller controller;
    try {
      controller = new Controller(srcPaths, outputPath, typeMapperFile,
              Charset.forName(charSet));
    } catch (JsonSyntaxException | IOException e) {
      throw new MojoExecutionException("Failed to start generation process", e);
    }
    controller.run(outputType);
    getLog().info("Finished generating sources");
    addGeneratedSourcesAsResource(outputPath);
    addGeneratedSourcesToCompilePath(outputPath);
  }

  private List<File> processInputArguments() {
    final List<File> srcFiles = new ArrayList<>();
    for (final String include : includes) {
      getLog().info("found file(s):" + include);
      final File file = new File(include);
      final File actualFile =
          file.isAbsolute() ? file : new File(project.getBasedir(), include);
      if (actualFile.exists()) {
        srcFiles.add(actualFile);
      } else {
        getLog().warn("Include doesn't exits, is ignored: " + actualFile);
      }
    }
    return srcFiles;
  }

  private void addGeneratedSourcesAsResource(final File outputPath) {
    getLog().info("add resource path" + outputPath.getAbsolutePath());
    final Resource resource = new Resource();
    resource.setDirectory(outputPath.getAbsolutePath());
    project.addResource(resource);
  }

  private void addGeneratedSourcesToCompilePath(final File outputPath) {
    project.addCompileSourceRoot(outputPath.getAbsolutePath());
  }
}
