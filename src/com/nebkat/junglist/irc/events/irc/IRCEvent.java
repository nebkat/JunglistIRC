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
import com.nebkat.junglist.irc.events.Event;

import java.lang.annotation.Annotation;

/**
 * Generic IRC event
 */
public abstract class IRCEvent extends Event {
    protected final Session mSession;
    protected final String mData;
    protected final Source mSource;

    public IRCEvent(long time, Session session, String data, Source source) {
        super(time);
        mSession = session;
        mData = data;
        mSource = source;
    }

    public Session getSession() {
        return mSession;
    }

    public String getData() {
        return mData;
    }

    public Source getSource() {
        return mSource;
    }

    @Override
    protected boolean filter(Annotation[] filters) {
        for (Annotation filter : filters) {
            if (filter instanceof SourceFilter) {
                String mask = ((SourceFilter) filter).value();
                if (!mSource.match(mask)) {
                    return false;
                }
            }
        }
        return super.filter(filters);
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] <" + mSource.getNick() + ">";
    }
}
