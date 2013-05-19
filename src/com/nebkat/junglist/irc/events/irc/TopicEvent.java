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
 * Channel topic event
 */
public class TopicEvent extends IRCEvent implements ChannelEvent {
    protected final Channel mChannel;
    protected final String mTopic;

    /**
     * Channel topic event
     *
     * @param session IRC session the event happened on.
     * @param data Raw event data.
     * @param user User that set the topic.
     * @param channel Channel the topic was set on.
     * @param topic Topic that was set.
     */
    public TopicEvent(long time, Session session, String data, Source user, Channel channel, String topic) {
        super(time, session, data, user);
        mChannel = channel;
        mTopic = topic;
    }

    @Override
    public Channel getChannel() {
        return mChannel;
    }

    @Override
    public Target getTarget() {
        return mChannel;
    }

    public String getTopic() {
        return mTopic;
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
        return "[" + getClass().getSimpleName() + "] <" + mSource.getNick() + "> set topic \"" + mTopic + "\" to " + mChannel.getChannel();
    }
}
