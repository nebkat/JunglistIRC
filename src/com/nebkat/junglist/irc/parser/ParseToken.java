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

package com.nebkat.junglist.irc.parser;

import com.nebkat.junglist.irc.Source;

public class ParseToken {
    private final String mRaw;
    private final Source mSource;
    private final String mCommand;
    private final String[] mParams;

    public ParseToken(String raw, Source source, String command, String[] params) {
        mRaw = raw;
        mSource = source;
        mCommand = command;
        mParams = params;
    }


    public String getRaw() {
        return mRaw;
    }

    public Source getSource() {
        return mSource;
    }

    public String getCommand() {
        return mCommand;
    }

    public String[] getParams() {
        return mParams;
    }
}
