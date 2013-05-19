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

package com.nebkat.junglist.irc;

import com.nebkat.junglist.irc.events.EventHandler;
import com.nebkat.junglist.irc.events.EventHandlerManager;
import com.nebkat.junglist.irc.events.EventListener;
import com.nebkat.junglist.irc.events.irc.PingEvent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Irc Controller
 *
 * Manages library
 */
public class Irc implements EventListener {
    // Statics
    public static final int IRC_DEFAULT_PORT = 6667;

    // Commands
    public static final String COMMAND_PING = "PING";
    public static final String COMMAND_PONG = "PONG";
    public static final String COMMAND_NICK = "NICK";
    public static final String COMMAND_USER = "USER";
    public static final String COMMAND_PASS = "PASS";
    public static final String COMMAND_PRIVMSG = "PRIVMSG";
    public static final String COMMAND_NOTICE = "NOTICE";
    public static final String COMMAND_TOPIC = "TOPIC";
    public static final String COMMAND_JOIN = "JOIN";
    public static final String COMMAND_PART = "PART";
    public static final String COMMAND_QUIT = "QUIT";
    public static final String COMMAND_MODE = "MODE";
    public static final String COMMAND_KICK = "KICK";
    public static final String COMMAND_AWAY = "AWAY";
    public static final String COMMAND_INVITE = "INVITE";
    public static final String COMMAND_WHOIS = "WHOIS";
    public static final String COMMAND_USERHOST = "USERHOST";

    public static final String TEXT_RESET = "\u000f";
    public static final String TEXT_ITALIC = "\u0016";
    public static final String TEXT_BOLD = "\u0002";
    public static final String TEXT_UNDERLINE = "\u001f";

    private final EventHandlerManager mEventHandlerManager;

    private final Set<Session> mSessions = Collections.synchronizedSet(new HashSet<>());

    /**
     * Initialize a new Irc instance.
     */
    public Irc() {
        mEventHandlerManager = new EventHandlerManager();
        mEventHandlerManager.registerEvents(this);
    }

    /**
     * Get event handler manager.
     *
     * @return Event handler manager.
     *
     * @see EventHandlerManager
     */
    public EventHandlerManager getEventHandlerManager() {
        return mEventHandlerManager;
    }

    /**
     * Connect to an IRC server.
     *
     * @param server Server address. If no port is specified the default IRC port {@value #IRC_DEFAULT_PORT} will be used.
     * @return Connection session.
     *
     * @see Session
     */
    public Session connect(String server) throws IOException {
        if (server.contains(":")) {
            String[] split = server.split(":");
            if (split.length != 2) {
                throw new MalformedURLException(server);
            }
            int port;
            try {
                port = Integer.parseInt(split[1]);
            } catch (NumberFormatException nfe) {
                throw new MalformedURLException(server);
            }
            return connect(split[0], port);
        }
        return connect(server, IRC_DEFAULT_PORT);
    }

    /**
     * Connect to an IRC server with a port.
     *
     * @param server Server address.
     * @param port Server port.
     * @return Connection session.
     */
    public Session connect(String server, int port) throws IOException {
        Session session = new Session(this);
        mSessions.add(session);
        session.connect(server, port);
        return session;
    }

    /**
     * Get all sessions.
     *
     * @return Immutable set containing all sessions.
     */
    public Set<Session> getSessions() {
        return Collections.unmodifiableSet(new HashSet<>(mSessions));
    }

    /**
     * Disconnect a Session and remove from sessions list.
     *
     * @param session Session to disconnect.
     */
    public void disconnect(Session session) {
        session.disconnect();
        mSessions.remove(session);
    }

    public void close() {
        mEventHandlerManager.clear();
        mSessions.forEach(Session::disconnect);
    }

    /**
     * Listeners
     */
    @EventHandler
    protected void onPing(PingEvent event) {
        Irc.pong(event.getSession(), event.getPingSource());
    }

    /**
     * Actions
     *
     * @param session The session to execute the commands on.
     */

    /**
     * Send a request to test presence of a server or an active connection.
     */
    public static void ping(Session session) {
        Irc.write(session, COMMAND_PING, session.getNick());
    }

    /**
     * Send a request to test presence of a server or an active connection.
     *
     * @param target Server to test presence of.
     */
    public static void ping(Session session, String target) {
        Irc.write(session, COMMAND_PING, session.getNick() + " :" + target);
    }

