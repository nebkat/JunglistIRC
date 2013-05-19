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

package com.nebkat.junglist.irc.parser.irc.response.error;

import com.nebkat.junglist.irc.Session;
import com.nebkat.junglist.irc.events.irc.IRCEvent;
import com.nebkat.junglist.irc.events.irc.response.error.NoSuchChannelErrorEvent;
import com.nebkat.junglist.irc.events.irc.response.error.NoSuchNickErrorEvent;
import com.nebkat.junglist.irc.events.irc.response.error.NoSuchServerErrorEvent;
import com.nebkat.junglist.irc.events.irc.response.error.NotOnChannelErrorEvent;
import com.nebkat.junglist.irc.parser.ParseToken;
import com.nebkat.junglist.irc.parser.Parser;

public class TargetErrorEventParser extends Parser {
    @Override
    public IRCEvent parse(long time, Session session, ParseToken token) {
        if (token.getParams().length < 3) {
            return null;
        }

        String target = token.getParams()[1];
        String message = token.getParams()[2];
        if (token.getCommand().equals(Parser.ERROR_NO_SUCH_NICK)) {
            return new NoSuchNickErrorEvent(time, session, token.getRaw(), token.getSource(), message, target);
        } else if (token.getCommand().equals(Parser.ERROR_NO_SUCH_SERVER)) {
            return new NoSuchServerErrorEvent(time, session, token.getRaw(), token.getSource(), message, target);
        } else if (token.getCommand().equals(Parser.ERROR_NO_SUCH_CHANNEL)) {
            return new NoSuchChannelErrorEvent(time, session, token.getRaw(), token.getSource(), message, target);
        } else if (token.getCommand().equals(Parser.ERROR_NOT_ON_CHANNEL)) {
            return new NotOnChannelErrorEvent(time, session, token.getRaw(), token.getSource(), message, target);
        } else {
            return null;
        }
    }
}
