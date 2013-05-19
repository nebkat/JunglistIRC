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

/**
 * User quit server event
 */
public class QuitEvent extends IRCEvent {
    protected final String mMessage;

    /**
     * User quit server event
     *
     * @param session IRC session the event happened on.
     * @param data Raw event data.
     * @param user User that quit the server.
     * @param message Message/reason the user quit.
     */
    public QuitEvent(long time, Session session, String data, Source user, String message) {
        super(time, session, data, user);
        mMessage = message;
    }

    public Source getUser() {
        return mSource;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] <" + mSource.getNick() + "> quit (" + mMessage + ")";
    }
}