    /**
     * Reply to a {@value #COMMAND_PING} command.
     *
     * @param target Target server to send reply to.
     */
    public static void pong(Session session, String target) {
        Irc.write(session, COMMAND_PONG, ":"+target);
    }

    /**
     * Request a nickname.
     *
     * @param nick Nickname to request from server.
     */
    public static void nick(Session session, String nick) {
        Irc.write(session, COMMAND_NICK, nick);
    }

    /**
     * Set user settings on connected to server.
     *
     * @param user Username to be identified by.
     * @param mode User mode (e.g. 8 - invisible).
     * @param realname Real name that can be seen using {@link #whois(Session, String)}.
     */
    public static void user(Session session, String user, int mode, String realname) {
        Irc.write(session, COMMAND_USER, user + " " + mode + " * :" + realname);
    }

    /**
     * Server password to connect.
     *
     * @param password Server password.
     */
    public static void pass(Session session, String password) {
        Irc.write(session, COMMAND_PASS, password);
    }

    /**
     * Send a message to a target.
     *
     * @param target Target to send the message to.
     * @param message Message to send.
     *
     * @see Target
     */
    public static void message(Session session, Target target, String message) {
        Irc.message(session, target.getName(), message);
    }

    /**
     * Send a message to a target
     *
     * @param target Target to send the message to.
     * @param message Message to send.
     *
     * @see Target
     */
    public static void message(Session session, String target, String message) {
        String[] lines = message.split("\n");
        for (String line : lines) {
            Irc.write(session, COMMAND_PRIVMSG, target + " :" + line);
        }
    }

    /**
     * Send a notice message to a target
     *
     * @param target Target to send the message to.
     * @param message Message to send.
     *
     * @see Target
     */
    public static void notice(Session session, Target target, String message) {
        Irc.notice(session, target.getName(), message);
    }

    /**
     * Send a notice message to a target
     *
     * @param target Target to send the message to.
     * @param message Message to send.
     *
     * @see Target
     */
    public static void notice(Session session, String target, String message) {
        String[] lines = message.split("\n");
        for (String line : lines) {
            Irc.write(session, COMMAND_NOTICE, target + " :" + line);
        }
    }

    /**
     * Request channel topic.
     *
     * @param channel Channel to request topic of.
     *
     * @see Channel
     */
    public static void topic(Session session, Channel channel) {
        Irc.topic(session, channel.getName());
    }

    /**
     * Request channel topic.
     *
     * @param channel Channel to request topic of.
     *
     * @see Channel
     */
    public static void topic(Session session, String channel) {
        Irc.write(session, COMMAND_TOPIC, channel);
    }

    /**
     * Set a channel topic.
     *
     * @param channel Channel to set the topic on.
     * @param topic Topic to set.
     *
     * @see Channel
     */
    public static void topic(Session session, Channel channel, String topic) {
        Irc.topic(session, channel.getName(), topic);
    }

    /**
     * Set a channel topic.
     *
     * @param channel Channel to set the topic on.
     * @param topic Topic to set.
     *
     * @see Channel
     */
    public static void topic(Session session, String channel, String topic) {
        Irc.write(session, COMMAND_TOPIC, channel + " :" + topic);
    }


    /**
     * Join a channel.
     *
     * @param channel Channel to join.
     */
    public static void join(Session session, Channel channel) {
        Irc.join(session, channel, null);
    }

    /**
     * Join a channel.
     *
     * @param channel Channel to join.
     */
    public static void join(Session session, String channel) {
        Irc.join(session, channel, null);
    }

    /**
     * Join a channel with a password.
     *
     * @param channel Channel to join.
     * @param key Channel password.
     */
    public static void join(Session session, Channel channel, String key) {
        Irc.join(session, channel.getName(), key);
    }

    /**
     * Join a channel with a password.
     *
     * @param channel Channel to join.
     * @param key Channel password.
     */
    public static void join(Session session, String channel, String key) {
        Irc.write(session, COMMAND_JOIN, channel + (key != null ? " " + key : ""));
    }

    /**
     * Leave a channel.
     *
     * @param channel Channel to leave.
     */
    public static void part(Session session, Channel channel) {
        Irc.part(session, channel, null);
    }

