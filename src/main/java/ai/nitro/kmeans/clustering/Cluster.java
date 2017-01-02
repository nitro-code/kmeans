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
import ai.nitro.kmeans.clustering.impl.ClusteringException;
import ai.nitro.kmeans.clustering.impl.CoordinateImpl;

public interface Cluster {

	public ClusterObjectImpl getAssignedObject(int j);

	public CoordinateImpl getCenter() throws ClusteringException;

	public int getNumberOfAssignedObjects();

	public ArrayList<ClusterObjectImpl> getObjects();

}
