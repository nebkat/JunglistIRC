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

import com.nebkat.junglist.irc.events.Event;
import com.nebkat.junglist.irc.events.bbq.SessionConnectEvent;
import com.nebkat.junglist.irc.events.bbq.SessionDisconnectEvent;
import com.nebkat.junglist.irc.events.irc.IRCEvent;
import com.nebkat.junglist.irc.events.irc.JoinEvent;
import com.nebkat.junglist.irc.events.irc.NickEvent;
import com.nebkat.junglist.irc.events.irc.PartEvent;
import com.nebkat.junglist.irc.events.irc.QuitEvent;
import com.nebkat.junglist.irc.events.irc.response.*;
import com.nebkat.junglist.irc.parser.Parser;
import com.nebkat.junglist.irc.utils.FloodProtectedOutputThread;
import com.nebkat.junglist.irc.utils.InputThread;
import com.nebkat.junglist.irc.utils.OutputThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Server Session
 *
 * Manages per server session variables
 */
public class Session implements InputThread.Callback, OutputThread.Callback {
    public static final int MIN_FLOOD_DELAY = 250;
    public static final int MAX_FLOOD_DELAY = 750;
    public static final int MAX_LINES_PER_SECOND = 8;
    public static final int DELAY_INCREASE_STEP = 200;
    public static final int COOL_DOWN_PERIOD = 1000;

    private String mServer;
    private String mServerHost;
    private String mServerVersion;

    private Status mStatus;
    private String mNick;

    private Irc mIrc;

    private Socket mSocket;

    private InputThread mInputThread;
    private OutputThread mOutputThread;

    private OutputStream mOutputStream;
    private InputStream mInputStream;

    private Map<String, Target> mTargets = new HashMap<>();

    public enum Status {
        CONNECTED, DISCONNECTED
    }

    protected Session(Irc irc) {
        mStatus = Status.DISCONNECTED;

        mIrc = irc;
    }

    public void onEvent(Event event) {
        if (event instanceof ServerInfoEvent) {
            ServerInfoEvent serverInfoEvent = (ServerInfoEvent) event;
            mNick = serverInfoEvent.getNick();
            mServerHost = serverInfoEvent.getServerHost();
            mServerVersion = serverInfoEvent.getServerVersion();
        } else if (event instanceof NickEvent) {
            NickEvent nickEvent = (NickEvent) event;
            String originalNick = nickEvent.getSource().getNick();
            if (originalNick.equals(mNick)) {
                mNick = nickEvent.getNick();
            }
            for (Channel channel : getChannels().values()) {
                Iterator<User> iterator = channel.getUsersModifiable().values().iterator();
                while (iterator.hasNext()) {
                    User user = iterator.next();
                    if (user.getNick().equals(originalNick)) {
                        iterator.remove();
                        user.setNick(nickEvent.getNick());
                    }
                }
            }
        } else if (event instanceof ResponseChannelEvent) {
            if (event instanceof ResponseTopicNoneEvent) {
                ((ResponseChannelEvent) event).getChannel().setTopic(null);
            } else if (event instanceof ResponseTopicMessageEvent) {
                ResponseTopicMessageEvent responseTopicMessageEvent = (ResponseTopicMessageEvent) event;
                responseTopicMessageEvent.getChannel().setTopic(responseTopicMessageEvent.getTopic());
            } else if (event instanceof ResponseTopicSetByEvent) {
                ResponseTopicSetByEvent responseTopicSetByEvent = (ResponseTopicSetByEvent) event;
                responseTopicSetByEvent.getChannel().setTopicSetBy(responseTopicSetByEvent.getSetBy());
                responseTopicSetByEvent.getChannel().setTopicSetAt(responseTopicSetByEvent.getSetAt());
            }
        } else if (event instanceof JoinEvent) {
            JoinEvent joinEvent = (JoinEvent) event;
            Channel channel = joinEvent.getChannel();
            channel.getUsersModifiable().put(joinEvent.getSource().getNick(), new User(joinEvent.getSource().getNick()));
        } else if (event instanceof PartEvent) {
            PartEvent partEvent = (PartEvent) event;
            Channel channel = partEvent.getChannel();
            channel.getUsersModifiable().remove(partEvent.getSource().getNick());
            if (partEvent.getSource().getNick().equals(getNick())) {
                mTargets.remove(channel.getName());
            }
        } else if (event instanceof QuitEvent) {
            QuitEvent quitEvent = (QuitEvent) event;
            for (Target target : mTargets.values()) {
                if (target instanceof Channel) {
                    ((Channel) target).getUsersModifiable().remove(quitEvent.getSource().getNick());
                }
            }
            if (quitEvent.getSource().getNick().equals(getNick())) {
                mIrc.disconnect(this);
            }
        } else if (event instanceof NamesListEvent) {
            NamesListEvent namesListEvent = (NamesListEvent) event;
            Map<String, User> users = namesListEvent.getChannel().getUsersModifiable();
            String[] names = namesListEvent.getNames();
            for (String name : names) {
                if (name.startsWith("@")) {
                    // TODO operator mode
                    name = name.substring(1);
                } else if (name.startsWith("+")) {
                    // TODO voice mode
                    name = name.substring(1);
                }
                users.putIfAbsent(name, new User(name));
            }
        }
        mIrc.getEventHandlerManager().callEvent(event);
    }

