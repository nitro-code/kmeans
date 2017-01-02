/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.math.utils;

import java.util.Random;

import ai.nitro.kmeans.math.Density1Dimensional;
import ai.nitro.kmeans.math.Density2Dimensional;

public class MonteCarloUtils {

	protected static Random generator = new Random();

	public static double generateRandomOneDimensionalValue(final Density1Dimensional density, final double lowerBound,
			final double upperBound) {
		double randomVariable = 0;
		double uniformDistributetVariable = 0;
		double probability = 0;

		do {
			randomVariable = lowerBound + (upperBound - lowerBound) * generator.nextDouble();
			probability = density.f(randomVariable);
			uniformDistributetVariable = generator.nextDouble();
		} while (uniformDistributetVariable > probability);

		return randomVariable;
	}

	public static double[] generateRandomTwoDimensionalValue(final Density2Dimensional density,
			final double lowerBoundX, final double upperBoundX, final double lowerBoundY, final double upperBoundY) {
		double randomVariableX = 0;
		double randomVariableY = 0;
		double uniformDistributetVariable = 0;
		double probability = 0;

		do {
			randomVariableX = lowerBoundX + (upperBoundX - lowerBoundX) * generator.nextDouble();
			randomVariableY = lowerBoundY + (upperBoundY - lowerBoundY) * generator.nextDouble();
			probability = density.f(randomVariableX, randomVariableY);
			uniformDistributetVariable = generator.nextDouble();
		} while (uniformDistributetVariable > probability);

		return new double[] { randomVariableX, randomVariableY };
	}
}
