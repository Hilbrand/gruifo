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
import java.util.HashMap;

public class Mappings {

  private HashMap<String, String> replace = new HashMap<>();
  private ArrayList<String> skip = new ArrayList<>();
  private HashMap<String, String> type = new HashMap<>();

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

  public HashMap<String, String> getType() {
    return type;
  }

  public void setType(final HashMap<String, String> type) {
    this.type = type;
  }
}
