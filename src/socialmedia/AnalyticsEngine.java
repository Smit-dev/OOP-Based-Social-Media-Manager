package socialmedia;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsEngine {
    private static final String ANALYTICS_LOG = "profiles/logs/analytics_data.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private Map<String, List<AnalyticsEntry>> userAnalytics;
    
    public AnalyticsEngine() {
        this.userAnalytics = new HashMap<>();
        loadAnalyticsData();
    }
    
    // Data classes for analytics
    public static class AnalyticsEntry {
        public final LocalDateTime timestamp;
        public final String platform;
        public final String metricType; // followers, likes, comments, shares
        public final int value;
        public final String contentId;
        
        public AnalyticsEntry(LocalDateTime timestamp, String platform, String metricType, int value, String contentId) {
            this.timestamp = timestamp;
            this.platform = platform;
            this.metricType = metricType;
            this.value = value;
            this.contentId = contentId;
        }
    }
    
    public static class TrendAnalysis {
        public final String metric;
        public final String platform;
        public final double growthRate;
        public final int totalChange;
        public final LocalDateTime periodStart;
        public final LocalDateTime periodEnd;
        
        public TrendAnalysis(String metric, String platform, double growthRate, int totalChange, 
                           LocalDateTime periodStart, LocalDateTime periodEnd) {
            this.metric = metric;
            this.platform = platform;
            this.growthRate = growthRate;
            this.totalChange = totalChange;
            this.periodStart = periodStart;
            this.periodEnd = periodEnd;
        }
    }
    
    public void recordMetric(String username, String platform, String metricType, int value, String contentId) {
        AnalyticsEntry entry = new AnalyticsEntry(LocalDateTime.now(), platform, metricType, value, contentId);
        userAnalytics.computeIfAbsent(username, k -> new ArrayList<>()).add(entry);
        saveAnalyticsData();
    }
    
    public void generateUserReport(String username) {
        List<AnalyticsEntry> entries = userAnalytics.getOrDefault(username, new ArrayList<>());
        
        if (entries.isEmpty()) {
            System.out.println("\n*** ANALYTICS OVERVIEW ***");
            System.out.println("========================================");
            System.out.println("No analytics data available yet.");
            System.out.println("TIP: Try option 4 to generate sample data first!");
            return;
        }
        
        System.out.println("\n*** YOUR SOCIAL MEDIA OVERVIEW ***");
        System.out.println("========================================");
        
        // Simple platform summary
        Map<String, List<AnalyticsEntry>> byPlatform = entries.stream()
            .collect(Collectors.groupingBy(e -> e.platform));
        
        for (Map.Entry<String, List<AnalyticsEntry>> platformEntry : byPlatform.entrySet()) {
            String platform = platformEntry.getKey();
            List<AnalyticsEntry> platformEntries = platformEntry.getValue();
            
            System.out.println("\n>> " + platform.toUpperCase() + " SUMMARY:");
            System.out.println("----------------------------------------");
            
            // Get latest numbers for each metric
            Map<String, Integer> latestMetrics = platformEntries.stream()
                .collect(Collectors.groupingBy(
                    e -> e.metricType,
                    Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparing(e -> e.timestamp)),
                        opt -> opt.map(e -> e.value).orElse(0)
                    )
                ));
            
            System.out.println("Followers: " + latestMetrics.getOrDefault("followers", 0));
            System.out.println("Likes: " + latestMetrics.getOrDefault("likes", 0));
            System.out.println("Comments: " + latestMetrics.getOrDefault("comments", 0));
            System.out.println("Shares: " + latestMetrics.getOrDefault("shares", 0));
        }
        
        System.out.println("\n*** QUICK INSIGHTS ***");
        System.out.println("----------------------------------------");
        System.out.println("✓ Your accounts are active and growing!");
        System.out.println("✓ Use option 2 to see growth trends");
        System.out.println("✓ Use option 3 to check scheduled posts");
    }
    
    public List<TrendAnalysis> analyzeTrends(String username, int daysBack) {
        List<AnalyticsEntry> entries = userAnalytics.getOrDefault(username, new ArrayList<>());
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysBack);
        
        List<AnalyticsEntry> recentEntries = entries.stream()
            .filter(e -> e.timestamp.isAfter(cutoff))
            .collect(Collectors.toList());
        
        List<TrendAnalysis> trends = new ArrayList<>();
        
        // Group by platform and metric
        Map<String, Map<String, List<AnalyticsEntry>>> grouped = recentEntries.stream()
            .collect(Collectors.groupingBy(
                e -> e.platform,
                Collectors.groupingBy(e -> e.metricType)
            ));
        
        for (Map.Entry<String, Map<String, List<AnalyticsEntry>>> platformEntry : grouped.entrySet()) {
            String platform = platformEntry.getKey();
            
            for (Map.Entry<String, List<AnalyticsEntry>> metricEntry : platformEntry.getValue().entrySet()) {
                String metric = metricEntry.getKey();
                List<AnalyticsEntry> metricData = metricEntry.getValue()
                    .stream()
                    .sorted(Comparator.comparing(e -> e.timestamp))
                    .collect(Collectors.toList());
                
                if (metricData.size() >= 2) {
                    AnalyticsEntry first = metricData.get(0);
                    AnalyticsEntry last = metricData.get(metricData.size() - 1);
                    
                    int totalChange = last.value - first.value;
                    double growthRate = first.value == 0 ? 0 : 
                        ((double) totalChange / first.value) * 100;
                    
                    trends.add(new TrendAnalysis(metric, platform, growthRate, totalChange, 
                        first.timestamp, last.timestamp));
                }
            }
        }
        
        return trends;
    }
    
    public void displayTrendAnalysis(String username, int daysBack) {
        List<TrendAnalysis> trends = analyzeTrends(username, daysBack);
        
        if (trends.isEmpty()) {
            System.out.println("\n*** GROWTH TRENDS ***");
            System.out.println("========================================");
            System.out.println("Not enough data to show trends yet.");
            System.out.println("TIP: Generate sample data first (option 4)!");
            return;
        }
        
        System.out.printf("\n*** GROWTH TRENDS - Last %d days ***\n", daysBack);
        System.out.println("========================================");
        
        boolean hasGrowth = false;
        for (TrendAnalysis trend : trends) {
            if (Math.abs(trend.growthRate) > 1) { // Only show significant changes
                String status = "";
                String icon = "";
                
                if (trend.growthRate > 5) {
                    status = "Great growth!";
                    icon = "↗";
                } else if (trend.growthRate > 0) {
                    status = "Growing steadily";
                    icon = "↗";
                } else if (trend.growthRate < -5) {
                    status = "Needs attention";
                    icon = "↘";
                } else {
                    status = "Slight decline";
                    icon = "↘";
                }
                
                System.out.printf("%s %s on %s: %s (%.0f%%)\n", 
                    icon, trend.metric.toUpperCase(), trend.platform.toUpperCase(), 
                    status, Math.abs(trend.growthRate));
                hasGrowth = true;
            }
        }
        
        if (!hasGrowth) {
            System.out.println(">> Your metrics are stable - no major changes detected.");
            System.out.println(">> This is normal for established accounts!");
        }
        
        System.out.println("\n*** RECOMMENDATIONS ***");
        System.out.println("• Post regularly to maintain engagement");
        System.out.println("• Use trending hashtags to increase reach");
        System.out.println("• Engage with your followers' comments");
    }
    
    public void simulateAnalyticsData(String username) {
        Random random = new Random();
        String[] platforms = {"Instagram", "X"};
        String[] metrics = {"followers", "likes", "comments", "shares"};
        
        System.out.println(">> Generating sample analytics data...");
        
        for (int day = 30; day >= 0; day--) {
            LocalDateTime timestamp = LocalDateTime.now().minusDays(day);
            
            for (String platform : platforms) {
                for (String metric : metrics) {
                    int baseValue = getBaseValue(metric);
                    int variation = random.nextInt(baseValue / 5) - (baseValue / 10);
                    int value = Math.max(1, baseValue + variation);
                    
                    AnalyticsEntry entry = new AnalyticsEntry(timestamp, platform, metric, 
                        value, "content_" + day + "_" + metric);
                    userAnalytics.computeIfAbsent(username, k -> new ArrayList<>()).add(entry);
                }
            }
        }
        
        saveAnalyticsData();
        System.out.println(">> Sample analytics data generated for 30 days!");
    }
    
    private int getBaseValue(String metric) {
        switch (metric) {
            case "followers": return 1000;
            case "likes": return 150;
            case "comments": return 25;
            case "shares": return 10;
            default: return 50;
        }
    }
    
    private void saveAnalyticsData() {
        try {
            File logDir = new File("profiles/logs");
            if (!logDir.exists()) logDir.mkdirs();
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ANALYTICS_LOG))) {
                writer.write("Username,Timestamp,Platform,MetricType,Value,ContentId");
                writer.newLine();
                
                for (Map.Entry<String, List<AnalyticsEntry>> userEntry : userAnalytics.entrySet()) {
                    String username = userEntry.getKey();
                    for (AnalyticsEntry entry : userEntry.getValue()) {
                        writer.write(String.format("%s,%s,%s,%s,%d,%s",
                            username,
                            entry.timestamp.format(FORMATTER),
                            entry.platform,
                            entry.metricType,
                            entry.value,
                            entry.contentId));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving analytics data: " + e.getMessage());
        }
    }
    
    private void loadAnalyticsData() {
        try {
            File file = new File(ANALYTICS_LOG);
            if (!file.exists()) return;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine(); // Skip header
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        try {
                            String username = parts[0];
                            LocalDateTime timestamp = LocalDateTime.parse(parts[1], FORMATTER);
                            String platform = parts[2];
                            String metricType = parts[3];
                            int value = Integer.parseInt(parts[4]);
                            String contentId = parts[5];
                            
                            AnalyticsEntry entry = new AnalyticsEntry(timestamp, platform, metricType, value, contentId);
                            userAnalytics.computeIfAbsent(username, k -> new ArrayList<>()).add(entry);
                        } catch (Exception e) {
                            System.err.println("Error parsing analytics entry: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading analytics data: " + e.getMessage());
        }
    }
    
    public void generatePerformanceReport(String username) {
        System.out.println("\n*** ACCOUNT PERFORMANCE SUMMARY ***");
        System.out.println("========================================");
        
        List<AnalyticsEntry> entries = userAnalytics.getOrDefault(username, new ArrayList<>());
        
        if (entries.isEmpty()) {
            System.out.println("📈 No performance data available yet.");
            System.out.println("💡 Try generating sample data first (option 4)!");
            return;
        }
        
        System.out.printf("📊 Total Activity Records: %d\n", entries.size());
        
        // Simple engagement summary
        Map<String, List<AnalyticsEntry>> byPlatform = entries.stream()
            .collect(Collectors.groupingBy(e -> e.platform));
            
        System.out.println("\n🎯 PLATFORM ACTIVITY:");
        System.out.println("----------------------------------------");
        for (String platform : byPlatform.keySet()) {
            int platformActivity = byPlatform.get(platform).size();
            System.out.printf("� %s: %d activities tracked\n", platform, platformActivity);
        }
        
        // Get most recent metrics
        if (!entries.isEmpty()) {
            AnalyticsEntry latest = entries.stream()
                .max(Comparator.comparing(e -> e.timestamp))
                .orElse(null);
                
            if (latest != null) {
                System.out.println("\n🏆 LATEST ACHIEVEMENT:");
                System.out.println("----------------------------------------");
                System.out.printf("� Latest Update: %s\n", latest.timestamp.format(FORMATTER));
                System.out.printf("📱 Platform: %s\n", latest.platform);
                System.out.printf("📊 Metric: %s reached %d\n", latest.metricType, latest.value);
            }
        }
        
        System.out.println("\n✨ PERFORMANCE HIGHLIGHTS:");
        System.out.println("• Your account is actively tracked ✅");
        System.out.println("• Data collection is working smoothly 📊");
        System.out.println("• Ready for growth analysis 📈");
        
        System.out.println("\n💡 NEXT STEPS:");
        System.out.println("• Check growth trends (option 2)");
        System.out.println("• Schedule more posts (option 6)");
        System.out.println("• Monitor engagement regularly");
    }
}
