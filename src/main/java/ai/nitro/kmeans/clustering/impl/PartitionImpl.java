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
import ai.nitro.kmeans.clustering.Partition;

public class PartitionImpl implements Partition {

	protected final ArrayList<Cluster> clusters = new ArrayList<Cluster>();

	protected ClusterObjectImpl currentObject;

	protected final ArrayList<ClusterObjectImpl> objects = new ArrayList<ClusterObjectImpl>();

	public void addCluster(final Cluster cluster) {
		clusters.add(cluster);
	}

	public void addObject(final ClusterObjectImpl object) {
		objects.add(object);
	}

	public void clearClusters() {
		clusters.clear();
	}

	@Override
	public ArrayList<Cluster> getClusters() {
		if (clusters.size() > 0) {
			return clusters;
		} else {
			final ArrayList<Cluster> clusters = new ArrayList<Cluster>();
			final ClusterImpl cluster = new ClusterImpl();

			for (int i = 0; i < objects.size(); i++) {
				cluster.add(objects.get(i));
			}

			clusters.add(cluster);
			return clusters;
		}
	}

	@Override
	public ClusterObjectImpl getCurrentObject() {
		return currentObject;
	}

	@Override
	public ArrayList<ClusterObjectImpl> getObjects() {
		return objects;
	}

	@Override
	public void reset() {
		clusters.clear();

		for (int i = 0; i < objects.size(); i++) {
			objects.get(i).assignToCluster(null);
		}

		currentObject = null;
	}

	@Override
	public void setCurrentObject(final ClusterObjectImpl object) {
		currentObject = object;
	}
}
