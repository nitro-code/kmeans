/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.clustering.kstarmeans.impl;

import java.util.ArrayList;
import java.util.Random;

import ai.nitro.kmeans.application.Window;
import ai.nitro.kmeans.clustering.Partition;
import ai.nitro.kmeans.clustering.impl.ClusterObjectImpl;
import ai.nitro.kmeans.clustering.impl.ClusteringException;
import ai.nitro.kmeans.clustering.impl.CoordinateImpl;

public class KStarMeansImpl extends Thread {

	protected final Random generator = new Random();

	protected int k = 0;

	protected final int maxIterations;

	protected final ArrayList<ClusterObjectImpl> objects;

	protected final Window w;

	public KStarMeansImpl(final int k, final Partition partition, final Window w, final int maxIterations) {
		this.k = k;
		objects = partition.getObjects();
		this.w = w;
		this.maxIterations = maxIterations;
	}

	@Override
	public void run() {
		w.writeText("\n~~~~~~~~~~~~~~~");
		w.writeText("starting K*-Means with max. # iterations " + maxIterations);
		w.writeText("~~~~~~~~~~~~~~~");

		/*
		 * initializing cluster centers
		 */
		w.writeText("generating randomized initial cluster centers");
		double maxX = 0;
		double maxY = 0;

		// define value range for randomizer
		for (int i = 0; i < objects.size(); i++) {
			final ClusterObjectImpl object = objects.get(i);
			final double x = object.getCoordinate().getTuple()[0];
			final double y = object.getCoordinate().getTuple()[1];

			if (x > maxX) {
				maxX = x;
			}
			if (y > maxY) {
				maxY = y;
			}
		}

		ClusterImpl[] densities = new ClusterImpl[k];

		// generating randomized centers
		for (int i = 0; i < k; i++) { // for all clusters
			try {
				final double mx = generator.nextDouble() * maxX;
				final double my = generator.nextDouble() * maxY;
				final CoordinateImpl coordinate = new CoordinateImpl(new double[] { mx, my });
				final double[][] s = new double[][] { { 1, 0 }, { 0, 1 } };
				densities[i] = new ClusterImpl(mx, my, s);
			} catch (final Exception e) {
				w.writeText(e.getMessage());
			}
		}

		final PartitionImpl partition = new PartitionImpl(densities, objects);
		densities = null;

		w.writeText("cluster centers generated");

		/**
		 * step 1: initial cluster assignment
		 */
		w.writeText(
				"step 1: initializing: Assigning objects to clusters using u_j. After a new assignment the center of the new cluster is adapted");
		w.writeText("for starting step 1 please click 'next' ...");
		w.partitionUpdate(partition);
		w.pause();

		boolean changesMade = false;
		int nIterationsInit = 0;
		double timeIterationsInit = 0;
		int nIterationsInitSub = 0;
		double timeIterationsInitSub = 0;

		do {
			final double timeIterationsInitA = System.currentTimeMillis();
			final ArrayList<ClusterObjectImpl> objects2 = (ArrayList<ClusterObjectImpl>) partition.getObjects().clone();
			while (objects2.size() > 0) {
				final double timeIterationsInitSubA = System.currentTimeMillis();
				final ClusterObjectImpl object = objects2.get(generator.nextInt(objects2.size()));

				if (w.pauseIsChecked()) {
					w.writeText("--------------------");
					w.writeText("object chosen");
					partition.setCurrentObject(object);
					w.partitionUpdate(partition);
					w.pause();
				}

				final double[] x = object.getCoordinate().getTuple();
				partition.assignmentMode = 1; // switch to u_j
				final ClusterImpl winner = partition.getAssignedCluster(x[0], x[1]);

				if (object.getCluster() == null || !object.getCluster().equals(winner)) {
					changesMade = true;
					object.assignToCluster(winner);
					winner.n++;
					winner.learnCenterWithoutCovariance(x);

					if (w.pauseIsChecked()) {
						w.writeText("object has been assigned to a different cluster -> adapt winning cluster");
						w.partitionUpdate(partition);
						w.pause();
					}
				} else if (w.pauseIsChecked()) {
					w.writeText("object is not reassigned");
					w.partitionUpdate(partition);
					w.pause();
				}

				objects2.remove(object);
				nIterationsInitSub++;
				timeIterationsInitSub += System.currentTimeMillis() - timeIterationsInitSubA;
			}

			w.writeText("iteration " + nIterationsInit + " completed");
			nIterationsInit++;
			timeIterationsInit += System.currentTimeMillis() - timeIterationsInitA;
			w.partitionUpdate(partition);
		} while (changesMade && nIterationsInit < maxIterations);

		for (int i = 0; i < partition.getObjects().size(); i++) {
			partition.getObjects().get(i).assignToCluster(null);
		}

		partition.setCurrentObject(null);
		w.partitionUpdate(partition);
		w.writeText("initialization completed");

		/**
		 * step 2: learning
		 */
		w.writeText(
				"step 2: optimizing the mixed distribution by adapting densities, by adapting the centers m, the weights a and the covariance s");
		w.writeText("for starting step 2 please click 'next' ...");
		w.pause();

		int nIterations = 0;
		double timeIterations = 0;
		int nIterationsSub = 0;
		double timeIterationsSub = 0;
		changesMade = false;
		partition.resetb();

		do {
			final double timeIterationsA = System.currentTimeMillis();
			final ArrayList<ClusterObjectImpl> objects2 = (ArrayList<ClusterObjectImpl>) partition.getObjects().clone();

			while (objects2.size() > 0) {
				final ClusterObjectImpl object = objects2.get(generator.nextInt(objects2.size()));

				if (w.pauseIsChecked()) {
					w.writeText("--------------------");
					w.writeText("object chosen");
					partition.setCurrentObject(object);
					w.partitionUpdate(partition);
					w.pause();
				}

				final double[] x = object.getCoordinate().getTuple();

				partition.assignmentMode = 0; // switch to I(j)
				final ClusterImpl winner = partition.getAssignedCluster(x[0], x[1]);

				if (object.getCluster() == null || !object.getCluster().equals(winner)) {

					final double timeIterationsSubA = System.currentTimeMillis();
					changesMade = true;
					object.assignToCluster(winner);

					// create center
					winner.learnCenterWithCovariance(x);

					// learn weights
					try {
						partition.learnWeights(winner);
					} catch (final ClusteringException ce) {
						w.writeText(ce.getMessage());
					}

					// learn covariance
					winner.learnCovariance(x);

					nIterationsSub++;
					timeIterationsSub += System.currentTimeMillis() - timeIterationsSubA;

					if (w.pauseIsChecked()) {
						w.writeText("object is assigned to other cluster -> adapt new cluster");
						w.partitionUpdate(partition);
						w.pause();
					}
				} else if (w.pauseIsChecked()) {
					w.writeText("object is not reassigned");
					w.partitionUpdate(partition);
					w.pause();
				}

				objects2.remove(object);
			}

			w.writeText("iteration " + nIterations + " completed");
			nIterations++;
			timeIterations += System.currentTimeMillis() - timeIterationsA;
			w.partitionUpdate(partition);
		} while (changesMade && nIterations < maxIterations);

		partition.setCurrentObject(null);

		w.writeText("---------------------------------------");
		w.writeText(
				"#iterations of total initialization: " + nIterationsInit + " in: " + timeIterationsInit / 1000 + " s");
		w.writeText("with #iterations of single object initializations: " + nIterationsInitSub + " in "
				+ timeIterationsInitSub / 1000 + " s");
		w.writeText("#iterations of total optimization: " + nIterations + " in: " + timeIterations / 1000 + " s");
		w.writeText("with #iterations of single cluster adaptions: " + nIterationsSub + " in "
				+ timeIterationsSub / 1000 + " s");
		w.partitionUpdate(partition);
		w.writeText(partition.toString());
	}
}
