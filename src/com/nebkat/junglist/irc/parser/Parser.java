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

import com.nebkat.junglist.irc.Log;
import com.nebkat.junglist.irc.Session;
import com.nebkat.junglist.irc.Source;
import com.nebkat.junglist.irc.events.irc.IRCEvent;
import com.nebkat.junglist.irc.events.irc.UnknownIRCEvent;
import com.nebkat.junglist.irc.parser.irc.JoinPartEventParser;
import com.nebkat.junglist.irc.parser.irc.KickEventParser;
import com.nebkat.junglist.irc.parser.irc.MessageEventParser;
import com.nebkat.junglist.irc.parser.irc.NickEventParser;
import com.nebkat.junglist.irc.parser.irc.PingPongEventParser;
import com.nebkat.junglist.irc.parser.irc.QuitEventParser;
import com.nebkat.junglist.irc.parser.irc.TopicEventParser;
import com.nebkat.junglist.irc.parser.irc.response.InviteEventParser;
import com.nebkat.junglist.irc.parser.irc.response.NamesEventParser;
import com.nebkat.junglist.irc.parser.irc.response.ResponseChannelMessageEventParser;
import com.nebkat.junglist.irc.parser.irc.response.ResponseMessageEventParser;
import com.nebkat.junglist.irc.parser.irc.response.ResponseTopicEventParser;
import com.nebkat.junglist.irc.parser.irc.response.ServerInfoEventParser;
import com.nebkat.junglist.irc.parser.irc.response.UserHostEventParser;
import com.nebkat.junglist.irc.parser.irc.response.error.ErrorMessageEventParser;
import com.nebkat.junglist.irc.parser.irc.response.error.NickErrorEventParser;
import com.nebkat.junglist.irc.parser.irc.response.error.TargetErrorEventParser;
import com.nebkat.junglist.irc.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Parser {
    private static final String TAG = "Parser";

    public static final String COMMAND_PING = "PING";
    public static final String COMMAND_PONG = "PONG";
    public static final String COMMAND_PRIVMSG = "PRIVMSG";
    public static final String COMMAND_NOTICE = "NOTICE";
    public static final String COMMAND_JOIN = "JOIN";
    public static final String COMMAND_PART = "PART";
    public static final String COMMAND_QUIT = "QUIT";
    public static final String COMMAND_TOPIC = "TOPIC";
    public static final String COMMAND_NICK = "NICK";
    public static final String COMMAND_INVITE = "INVITE";
    public static final String COMMAND_KICK = "KICK";

    public static final String RESPONSE_SERVER_CONNECTED = "001";
    public static final String RESPONSE_SERVER_INFO = "004";
    public static final String RESPONSE_USER_HOST = "302";
    public static final String RESPONSE_MOTD_START = "375";
    public static final String RESPONSE_MOTD_CONTENT = "372";
    public static final String RESPONSE_MOTD_END = "376";
    public static final String RESPONSE_INFO_START = "373";
    public static final String RESPONSE_INFO_CONTENT = "371";
    public static final String RESPONSE_INFO_END = "374";
    public static final String RESPONSE_TOPIC_NONE = "331";
    public static final String RESPONSE_TOPIC_MESSAGE = "332";
    public static final String RESPONSE_TOPIC_SETBY = "333";
    public static final String RESPONSE_NAMES_LIST = "353";
    public static final String RESPONSE_NAMES_END = "366";
    public static final String RESPONSE_CHANNEL_URL = "328";

    public static final String ERROR_NO_SUCH_NICK = "401";
    public static final String ERROR_NO_SUCH_SERVER = "402";
    public static final String ERROR_NO_SUCH_CHANNEL = "403";
    public static final String ERROR_CANNOT_SEND_TO_CHAN = "404";
    public static final String ERROR_TOO_MANY_CHANNELS = "405";
    public static final String ERROR_NO_ORIGIN = "409";
    public static final String ERROR_NO_RECIPIENT = "411";
    public static final String ERROR_NO_TEXT_TO_SEND = "412";
    public static final String ERROR_NO_MOTD = "422";
    public static final String ERROR_NO_NICK_GIVEN = "431";
    public static final String ERROR_NICK_INVALID = "432";
    public static final String ERROR_NICK_IN_USE = "433";
    public static final String ERROR_NOT_ON_CHANNEL = "442";
    public static final String ERROR_WRONG_PASSWORD = "464";

    private static final Map<String, Parser> sParsers = new HashMap<>();

    static {
        /**
         * Commands
         */
        // Ping and pong events
        PingPongEventParser pingPongEventParser = new PingPongEventParser();
        sParsers.put(COMMAND_PING, pingPongEventParser);
        sParsers.put(COMMAND_PONG, pingPongEventParser);

        // Message (privmsg and notice) events
        MessageEventParser messageEventParser = new MessageEventParser();
        sParsers.put(COMMAND_PRIVMSG, messageEventParser);
        sParsers.put(COMMAND_NOTICE, messageEventParser);

        // Join and part events
        JoinPartEventParser joinPartEventParser = new JoinPartEventParser();
        sParsers.put(COMMAND_JOIN, joinPartEventParser);
        sParsers.put(COMMAND_PART, joinPartEventParser);

        // Quit event
        sParsers.put(COMMAND_QUIT, new QuitEventParser());

        // Topic event
        sParsers.put(COMMAND_TOPIC, new TopicEventParser());

        // Nick event
        sParsers.put(COMMAND_NICK, new NickEventParser());

        // Invite event
        sParsers.put(COMMAND_INVITE, new InviteEventParser());

        // Kick event
        sParsers.put(COMMAND_KICK, new KickEventParser());

        /**
         * Responses
         */
        // Simple message responses
        ResponseMessageEventParser responseMessageEventParser = new ResponseMessageEventParser();
        sParsers.put(RESPONSE_SERVER_CONNECTED, responseMessageEventParser);
        sParsers.put(RESPONSE_MOTD_START, responseMessageEventParser);
        sParsers.put(RESPONSE_MOTD_CONTENT, responseMessageEventParser);
        sParsers.put(RESPONSE_MOTD_END, responseMessageEventParser);

        // Server info event
        sParsers.put(RESPONSE_SERVER_INFO, new ServerInfoEventParser());

        // Topic events
        ResponseTopicEventParser responseTopicEventParser = new ResponseTopicEventParser();
        sParsers.put(RESPONSE_TOPIC_NONE, responseTopicEventParser);
        sParsers.put(RESPONSE_TOPIC_MESSAGE, responseTopicEventParser);
        sParsers.put(RESPONSE_TOPIC_SETBY, responseTopicEventParser);

        // User host event
        sParsers.put(RESPONSE_USER_HOST, new UserHostEventParser());

        // Channel names list event
        NamesEventParser namesEventParser = new NamesEventParser();
        sParsers.put(RESPONSE_NAMES_LIST, namesEventParser);
        sParsers.put(RESPONSE_NAMES_END, namesEventParser);

        // Channel message events
        ResponseChannelMessageEventParser responseChannelMessageEventParser = new ResponseChannelMessageEventParser();
        sParsers.put(RESPONSE_CHANNEL_URL, responseChannelMessageEventParser);

        /**
         * Errors
         */
        // Simple message errors
        ErrorMessageEventParser errorMessageEventParser = new ErrorMessageEventParser();
        sParsers.put(ERROR_NO_ORIGIN, errorMessageEventParser);
        sParsers.put(ERROR_NO_RECIPIENT, errorMessageEventParser);
        sParsers.put(ERROR_NO_TEXT_TO_SEND, errorMessageEventParser);
        sParsers.put(ERROR_NO_MOTD, errorMessageEventParser);
        sParsers.put(ERROR_NO_NICK_GIVEN, errorMessageEventParser);

        // No such target errors
        TargetErrorEventParser targetErrorEventParser = new TargetErrorEventParser();
        sParsers.put(ERROR_NO_SUCH_NICK, targetErrorEventParser);
        sParsers.put(ERROR_NO_SUCH_SERVER, targetErrorEventParser);
        sParsers.put(ERROR_NO_SUCH_CHANNEL, targetErrorEventParser);
        sParsers.put(ERROR_NOT_ON_CHANNEL, targetErrorEventParser);


        // Nick errors
        NickErrorEventParser nickErrorEventParser = new NickErrorEventParser();
        sParsers.put(ERROR_NICK_IN_USE, nickErrorEventParser);
        sParsers.put(ERROR_NICK_INVALID, nickErrorEventParser);
    }

    public static IRCEvent parse(Session session, String line) {
        // Split at the last ":" (message character) when spaces are part of the parameter
        String[] parts = Utils.splitUntilOccurenceAfterOccurence(line, " ", ":", 1, " ", line.startsWith(":") ? 1 : 0);

        // Event time
        long time = System.currentTimeMillis();

        // All messages must at least contain the command parameter
        if (parts.length < 1 || (parts[0].startsWith(":") && parts.length < 2)) {
            Log.w(TAG, "Line too short: " + line);
            return new UnknownIRCEvent(time, session, line, new Source(session.getServerHost() != null ? session.getServerHost() : session.getServer()));
        }

        // Use server host as source of message if source is not supplied
        if (!parts[0].startsWith(":")) {
            String[] modifiedParts = new String[parts.length + 1];
            System.arraycopy(parts, 0, modifiedParts, 1, parts.length);
            modifiedParts[0] = session.getServerHost() != null ? session.getServerHost() : session.getServer();
            parts = modifiedParts;
        }

        String source = parts[0].replaceAll("^:", "");
        String command = parts[1];
        String[] params = Arrays.copyOfRange(parts, 2, parts.length);

        ParseToken token = new ParseToken(line, new Source(source), command, params);

        IRCEvent e;
        if (sParsers.containsKey(command)) {
            e = sParsers.get(command).parse(time, session, token);
            if (e != null) {
                return e;
            } else {
                Log.e(TAG, "Error parsing line: " + line);
            }
        } else {
            Log.d(TAG, "No parser for command: " + command);
        }
        return new UnknownIRCEvent(time, session, line, new Source(source));
    }

    public abstract IRCEvent parse(long time, Session session, ParseToken token);
}