    @Override
    public void onLineRead(String line) {
        IRCEvent event = Parser.parse(this, line);
        if (event != null) {
            onEvent(event);
        }
    }

    @Override
    public void onStreamClosed() {
        mIrc.disconnect(this);
    }

    protected void connect(String server, int port) throws IOException {
        if (mStatus == Status.CONNECTED) {
            return;
        }

        mServer = server;
        mSocket = new Socket(server, port);

        mInputStream = mSocket.getInputStream();
        mOutputStream = mSocket.getOutputStream();

        mInputThread = new InputThread(mInputStream, this);
        mOutputThread = new FloodProtectedOutputThread(mOutputStream, this, MIN_FLOOD_DELAY, MAX_FLOOD_DELAY, MAX_LINES_PER_SECOND, DELAY_INCREASE_STEP, COOL_DOWN_PERIOD);

        mInputThread.start();
        mOutputThread.start();

        mStatus = Status.CONNECTED;

        mIrc.getEventHandlerManager().callEvent(new SessionConnectEvent(System.currentTimeMillis(), this));
    }

    protected void disconnect() {
        if (mStatus == Status.DISCONNECTED) {
            return;
        }

        mStatus = Status.DISCONNECTED;

        mInputThread.interrupt();
        mOutputThread.interrupt();
        mInputThread = null;
        mOutputThread = null;

        try {
            mInputStream.close();
            mOutputStream.close();
        } catch (IOException e) {
            // Ignore
        } finally {
            mInputStream = null;
            mOutputStream = null;
        }

        try {
            mSocket.close();
        } catch (IOException e) {
            // Ignore
        } finally {
            mSocket = null;
        }

        mIrc.getEventHandlerManager().callEvent(new SessionDisconnectEvent(System.currentTimeMillis(), this));
    }

    public String getServer() {
        return mServer;
    }

    public String getServerHost() {
        return mServerHost;
    }

    public String getServerVersion() {
        return mServerVersion;
    }

    public Status getStatus() {
        return mStatus;
    }

    public String getNick() {
        return mNick;
    }

    protected void write(String line) {
        if (mStatus != Status.CONNECTED) {
            return;
        }
        mOutputThread.write(line);
    }

    public Channel getChannel(String channel) {
        return mTargets.get(channel) instanceof Channel ? (Channel) mTargets.get(channel) : null;
    }

    private Map<String, Channel> getChannels() {
        return mTargets.values().stream()
                .filter((target) -> target instanceof Channel)
                .collect(Collectors.toMap(Target::getName, (target) -> (Channel) target));
    }

    public Target getOrInitiateTarget(String target) {
        Target t;
        if (mTargets.containsKey(target)) {
            return mTargets.get(target);
        } else {
            if (target.startsWith("#") || target.startsWith("&")) {
                t = new Channel(this, target);
            } else {
                t = new Target(this, target);
            }
            mTargets.put(target, t);
            return t;
        }
    }
}
