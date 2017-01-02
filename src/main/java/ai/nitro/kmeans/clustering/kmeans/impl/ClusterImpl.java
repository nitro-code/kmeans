/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.clustering.kmeans.impl;

import java.util.ArrayList;

import ai.nitro.kmeans.clustering.Cluster;
import ai.nitro.kmeans.clustering.impl.ClusterObjectImpl;
import ai.nitro.kmeans.clustering.impl.ClusteringException;
import ai.nitro.kmeans.clustering.impl.CoordinateImpl;

public class ClusterImpl implements Cluster {

	public static double learningRatio = 0.1;

	protected final ArrayList<ClusterObjectImpl> assignedObjects = new ArrayList<ClusterObjectImpl>();

	protected CoordinateImpl center;

	protected ClusterObjectImpl lastAssignedObject;

	protected ClusterObjectImpl lastRemovedObject;

	protected final String name;

	public ClusterImpl(final String name) {
		this.name = name;
	}

	public ClusterImpl(final String name, final CoordinateImpl center) {
		this.name = name;
		this.center = center;
	}

	public void add(final ClusterObjectImpl object) {
		assignedObjects.add(object);
		lastAssignedObject = object;
	}

	@Override
	public ClusterObjectImpl getAssignedObject(final int number) {
		return assignedObjects.get(number);
	}

	@Override
	public CoordinateImpl getCenter() throws ClusteringException {
		if (center == null) {
			throw new ClusteringException("no center defined");
		}

		return center;
	}

	@Override
	public int getNumberOfAssignedObjects() {
		return assignedObjects.size();
	}

	@Override
	public ArrayList<ClusterObjectImpl> getObjects() {
		return assignedObjects;
	}

	public void remove(final ClusterObjectImpl object) {
		assignedObjects.remove(object);
		lastRemovedObject = object;
	}

	@Override
	public String toString() {
		final double[] tuple = center.getTuple();
		String retstr = name + " (";

		for (int i = 0; i < CoordinateImpl.o; i++) {
			retstr = retstr + tuple[i];
			if (i < tuple.length - 1) {
				retstr = retstr + " ";
			}
		}

		return retstr + ")";
	}

	public void updateCenterIterativ() {
		try {
			final CoordinateImpl difference = CoordinateImpl.subtractCoordinates(lastAssignedObject.getCoordinate(), center);
			final CoordinateImpl difference2 = CoordinateImpl.multiplyCoordinate(difference, learningRatio);
			center = CoordinateImpl.sumCoordinates(center, difference2);
		} catch (final Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public CoordinateImpl updateCenterMeanValue() throws Exception {
		final double[] totalSums = new double[CoordinateImpl.o];

		if (assignedObjects.size() == 0) {
			throw new Exception("no objects contained in Cluster");
		}

		for (int i = 0; i < assignedObjects.size(); i++) {
			final ClusterObjectImpl object = assignedObjects.get(i);
			final double[] tuple = object.getCoordinate().getTuple();

			for (int j = 0; j < CoordinateImpl.o; j++) {
				totalSums[j] += tuple[j];
			}
		}

		for (int j = 0; j < CoordinateImpl.o; j++) {
			totalSums[j] = totalSums[j] / assignedObjects.size();
		}

		center = new CoordinateImpl(totalSums);
		return center;
	}
}