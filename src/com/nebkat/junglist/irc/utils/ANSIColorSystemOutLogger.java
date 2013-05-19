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

package com.nebkat.junglist.irc.utils;

import com.nebkat.junglist.irc.Log;

import java.util.EnumMap;
import java.util.Map;

public class ANSIColorSystemOutLogger extends SystemOutLogger {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private static final Map<Log.Level, String> COLOR_MAP = new EnumMap<Log.Level, String>(Log.Level.class) {
        {
            put(Log.Level.VERBOSE, ANSI_WHITE);
            put(Log.Level.DEBUG, ANSI_BLUE);
            put(Log.Level.INFO, ANSI_GREEN);
            put(Log.Level.WARNING, ANSI_YELLOW);
            put(Log.Level.ERROR, ANSI_RED);
            put(Log.Level.FATAL, ANSI_RED);
        }
    };

    @Override
    public void log(Log.Level level, String tag, String message) {
        System.out.println(COLOR_MAP.get(level) + level.name().substring(0, 1) + "/" + tag + ": " + message + ANSI_RESET);
    }

    @Override
    public void log(Log.Level level, String tag, Throwable throwable) {
        String[] stackTrace = Log.getStackTraceString(throwable).split("\n");
        for (String s : stackTrace) {
            System.out.println(COLOR_MAP.get(level) + level.name().substring(0, 1) + "/" + tag + ": " + s + ANSI_RESET);
        }
    }
}
