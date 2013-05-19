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

import com.nebkat.junglist.irc.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * Manages all {@link EventListener}s and {@link Event} distribution.
 */
public class EventHandlerManager {
    private static final String TAG = "EventHandlerManager";

    private Map<Class<?>, List<RegisteredListener>> mHandlers = new HashMap<>();
    private BlockingQueue<Function> mEventExecutorQueue = new LinkedBlockingQueue<>();

    /**
     * Clear all {@link EventHandler}s
     */
    public void clear() {
        mHandlers.values().forEach(List::clear);
        mHandlers.clear();
    }

    /**
     * Register {@link EventHandler}s in the listener for events.
     *
     * @param listener Event listener.
     */
    public void registerEvents(EventListener listener) {
        for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : createRegisteredListeners(listener).entrySet()) {
            mHandlers.putIfAbsent(entry.getKey(), new CopyOnWriteArrayList<>());
            mHandlers.get(entry.getKey()).addAll(entry.getValue());
        }
    }

    /**
     * Unegister {@link EventHandler}s in the listener for events.
     *
     * @param listener Event listener.
     */
    public void unregisterEvents(EventListener listener) {
        mHandlers.values().forEach((list) -> list.removeIf((registeredListener) -> registeredListener.getListener() == listener));
    }

    private Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(EventListener listener) {
        Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<>();

        for (final Method method : listener.getClass().getDeclaredMethods()) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;
            if (method.getParameterTypes().length != 1) {
                Log.e(TAG, "Could not register event handler " + method.getName() + ": Must contain only one paramater");
                continue;
            }
            final Class<?> checkClass = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(checkClass)) {
                Log.e(TAG, "Could not register event handler " + method.getName() + ": " + checkClass.getSimpleName() + " does not extend Event.class");
                continue;
            }
            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            Set<RegisteredListener> eventSet = ret.get(eventClass);
            if (eventSet == null) {
                eventSet = new HashSet<>();
                ret.put(eventClass, eventSet);
            }

            // Check for extending deprecated events
            for (Class<?> clazz = eventClass; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
                if (clazz.getAnnotation(Deprecated.class) != null) {
                    Log.w(TAG, "Event handler method " + method.getName() + " uses deprecated event " + eventClass.getSimpleName());
                    break;
                }
            }

            // Get all event filters for method
            Annotation[] filters = Arrays.stream(method.getDeclaredAnnotations())
                    .filter((annotation) -> annotation.annotationType().getAnnotation(EventFilter.class) != null)
                    .toArray(Annotation[]::new);

            eventSet.add(new RegisteredListener(listener, method, filters));
        }
        return ret;
    }

    /**
     * Pass an event to registered {@link EventHandler}s.
     *
     * @param event Event to pass.
     */
    public void callEvent(Event event) {
        for (Class<?> clazz = event.getClass(); Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
            List<RegisteredListener> handlers = mHandlers.get(clazz);
            if (handlers != null) {
                for (RegisteredListener registration : handlers) {
                    try {
                        registration.callEvent(event);
                    } catch (Throwable ex) {
                        Log.e(TAG, "Could not pass event " + event.getClass().getSimpleName() + " to " + registration.getListener().getClass().getName(), ex);
                    }
                }
            }
        }
    }
}
