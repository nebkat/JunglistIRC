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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Flood protected queued output writer
 */
public class FloodProtectedOutputThread extends OutputThread {
    private long[] mLastTimestamps;
    private int mCurrentDelay;
    private boolean mInDelay;
    private long mCoolDown;

    /**
     * Minimum delay between two writes, in ms
     */
    private int mMinDelay;

    /**
     * Maximum delay between two writes, in ms
     */
    private int mMaxDelay;

    /**
     * How many lines per second are required for the flood protection to start
     */
    private int mMaxLinesPerSecond;

    /**
     * By how many ms to increase the flood protection, up to mMaxDelay
     */
    private int mDelayIncreaseStep;

    /**
     * How many ms to wait before disabling flood protection
     */
    private int mCoolDownPeriod;

    public FloodProtectedOutputThread(OutputStream output, Callback callback, int minDelay, int maxDelay, int maxLinesPerSecond, int delayIncreaseStep, int coolDownPeriod) {
        super(output, callback);
        setName(FloodProtectedOutputThread.class.getSimpleName() + getId());
        mMinDelay = minDelay;
        mMaxDelay = maxDelay;
        mMaxLinesPerSecond = maxLinesPerSecond;
        mDelayIncreaseStep = delayIncreaseStep;
        mCoolDownPeriod = coolDownPeriod;

        mLastTimestamps = new long[mMaxLinesPerSecond + 1];
        mLastTimestamps[mMaxLinesPerSecond] = System.currentTimeMillis();

        mCurrentDelay = mMinDelay;
    }


    @Override
    protected void process(Writer writer) throws InterruptedException, IOException {
        String line;
        while (!isInterrupted() && (line = mWriteQueue.take()) != null) {
            System.arraycopy(mLastTimestamps, 1, mLastTimestamps, 0, mMaxLinesPerSecond);
            mLastTimestamps[mMaxLinesPerSecond] = System.currentTimeMillis();

            long firstLastDifference = mLastTimestamps[mMaxLinesPerSecond] - mLastTimestamps[0];
            if (firstLastDifference < 1000) {
                mCurrentDelay = Math.max(mMinDelay, Math.min(mMaxDelay, mCurrentDelay + mDelayIncreaseStep));
                mInDelay = true;
                mCoolDown = System.currentTimeMillis();
            } else if (mInDelay && System.currentTimeMillis() - mCoolDown > mCoolDownPeriod) {
                mInDelay = false;
            }
            if (mInDelay) {
                sleep(mCurrentDelay);
            }

            writer.write(line + "\r\n");
            writer.flush();
        }
    }
}
