/**
 * Copyright 2018 The original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dekorate.logger;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.jline.jansi.AnsiConsole;
import org.junit.jupiter.api.Test;

class AnsiLoggerTest {

  /**
   * Creating a logger must not create a JLine system terminal.
   * <p>
   * {@link AnsiConsole#out()} builds (and never closes) a JLine system terminal on the process' stdin/stdout.
   * On a real tty this probes the terminal capabilities, which temporarily switches the tty to raw mode
   * (-icanon -echo) and restores it from a snapshot taken at creation time. When Dekorate runs in several
   * class loaders of the same JVM at the same time (e.g. a parallel Maven build: mvn -T 1C), the snapshots can
   * be taken while another terminal is probing, and the shell is left with echo disabled after the build.
   */
  @Test
  public void shouldNotCreateSystemTerminal() {
    assertNull(AnsiConsole.getTerminal(), "no terminal expected before the logger is created");
    new AnsiLogger();
    assertNull(AnsiConsole.getTerminal(), "AnsiLogger must not create a JLine system terminal");
  }
}
