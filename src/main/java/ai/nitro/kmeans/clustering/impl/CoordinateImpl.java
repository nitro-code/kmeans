/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.clustering.impl;

public class CoordinateImpl {

	public static int o = 2;

	public static double getDistance(final CoordinateImpl coordinate1, final CoordinateImpl coordinate2)
			throws ClusteringException {
		if (coordinate1 == null) {
			throw new ClusteringException("coordinate 1 is null");
		}

		if (coordinate2 == null) {
			throw new ClusteringException("coordinate 2 is null");
		}

		double distance = 0;

		final double[] tupleCenter = coordinate1.getTuple();
		final double[] tupleObject = coordinate2.getTuple();

		if (tupleCenter.length == tupleObject.length) {
			for (int i = 0; i < tupleCenter.length; i++) {
				distance += Math.pow(tupleCenter[i] - tupleObject[i], 2);
			}
		}

		return distance;
	}

	public static CoordinateImpl multiplyCoordinate(final CoordinateImpl coordinate, final double factor) throws Exception {
		final double[] tuple1 = coordinate.getTuple();
		final double[] tupleNew = new double[o];

		for (int i = 0; i < tuple1.length; i++) {
			tupleNew[i] = tuple1[i] * factor;
		}

		return new CoordinateImpl(tupleNew);
	}

	public static CoordinateImpl subtractCoordinates(final CoordinateImpl coordinate1, final CoordinateImpl coordinate2)
			throws Exception {
		final double[] tuple1 = coordinate1.getTuple();
		final double[] tuple2 = coordinate2.getTuple();
		final double[] tupleNew = new double[o];

		for (int i = 0; i < tuple1.length; i++) {
			tupleNew[i] = tuple1[i] - tuple2[i];
		}

		return new CoordinateImpl(tupleNew);
	}

	public static CoordinateImpl sumCoordinates(final CoordinateImpl coordinate1, final CoordinateImpl coordinate2)
			throws Exception {
		final double[] tuple1 = coordinate1.getTuple();
		final double[] tuple2 = coordinate2.getTuple();
		final double[] tupleNew = new double[o];

		for (int i = 0; i < tuple1.length; i++) {
			tupleNew[i] = tuple1[i] + tuple2[i];
		}

		return new CoordinateImpl(tupleNew);
	}

	protected final double[] tuple;

	public CoordinateImpl(final double[] tuple) throws ClusteringException {
		if (tuple.length != o) {
			throw new ClusteringException("incorrect number of variables");
		}

		this.tuple = tuple;
	}

	public double[] getTuple() {
		return tuple;
	}

	public double getTupleSum() {
		double sum = 0;

		for (int i = 0; i < tuple.length; i++) {
			sum += tuple[i];
		}

		return sum;
	}
}
