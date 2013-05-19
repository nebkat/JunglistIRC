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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Queued output writer
 */
public class OutputThread extends Thread {
    private static final String TAG = "OutputThread";
    public static final String DEFAULT_ENCODING = "UTF-8";

    protected OutputStream mStream;
    protected String mEncoding;
    protected Callback mCallback;

    protected LinkedBlockingQueue<String> mWriteQueue = new LinkedBlockingQueue<>();

    public OutputThread(OutputStream output, Callback callback) {
        this(output, callback, DEFAULT_ENCODING);
    }

    public OutputThread(OutputStream output, Callback callback, String encoding) {
        super();
        setName(OutputThread.class.getSimpleName() + getId());
        mStream = output;
        mEncoding = encoding;
        mCallback = callback;
    }

    @Override
    public void run() {
        try (Writer writer = new OutputStreamWriter(mStream, mEncoding)) {
            process(writer);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, mEncoding + " encoding not supported", e);
        } catch (IOException e) {
            Log.e(TAG, e);
        } catch (InterruptedException e) {
            // Ignore
        }

        if (!isInterrupted()) {
            mCallback.onStreamClosed();
        }
    }

    /**
     * Move stream processing to helper class for easier extension
     */
    protected void process(Writer writer) throws InterruptedException, IOException {
        String line;
        while (!isInterrupted() && (line = mWriteQueue.take()) != null) {
            writer.write(line + "\r\n");
            writer.flush();
        }
    }

    public void write(String line) {
        try {
            mWriteQueue.put(line);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    public static interface Callback {
        public void onStreamClosed();
    }
}
