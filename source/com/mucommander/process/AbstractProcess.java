/*
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (c) 2002-2007 Maxence Bernard
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with muCommander; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.mucommander.process;


import com.mucommander.Debug;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * muCommander specific version of a process.
 * <p>
 * Implementations of this class are used to execute various types of processes.
 * It can be a {@link com.mucommander.file.impl.local.LocalProcess}, but some types of
 * {@link com.mucommander.file.AbstractFile abstract files}, such as SFTP files,
 * allow for commands to be executed.
 * </p>
 * <p>
 * Unlike normal instances of <code>java.lang.Process</code>, abstract processes
 * will empty their own streams, preventing deadlocks from occuring on some systems.
 * </p>
 * <p>
 * Note that abstract processes should not be created directly. They should be
 * instanciated through {@link com.mucommander.process.ProcessRunner#execute(String[],com.mucommander.file.AbstractFile,ProcessListener)}.
 * </p>
 * @author Nicolas Rinaudo
 */
public abstract class AbstractProcess {
    // - Instance fields -------------------------------------------------------
    // -------------------------------------------------------------------------
    /** Stdout monitor. */
    private ProcessOutputMonitor stdoutMonitor;
    /** Stderr monitor. */
    private ProcessOutputMonitor stderrMonitor;



    // - Process monitoring ----------------------------------------------------
    // -------------------------------------------------------------------------
    /**
     * Kills the process.
     */
    public final void destroy() {
        // Process destruction occurs in a separate thread, as in some (rare)
        // cases, deadlocks will occur while trying to kill a native process.
        // An example of that is executing <code>echo blah | ssh localhost ls -l</code>
        // under MAC OS X.
        // Using a separate thread allows muCommander to continue working properly even
        // when that occurs.
        new Thread() {
            public void run() {
                // Closes the process' streams.
		if(Debug.ON) Debug.trace("Destroying process...");
		stdoutMonitor.stopMonitoring();
		if(stderrMonitor != null)
		    stderrMonitor.stopMonitoring();

                // Destroys the process.
                try {destroyProcess();}
                catch(IOException e) {if(Debug.ON) Debug.trace(e);}
            }
        }.start();
    }

    /**
     * Tries to open a reader on the specified stream, using the specified encoding.
     * <p>
     * If the specified encoding cannot be found, or is set to <code>null</code>, this method
     * will use the default system encoding. In theory, this should be the <code>file.encoding</code>
     * system property, but this is not stated clearly on the documentation and might change depending
     * on the VM implementation.
     * </p>
     * @param           in InputStream on which to open the reader.
     * @param  encoding encoding to use on the stream.
     * @return          a reader on the specified stream using the specified encoding.
     */
    private static Reader getStreamReader(InputStream in, String encoding) {
        if(encoding != null) {
            try {return new InputStreamReader(in, encoding);}
            catch(Exception e) {e.printStackTrace();}
        }
        return new InputStreamReader(in);
    }

    /**
     * Starts monitoring the process.
     * @param listener if non <code>null</code>, <code>listener</code> will receive updates about the process' event.
     * @param encoding encoding that should be used by the process' stdout and stderr streams.
     */
    final void startMonitoring(ProcessListener listener, String encoding) throws IOException {
        // Only monitors stdout if the process uses merged streams.
        if(usesMergedStreams()) {
            if(Debug.ON) Debug.trace("Starting process merged output monitor...");
            new Thread(stdoutMonitor = new ProcessOutputMonitor(getStreamReader(getInputStream(), encoding), listener, this), "Process sdtout/stderr monitor").start();
        }
        // Monitors both stdout and stderr.
        else {
            if(Debug.ON) Debug.trace("Starting process stdout and stderr monitors...");
            new Thread(stdoutMonitor = new ProcessOutputMonitor(getStreamReader(getInputStream(), encoding), listener, this), "Process stdout monitor").start();
            new Thread(stderrMonitor = new ProcessOutputMonitor(getStreamReader(getErrorStream(), encoding), listener), "Process stderr monitor").start();
        }
    }



    // - Abstract methods ------------------------------------------------------
    // -------------------------------------------------------------------------
    /**
     * Returns <code>true</code> if this process only uses one output stream.
     * <p>
     * Some processes will use a single stream for their standard error and standard output streams. Such
     * processes should return <code>true</code> here to prevent both streams from being monitored.<br/>
     * Note that if a process uses merged streams, {@link #getInputStream()} will be monitored.
     * </p>
     * @return <code>true</code> if this process merges his output streams, <code>false</code> otherwise.
     */
    public abstract boolean usesMergedStreams();

    /**
     * Makes the current thread wait for the process to die.
     * @return the process' exit code.
     * @throws InterruptedException thrown if the current thread is interrupted while wainting on the process to die.
     * @throws IOException          thrown if an error occurs while waiting for the process to die.
     */
    public abstract int waitFor() throws InterruptedException, IOException;

    /**
     * Destroys the process.
     * @throws IOException thrown if an error occurs while destroying the process.
     */
    protected abstract void destroyProcess() throws IOException;

    /**
     * Returns this process' exit value.
     * @return this process' exit value.
     */
    public abstract int exitValue();

    /**
     * Returns the stream used to send data to the process.
     * @return             the stream used to send data to the process.
     * @throws IOException thrown if an error occurs while retrieving the process' output stream.
     */
    public abstract OutputStream getOutputStream() throws IOException;

    /**
     * Returns the process' standard output stream.
     * @return             the process' standard output stream.
     * @throws IOException thrown if an error occurs while retrieving the process' input stream.
     */
    public abstract InputStream getInputStream() throws IOException;

    /**
     * Returns the process' standard error stream.
     * @return             the process' standard error stream.
     * @throws IOException thrown if an error occurs while retrieving the process' error stream.
     */
    public abstract InputStream getErrorStream() throws IOException;

}
