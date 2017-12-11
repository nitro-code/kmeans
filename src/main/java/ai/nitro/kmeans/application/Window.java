/*
 * Copyright (C) 2017, nitro ventures GmbH
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package ai.nitro.kmeans.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ai.nitro.kmeans.clustering.Partition;
import ai.nitro.kmeans.clustering.impl.PartitionImpl;
import ai.nitro.kmeans.clustering.kmeans.impl.KmeansImpl;
import ai.nitro.kmeans.clustering.kstarmeans.impl.KStarMeansImpl;
import ai.nitro.kmeans.clustering.utils.TestDataGeneratorUtils;

public class Window extends JFrame implements ActionListener {

	protected static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		final Window app = new Window();
		app.setTitle("K-Means Demo");
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setSize(1000, 670);
		app.init();
		app.setVisible(true);
	}

	protected JComboBox<String> algorithmType;

	protected JPanel controlPanel;

	protected CoordinateSystem cs;

	protected JComboBox<String> densityTypes;

	protected JButton genDataButton;

	protected JButton nextButton;

	protected JTextField numberOfClustersInDataGen;

	protected JTextField numberOfClustersInKMeans;

	protected JTextField numberOfObjects;

	protected PartitionImpl partition;

	protected JCheckBox pause;

	protected JPanel rightPanel;

	protected JScrollPane scrollPane;

	protected JButton startButton;

	public Thread synchro;

	protected JTextArea textArea;

	@Override
	public void actionPerformed(final ActionEvent evt) {
		if (evt.getSource() == genDataButton) {
			handleGenData();
		} else if (evt.getSource() == startButton) {
			handleStart();
		} else if (evt.getSource() == nextButton) {
			handleNext();
		}
	}

	protected void addControlPanel() {
		rightPanel = new JPanel(new BorderLayout());

		controlPanel = new JPanel(new GridLayout(0, 1));

		addGenDataButton();
		addGenDataPanel();
		addStartButton();
		addInitialPanel();
		addNextButton();
		addGoPanel();

		rightPanel.add(BorderLayout.NORTH, controlPanel);

		this.add(BorderLayout.EAST, rightPanel);
	}

	protected void addGenDataButton() {
		genDataButton = new JButton("generate data");
		genDataButton.setSize(new Dimension(40, 100));
		genDataButton.addActionListener(this);
		controlPanel.add(genDataButton);
	}

	protected void addGenDataPanel() {
		final JPanel genDataPanel = new JPanel(new FlowLayout());
		densityTypes = new JComboBox<String>(new String[] { "random", "multi normal distributed" });
		densityTypes.setSelectedIndex(1);
		genDataPanel.add(densityTypes);
		numberOfClustersInDataGen = new JTextField("3", 2);
		genDataPanel.add(numberOfClustersInDataGen);
		numberOfObjects = new JTextField("5000", 10);
		genDataPanel.add(numberOfObjects);
		controlPanel.add(genDataPanel);
	}

	protected void addGoPanel() {
		final JPanel goPanel = new JPanel(new FlowLayout());
		pause = new JCheckBox("pause", false);
		goPanel.add(pause);
		controlPanel.add(goPanel);
	}

	protected void addInitialPanel() {
		final JPanel initialPanel = new JPanel(new FlowLayout());
		algorithmType = new JComboBox<String>(new String[] { "K-Means", "K*-Means" });
		algorithmType.setSelectedIndex(0);
		initialPanel.add(algorithmType);
		numberOfClustersInKMeans = new JTextField("3", 2);
		initialPanel.add(numberOfClustersInKMeans);
		controlPanel.add(initialPanel);
	}

	protected void addNextButton() {
		nextButton = new JButton("next");
		nextButton.setSize(new Dimension(40, 100));
		nextButton.setEnabled(false);
		nextButton.addActionListener(this);
		controlPanel.add(nextButton);
	}

	protected void addStartButton() {
		startButton = new JButton("start cluster analysis");
		startButton.setSize(new Dimension(40, 100));
		startButton.setEnabled(false);
		startButton.addActionListener(this);
		controlPanel.add(startButton);
	}

	protected void addTextArea() {
		textArea = new JTextArea(10, 30);
		textArea.setEditable(false);
		textArea.setCaretPosition(0);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		scrollPane = new JScrollPane(textArea);
		rightPanel.add(BorderLayout.CENTER, scrollPane);
	}

	protected void generateData(final int type, final int numberOfObjects, final int upperBound, final int k) {
		try {
			textArea.setText("");

			switch (type) {
			case 0:
				partition = TestDataGeneratorUtils.getRandomData_UniformDistribution(numberOfObjects, upperBound);
				break;
			case 1:
				partition = TestDataGeneratorUtils.getRandomData_NormalDistributionMixed2Dim(numberOfObjects, k, 3, upperBound,
						this);
				break;
			default:
				partition = TestDataGeneratorUtils.getRandomData_UniformDistribution(numberOfObjects, upperBound);
			}

			this.partitionUpdate(partition);
		} catch (final Exception e) {
			textArea.append(e.getMessage());
		}
	}

	protected void handleGenData() {
		final int densityType = densityTypes.getSelectedIndex();
		final int numberOfObjects = Integer.parseInt(this.numberOfObjects.getText());
		final int numberOfClusters = Integer.parseInt(numberOfClustersInDataGen.getText());
		this.generateData(densityType, numberOfObjects, 29, numberOfClusters);
		startButton.setEnabled(true);
	}

	protected void handleNext() {
		synchronized (synchro) {
			synchro.notify();
		}
	}

	protected void handleStart() {
		final int algorithmType = this.algorithmType.getSelectedIndex();

		if (algorithmType == 0) {
			startAnalysisKMeans(Integer.parseInt(numberOfClustersInKMeans.getText()));
		} else if (algorithmType == 1) {
			startAnalyseKMeansStar(Integer.parseInt(numberOfClustersInKMeans.getText()));
		}

		nextButton.setEnabled(true);
	}

	public void init() {
		this.setLayout(new BorderLayout());

		cs = new CoordinateSystem();
		this.add(BorderLayout.CENTER, cs);

		addControlPanel();
		addTextArea();
	}

	public void partitionUpdate(final Partition partition) {
		cs.partition = partition;
		cs.updateUI();
	}

	public void pause() {
		try {
			if (synchro != null) {
				synchronized (synchro) {
					synchro.wait();
				}
			}
		} catch (final Exception e) {
			this.writeText(e.getMessage());
		}
	}

	public boolean pauseIsChecked() {
		return pause.isSelected();
	}

	protected void startAnalyseKMeansStar(final int k) {
		final KStarMeansImpl kmeansStar = new KStarMeansImpl(k, partition, this, 10);
		synchro = kmeansStar;
		kmeansStar.start();
	}

	protected void startAnalysisKMeans(final int k) {
		final KmeansImpl kmeans = new KmeansImpl(k, partition, this, 10);
		synchro = kmeans;
		kmeans.start();
	}

	public synchronized void writeText(final String text) {
		textArea.append(text + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}
