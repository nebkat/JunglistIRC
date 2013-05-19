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

import com.nebkat.junglist.irc.Channel;
import com.nebkat.junglist.irc.Session;
import com.nebkat.junglist.irc.Source;
import com.nebkat.junglist.irc.Target;

import java.lang.annotation.Annotation;

/**
 * User kicked from channel event
 */
public class KickEvent extends IRCEvent implements ChannelEvent {
    protected final String mNick;
    protected final Channel mChannel;
    protected final String mMessage;

    /**
     * User kicked from channel event
     *
     * @param session IRC session the event happened on.
     * @param data Raw event data.
     * @param kicker User that kicked user.
     * @param nick User that was kicked.
     * @param channel Channel the user was kicked from.
     * @param message Message/reason for the kick.
     */
    public KickEvent(long time, Session session, String data, Source kicker, String nick, Channel channel, String message) {
        super(time, session, data, kicker);
        mNick = nick;
        mChannel = channel;
        mMessage = message;
    }

    @Override
    public Channel getChannel() {
        return mChannel;
    }

    @Override
    public Target getTarget() {
        return mChannel;
    }

    public String getNick() {
        return mNick;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    protected boolean filter(Annotation[] filters) {
        for (Annotation filter : filters) {
            if (filter instanceof TargetFilter) {
                String target = ((TargetFilter) filter).value();
                if (!mChannel.getName().equalsIgnoreCase(target)) {
                    return false;
                }
            }
        }
        return super.filter(filters);
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] <" + mSource.getNick() + "> kicked " + mNick + " from " + mChannel.getChannel() + " (" + mMessage + ")";
    }
}
