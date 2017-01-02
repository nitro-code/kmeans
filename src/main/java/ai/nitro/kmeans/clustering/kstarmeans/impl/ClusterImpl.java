/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.clustering.kstarmeans.impl;

import java.util.ArrayList;

import ai.nitro.kmeans.clustering.Cluster;
import ai.nitro.kmeans.clustering.impl.ClusterObjectImpl;
import ai.nitro.kmeans.clustering.impl.ClusteringException;
import ai.nitro.kmeans.clustering.impl.CoordinateImpl;
import ai.nitro.kmeans.math.impl.NormalDistributionDensity2Dimensional;
import ai.nitro.kmeans.math.utils.MatrixUtils;

public class ClusterImpl extends NormalDistributionDensity2Dimensional implements Cluster {

	protected double[] b;

	protected double learningRatioM = 0.001;

	protected double learningRatioS = 0.0001;

	protected double n = 1;

	protected PartitionImpl partition;

	public ClusterImpl(final double mx, final double my, final double[][] s) {
		super(mx, my, s);
	}

	@Override
	public ClusterObjectImpl getAssignedObject(final int i) {
		return this.getObjects().get(i);
	}

	@Override
	public CoordinateImpl getCenter() throws ClusteringException {
		return new CoordinateImpl(this.getm());
	}

	@Override
	public int getNumberOfAssignedObjects() {
		return this.getObjects().size();
	}

	@Override
	public ArrayList<ClusterObjectImpl> getObjects() {
		final ArrayList<ClusterObjectImpl> objects = partition.getObjects();
		final ArrayList<ClusterObjectImpl> myObjects = new ArrayList<ClusterObjectImpl>();

		for (int i = 0; i < objects.size(); i++) {
			final ClusterObjectImpl object = objects.get(i);
			final double x = object.getCoordinate().getTuple()[0];
			final double y = object.getCoordinate().getTuple()[1];
			final ClusterImpl cluster = partition.getAssignedCluster(x, y);

			if (cluster.equals(this)) {
				myObjects.add(object);
			}
		}

		return myObjects;
	}

	public void learnCenterWithCovariance(final double[] x) {
		final double[] m = this.getm();
		final double[][] s = this.gets();

		final double[] diff = MatrixUtils.mult(
				MatrixUtils.make1xn(
						MatrixUtils.mult(MatrixUtils.inverse2x2(s), MatrixUtils.makenxn(MatrixUtils.sub(x, m)))),
				learningRatioM);
		final double[] mnew = MatrixUtils.add(m, diff);
		this.setm(mnew);
	}

	public void learnCenterWithoutCovariance(final double[] x) {
		final double[] m = this.getm();
		final double[] diff = MatrixUtils.mult(MatrixUtils.sub(x, m), learningRatioM);
		final double[] mnew = MatrixUtils.add(m, diff);
		this.setm(mnew);
	}

	public void learnCovariance(final double[] x) {
		final double[] zt = MatrixUtils.sub(x, this.getm());
		final double[] zt2 = MatrixUtils.mult(zt, learningRatioS);
		final double[][] difference = MatrixUtils.multCartesianProduct(zt2, zt);
		final double[][] snew = MatrixUtils.add(MatrixUtils.mult(s, 1 - learningRatioS), difference);
		s = snew;
	}

	public void setPartition(final PartitionImpl partition) {
		this.partition = partition;
	}
}
