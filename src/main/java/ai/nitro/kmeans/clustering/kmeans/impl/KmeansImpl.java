/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.clustering.kmeans.impl;

import java.util.ArrayList;

import ai.nitro.kmeans.application.Window;
import ai.nitro.kmeans.clustering.impl.ClusterObjectImpl;
import ai.nitro.kmeans.clustering.impl.ClusteringException;
import ai.nitro.kmeans.clustering.impl.CoordinateImpl;
import ai.nitro.kmeans.clustering.impl.PartitionImpl;

public class KmeansImpl extends Thread {

	protected static void assignObjectToCluster(final ClusterObjectImpl object, final ClusterImpl cluster) {
		final ClusterImpl currentCluster = (ClusterImpl) object.getCluster();

		if (currentCluster != null) {
			currentCluster.remove(object);
		}

		object.assignToCluster(cluster);
		cluster.add(object);
	}

	protected int k = 0;

	protected final int maxIterationen;

	protected final PartitionImpl partition;

	protected final ai.nitro.kmeans.application.Window w;

	public KmeansImpl(final int k, final PartitionImpl partition, final Window w, final int maxIterationen) {
		this.k = k;
		this.partition = partition;
		this.w = w;
		this.maxIterationen = maxIterationen;
	}

	protected void modifyClustersInPartition(final ArrayList<ClusterImpl> clusters, final PartitionImpl partition) {
		partition.clearClusters();

		for (int i = 0; i < clusters.size(); i++) {
			partition.addCluster(clusters.get(i));
		}
	}

