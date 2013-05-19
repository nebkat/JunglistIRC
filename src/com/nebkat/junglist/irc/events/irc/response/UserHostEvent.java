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

package com.nebkat.junglist.irc.events.irc.response;

import com.nebkat.junglist.irc.Session;
import com.nebkat.junglist.irc.Source;

public class UserHostEvent extends ResponseEvent {
    private final String[] mNicks;
    private final Source[] mHosts;

    public UserHostEvent(long time, Session session, String data, Source source, String[] nicks, Source[] hosts) {
        super(time, session, data, source);
        mNicks = nicks;
        mHosts = hosts;
    }

    public String getNick() {
        return mNicks != null && mNicks.length > 0 ? mNicks[0] : null;
    }

    public Source getUserHost() {
        return mHosts != null && mHosts.length > 0 ? mHosts[0] : null;
    }

    public String getUser() {
        return mHosts != null && mHosts.length > 0 && mHosts[0] != null ? mHosts[0].getUser() : null;
    }

    public String getHost() {
        return mHosts != null && mHosts.length > 0 && mHosts[0] != null ? mHosts[0].getHost() : null;
    }

    public String[] getNicks() {
        return mNicks;
    }

    public Source[] getHosts() {
        return mHosts;
    }
}
