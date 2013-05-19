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

package com.nebkat.junglist.irc.events.irc.response.error;

import com.nebkat.junglist.irc.Session;
import com.nebkat.junglist.irc.Source;

public class NotEnoughParametersErrorEvent extends ErrorMessageEvent {
    protected final String mCommand;

    public NotEnoughParametersErrorEvent(long time, Session session, String data, Source source, String message, String command) {
        super(time, session, data, source, message);
        mCommand = command;
    }

    public String getCommand() {
        return mCommand;
    }
}

