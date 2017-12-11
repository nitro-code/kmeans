/*
 * Copyright (C) 2017, nitro ventures GmbH
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.math.utils;

public class DistanceUtils {

	public static double euclideanSquared(final double[] x, final double[] y) {
		double distance = 0;

		for (int i = 0; i < x.length; i++) {
			distance += Math.pow(x[i] - y[i], 2);
		}

		return distance;
	}
}
