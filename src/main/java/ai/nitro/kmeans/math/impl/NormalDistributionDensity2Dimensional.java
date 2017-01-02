/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.math.impl;

import ai.nitro.kmeans.math.Density2Dimensional;
import ai.nitro.kmeans.math.utils.MatrixUtils;
import ai.nitro.kmeans.math.utils.StringUtils;

public class NormalDistributionDensity2Dimensional implements Density2Dimensional {

	protected double mx;

	protected double my;

	protected double[][] s;

	public NormalDistributionDensity2Dimensional(final double mx, final double my, final double[][] s) {
		this.mx = mx;
		this.my = my;
		this.s = s;
	}

	@Override
	public double f(final double x, final double y) {
		final double sx = s[0][0];
		final double sy = s[1][1];
		final double r = s[1][0] / (Math.sqrt(sx) * Math.sqrt(sy));
		final double base = 1 / (2 * Math.PI * sx * sy * Math.sqrt(1 - Math.pow(r, 2)));
		final double exponentPart1 = -0.5 / (2 * (1 - Math.pow(r, 2)));
		final double exponentPart2 = Math.pow((x - mx) / sx, 2) - 2 * r * ((x - mx) / sx) * ((y - my) / sy)
				+ Math.pow((y - my) / sy, 2);

		return base * Math.pow(Math.E, exponentPart1 * exponentPart2);
	}

	public double[] getm() {
		return new double[] { mx, my };
	}

	public double getMx() {
		return mx;
	}

	public double getMy() {
		return my;
	}

	public double[][] gets() {
		return s;
	}

	public void setm(final double[] m) {
		mx = m[0];
		my = m[1];
	}

	public void setmx(final double mx) {
		this.mx = mx;
	}

	public void setmy(final double my) {
		this.my = my;
	}

	public void sets(final double[][] s) {
		this.s = s;
	}

	@Override
	public String toString() {
		return "N(m[" + StringUtils.format(mx) + "," + StringUtils.format(my)
				+ "],s[" + MatrixUtils.toString(s) + "])";
	}
}