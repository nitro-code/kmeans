/*
 * Copyright (C) 2017, nitro ventures GmbH
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.clustering.impl;

import java.util.ArrayList;

import ai.nitro.kmeans.clustering.Cluster;

public class ClusterImpl implements Cluster {

	protected final ArrayList<ClusterObjectImpl> assignedObjects = new ArrayList<ClusterObjectImpl>();

	protected CoordinateImpl center;

	public void add(final ClusterObjectImpl object) {
		assignedObjects.add(object);
	}

	@Override
	public ClusterObjectImpl getAssignedObject(final int nummer) {
		return assignedObjects.get(nummer);
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
	}
}
