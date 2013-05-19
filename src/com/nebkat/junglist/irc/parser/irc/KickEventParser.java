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
import com.nebkat.junglist.irc.events.irc.IRCEvent;
import com.nebkat.junglist.irc.events.irc.KickEvent;
import com.nebkat.junglist.irc.parser.ParseToken;
import com.nebkat.junglist.irc.parser.Parser;
import com.nebkat.junglist.irc.utils.Utils;

public class KickEventParser extends Parser {
    @Override
    public IRCEvent parse(long time, Session session, ParseToken token) {
        if (token.getParams().length < 2) {
            return null;
        }

        String channel = token.getParams()[0];
        Channel target = (Channel) session.getOrInitiateTarget(channel);
        String user = token.getParams()[1];
        String message = Utils.indexOrDefault(token.getParams(), 2, null);

        if (token.getCommand().equals(Parser.COMMAND_KICK)) {
            return new KickEvent(time, session, token.getRaw(), token.getSource(), user, target, message);
        } else {
            return null;
        }
    }
}
