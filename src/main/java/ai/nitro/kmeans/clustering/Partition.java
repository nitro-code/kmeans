/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.clustering;

import java.util.ArrayList;

import ai.nitro.kmeans.clustering.impl.ClusterObjectImpl;

public interface Partition {

	public ArrayList<Cluster> getClusters();

	public ClusterObjectImpl getCurrentObject();

	public ArrayList<ClusterObjectImpl> getObjects();

	public void reset();

	public void setCurrentObject(ClusterObjectImpl object);

}
