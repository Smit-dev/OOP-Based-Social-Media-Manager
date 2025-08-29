package socialmedia;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MarketAnalyst extends User{
	
	private InstagramAnalysis instagramAnalysis;
    private XAnalysis xAnalysis;

    public MarketAnalyst(String username) {
        super(username);
        this.instagramAnalysis = new InstagramAnalysis();
        this.xAnalysis = new XAnalysis();
    }

    @Override
    public void displayOptions() {
        System.out.println("\n========================================");
        System.out.println("        Market Analyst Dashboard        ");
        System.out.println("========================================\n");
        
        System.out.println("Choose an action:");
        System.out.println("----------------------------------------");
        System.out.println("1. Analyze Instagram");
        System.out.println("2. Analyze X");
        System.out.println("3. Plot Follower Trends");
        System.out.println("4. Exit");
        System.out.println("----------------------------------------\n");
    }

    public void generateAnalytics(Profile profile) {
        System.out.println("\n----------------------------------------");
        System.out.println("        Analytics for " + profile.platformName);
        System.out.println("----------------------------------------");
        
        int growth = simulateGrowth(profile.followers);
        int engagement = simulateEngagement(profile.posts);
        int hashtags = simulateHashtagTrends(profile.posts);

        System.out.println("Follower Growth: " + growth);
        System.out.println("Post Engagement: " + engagement);
        System.out.println("Hashtag Usage: " + hashtags);
        System.out.println("----------------------------------------\n");
    }

    private int simulateGrowth(int base) {
        return base + ThreadLocalRandom.current().nextInt(-10, 11);
    }

    private int simulateEngagement(List<Profile.Post> posts) {
        return posts.stream().mapToInt(p -> p.likes + p.comments.size()).sum();
    }

    private int simulateHashtagTrends(List<Profile.Post> posts) {
        return (int) posts.stream().flatMap(p -> p.hashtags.stream()).distinct().count();
    }

    public void analyzeInstagram() {
        System.out.println("\n----------------------------------------");
        System.out.println("      Analyzing Instagram Profile       ");
        System.out.println("----------------------------------------");
        generateAnalytics(instagram);
        try {
            instagramAnalysis.extractMetrics(new File("profiles/profiles.csv"));
            System.out.println("\n----------------------------------------");
            System.out.println("      Instagram Follower Analysis     ");
            System.out.println("----------------------------------------");
            System.out.println("Average Followers: " + instagramAnalysis.getFollowerCount());
            System.out.println("----------------------------------------\n");
            instagramAnalysis.plotTrends("Instagram", instagramAnalysis.getFollowerCount());
        } catch (IOException e) {
            System.out.println("Error analyzing Instagram: " + e.getMessage());
        }
    }

    public void analyzeX() {
        System.out.println("\n----------------------------------------");
        System.out.println("         Analyzing X Profile           ");
        System.out.println("----------------------------------------");
        generateAnalytics(x);
        try {
            xAnalysis.extractMetrics(new File("profiles/profiles.csv"));
            System.out.println("\n----------------------------------------");
            System.out.println("        X Follower Analysis           ");
            System.out.println("----------------------------------------");
            System.out.println("Average Followers: " + xAnalysis.getFollowerCount());
            System.out.println("----------------------------------------\n");
            xAnalysis.plotTrends("X", xAnalysis.getFollowerCount());
        } catch (IOException e) {
            System.out.println("Error analyzing X: " + e.getMessage());
        }
    }

    public void handleInstagramAnalytics() {
        analyzeInstagram();
    }

    public void handleXAnalytics() {
        analyzeX();
    }

    public void handleBothAnalytics() {
        System.out.println("\n========================================");
        System.out.println("      Combined Platform Analytics       ");
        System.out.println("========================================\n");
        
        generateAnalytics(instagram);
        generateAnalytics(x);
        
        try {
            instagramAnalysis.extractMetrics(new File("profiles/profiles.csv"));
            xAnalysis.extractMetrics(new File("profiles/profiles.csv"));
            
            System.out.println("\n----------------------------------------");
            System.out.println("      Combined Follower Analysis      ");
            System.out.println("----------------------------------------");
            System.out.println("Instagram Average: " + instagramAnalysis.getFollowerCount());
            System.out.println("X Average: " + xAnalysis.getFollowerCount());
            System.out.println("----------------------------------------\n");
            
            instagramAnalysis.plotTrends("Instagram", instagramAnalysis.getFollowerCount());
            xAnalysis.plotTrends("X", xAnalysis.getFollowerCount());
        } catch (IOException e) {
            System.out.println("Error analyzing platforms: " + e.getMessage());
        }
    }

    public void plotFollowerTrends() {
        System.out.println("\n========================================");
        System.out.println("      Follower Trend Analysis           ");
        System.out.println("========================================\n");
        
        try {
            instagramAnalysis.extractMetrics(new File("profiles/profiles.csv"));
            xAnalysis.extractMetrics(new File("profiles/profiles.csv"));
            
            System.out.println("\n----------------------------------------");
            System.out.println("      Initial Follower Counts          ");
            System.out.println("----------------------------------------");
            System.out.println("Instagram: " + instagramAnalysis.getFollowerCount());
            System.out.println("X: " + xAnalysis.getFollowerCount());
            System.out.println("----------------------------------------\n");
            
            instagramAnalysis.plotTrends("Instagram", instagramAnalysis.getFollowerCount());
            xAnalysis.plotTrends("X", xAnalysis.getFollowerCount());
        } catch (IOException e) {
            System.out.println("Error plotting trends: " + e.getMessage());
        }
    }

}
