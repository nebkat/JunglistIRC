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

package com.nebkat.junglist.irc.parser.irc.response;

import com.nebkat.junglist.irc.Channel;
import com.nebkat.junglist.irc.Session;
import com.nebkat.junglist.irc.events.irc.IRCEvent;
import com.nebkat.junglist.irc.events.irc.response.ResponseTopicMessageEvent;
import com.nebkat.junglist.irc.events.irc.response.ResponseTopicNoneEvent;
import com.nebkat.junglist.irc.events.irc.response.ResponseTopicSetByEvent;
import com.nebkat.junglist.irc.parser.ParseToken;
import com.nebkat.junglist.irc.parser.Parser;
import com.nebkat.junglist.irc.utils.Utils;

public class ResponseTopicEventParser extends Parser {
    @Override
    public IRCEvent parse(long time, Session session, ParseToken token) {
        if (token.getParams().length < 2) {
            return null;
        }

        String channel = token.getParams()[1];
        Channel target = (Channel) session.getOrInitiateTarget(channel);
        if (token.getCommand().equals(Parser.RESPONSE_TOPIC_NONE)) {
            return new ResponseTopicNoneEvent(time, session, token.getRaw(), token.getSource(), target);
        } else if (token.getCommand().equals(Parser.RESPONSE_TOPIC_MESSAGE)) {
            if (token.getParams().length < 3) {
                return null;
            }
            String topic = token.getParams()[2];
            return new ResponseTopicMessageEvent(time, session, token.getRaw(), token.getSource(), target, topic);
        } else if (token.getCommand().equals(Parser.RESPONSE_TOPIC_SETBY)) {
            if (token.getParams().length < 3) {
                return null;
            }
            String setBy = token.getParams()[2];
            String setAtString = Utils.indexOrDefault(token.getParams(), 3, null);
            long setAt = setAtString == null ? (System.currentTimeMillis() / 1000) : Long.parseLong(setAtString);
            return new ResponseTopicSetByEvent(time, session, token.getRaw(), token.getSource(), target, setBy, setAt);
        } else {
            return null;
        }
    }
}
