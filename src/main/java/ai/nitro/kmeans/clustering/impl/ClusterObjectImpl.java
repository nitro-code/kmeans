/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.clustering.impl;

import ai.nitro.kmeans.clustering.Cluster;

public class ClusterObjectImpl {

	protected Cluster assignedCluster;

	protected final CoordinateImpl coordinate;

	protected final String name;

	public ClusterObjectImpl(final CoordinateImpl coordinate, final String name) {
		this.coordinate = coordinate;
		this.name = name;
	}

	public void assignToCluster(final Cluster cluster) {
		assignedCluster = cluster;
	}

	public Cluster getCluster() {
		return assignedCluster;
	}

	public CoordinateImpl getCoordinate() {
		return coordinate;
	}

	@Override
	public String toString() {
		String retstr = name + " (";

		for (int i = 0; i < coordinate.getTuple().length; i++) {
			retstr = retstr + coordinate.getTuple()[i];

			if (i < coordinate.getTuple().length - 1) {
				retstr = retstr + " ";
			}
		}

		return retstr + ")";
	}
}
