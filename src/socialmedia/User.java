package socialmedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class User {
    protected String username;
    protected Profile instagram;
    protected Profile x;

    public User(String username) {
        this.username = username;
        this.instagram = new Profile("Instagram");
        this.x = new Profile("X");
        loadProfileDataFromCSV();
    }

    public abstract void displayOptions();

    protected void loadProfileDataFromCSV() {
        try {
            File csvFile = new File("profiles/profiles.csv");
            if (!csvFile.exists()) return;
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 6) continue;
                if (!data[0].equals(username)) continue;

                Profile profile = data[1].equals("Instagram") ? instagram : x;
                profile.followers = Math.max(1, Integer.parseInt(data[2]));
                profile.following = Integer.parseInt(data[3]);
                profile.postsCount = Integer.parseInt(data[4]);

              
                for (int i = 0; i < profile.postsCount && i < 5 && (i + 5) < data.length; i++) {
                    String content = data[5 + i];
                    if (content != null && !content.trim().isEmpty()) {
                        profile.posts.add(new Profile.Post(content));
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error loading CSV profile data: " + e.getMessage());
        }
    }

 
    protected void saveProfileDataToCSV() {
        try {
            File dir = new File("profiles");
            if (!dir.exists()) dir.mkdirs(); 
            File file = new File(dir, "profiles.csv");
            
            if (!file.exists()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write("Username,Platform,Followers,Following,PostsCount,Post1,Post2,Post3,Post4,Post5");
                writer.newLine();
                writer.close();
            }
            
            
            Map<String, List<String>> existingProfiles = new HashMap<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 2) {
                    String key = data[0] + "," + data[1];
                    existingProfiles.put(key, Arrays.asList(data));
                }
            }
            reader.close();
    
            for (Profile profile : List.of(instagram, x)) {
                StringBuilder sb = new StringBuilder();
                sb.append(username).append(",");
                sb.append(profile.platformName).append(",");
                sb.append(profile.followers).append(",");
                sb.append(profile.following).append(",");
                sb.append(profile.postsCount);
                for (int i = 0; i < 5; i++) {
                    sb.append(",");
                    if (i < profile.posts.size()) {
                        sb.append(profile.posts.get(i).content.replace(",", ";"));
                    }
                }
                String key = username + "," + profile.platformName;
                String[] profileData = sb.toString().split(",");
                existingProfiles.put(key, Arrays.asList(profileData));
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("Username,Platform,Followers,Following,PostsCount,Post1,Post2,Post3,Post4,Post5");
            writer.newLine();

            for (List<String> profileData : existingProfiles.values()) {
                writer.write(String.join(",", profileData));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving profile data to CSV: " + e.getMessage());
        }
    }

}
