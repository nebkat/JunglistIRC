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

/**
 * IRC source (nick, user, host).
 */
public class Source {
    private String mRaw;
    private String mNick;
    private String mUser;
    private String mHost;

    /**
     * Create a new Source from a raw source.
     *
     * @param raw Raw source.
     */
    public Source(String raw) {
        mRaw = raw;
        if (!mRaw.contains("@")) {
            mHost = mRaw;
        } else {
            if (mRaw.contains("!")) {
                mNick = mRaw.substring(0, mRaw.indexOf("!"));
                mUser = mRaw.substring(mRaw.indexOf("!") + 1, mRaw.indexOf("@"));
                mHost = mRaw.substring(mRaw.indexOf("@") + 1);
            } else {
                mNick = mRaw.substring(0, mRaw.indexOf("@"));
                mHost = mRaw.substring(mRaw.indexOf("@") + 1);
            }
        }
    }

    /**
     * Create a new Source from paramaters.
     *
     * @param nick Source nick.
     * @param user Source user.
     * @param host Source host.
     */
    public Source(String nick, String user, String host) {
        mNick = nick;
        mUser = user;
        mHost = host;
        mRaw = nick + "!" + user + "@" + host;
    }

    /**
     * Get raw source string.
     *
     * @return Raw source.
     */
    public String getRaw() {
        return mRaw;
    }

    /**
     * Get source nick.
     *
     * @return Source nick.
     */
    public String getNick() {
        return mNick;
    }

    /**
     * Get source user.
     *
     * @return Source user.
     */
    public String getUser() {
        return mUser;
    }

    /**
     * Get source host.
     *
     * @return Source host.
     */
    public String getHost() {
        return mHost;
    }

    public boolean match(String mask) {
        return match(mRaw, mask);
    }

    public static boolean match(String host, String mask) {
        String[] sections = mask.split("\\*");
        String text = host;
        for (String section : sections) {
            int index = text.indexOf(section);
            if (index == -1) {
                return false;
            }
            text = text.substring(index + section.length());
        }
        return true;
    }
}
