/*
 * Copyright (C) 2017, nitro ventures GmbH
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.clustering.impl;

public class ClusteringException extends Exception {

	protected static final long serialVersionUID = 1L;

	public ClusteringException(final String message) {
		super(message);
	}
}
