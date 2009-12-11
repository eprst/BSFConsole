package org.kos.bsfconsoleplugin;

/**
 * Triple of objects.
 */
public class Triple<A,B,C> {
	private final A fst;
	private final B snd;
	private final C trd;

	public Triple(final A fst, final B snd, final C trd) {
		this.fst = fst;
		this.snd = snd;
		this.trd = trd;
	}

	public A getFst() {
		return fst;
	}

	public B getSnd() {
		return snd;
	}

	public C getTrd() {
		return trd;
	}
}
