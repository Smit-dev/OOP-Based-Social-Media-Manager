package socialmedia;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScheduledPost {
    private String content;
    private LocalDateTime scheduledTime;
    private String platform;
    private String author;
    private boolean isPosted;
    private String postId;
    
    public ScheduledPost(String content, LocalDateTime scheduledTime, String platform, String author) {
        this.content = content;
        this.scheduledTime = scheduledTime;
        this.platform = platform;
        this.author = author;
        this.isPosted = false;
        this.postId = generatePostId();
    }
    
    private String generatePostId() {
        return "POST_" + System.currentTimeMillis() + "_" + author.hashCode();
    }
    
    public boolean isReadyToPost() {
        return LocalDateTime.now().isAfter(scheduledTime) && !isPosted;
    }
    
    public void markAsPosted() {
        this.isPosted = true;
    }
    
    // Getters and setters
    public String getContent() { return content; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public String getPlatform() { return platform; }
    public String getAuthor() { return author; }
    public boolean isPosted() { return isPosted; }
    public String getPostId() { return postId; }
    
    public String getFormattedScheduledTime() {
        return scheduledTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    @Override
    public String toString() {
        return String.format("ScheduledPost{id='%s', content='%s', time='%s', platform='%s', posted=%b}", 
                postId, content.substring(0, Math.min(content.length(), 30)) + "...", 
                getFormattedScheduledTime(), platform, isPosted);
    }
}
