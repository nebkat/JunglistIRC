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

package com.nebkat.junglist.irc.parser.irc;

import com.nebkat.junglist.irc.Channel;
import com.nebkat.junglist.irc.Session;
import com.nebkat.junglist.irc.Source;
import com.nebkat.junglist.irc.events.irc.IRCEvent;
import com.nebkat.junglist.irc.events.irc.JoinEvent;
import com.nebkat.junglist.irc.events.irc.PartEvent;
import com.nebkat.junglist.irc.parser.ParseToken;
import com.nebkat.junglist.irc.parser.Parser;
import com.nebkat.junglist.irc.utils.Utils;

public class JoinPartEventParser extends Parser {
    @Override
    public IRCEvent parse(long time, Session session, ParseToken token) {
        if (token.getParams().length < 1) {
            return null;
        }

        Source user = token.getSource();
        String channel = token.getParams()[0];
        Channel target = (Channel) session.getOrInitiateTarget(channel);

        if (token.getCommand().equals(Parser.COMMAND_JOIN)) {
            return new JoinEvent(time, session, token.getRaw(), user, target);
        } else if (token.getCommand().equals(Parser.COMMAND_PART)) {
            String message = Utils.indexOrDefault(token.getParams(), 1, null);
            return new PartEvent(time, session, token.getRaw(), user, target, message);
        } else {
            return null;
        }
    }
}
