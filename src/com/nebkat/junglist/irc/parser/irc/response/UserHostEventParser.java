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

import com.nebkat.junglist.irc.Session;
import com.nebkat.junglist.irc.Source;
import com.nebkat.junglist.irc.events.irc.IRCEvent;
import com.nebkat.junglist.irc.events.irc.response.UserHostEvent;
import com.nebkat.junglist.irc.parser.ParseToken;
import com.nebkat.junglist.irc.parser.Parser;

public class UserHostEventParser extends Parser {
    @Override
    public IRCEvent parse(long time, Session session, ParseToken token) {
        if (token.getParams().length < 2) {
            return null;
        }

        // TODO return multiple
        String[] infos = token.getParams()[1].split(" ")[0].split(" ");
        String[] nicks = new String[infos.length];
        Source[] hosts = new Source[infos.length];
        for (int i = 0; i < infos.length; i++) {
            String[] info = infos[i].split("=");
            if (info.length < 2) {
                continue;
            }
            String nick = info[0];
            String host = info[1].replaceAll("^(\\+|\\-)", "");
            Source parsedData = new Source(nick + "!" + host);
            nicks[i] = nick;
            hosts[i] = parsedData;
        }



        if (token.getCommand().equals(Parser.RESPONSE_USER_HOST)) {
            return new UserHostEvent(time, session, token.getRaw(), token.getSource(), nicks, hosts);
        } else {
            return null;
        }
    }
}