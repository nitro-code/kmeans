/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.math.impl;

import ai.nitro.kmeans.math.Density1Dimensional;

public class MixedDensity1DimensionalImpl implements Density1Dimensional {

	protected final Density1Dimensional[] densities;

	public MixedDensity1DimensionalImpl(final Density1Dimensional[] densities) {
		this.densities = densities;
	}

	@Override
	public double f(final double x) {
		double ret = 0;

		for (int i = 0; i < densities.length; i++) {
			final Density1Dimensional density = densities[i];
			ret = ret + density.f(x);
		}

		return ret / densities.length;
	}
}
