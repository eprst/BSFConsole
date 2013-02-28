package org.kos.bsfconsoleplugin;


import org.jetbrains.annotations.Nullable;


/**
 * Triple of objects.
 */
public class Triple<A,B,C> {
	private final A fst;
	private final B snd;
	private final C trd;

	public Triple(@Nullable final A fst, @Nullable final B snd, @Nullable final C trd) {
		this.fst = fst;
		this.snd = snd;
		this.trd = trd;
	}

	@Nullable
	public A getFst() {
		return fst;
	}

	@Nullable
	public B getSnd() {
		return snd;
	}

	@Nullable
	public C getTrd() {
		return trd;
	}
}
