package org.kos.bsfconsoleplugin;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * The overridden read method in this class will not throw "Broken pipe"
 * IOExceptions;  It will simply wait for new writers and data.
 * This is used by the JConsole internal read thread to allow writers
 * in different (and in particular ephemeral) threads to write to the pipe.
 * <p/>
 * It also checks a little more frequently than the original read().
 * <p/>
 * Warning: read() will not even error on a read to an explicitly closed
 * pipe (override closed to for that).
 */
public class BlockingPipedInputStream extends PipedInputStream {
	boolean closed;

	public BlockingPipedInputStream(final PipedOutputStream pout)
			throws IOException {
		super(pout);
	}

	@Override
	public synchronized int read() throws IOException {
		if (closed)
			throw new IOException("stream closed");

		while (super.in < 0) {	// While no data */
			notifyAll();	// Notify any writers to wake up
			try {
				wait(750);
			} catch (InterruptedException e) {
				throw new InterruptedIOException();
			}
		}
		// This is what the superclass does.
		final int ret = buffer[super.out++] /*& 0xFF*/;
		if (super.out >= buffer.length)
			super.out = 0;
		if (super.in == super.out)
			super.in = -1;  /* now empty */
		return ret;
	}

	@Override
	public void close() throws IOException {
		closed = true;
		super.close();
	}
}