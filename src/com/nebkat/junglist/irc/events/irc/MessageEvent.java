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

import com.nebkat.junglist.irc.Session;
import com.nebkat.junglist.irc.Source;
import com.nebkat.junglist.irc.Target;

import java.lang.annotation.Annotation;

/**
 * Generic channel/private message event
 */
public abstract class MessageEvent extends IRCEvent implements TargetEvent {
    protected final Target mTarget;
    protected final String mMessage;

    /**
     * Generic channel/private message event
     *
     * @param session IRC session the event happened on.
     * @param data Raw event data.
     * @param source Source of the message (user, server, etc).
     * @param target Target of the message (channel, user/private, etc).
     * @param message Message that was sent.
     */
    public MessageEvent(long time, Session session, String data, Source source, Target target, String message) {
        super(time, session, data, source);
        mTarget = target;
        mMessage = message;
    }

    public Target getTarget() {
        return mTarget;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    protected boolean filter(Annotation[] filters) {
        for (Annotation filter : filters) {
            if (filter instanceof TargetFilter) {
                String target = ((TargetFilter) filter).value();
                if (!mTarget.getName().equalsIgnoreCase(target)) {
                    return false;
                }
            }
        }
        return super.filter(filters);
    }
}
