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
 * 
**/

package io.dekorate.logger;

import static org.jline.jansi.Ansi.*;
import static org.jline.jansi.Ansi.Color.*;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.jline.jansi.Ansi;

import io.dekorate.Logger;
import io.dekorate.LoggerFactory;

public class AnsiLogger extends LoggerFactory<PrintStream> implements Logger {

  // AnsiConsole.out() installs a never-closed JLine system terminal that races on the tty attributes
  // (leaving the user's shell without echo, e.g. in parallel Maven builds). Write to System.out directly instead.
  private static final boolean COLORS = System.console() != null && System.getenv("NO_COLOR") == null
      && !"dumb".equals(System.getenv("TERM"));

  private PrintStream stream;

  public Logger create(PrintStream stream) {
    return new AnsiLogger(stream);
  }

  //Should not be used by user code. Only needed for Java SPI.
  public AnsiLogger() {
    this.stream = System.out;
  }

  public AnsiLogger(PrintStream stream) {
    check();
    this.stream = stream != null ? stream : System.out;
  }

  @Override
  public void debug(String message) {
    if (isDebugEnabled()) {
      check();
      stream.println(format(Level.DEBUG, CYAN, message));
    }
  }

  @Override
  public void info(String message) {
    if (isInfoEnabled()) {
      check();
      stream.println(format(Level.INFO, BLUE, message));
    }
  }

  @Override
  public void warning(String message) {
    if (isWarnEnabled()) {
      check();
      stream.println(format(Level.WARN, MAGENTA, message));
    }
  }

  @Override
  public void error(String message) {
    if (isErrorEnabled()) {
      check();
      stream.println(format(Level.ERROR, RED, message));
    }
  }

  private static String format(Level level, Ansi.Color color, String message) {
    if (!COLORS) {
      return "[" + level.name() + "] " + message;
    }
    return Ansi.ansi().a("[").fg(color).bold().a(level.name()).reset().a("] ").a(message).toString();
  }

  private void check() {
    if (stream == null) {
      stream = new PrintStream(new FileOutputStream(FileDescriptor.out));
      stream.println("Invalid AnsiLogger Stream -> Swapping to default sdt out logger.");
    }
  }

}
