/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.math.impl;

import ai.nitro.kmeans.math.Density2Dimensional;
import ai.nitro.kmeans.math.utils.StringUtils;

public class MixedDensity2DimensionalImpl implements Density2Dimensional {

	protected double[] a;

	protected Density2Dimensional[] densities;

	public MixedDensity2DimensionalImpl(final Density2Dimensional[] densities) {
		a = new double[densities.length];

		for (int i = 0; i < a.length; i++) {
			a[i] = 1 / (double) densities.length;
		}

		this.densities = densities;
	}

	public MixedDensity2DimensionalImpl(final Density2Dimensional[] densities, final double[] a) throws Exception {
		this.densities = densities;
		setA(a);
	}

	@Override
	public double f(final double x, final double y) {
		double ret = 0;

		for (int i = 0; i < densities.length; i++) {
			final Density2Dimensional density = densities[i];
			ret = ret + a[i] * density.f(x, y);
		}

		return ret;
	}

	public double[] getA() {
		return a;
	}

	public Density2Dimensional[] getDensities() {
		return densities;
	}

	public Density2Dimensional getDensity(final int j) {
		return densities[j];
	}

	public int getId(final Density2Dimensional density) {
		int id = -1;

		for (int i = 0; i < densities.length; i++) {
			if (densities[i].equals(density)) {
				id = i;
			}
		}

		return id;
	}

	public int numberOfDensities() {
		return densities.length;
	}

	public void setA(final double[] a) throws Exception {
		double asum = 0;

		for (int i = 0; i < a.length; i++) {
			asum += a[i];
		}

		if (Math.abs(asum - 1) > 0.001) {
			throw new Exception("sum of a not 1");
		}

		if (a.length != densities.length) {
			throw new Exception("mismatch in length of arrays 'a' and 'densities'");
		}

		this.a = a;
	}

	@Override
	public String toString() {
		String retstr = "";

		for (int i = 0; i < densities.length; i++) {
			retstr = retstr + "a=" + StringUtils.format(a[i]) + "*";
			retstr = retstr + densities[i].toString() + "\n";
		}

		return retstr;
	}
}
