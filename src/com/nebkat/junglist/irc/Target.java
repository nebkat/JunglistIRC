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
 * IRC target. Either a private message or @{link Channel}.
 */
public class Target {
    private final Session mSession;
    protected final String mTarget;

    public Target(Session session, String target) {
        mSession = session;
        mTarget = target;
    }

    /**
     * Get the session associated with this target.
     *
     * @return Session associated with this target.
     */
    public Session getSession() {
        return mSession;
    }

    /**
     * Get the target name.
     *
     * @return Target name.
     */
    public String getName() {
        return mTarget;
    }
}
