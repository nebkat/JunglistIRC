/*
 * Copyright 2013 Nebojsa Cvetkovic. All rights reserved.
 *
 * This file is part of JunglistIRC.
 *
 * JunglistIRC is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JunglistIRC is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JunglistIRC.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nebkat.junglist.irc;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Log {
    public enum Level {
        VERBOSE, DEBUG, INFO, WARNING, ERROR, FATAL
    }

    private static Logger sLogger;

    public static void setLogger(Logger logger) {
        if (sLogger != null) {
            throw new RuntimeException("Logger can only be set once");
        }
        sLogger = logger;
    }

    public static void v(String tag, String message) {
        sLogger.log(Level.VERBOSE, tag, message);
    }

    public static void v(String tag, Throwable throwable) {
        sLogger.log(Level.VERBOSE, tag, throwable);
    }

    public static void v(String tag, String message, Throwable throwable) {
        sLogger.log(Level.VERBOSE, tag, message, throwable);
    }

    public static void d(String tag, String message) {
        sLogger.log(Level.DEBUG, tag, message);
    }

    public static void d(String tag, Throwable throwable) {
        sLogger.log(Level.DEBUG, tag, throwable);
    }

    public static void d(String tag, String message, Throwable throwable) {
        sLogger.log(Level.DEBUG, tag, message, throwable);
    }

    public static void i(String tag, String message) {
        sLogger.log(Level.INFO, tag, message);
    }

    public static void i(String tag, Throwable throwable) {
        sLogger.log(Level.INFO, tag, throwable);
    }

    public static void i(String tag, String message, Throwable throwable) {
        sLogger.log(Level.INFO, tag, message, throwable);
    }

    public static void w(String tag, String message) {
        sLogger.log(Level.WARNING, tag, message);
    }

    public static void w(String tag, Throwable throwable) {
        sLogger.log(Level.WARNING, tag, throwable);
    }

    public static void w(String tag, String message, Throwable throwable) {
        sLogger.log(Level.WARNING, tag, message, throwable);
    }

    public static void e(String tag, String message) {
        sLogger.log(Level.ERROR, tag, message);
    }

    public static void e(String tag, Throwable throwable) {
        sLogger.log(Level.ERROR, tag, throwable);
    }

    public static void e(String tag, String message, Throwable throwable) {
        sLogger.log(Level.ERROR, tag, message, throwable);
    }

    public static void wtf(String tag, String message) {
        sLogger.log(Level.FATAL, tag, message);
    }

    public static void wtf(String tag, Throwable throwable) {
        sLogger.log(Level.FATAL, tag, throwable);
    }

    public static void wtf(String tag, String message, Throwable throwable) {
        sLogger.log(Level.FATAL, tag, message, throwable);
    }

    public static String getStackTraceString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    public interface Logger {
        public void log(Level level, String tag, String message);
        public void log(Level level, String tag, Throwable throwable);
        public default void log(Level level, String tag, String message, Throwable throwable) {
            log(level, tag, message);
            log(level, tag, throwable);
        }
    }
}
