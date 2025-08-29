package socialmedia;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PostScheduler {
    private static final String SCHEDULED_POSTS_FILE = "profiles/scheduled_posts.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private Map<String, List<ScheduledPost>> userScheduledPosts;
    private Timer scheduler;
    
    public PostScheduler() {
        this.userScheduledPosts = new ConcurrentHashMap<>();
        this.scheduler = new Timer("PostScheduler", true);
        loadScheduledPosts();
        startScheduler();
    }
    
    public void schedulePost(String username, String content, LocalDateTime scheduledTime, String platform) {
        ScheduledPost post = new ScheduledPost(content, scheduledTime, platform, username);
        
        userScheduledPosts.computeIfAbsent(username, k -> new ArrayList<>()).add(post);
        saveScheduledPosts();
        
        System.out.println("✅ Post scheduled successfully!");
        System.out.println("📅 Scheduled for: " + post.getFormattedScheduledTime());
        System.out.println("📱 Platform: " + platform);
        System.out.println("📝 Content: " + content.substring(0, Math.min(content.length(), 50)) + "...");
    }
    
    public List<ScheduledPost> getUserScheduledPosts(String username) {
        return userScheduledPosts.getOrDefault(username, new ArrayList<>());
    }
    
    public void cancelScheduledPost(String username, String postId) {
        List<ScheduledPost> posts = userScheduledPosts.get(username);
        if (posts != null) {
            posts.removeIf(post -> post.getPostId().equals(postId));
            saveScheduledPosts();
            System.out.println("🗑️ Scheduled post cancelled: " + postId);
        }
    }
    
    private void startScheduler() {
        // Check for posts to publish every minute
        scheduler.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndPublishPosts();
            }
        }, 0, 60000); // Check every minute
    }
    
    private void checkAndPublishPosts() {
        for (Map.Entry<String, List<ScheduledPost>> entry : userScheduledPosts.entrySet()) {
            String username = entry.getKey();
            List<ScheduledPost> posts = entry.getValue();
            
            for (ScheduledPost post : posts) {
                if (post.isReadyToPost()) {
                    publishPost(username, post);
                    post.markAsPosted();
                }
            }
        }
        saveScheduledPosts();
    }
    
    private void publishPost(String username, ScheduledPost post) {
        try {
            // Simulate posting to the platform
            System.out.println("\n🚀 AUTO-POSTING SCHEDULED CONTENT:");
            System.out.println("👤 User: " + username);
            System.out.println("📱 Platform: " + post.getPlatform());
            System.out.println("📝 Content: " + post.getContent());
            System.out.println("⏰ Posted at: " + LocalDateTime.now().format(FORMATTER));
            System.out.println("----------------------------------------");
            
            // Log the posting activity
            logPostActivity(username, post);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to publish scheduled post: " + e.getMessage());
        }
    }
    
    private void logPostActivity(String username, ScheduledPost post) {
        try {
            File logDir = new File("profiles/logs");
            if (!logDir.exists()) logDir.mkdirs();
            
            File logFile = new File(logDir, "post_activity.csv");
            boolean isNewFile = !logFile.exists();
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                if (isNewFile) {
                    writer.write("Timestamp,Username,Platform,Content,Type,PostId");
                    writer.newLine();
                }
                
                writer.write(String.format("%s,%s,%s,\"%s\",SCHEDULED,%s",
                    LocalDateTime.now().format(FORMATTER),
                    username,
                    post.getPlatform(),
                    post.getContent().replace("\"", "\"\""), // Escape quotes
                    post.getPostId()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to log post activity: " + e.getMessage());
        }
    }
    
    private void saveScheduledPosts() {
        try {
            File dir = new File("profiles");
            if (!dir.exists()) dir.mkdirs();
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCHEDULED_POSTS_FILE))) {
                writer.write("Username,Content,ScheduledTime,Platform,Posted,PostId");
                writer.newLine();
                
                for (Map.Entry<String, List<ScheduledPost>> entry : userScheduledPosts.entrySet()) {
                    for (ScheduledPost post : entry.getValue()) {
                        writer.write(String.format("%s,\"%s\",%s,%s,%b,%s",
                            entry.getKey(),
                            post.getContent().replace("\"", "\"\""),
                            post.getFormattedScheduledTime(),
                            post.getPlatform(),
                            post.isPosted(),
                            post.getPostId()));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving scheduled posts: " + e.getMessage());
        }
    }
    
    private void loadScheduledPosts() {
        try {
            File file = new File(SCHEDULED_POSTS_FILE);
            if (!file.exists()) return;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine(); // Skip header
                while ((line = reader.readLine()) != null) {
                    String[] parts = parseCSVLine(line);
                    if (parts.length >= 6) {
                        try {
                            String username = parts[0];
                            String content = parts[1];
                            LocalDateTime scheduledTime = LocalDateTime.parse(parts[2], FORMATTER);
                            String platform = parts[3];
                            boolean posted = Boolean.parseBoolean(parts[4]);
                            
                            ScheduledPost post = new ScheduledPost(content, scheduledTime, platform, username);
                            if (posted) post.markAsPosted();
                            
                            userScheduledPosts.computeIfAbsent(username, k -> new ArrayList<>()).add(post);
                        } catch (DateTimeParseException e) {
                            System.err.println("Error parsing scheduled post date: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading scheduled posts: " + e.getMessage());
        }
    }
    
    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++; // Skip the next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
    
    public void displayScheduledPosts(String username) {
        List<ScheduledPost> posts = getUserScheduledPosts(username);
        
        if (posts.isEmpty()) {
            System.out.println("📅 No scheduled posts found.");
            return;
        }
        
        System.out.println("\n📅 SCHEDULED POSTS");
        System.out.println("========================================");
        
        for (int i = 0; i < posts.size(); i++) {
            ScheduledPost post = posts.get(i);
            System.out.printf("%d. %s [%s]\n", 
                i + 1, 
                post.getFormattedScheduledTime(), 
                post.isPosted() ? "✅ POSTED" : "⏳ PENDING");
            System.out.printf("   Platform: %s\n", post.getPlatform());
            System.out.printf("   Content: %s\n", 
                post.getContent().length() > 60 ? 
                post.getContent().substring(0, 60) + "..." : 
                post.getContent());
            System.out.printf("   ID: %s\n", post.getPostId());
            System.out.println();
        }
    }
    
    public void shutdown() {
        if (scheduler != null) {
            scheduler.cancel();
        }
    }
}
