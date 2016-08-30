package gruifo.process;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

class Mapper {

  private final Gson gson = new Gson();
  private final Mappings mapper;

  public Mapper(final String fileName, final Charset charSet)
      throws JsonSyntaxException, IOException {
    mapper = gson.fromJson(FileUtils.readFileToString(new File(fileName),
        charSet), Mappings.class);
  }

  public String replace(final String string1, final String string2,
      final String string3) {
    return replace(string1, join(string2, string3));
  }

  public String replace(final String string1, final String string2) {
    final String string = join(string1, string2);
    return mapper.getReplace().get(string);
  }

  public boolean skip(final String fullClassName, final String methodName) {
    return skip(join(fullClassName, methodName));
  }

  public boolean skip(final String string) {
    return mapper.getSkip().contains(string);
  }

  public void replaceType() {
    mapper.getType();
  }

  private String join(final String string1, final String string2) {
    return string1 + '#' + string2;
  }
}
