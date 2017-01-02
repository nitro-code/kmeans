/*
 * Copyright (C) 2016, nitro.ai
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD 3-clause license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.math.utils;

public class StringUtils {

	public static String format(final double number) {
		return new Double(number).toString().substring(0, Math.min(5, new Double(number).toString().length()));
	}
}
