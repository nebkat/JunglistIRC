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

package com.nebkat.junglist.irc.events;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Stores relevant information for event listeners.
 */
class RegisteredListener {
    private final EventListener mListener;
    private final Method mMethod;
    private final Annotation[] mFilters;

    public RegisteredListener(final EventListener listener, final Method method, final Annotation[] filters) {
        mListener = listener;
        mMethod = method;
        mFilters = filters;
    }

    /**
     * Gets the listener for this registration.
     *
     * @return Registered listener.
     */
    public EventListener getListener() {
        return mListener;
    }

    /**
     * Calls the event executor if filter matches.
     *
     * @param event The event to call.
     * @throws Exception If an event handler throws an exception.
     */
    public void callEvent(final Event event) throws Exception {
        try {
            if (event.filter(mFilters)) {
                mMethod.invoke(mListener, event);
            }
        } catch (InvocationTargetException ex) {
            throw new Exception(ex.getCause());
        } catch (Throwable t) {
            throw new Exception(t);
        }
    }
}