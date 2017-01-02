/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.math.utils;

public class MatrixUtils {

	public static double[] add(final double[] m1, final double[] m2) {
		final double[] r = new double[m1.length];

		for (int i = 0; i < m1.length; i++) {
			r[i] = m1[i] + m2[i];
		}

		return r;
	}

	public static double[][] add(final double[][] m1, final double[][] m2) {
		final double[][] r = new double[m1.length][m1[0].length];

		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m1[0].length; j++) {
				r[i][j] = m1[i][j] + m2[i][j];
			}
		}

		return r;
	}

	public static double[][] inverse2x2(final double[][] m) {
		final double a = 1 / (m[0][0] * m[1][1] - m[0][1] * m[1][0]);
		return new double[][] { { a * m[1][1], -a * m[0][1] }, { -a * m[1][0], a * m[0][0] } };
	}

	public static double[] make1xn(final double[][] x) {
		final double[] r = new double[x.length];

		for (int i = 0; i < x.length; i++) {
			r[i] = x[i][0];
		}

		return r;
	}

	public static double[][] makenxn(final double[] x) {
		final double[][] r = new double[x.length][x.length];

		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x.length; j++) {
				if (j == 0) {
					r[i][j] = x[i];
				} else {
					r[i][j] = 0;
				}
			}
		}

		return r;
	}

	public static double[] mult(final double[] m1, final double f) {
		final double[] r = new double[m1.length];

		for (int i = 0; i < m1.length; i++) {
			r[i] = m1[i] * f;
		}

		return r;
	}

	public static double[][] mult(final double[][] m1, final double f) {
		final double[][] r = new double[m1.length][m1[0].length];

		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m1[0].length; j++) {
				r[i][j] = m1[i][j] * f;
			}
		}

		return r;
	}

	public static double[][] mult(final double[][] m1, final double[][] m2) {
		final double[][] result = new double[m1.length][m2[0].length];

		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m2[0].length; j++) {
				double value = 0;

				for (int k = 0; k < m1[0].length; k++) {
					final double x = m1[i][k];
					final double y = m2[k][j];
					value = value + x * y;
				}

				result[i][j] = value;
			}
		}

		return result;
	}

	public static double[][] multCartesianProduct(final double[] m1, final double[] m2) {
		final double[][] result = new double[m1.length][m2.length];

		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m2.length; j++) {
				double value = 0;
				final double x = m1[i];
				final double y = m2[j];
				value = value + x * y;
				result[i][j] = value;
			}
		}

		return result;
	}

	public static double[] sub(final double[] m1, final double[] m2) {
		return add(m1, mult(m2, -1));
	}

	public static double[][] sub(final double[][] m1, final double[][] m2) {
		return add(m1, mult(m2, -1));
	}

	public static String toString(final double[][] m) {
		String retstr = "";
		boolean started = false;

		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				final Double v = new Double(m[i][j]);

				if (started) {
					retstr = retstr + ",";
				}

				retstr = retstr + StringUtils.format(v);
				started = true;
			}
		}

		return retstr;
	}

	public static double[][] transpose(final double[] m) {
		final double[][] ret = new double[1][m.length];

		for (int i = 0; i < m.length; i++) {
			ret[0][i] = m[i];
		}

		return ret;
	}
}
