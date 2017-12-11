/*
 * Copyright (C) 2017, nitro ventures GmbH
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.clustering.kstarmeans.impl;

import java.util.ArrayList;

import ai.nitro.kmeans.clustering.Cluster;
import ai.nitro.kmeans.clustering.Partition;
import ai.nitro.kmeans.clustering.impl.ClusterObjectImpl;
import ai.nitro.kmeans.clustering.impl.ClusteringException;
import ai.nitro.kmeans.math.impl.MixedDensity2DimensionalImpl;
import ai.nitro.kmeans.math.utils.DistanceUtils;
import ai.nitro.kmeans.math.utils.MatrixUtils;
import ai.nitro.kmeans.math.utils.StringUtils;

public class PartitionImpl extends MixedDensity2DimensionalImpl implements Partition {

	protected int assignmentMode = 0;

	protected double[] b;

	protected ClusterObjectImpl currentObject;

	protected double learningRatioB = 0.001;

	protected ArrayList<ClusterObjectImpl> objects = new ArrayList<ClusterObjectImpl>();

	public PartitionImpl(final ClusterImpl[] densities, final ArrayList<ClusterObjectImpl> objects) {
		super(densities);

		for (int i = 0; i < densities.length; i++) {
			densities[i].setPartition(this);
		}

		this.objects = objects;
	}

	public PartitionImpl(final ClusterImpl[] densities, final ArrayList<ClusterObjectImpl> objects, final double[] a)
			throws Exception {
		super(densities, a);

		for (int i = 0; i < densities.length; i++) {
			densities[i].setPartition(this);
		}

		this.objects = objects;
	}

	public ClusterImpl getAssignedCluster(final double x, final double y) {
		if (assignmentMode == 1) {
			return u(x, y);
		} else {
			return I(x, y);
		}
	}

	public ClusterImpl getCluster(final int i) {
		return (ClusterImpl) densities[i];
	}

	@Override
	public ArrayList<Cluster> getClusters() {
		final ArrayList<Cluster> clusters = new ArrayList<Cluster>();

		for (int i = 0; i < densities.length; i++) {
			clusters.add((Cluster) densities[i]);
		}

		return clusters;
	}

	@Override
	public ClusterObjectImpl getCurrentObject() {
		return currentObject;
	}

	@Override
	public ArrayList<ClusterObjectImpl> getObjects() {
		return objects;
	}

	protected ClusterImpl I(final double x, final double y) {
		double maxValue = 0;
		ClusterImpl cluster = (ClusterImpl) densities[0];

		for (int i = 0; i < densities.length; i++) {
			final double value = densities[i].f(x, y);

			if (value > maxValue) {
				maxValue = value;
				cluster = (ClusterImpl) densities[i];
			}
		}

		return cluster;
	}

	public void learnWeights(final ClusterImpl cluster) throws ClusteringException {
		int winner = 0;

		for (int i = 0; i < densities.length; i++) {
			if (densities[i].equals(cluster)) {
				winner = i;
			}
		}

		final double bold = b[winner];
		final double aold = a[winner];
		final double newb = bold + learningRatioB * (1 - aold);

		b[winner] = newb;

		if (newb > 1000) {
			try {
				b = MatrixUtils.mult(b, 0.5);
			} catch (final Exception e) {
				throw new ClusteringException(e.getMessage());
			}
		}

		double bsum = 0;
		final double[] bs2 = b;

		for (int i = 0; i < bs2.length; i++) {
			bsum += Math.pow(Math.E, bs2[i]);
		}

		final double[] newas = a;

		for (int i = 0; i < newas.length; i++) {
			newas[i] = Math.pow(Math.E, b[i]) / bsum;
		}

		double newassum = 0;

		for (int i = 0; i < newas.length - 1; i++) {
			newassum += newas[i];
		}

		newas[newas.length - 1] = 1 - newassum;

		try {
			this.setA(newas);
		} catch (final Exception e) {
			throw new ClusteringException(e.getMessage());
		}
	}

	public double p(final ClusterImpl cluster, final double x, final double y) {
		return cluster.f(x, y) / this.f(x, y);
	}

	@Override
	public void reset() {
		currentObject = null;
		a = null;
		b = null;
		densities = null;
		assignmentMode = 0;
	}

	public void resetb() {
		b = new double[densities.length];

		for (int i = 0; i < densities.length; i++) {
			b[i] = 1;
		}
	}

	@Override
	public void setCurrentObject(final ClusterObjectImpl object) {
		currentObject = object;
	}

	@Override
	public String toString() {
		String retstr = "";

		for (int i = 0; i < densities.length; i++) {
			retstr = retstr + "a=" + StringUtils.format(a[i]) + "*" + densities[i].toString() + "\n";
		}

		return retstr;
	}

	protected ClusterImpl u(final double x, final double y) {
		ClusterImpl winner = (ClusterImpl) densities[0];
		double smallestU = -1;

		double nSum = 0;

		for (int i = 0; i < densities.length; i++) {
			final ClusterImpl cluster = (ClusterImpl) densities[i];
			nSum += cluster.n;
		}

		for (int i = 0; i < densities.length; i++) {
			final ClusterImpl cluster = (ClusterImpl) densities[i];
			final double u = cluster.n / nSum * DistanceUtils
					.euclideanSquared(new double[] { cluster.getMx(), cluster.getMy() }, new double[] { x, y });

			if (smallestU == -1 || u < smallestU) {
				smallestU = u;
				winner = cluster;
			}
		}

		return winner;
	}
}