	@Override
	public void run() {
		w.writeText("\n~~~~~~~~~~~~~~~");
		w.writeText("starting K-Means with max. # iterations " + maxIterationen);
		w.writeText("~~~~~~~~~~~~~~~");

		final ArrayList<ClusterImpl> clusters = new ArrayList<ClusterImpl>();
		partition.reset();

		/**
		 * initializing memory
		 */
		for (int i = 0; i < k; i++) {
			clusters.add(new ClusterImpl("cluster " + i));
		}

		/**
		 * step 1: initializing cluster centers
		 */
		w.writeText("step 1: defining initial cluster centers");
		double MIN = -1;
		double MAX = -1;

		/**
		 * calculate minimum and maximum tuple sums
		 */
		for (int i = 0; i < partition.getObjects().size(); i++) {
			final ClusterObjectImpl object = partition.getObjects().get(i);
			final double SUM = object.getCoordinate().getTupleSum();

			if (MIN == -1) {
				MIN = SUM;
			}

			if (MAX == -1) {
				MAX = SUM;
			}

			if (SUM < MIN) {
				MIN = SUM;
			}

			if (SUM > MAX) {
				MAX = SUM;
			}
		}

		/**
		 * initial cluster assignments
		 */
		double timeIterationsInit = 0;
		int nIterationsInit = 0;

		for (int i = 0; i < partition.getObjects().size(); i++) {
			final double timeIterationsInitA = System.currentTimeMillis();
			final ClusterObjectImpl object = partition.getObjects().get(i);
			final double SUM = object.getCoordinate().getTupleSum();
			final Double clusterNumberDouble = new Double(k * (SUM - MIN) / (MAX - MIN) + 1);
			final int clusterNumber = Math.min((int) Math.floor(clusterNumberDouble), k);

			KmeansImpl.assignObjectToCluster(object, clusters.get(clusterNumber - 1));

			try {
				clusters.get(clusterNumber - 1).updateCenterMeanValue();
			} catch (final Exception e) {
				w.writeText(e.getMessage());
			}

			nIterationsInit++;
			timeIterationsInit += System.currentTimeMillis() - timeIterationsInitA;
		}

		modifyClustersInPartition(clusters, partition);

		w.partitionUpdate(partition);
		w.writeText("initialization finished");
		w.writeText(
				"steps 2 and 3: object-wise analysis of better neighbor partitions and reassignment of objects to clusters");
		w.writeText("for starting steps 2 und 3 please click 'next' ...");
		w.pause();

		/**
		 * steps 2 and 3: optimization
		 */
		int nIterations = 0;
		double timeIterations = 0;
		int nIterationsSub = 0;
		double timeIterationsSub = 0;
		boolean modificationsDone = false;

		do {
			final double timeIterationenA = System.currentTimeMillis();
			w.writeText("starting iteration " + nIterations);
			modificationsDone = false;

			for (int i = 0; i < partition.getObjects().size(); i++) {
				final double timeIterationenSubA = System.currentTimeMillis();
				final ClusterObjectImpl object = partition.getObjects().get(i);

				if (w.pauseIsChecked()) {
					w.writeText("--------------------");
					w.writeText("object " + i);
					partition.setCurrentObject(object);
					w.partitionUpdate(partition);
					w.pause();
				}

				try {
					final int nji = object.getCluster().getNumberOfAssignedObjects();
					final double Dji = CoordinateImpl.getDistance(object.getCoordinate(), object.getCluster().getCenter());

					double minDeltaError = 0;
					ClusterImpl minDeltaErrorCluster = (ClusterImpl) object.getCluster();

					for (int j = 0; j < clusters.size(); j++) {
						if (!clusters.get(j).equals(object.getCluster())) {
							// for all clusters except the one of the object ->
							// neighbor partitions
							final ClusterImpl cluster = clusters.get(j);
							final int nj = cluster.getNumberOfAssignedObjects();
							final double Dj = CoordinateImpl.getDistance(object.getCoordinate(), cluster.getCenter());
							final double deltaError = nj * Dj / (nj + 1) - nji * Dji / (nj - 1);

							if (w.pauseIsChecked()) {
								w.writeText("cluster " + j + ": " + deltaError);
							}

							if (deltaError < minDeltaError) {
								minDeltaError = deltaError;
								minDeltaErrorCluster = cluster;
							}
						}
					}

					if (minDeltaError < 0 && !minDeltaErrorCluster.equals(object.getCluster())) {
						final ClusterImpl clusterOld = (ClusterImpl) object.getCluster();
						KmeansImpl.assignObjectToCluster(object, minDeltaErrorCluster);

						try {
							clusterOld.updateCenterMeanValue();
						} catch (final Exception e) {
							w.writeText(e.getMessage());
						}

						try {
							minDeltaErrorCluster.updateCenterMeanValue();
						} catch (final Exception e) {
							w.writeText(e.getMessage());
						}

						modificationsDone = true;

						if (w.pauseIsChecked()) {
							w.writeText("improvement: " + minDeltaError);
							modifyClustersInPartition(clusters, partition);
							w.partitionUpdate(partition);
							w.pause();
						}
					}
				} catch (final ClusteringException ce) {
					w.writeText(ce.getMessage());
				}

				nIterationsSub++;
				timeIterationsSub += System.currentTimeMillis() - timeIterationenSubA;
			}

			partition.setCurrentObject(null);
			nIterations++;
			timeIterations += System.currentTimeMillis() - timeIterationenA;

			w.partitionUpdate(partition);
		}

		/**
		 * step 4: stop rule
		 */
		while (modificationsDone && nIterations < maxIterationen);
		modifyClustersInPartition(clusters, partition);

		w.partitionUpdate(partition);

		w.writeText("---------------------------------------");
		w.writeText("nIterationsInit: " + nIterationsInit + " in " + timeIterationsInit / 1000 + " s");
		w.writeText("nIterations: " + nIterations + " in: " + timeIterations / 1000 + " s");
		w.writeText("nIterationsSub: " + nIterationsSub + " in " + timeIterationsSub / 1000 + " s");

		for (int i = 0; i < clusters.size(); i++) {
			final ClusterImpl cluster = clusters.get(i);
			w.writeText(cluster.toString() + ": " + cluster.getNumberOfAssignedObjects() + " objects");
		}
	}
}