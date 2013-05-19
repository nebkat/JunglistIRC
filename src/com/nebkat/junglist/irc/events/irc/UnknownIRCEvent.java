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

package com.nebkat.junglist.irc.events.irc;

import com.nebkat.junglist.irc.Session;
import com.nebkat.junglist.irc.Source;

/**
 * IRC event that could not be parsed or is not supported.
 */
public class UnknownIRCEvent extends IRCEvent {
    /**
     * IRC event that could not be parsed or is not supported.
     *
     * @param session IRC session the event happened on.
     * @param data Raw event data.
     * @param source Source of the event (user, server, etc).
     */
    public UnknownIRCEvent(long time, Session session, String data, Source source) {
        super(time, session, data, source);
    }
}