    /**
     * Leave a channel.
     *
     * @param channel Channel to leave.
     */
    public static void part(Session session, String channel) {
        Irc.part(session, channel, null);
    }

    /**
     * Leave a channel with a reason.
     *
     * @param channel Channel to leave.
     * @param reason Reason for leaving channel.
     */
    public static void part(Session session, Channel channel, String reason) {
        Irc.part(session, channel.getName(), reason);
    }

    /**
     * Leave a channel with a reason.
     *
     * @param channel Channel to leave.
     * @param reason Reason for leaving channel.
     */
    public static void part(Session session, String channel, String reason) {
        Irc.write(session, COMMAND_PART, channel + (reason != null ? " :" + reason : ""));
    }

    /**
     * Quit from a session.
     */
    public static void quit(Session session) {
        Irc.quit(session, null);
    }

    /**
     * Quit from a session with a reason.
     *
     * @param reason Reason for quitting session.
     */
    public static void quit(Session session, String reason) {
        Irc.write(session, COMMAND_QUIT, (reason != null ? ":" + reason : ""));
    }

    /**
     * Invite a user to a channel.
     *
     * @param channel Channel to invite the user to.
     * @param user User to invite.
     */
    public static void invite(Session session, Channel channel, String user) {
        Irc.invite(session, channel.getName(), user);
    }

    /**
     * Invite a user to a channel.
     *
     * @param channel Channel to invite the user to.
     * @param user User to invite.
     */
    public static void invite(Session session, String channel, String user) {
        Irc.write(session, COMMAND_INVITE, user + " " + channel);
    }

    /**
     * Set a user mode in a channel.
     *
     * @param mode Modes to set on the user.
     * @param channel Channel to set the user mode on.
     * @param user User to set the mode on.
     */
    public static void mode(Session session, String mode, Channel channel, String user) {
        Irc.mode(session, mode, channel.getName(), user);
    }

    /**
     * Set a user mode in a channel.
     *
     * @param mode Modes to set on the user.
     * @param channel Channel to set the user mode on.
     * @param user User to set the mode on.
     */
    public static void mode(Session session, String mode, String channel, String user) {
        Irc.write(session, COMMAND_MODE, channel + " " + mode + " " + user);
    }

    /**
     * Give a user operator privileges on a channel.
     *
     * @param channel Channel to give the user operator privileges on.
     * @param user User to give operator privileges to.
     */
    public static void op(Session session, Channel channel, String user) {
        Irc.mode(session, "+o", channel, user);
    }

    /**
     * Give a user operator privileges on a channel.
     *
     * @param channel Channel to give the user operator privileges on.
     * @param user User to give operator privileges to.
     */
    public static void op(Session session, String channel, String user) {
        Irc.mode(session, "+o", channel, user);
    }

    /**
     * Remove a user's operator privileges on a channel.
     *
     * @param channel Channel to remove the user operator privileges on.
     * @param user User to remove operator privileges to.
     */
    public static void deop(Session session, Channel channel, String user) {
        Irc.mode(session, "-o", channel, user);
    }

    /**
     * Remove a user's operator privileges on a channel.
     *
     * @param channel Channel to remove the user operator privileges on.
     * @param user User to remove operator privileges to.
     */
    public static void deop(Session session, String channel, String user) {
        Irc.mode(session, "-o", channel, user);
    }

    /**
     * Give a user voice privileges on a channel.
     *
     * @param channel Channel to give the user voice privileges on.
     * @param user User to give voice privileges to.
     */
    public static void voice(Session session, Channel channel, String user) {
        Irc.mode(session, "+v", channel, user);
    }

    /**
     * Give a user voice privileges on a channel.
     *
     * @param channel Channel to give the user voice privileges on.
     * @param user User to give voice privileges to.
     */
    public static void voice(Session session, String channel, String user) {
        Irc.mode(session, "+v", channel, user);
    }

    /**
     * Remove a user's voice privileges on a channel.
     *
     * @param channel Channel to remove the user voice privileges on.
     * @param user User to remove voice privileges to.
     */
    public static void devoice(Session session, Channel channel, String user) {
        Irc.mode(session, "-v", channel, user);
    }

    /**
     * Remove a user's voice privileges on a channel.
     *
     * @param channel Channel to remove the user voice privileges on.
     * @param user User to remove voice privileges to.
     */
    public static void devoice(Session session, String channel, String user) {
        Irc.mode(session, "-v", channel, user);
    }

