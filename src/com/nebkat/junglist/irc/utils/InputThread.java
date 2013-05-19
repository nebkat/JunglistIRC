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

package com.nebkat.junglist.irc.utils;

import com.nebkat.junglist.irc.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class InputThread extends Thread {
    private static final String TAG = "InputThread";
    public static final String DEFAULT_ENCODING = "UTF-8";

    protected InputStream mStream;
    protected String mEncoding;
    protected Callback mCallback;

    public InputThread(InputStream input, Callback callback) {
        this(input, callback, DEFAULT_ENCODING);
    }

    public InputThread(InputStream input, Callback callback, String encoding) {
        super();
        setName(InputThread.class.getSimpleName() + getId());
        mStream = input;
        mEncoding = encoding;
        mCallback = callback;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(mStream, mEncoding))) {
            process(reader);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, mEncoding + " encoding not supported", e);
        } catch (IOException e) {
            // Ignore
        }

        if (!isInterrupted()) {
            mCallback.onStreamClosed();
        }
    }

    /**
     * Move stream processing to helper class for easier extension
     */
    protected void process(BufferedReader reader) throws IOException {
        String line;
        while (!isInterrupted() && (line = reader.readLine()) != null) {
            mCallback.onLineRead(line);
        }
    }

    public static interface Callback {
        public void onLineRead(String line);
        public void onStreamClosed();
    }
}
