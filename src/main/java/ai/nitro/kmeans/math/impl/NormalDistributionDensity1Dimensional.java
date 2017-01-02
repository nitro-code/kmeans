/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.math.impl;

import ai.nitro.kmeans.math.Density1Dimensional;
import ai.nitro.kmeans.math.utils.StringUtils;

public class NormalDistributionDensity1Dimensional implements Density1Dimensional {

	protected final double m;

	protected final double s;

	public NormalDistributionDensity1Dimensional(final double m, final double s) {
		this.m = m;
		this.s = s;
	}

	@Override
	public double f(final double x) {
		final double base = (x - m) / s;
		final double exponent = -0.5 * Math.pow(base, 2);
		return 1 / (s * Math.sqrt(2 * Math.PI)) * Math.pow(Math.E, exponent);
	}

	@Override
	public String toString() {
		return "N(m[" + StringUtils.format(m) + "],s[" + StringUtils.format(s)
				+ "])";
	}
}