    /**
     * Ban a user on a channel.
     *
     * @param channel Channel to ban the user on.
     * @param user User to ban.
     */
    public static void ban(Session session, Channel channel, String user) {
        Irc.mode(session, "+b", channel, user);
    }

    /**
     * Ban a user on a channel.
     *
     * @param channel Channel to ban the user on.
     * @param user User to ban.
     */
    public static void ban(Session session, String channel, String user) {
        Irc.mode(session, "+b", channel, user);
    }

    /**
     * Unban a user on a channel.
     *
     * @param channel Channel to unban the user on.
     * @param user User to unban.
     */
    public static void unban(Session session, Channel channel, String user) {
        Irc.mode(session, "-b", channel, user);
    }


    /**
     * Unban a user on a channel.
     *
     * @param channel Channel to unban the user on.
     * @param user User to unban.
     */
    public static void unban(Session session, String channel, String user) {
        Irc.mode(session, "-b", channel, user);
    }

    /**
     * Mute a user on a channel.
     *
     * @param channel Channel to mute the user on.
     * @param user User to mute.
     */
    public static void mute(Session session, Channel channel, String user) {
        Irc.mode(session, "+q", channel, user);
    }

    /**
     * Mute a user on a channel.
     *
     * @param channel Channel to mute the user on.
     * @param user User to mute.
     */
    public static void mute(Session session, String channel, String user) {
        Irc.mode(session, "+q", channel, user);
    }

    /**
     * Unmute a user on a channel.
     *
     * @param channel Channel to unmute the user on.
     * @param user User to unmute.
     */
    public static void unmute(Session session, Channel channel, String user) {
        Irc.mode(session, "-q", channel, user);
    }

    /**
     * Unmute a user on a channel.
     *
     * @param channel Channel to unmute the user on.
     * @param user User to unmute.
     */
    public static void unmute(Session session, String channel, String user) {
        Irc.mode(session, "-q", channel, user);
    }

    /**
     * Kick a user from a channel.
     *
     * @param channel Channel to kick the user from.
     * @param user User to kick.
     */
    public static void kick(Session session, Channel channel, String user) {
        Irc.kick(session, channel, user, null);
    }

    /**
     * Kick a user from a channel.
     *
     * @param channel Channel to kick the user from.
     * @param user User to kick.
     */
    public static void kick(Session session, String channel, String user) {
        Irc.kick(session, channel, user, null);
    }

    /**
     * Kick a user from a channel with a reason.
     *
     * @param channel Channel to kick the user from.
     * @param user User to kick.
     * @param reason Reason for kicking the user.
     *
     */
    public static void kick(Session session, Channel channel, String user, String reason) {
        Irc.kick(session, channel.getName(), user, reason);
    }

    /**
     * Kick a user from a channel with a reason.
     *
     * @param channel Channel to kick the user from.
     * @param user User to kick.
     * @param reason Reason for kicking the user.
     *
     */
    public static void kick(Session session, String channel, String user, String reason) {
        Irc.write(session, COMMAND_KICK, channel + " " + user + (reason != null ? " :" + reason : ""));
    }

    /**
     * Request info about a user.
     *
     * @param user User to request info about.
     */
    public static void whois(Session session, String user) {
        Irc.write(session, COMMAND_WHOIS, user);
    }

    /**
     * Set away status.
     *
     * @param message Reason for being away.
     */
    public static void away(Session session, String message) {
        Irc.write(session, COMMAND_AWAY, ":" + message);
    }

    /**
     * Unset away status.
     */
    public static void unaway(Session session) {
        Irc.write(session, COMMAND_AWAY);
    }

    /**
     * Request a user's hostname
     *
     * @param user User to request hostname for.
     */
    public static void userhost(Session session, String user) {
        Irc.write(session, COMMAND_USERHOST, user);
    }

    /**
     * Write a command with parameters.
     *
     * @param command Command to write.
     * @param data Command paramaters.
     */
    public static void write(Session session, String command, String data) {
        Irc.write(session, command + " " + data);
    }

    /**
     * Write a raw line.
     *
     * @param line Raw line to write.
     */
    public static void write(Session session, String line) {
        session.write(line);
    }
}
