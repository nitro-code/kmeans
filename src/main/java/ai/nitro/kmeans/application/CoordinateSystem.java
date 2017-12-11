/*
 * Copyright (C) 2017, nitro ventures GmbH
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.application;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import ai.nitro.kmeans.clustering.Cluster;
import ai.nitro.kmeans.clustering.Partition;
import ai.nitro.kmeans.clustering.impl.ClusterObjectImpl;
import ai.nitro.kmeans.clustering.impl.ClusteringException;

public class CoordinateSystem extends JPanel {

	protected static final float FONT_SIZE = 12f;

	protected static final int MAX_X = 30;

	protected static final int MAX_Y = 30;

	protected static final int PAD = 20;

	protected static final long serialVersionUID = 1L;

	protected int height;

	public Partition partition;

	protected float xScale;

	protected float yScale;

	protected double calculateXCoord(final double x) {
		return PAD + x * xScale;
	}

	protected double calculateYCoord(final double y) {
		return height - PAD - y * yScale;
	}

	protected int drawCluster(final Graphics2D g2, final ArrayList<Cluster> clusters, int currentObjectClusterId,
			final int i, final Cluster cluster) {
		final ArrayList<ClusterObjectImpl> assignedObjects = cluster.getObjects();

		for (int j = 0; j < assignedObjects.size(); j++) {
			final ClusterObjectImpl object = assignedObjects.get(j);
			currentObjectClusterId = drawObject(g2, clusters, currentObjectClusterId, i, assignedObjects, object);
		}

		if (partition.getCurrentObject() != null) {
			drawCurrentObject(g2, currentObjectClusterId);
		}

		g2.setPaint(Color.red);

		try {
			if (cluster.getCenter() != null) {
				drawClusterCenter(g2, cluster);
			}
		} catch (final ClusteringException e) {

		}

		return currentObjectClusterId;
	}

	protected void drawClusterCenter(final Graphics2D g2, final Cluster cluster) throws ClusteringException {
		final double[] tuple = cluster.getCenter().getTuple();
		g2.fill(new Line2D.Double(calculateXCoord(tuple[0]) - 10, calculateYCoord(tuple[1]),
				calculateXCoord(tuple[0]) + 10, calculateYCoord(tuple[1])));
		g2.fill(new Line2D.Double(calculateXCoord(tuple[0]), calculateYCoord(tuple[1]) - 10, calculateXCoord(tuple[0]),
				calculateYCoord(tuple[1]) + 10));
	}

	protected void drawClusters(final Graphics2D g2) {
		final ArrayList<Cluster> clusters = partition.getClusters();

		int currentObjectClusterId = -1;

		for (int i = 0; i < clusters.size(); i++) {
			final Cluster cluster = clusters.get(i);
			currentObjectClusterId = drawCluster(g2, clusters, currentObjectClusterId, i, cluster);
		}
	}

	protected void drawCurrentObject(final Graphics2D g2, final int currentObjectClusterId) {
		g2.setPaint(this.getColor(currentObjectClusterId));
		final double[] tuple = partition.getCurrentObject().getCoordinate().getTuple();
		g2.fill(new Rectangle2D.Double(calculateXCoord(tuple[0]) - 8, calculateYCoord(tuple[1]) - 8, 16, 16));

		g2.setPaint(Color.red);
		g2.fill(new Line2D.Double(calculateXCoord(tuple[0]) - 7, calculateYCoord(tuple[1]) + 7,
				calculateXCoord(tuple[0]) + 7, calculateYCoord(tuple[1]) - 7));
		g2.fill(new Line2D.Double(calculateXCoord(tuple[0]) - 7, calculateYCoord(tuple[1]) - 7,
				calculateXCoord(tuple[0]) + 7, calculateYCoord(tuple[1]) + 7));
	}

	protected int drawObject(final Graphics2D g2, final ArrayList<Cluster> clusters, int currentObjectClusterId,
			final int i, final ArrayList<ClusterObjectImpl> assignedObjects, final ClusterObjectImpl object) {
		final Color clusterColor;

		if (clusters.size() == 1) {
			clusterColor = Color.black;
		} else {
			clusterColor = this.getColor(i);
		}

		g2.setPaint(clusterColor);

		final double[] tuple = object.getCoordinate().getTuple();

		if (partition.getCurrentObject() != null && partition.getCurrentObject().equals(object)) {
			currentObjectClusterId = i;
		}

		g2.fill(new Rectangle2D.Double(calculateXCoord(tuple[0]), calculateYCoord(tuple[1]), 2, 2));
		return currentObjectClusterId;
	}

	protected void drawXAxis(final Graphics2D g2) {
		g2.draw(new Line2D.Double(calculateXCoord(0), calculateYCoord(0), calculateXCoord(MAX_X), calculateYCoord(0)));
	}

	protected void drawXAxisLabels(final Graphics2D g2) {
		final Font font = g2.getFont().deriveFont(FONT_SIZE);
		g2.setFont(font);

		final FontRenderContext frc = g2.getFontRenderContext();
		final LineMetrics lm = font.getLineMetrics("0", frc);

		for (int j = 0; j <= MAX_X; j++) {
			if (j == MAX_X) {
				continue;
			}

			final String s = String.valueOf(j);
			g2.drawString(s, (float) calculateXCoord(j) - 4, (float) calculateYCoord(0) + lm.getAscent() + 5);
			g2.draw(new Line2D.Double((float) calculateXCoord(j), (float) calculateYCoord(0) - 5,
					(float) calculateXCoord(j), calculateYCoord(0) + 5));
		}
	}

	protected void drawYAxis(final Graphics2D g2) {
		g2.draw(new Line2D.Double(calculateXCoord(0), calculateYCoord(0), calculateXCoord(0), calculateYCoord(MAX_Y)));
	}

	protected void drawYAxisLabels(final Graphics2D g2) {
		for (int j = 0; j <= MAX_Y; j++) {
			if (j == MAX_Y) {
				continue;
			}

			final String s = String.valueOf(j);
			g2.drawString(s, 3, (float) calculateYCoord(j) + 4);
			g2.draw(new Line2D.Double((float) calculateXCoord(0) - 5, (float) calculateYCoord(j),
					(float) calculateXCoord(0) + 5, calculateYCoord(j)));
		}
	}

	protected Color getColor(final int i) {
		final Color result;

		switch (i) {
		case 0:
			result = Color.blue;
			break;
		case 1:
			result = Color.green;
			break;
		case 2:
			result = Color.pink;
			break;
		case 3:
			result = Color.orange;
			break;
		case 4:
			result = Color.yellow;
			break;
		case 5:
			result = Color.magenta;
			break;
		case 6:
			result = Color.darkGray;
			break;
		case 7:
			result = Color.black;
			break;
		case 8:
			result = Color.cyan;
			break;
		case 9:
			result = Color.white;
			break;
		default:
			result = Color.yellow;
		}

		return result;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		final Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		height = getHeight();
		xScale = (float) (getWidth() - 2 * PAD) / MAX_X;
		yScale = (float) (height - 2 * PAD) / MAX_Y;

		drawXAxis(g2);
		drawYAxis(g2);
		drawXAxisLabels(g2);
		drawYAxisLabels(g2);

		if (partition != null) {
			drawClusters(g2);
		}
	}
}
