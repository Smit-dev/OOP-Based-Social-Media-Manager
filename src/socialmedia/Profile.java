package socialmedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Profile {
	
	String platformName;
    
    int postsCount;
    
    int followers;
    
    int following;
    
    List<Post> posts;

    
    public Profile(String platformName) {
        this.platformName = platformName;
        this.posts = new ArrayList<>();
        this.followers = 100;
        this.following = 50;
    }

    
    public boolean addPost(String content) {
        if (posts.size() < 5) {
            posts.add(new Post(content));
            postsCount++;
            return true;
        }
        return false;
    }

    // Overloaded method to add multiple posts at once (varargs)
    public boolean addPost(String... contents) {
        boolean added = false;
        for (String content : contents) {
            if (posts.size() < 5) {
                posts.add(new Post(content));
                postsCount++;
                added = true;
            } else {
                break;
            }
        }
        return added;
    }

    public void removePost(int index) {
        if (index >= 0 && index < posts.size()) {
            posts.remove(index);
            postsCount--;
        }
    }

    public void addComment(int index, String comment) {
        if (index >= 0 && index < posts.size()) {
            posts.get(index).comments.add(comment);
        }
    }

    public void follow(String user) {
        following++;
        
        try {
            File csvFile = new File("profiles/profiles.csv");
            if (!csvFile.exists()) return;

            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        lines.add(line);
                        firstLine = false;
                        continue;
                    }
                    String[] data = line.split(",");
                    if (data.length >= 2 && data[0].equals(user) && data[1].equals(platformName)) {
                        int updatedFollowers = Integer.parseInt(data[2]);
                        updatedFollowers++;
                        data[2] = String.valueOf(updatedFollowers);
                        line = String.join(",", data);
                    }
                    lines.add(line);
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error updating followers in CSV: " + e.getMessage());
        }
    }

    public void unfollow(String user) {
        following--;
        
        try {
            File csvFile = new File("profiles/profiles.csv");
            if (!csvFile.exists()) return;

            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        lines.add(line);
                        firstLine = false;
                        continue;
                    }
                    String[] data = line.split(",");
                    if (data.length >= 2 && data[0].equals(user) && data[1].equals(platformName)) {
                        int updatedFollowers = Integer.parseInt(data[2]);
                        updatedFollowers--;
                        data[2] = String.valueOf(updatedFollowers);
                        line = String.join(",", data);
                    }
                    lines.add(line);
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error updating followers in CSV: " + e.getMessage());
        }
    }

    /**
     * Nested class representing a social media post.
     * Contains content, engagement metrics, and associated data.
     */
    static class Post {
       
        String content;
        
        int likes;
        
        List<String> comments;
        
        List<String> hashtags;

        
        public Post(String content) {
            this.content = content;
            this.likes = new Random().nextInt(100);
            this.comments = new ArrayList<>();
            this.hashtags = new ArrayList<>(List.of("#AI", "#Tech"));
        }
    }

}
