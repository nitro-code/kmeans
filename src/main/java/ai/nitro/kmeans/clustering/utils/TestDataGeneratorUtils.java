/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.clustering.utils;

import java.util.Random;

import ai.nitro.kmeans.application.Window;
import ai.nitro.kmeans.clustering.impl.ClusterObjectImpl;
import ai.nitro.kmeans.clustering.impl.CoordinateImpl;
import ai.nitro.kmeans.clustering.impl.PartitionImpl;
import ai.nitro.kmeans.math.Density1Dimensional;
import ai.nitro.kmeans.math.Density2Dimensional;
import ai.nitro.kmeans.math.impl.MixedDensity1DimensionalImpl;
import ai.nitro.kmeans.math.impl.MixedDensity2DimensionalImpl;
import ai.nitro.kmeans.math.impl.NormalDistributionDensity1Dimensional;
import ai.nitro.kmeans.math.impl.NormalDistributionDensity2Dimensional;
import ai.nitro.kmeans.math.utils.MonteCarloUtils;
import ai.nitro.kmeans.math.utils.StringUtils;

public class TestDataGeneratorUtils {

	protected static Random generator = new Random();

	protected static PartitionImpl getFunctionGraph(final Density1Dimensional mixedDensity, final int numberOfObjects,
			final int upperBound) throws Exception {
		final PartitionImpl partition = new PartitionImpl();

		final int granularity = 10;
		final int[] numberOfOccurrences = new int[upperBound * granularity];

		for (int i = 0; i < numberOfObjects; i++) {
			final Double randomVariable = MonteCarloUtils.generateRandomOneDimensionalValue(mixedDensity, 0,
					upperBound);
			final Double indexDouble = new Double(randomVariable * granularity);
			numberOfOccurrences[indexDouble.intValue()]++;
		}

		for (int i = 0; i < numberOfOccurrences.length; i++) {
			final double x = (double) i / granularity;
			final int y = numberOfOccurrences[i];
			partition.addObject(new ClusterObjectImpl(new CoordinateImpl(new double[] { x, y }), "object " + i + 1));
		}

		return partition;
	}

	public static PartitionImpl getRandomData_NormalDistributionMixed(final int numberOfObjects, final int k,
			final int upperBound, final Window w) throws Exception {

		final Density1Dimensional[] densities = new Density1Dimensional[k];

		for (int i = 0; i < k; i++) {
			final double m = generator.nextDouble() * upperBound;
			final double s = Math.max(0.5, generator.nextDouble() * 1.5);
			densities[i] = new NormalDistributionDensity1Dimensional(m, s);
		}

		final MixedDensity1DimensionalImpl mixedDensity = new MixedDensity1DimensionalImpl(densities);

		String retstr = "";

		for (int i = 0; i < densities.length; i++) {
			retstr = retstr + densities[i].toString() + "\n";
		}

		w.writeText(retstr);
		return getFunctionGraph(mixedDensity, numberOfObjects, upperBound);
	}

	public static PartitionImpl getRandomData_NormalDistributionMixed2Dim(final int numberOfObjects, final int k,
			final int padding, final int upperBound, final Window w) throws Exception {
		final PartitionImpl partition = new PartitionImpl();

		final Density2Dimensional[] densities = new Density2Dimensional[k];
		final double[] a = new double[k];

		for (int i = 0; i < k; i++) {
			final double mx = generator.nextDouble() * (upperBound - 2 * padding) + padding;
			final double my = generator.nextDouble() * (upperBound - 2 * padding) + padding;

			final double[][] s = new double[2][2];
			s[0][0] = Math.max(0.5, generator.nextDouble() * 1.5);
			s[1][1] = Math.max(0.5, generator.nextDouble() * 1.5);

			final double covar = generator.nextDouble() - 0.5;
			s[1][0] = covar;
			s[0][1] = covar;
			densities[i] = new NormalDistributionDensity2Dimensional(mx, my, s);
			a[i] = generator.nextDouble();
		}

		double asum = 0;

		for (int i = 0; i < a.length; i++) {
			asum += a[i];
		}

		for (int i = 0; i < a.length; i++) {
			a[i] = a[i] / asum;
		}

		asum = 0;

		for (int i = 0; i < a.length - 1; i++) {
			asum += a[i];
		}

		a[a.length - 1] = 1 - asum;
		final MixedDensity2DimensionalImpl mixedDensity = new MixedDensity2DimensionalImpl(densities, a);

		for (int i = 0; i < numberOfObjects; i++) {
			final double[] randomVariables = MonteCarloUtils.generateRandomTwoDimensionalValue(mixedDensity, 0,
					upperBound, 0, upperBound);
			partition.addObject(new ClusterObjectImpl(new CoordinateImpl(randomVariables), "object " + i + 1));
		}

		String retstr = "";

		for (int i = 0; i < densities.length; i++) {
			retstr = retstr + "a=" + StringUtils.format(a[i]) + "*";
			retstr = retstr + densities[i].toString() + "\n";
		}

		w.writeText(retstr);
		return partition;
	}

	public static PartitionImpl getRandomData_UniformDistribution(final int numberOfObjects, final int upperBound)
			throws Exception {
		final PartitionImpl partition = new PartitionImpl();

		for (int i = 0; i < numberOfObjects; i++) {
			final double coord1 = generator.nextDouble() * upperBound;
			final double coord2 = generator.nextDouble() * upperBound;
			partition.addObject(
					new ClusterObjectImpl(new CoordinateImpl(new double[] { coord1, coord2 }), "object " + i + 1));
		}

		return partition;
	}

	public static PartitionImpl getSimpleData() throws Exception {
		final PartitionImpl partition = new PartitionImpl();
		partition.addObject(new ClusterObjectImpl(new CoordinateImpl(new double[] { 2, 8 }), "object 1"));
		partition.addObject(new ClusterObjectImpl(new CoordinateImpl(new double[] { 1, 2 }), "object 2"));
		partition.addObject(new ClusterObjectImpl(new CoordinateImpl(new double[] { 1, 1 }), "object 3"));
		partition.addObject(new ClusterObjectImpl(new CoordinateImpl(new double[] { 10, 2 }), "object 4"));
		partition.addObject(new ClusterObjectImpl(new CoordinateImpl(new double[] { 13, 3 }), "object 5"));
		return partition;
	}
}
