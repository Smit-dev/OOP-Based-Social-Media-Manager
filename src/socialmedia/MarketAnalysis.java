package socialmedia;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public abstract class MarketAnalysis {
	
	private int followers;

    public abstract void extractMetrics(File csvFile) throws IOException;
    public abstract int getFollowerCount();

    public void plotTrends(String platform, int initialFollowers) {
        try {
            File chartsDir = new File("charts");
            if (!chartsDir.exists()) {
                chartsDir.mkdirs();
            }

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            int currentFollowers = initialFollowers;
            
            dataset.addValue(currentFollowers, "Followers", "Week 0");
            System.out.println("Week 0: " + currentFollowers + " followers (initial)");
            
            for (int week = 1; week <= 10; week++) {
                int change;
                if (currentFollowers <= 250) {
                    change = ThreadLocalRandom.current().nextInt(0, 251); 
                } else {
                    change = ThreadLocalRandom.current().nextInt(-250, 251);
                }
                currentFollowers += change;
                if (currentFollowers < 1) currentFollowers = 1; 
                dataset.addValue(currentFollowers, "Followers", "Week " + week);
                System.out.println("Week " + week + ": " + currentFollowers + " followers (" +
                    (change >= 0 ? "+" : "") + change + " change)");
            }

            JFreeChart lineChart = ChartFactory.createLineChart(
                    platform + " Follower Trends",
                    "Week",
                    "Followers",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false);

            File chartFile = new File("charts/" + platform + "_TrendChart.jpg");
            ChartUtils.saveChartAsJPEG(chartFile, lineChart, 640, 480);
            
            System.out.println("\n----------------------------------------");
            System.out.println("Generating trend chart...");
            System.out.println("Chart saved as: " + chartFile.getAbsolutePath());
            System.out.println("----------------------------------------");

            ChartPanel chartPanel = new ChartPanel(lineChart);
            chartPanel.setPreferredSize(new java.awt.Dimension(640, 480));
            
            JFrame frame = new JFrame(platform + " Follower Trends");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(chartPanel);
            frame.pack();
            frame.setVisible(true);
            
        } catch (IOException e) {
            System.err.println("Error creating chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
