/*
 * Copyright (C) 2017, nitro ventures GmbH
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
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
