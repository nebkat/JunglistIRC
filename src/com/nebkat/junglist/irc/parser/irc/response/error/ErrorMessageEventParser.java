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
import com.nebkat.junglist.irc.events.irc.response.error.NoMotdErrorEvent;
import com.nebkat.junglist.irc.events.irc.response.error.NoNickGivenErrorEvent;
import com.nebkat.junglist.irc.events.irc.response.error.NoOriginErrorEvent;
import com.nebkat.junglist.irc.events.irc.response.error.NoRecipientErrorEvent;
import com.nebkat.junglist.irc.events.irc.response.error.NoTextToSendErrorEvent;
import com.nebkat.junglist.irc.events.irc.response.error.WrongPasswordErrorEvent;
import com.nebkat.junglist.irc.parser.ParseToken;
import com.nebkat.junglist.irc.parser.Parser;

public class ErrorMessageEventParser extends Parser {
    @Override
    public IRCEvent parse(long time, Session session, ParseToken token) {
        if (token.getParams().length < 2) {
            return null;
        }

        String message = token.getParams()[1];
        if (token.getCommand().equals(Parser.ERROR_NO_ORIGIN)) {
            return new NoOriginErrorEvent(time, session, token.getRaw(), token.getSource(), message);
        } else if (token.getCommand().equals(Parser.ERROR_NO_RECIPIENT)) {
            return new NoRecipientErrorEvent(time, session, token.getRaw(), token.getSource(), message);
        } else if (token.getCommand().equals(Parser.ERROR_NO_TEXT_TO_SEND)) {
            return new NoTextToSendErrorEvent(time, session, token.getRaw(), token.getSource(), message);
        } else if (token.getCommand().equals(Parser.ERROR_NO_MOTD)) {
            return new NoMotdErrorEvent(time, session, token.getRaw(), token.getSource(), message);
        } else if (token.getCommand().equals(Parser.ERROR_WRONG_PASSWORD)) {
            return new WrongPasswordErrorEvent(time, session, token.getRaw(), token.getSource(), message);
        } else if (token.getCommand().equals(Parser.ERROR_NO_NICK_GIVEN)) {
            return new NoNickGivenErrorEvent(time, session, token.getRaw(), token.getSource(), message);
        } else {
            return null;
        }
    }
}
