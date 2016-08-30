package gruifo.process;

import java.util.ArrayList;
import java.util.HashMap;

public class Mappings {

  private HashMap<String, String> replace = new HashMap<>();
  private ArrayList<String> skip = new ArrayList<>();
  private ArrayList<String> type = new ArrayList<>();

  public HashMap<String, String> getReplace() {
    return replace;
  }

  public void setReplace(final HashMap<String, String> replace) {
    this.replace = replace;
  }

  public ArrayList<String> getSkip() {
    return skip;
  }

  public void setSkip(final ArrayList<String> skip) {
    this.skip = skip;
  }

  public ArrayList<String> getType() {
    return type;
  }

  public void setType(final ArrayList<String> type) {
    this.type = type;
  }
}
