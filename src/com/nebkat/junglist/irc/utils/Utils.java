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

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Misc string utilities
 */
public class Utils {

    /**
     * Return true if string is null or length 0
     */
    public static boolean empty(String string) {
        return string == null || string.length() == 0;
    }

    /**
     * Split a string until a certain string occurs
     */
    public static String[] splitUntil(String source, String split, String until) {
        return splitUntilOccurrence(source, split, until, 1);
    }

    /**
     * Split a string until a certain string occurs a certain amount of times
     */
    public static String[] splitUntilOccurrence(String source, String split, String until, int occurrence) {
        int untilIndex = -until.length();
        for (int i = 0; i < occurrence; i++) {
            untilIndex = source.indexOf(until, untilIndex + until.length());
        }
        if (untilIndex <= 0 || untilIndex >= source.length()) {
            return source.split(split);
        }
        String toSplit = source.substring(0, untilIndex);
        String remainder = source.substring(untilIndex + until.length());
        String[] splitted = toSplit.split(split);

        String[] out = new String[splitted.length + 1];

        System.arraycopy(splitted, 0, out, 0, splitted.length);
        out[out.length - 1] = remainder;

        return out;
    }

    /**
     * Split a string until a certain string occurs a certain amount of times after another certain string occurs a certain amount of times
     */
    public static String[] splitUntilOccurenceAfterOccurence(String source, String split, String until, int occurrence, String after, int afterOccurrence) {
        if (occurrence <= 0) {
            return source.split(split);
        }
        int afterIndex = -after.length();
        for (int i = 0; i < afterOccurrence; i++) {
            afterIndex = source.indexOf(after, afterIndex + after.length());
        }
        if (afterIndex <= 0 || afterIndex >= source.length()) {
            return source.split(split);
        }
        int occurenceCountUntilAfter = 0;
        int untilIndex = source.indexOf(until);
        while (untilIndex != -1 && untilIndex < afterIndex + after.length()) {
            untilIndex = source.indexOf(until, untilIndex + until.length());
            occurenceCountUntilAfter++;
        }
        return splitUntilOccurrence(source, split, until, occurenceCountUntilAfter + occurrence);
    }


    public static <T> T indexOrDefault(T[] items, int index, T def) {
        if (index >= 0 && index < items.length) {
            return items[index];
        }
        return def;
    }

    public static <T> void forEach(T[] items, Consumer<T> action) {
        for (T item : items) {
            action.accept(item);
        }
    }

    public static String implode(Collection<String> items, String join) {
        String result = "";
        int i = 0;
        for (String s : items) {
            result += s;
            if (i < items.size() - 1) {
                result += join;
            }
            i++;
        }
        return result;
    }

    public static String implode(String[] items, String join) {
        return implode(items, join, 0, items.length);
    }

    public static String implode(String[] items, String join, int begin, int end) {
        String result = "";
        for (int i = begin; i < end; i++) {
            result += items[i];
            if (i < end - 1) {
                result += join;
            }
        }
        return result;
    }

    public static int countMatches(String haystack, String needle) {
        if (Utils.empty(haystack) || Utils.empty(needle)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i + needle.length() <= haystack.length();) {
            if (haystack.substring(i, i + needle.length()).equals(needle)) {
                i += needle.length();
                count++;
            } else {
                i++;
            }
        }
        return count;
    }
}
