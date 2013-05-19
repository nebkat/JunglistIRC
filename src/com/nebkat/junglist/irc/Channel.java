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

import java.util.HashMap;
import java.util.Map;

/**
 * IRC channel.
 */
public class Channel extends Target {
    private String mTopic;
    private String mTopicSetBy;
    private long mTopicSetAt;

    private Map<String, User> mUsers = new HashMap<>();

    public Channel(Session session, String channel) {
        super(session, channel);
    }

    /**
     * Get the channel name.
     *
     * @return Channel name.
     */
    public String getChannel() {
        return mTarget;
    }

    protected void setTopic(String topic) {
        mTopic = topic;
        mTopicSetBy = null;
        mTopicSetAt = System.currentTimeMillis() / 1000;
    }

    protected void setTopicSetBy(String setBy) {
        mTopicSetBy = setBy;
    }

    protected void setTopicSetAt(long topicSetAt) {
        mTopicSetAt = topicSetAt;
    }

    /**
     * Get the channel topic.
     *
     * @return Channel topic.
     */
    public String getTopic() {
        return mTopic;
    }

    /**
     * Get the user that set the topic.
     *
     * @return Topic setter.
     */
    public String getTopicSetBy() {
        return mTopicSetBy;
    }

    /**
     * Gets the time the topic was set.
     *
     * @return Topic set time.
     */
    public long getTopicSetAt() {
        return mTopicSetAt;
    }

    /**
     * Get channel user list.
     *
     * @return Immutable map containing users.
     */
    public Map<String, User> getUsers() {
        return new HashMap<>(mUsers);
    }

    protected Map<String, User> getUsersModifiable() {
        return mUsers;
    }
}
