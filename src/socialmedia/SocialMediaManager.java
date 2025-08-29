package socialmedia;

import java.io.*;
import java.util.*;
// JFreeChart imports are used by MarketAnalyst class
import javax.swing.*;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class SocialMediaManager {
    private static final String USER_AUTH_FILE = "profiles/user_auth.csv";

    private static void initializeAuthFile() {
        try {
            File dir = new File("profiles");
            if (!dir.exists()) dir.mkdirs();
            
            File authFile = new File(USER_AUTH_FILE);
            if (!authFile.exists()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(authFile))) {
                    writer.write("Username,Password,Role");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing auth file: " + e.getMessage());
        }
    }

    private static boolean isExistingUser(String username) {
        try {
            File authFile = new File(USER_AUTH_FILE);
            if (!authFile.exists()) return false;

            try (BufferedReader reader = new BufferedReader(new FileReader(authFile))) {
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] data = line.split(",");
                    if (data.length >= 1 && data[0].equals(username)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }


    private static boolean verifyPassword(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_AUTH_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error verifying password: " + e.getMessage());
        }
        return false;
    }

    private static int getUserRole(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_AUTH_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data[0].equals(username)) {
                    return Integer.parseInt(data[2]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error getting user role: " + e.getMessage());
        }
        return -1;
    }

    private static void saveNewUser(String username, String password, int role) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_AUTH_FILE, true))) {
            writer.write(String.format("%s,%s,%d", username, password, role));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving new user: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        initializeAuthFile();
        
        System.out.println("\n========================================");
        System.out.println("      Social Media Manager Login        ");
        System.out.println("========================================\n");
        
        System.out.println("Enter username:");
        String username = sc.nextLine();

        int role;
        if (isExistingUser(username)) {
            System.out.println("Enter password:");
            String password = sc.nextLine();
            
            if (!verifyPassword(username, password)) {
                System.out.println("Incorrect password. Access denied.");
                sc.close();
                return;
            }
            
            role = getUserRole(username);
        } else {
    
            System.out.println("New user detected. Please set up your account.");
            System.out.println("Enter password:");
            String password = sc.nextLine();
            
            System.out.println("\nSelect your role:");
            System.out.println("----------------------------------------");
            System.out.println("1. Admin");
            System.out.println("2. Content Creator");
            System.out.println("3. Market Analyst");
            System.out.println("----------------------------------------");
            
            role = sc.nextInt();
            sc.nextLine();
            
            saveNewUser(username, password, role);
        }

        User user;
        switch (role) {
            case 1:
                user = new Admin(username);
                break;
            case 2:
                user = new ContentCreator(username, "Nike", "Apple");
                break;
            case 3:
                user = new MarketAnalyst(username);
                break;
            default:
                System.out.println("Invalid role. Access denied.");
                sc.close();
                return;
        }
    
        if (user != null) {
            boolean continueRunning = true;
            while (continueRunning) {
                user.displayOptions();
    
                if (user instanceof MarketAnalyst) {
                    int analystAction = sc.nextInt();
                    sc.nextLine();
                    
                    switch (analystAction) {
                        case 1:
                            ((MarketAnalyst) user).handleInstagramAnalytics();
                            break;
                        case 2:
                            ((MarketAnalyst) user).handleXAnalytics();
                            break;
                        case 3:
                            ((MarketAnalyst) user).plotFollowerTrends();
                            break;
                        case 4:
                            System.out.println("\n========================================");
                            System.out.println("      Thank you for using the           ");
                            System.out.println("      Social Media Manager!            ");
                            System.out.println("========================================\n");
                            continueRunning = false;
                            break;
                        default:
                            System.out.println("\n----------------------------------------");
                            System.out.println("          Invalid Selection            ");
                            System.out.println("----------------------------------------");
                            System.out.println("Please choose a valid option (1-4)");
                            System.out.println("----------------------------------------\n");
                    }
                } else if (user instanceof ContentCreator) {
                    int creatorAction = sc.nextInt();
                    sc.nextLine(); 

                    switch (creatorAction) {
                        case 1:
                            ((ContentCreator) user).displayBrandAffiliations();
                            break;
                        case 2:
                            ((ContentCreator) user).displayProductSponsored();
                            break;
                        case 3:
                            ((ContentCreator) user).updateBrandAffiliations(sc);
                            break;
                        case 4:
                            ((ContentCreator) user).updateSponsoredProducts(sc);
                            break;
                        case 5:
                            System.out.println("\n----------------------------------------");
                            System.out.println("            Post Content                ");
                            System.out.println("----------------------------------------");
                            System.out.println("Enter content to post:");
                            String content = sc.nextLine();
                            System.out.println("Do you want to post on Instagram? (yes/no)");
                            boolean postToInstagram = sc.nextLine().equalsIgnoreCase("yes");
                            System.out.println("Do you want to post on X? (yes/no)");
                            boolean postToX = sc.nextLine().equalsIgnoreCase("yes");
                            ((ContentCreator) user).handlePostAction(content, postToInstagram, postToX);
                            break;
                        case 6:
                            continueRunning = false;
                            break;
                        default:
                            System.out.println("Invalid option.");
                    }
                } else if (user instanceof Admin) {
                    int adminAction = sc.nextInt();
                    sc.nextLine(); 
                    Admin admin = (Admin) user;
    
                    switch(adminAction) {
                        case 1:
                            System.out.println("Enter content to post:");
                            String content = sc.nextLine();
                            System.out.println("Do you want to post on Instagram? (yes/no)");
                            boolean postToInstagram = sc.nextLine().equalsIgnoreCase("yes");
                            System.out.println("Do you want to post on X? (yes/no)");
                            boolean postToX = sc.nextLine().equalsIgnoreCase("yes");
                            admin.handlePostAction(content, postToInstagram, postToX);
                            break;
    
                        case 2:
                            System.out.println("Enter post index to delete:");
                            int deleteIndex = sc.nextInt();
                            sc.nextLine(); 
                            admin.handleDeleteAction(deleteIndex);
                            break;
    
                        case 3:
                            System.out.println("Enter post index to comment on:");
                            int postIndex = sc.nextInt();
                            sc.nextLine();
                            System.out.println("Enter comment:");
                            String comment = sc.nextLine();
                            admin.handleCommentAction(postIndex, comment);
                            break;
    
                        case 4:
                            System.out.println("Enter username to follow:");
                            String followUser = sc.nextLine();
                            admin.handleFollowAction(followUser, sc);
                            break;
    
                        case 5:
                            System.out.println("Enter username to unfollow:");
                            String unfollowUser = sc.nextLine();
                            admin.handleUnfollowAction(unfollowUser, sc);
                            break;
                            
                        case 6:
                            admin.handleSchedulePost(sc);
                            break;
                            
                        case 7:
                            admin.viewAnalytics(sc);
                            break;
                            
                        case 8:
                            admin.generateReports(sc);
                            break;
    
                        case 9:
                            continueRunning = false;
                            break;
    
                        default:
                            System.out.println("Invalid option.");
                    }
    
                    if (adminAction != 9) {
                        admin.saveProfileDataToCSV();
                    }
                }
                
                if (continueRunning) {
                    System.out.println("\nPress Enter to continue...");
                    try {
                        sc.nextLine();
                    } catch (Exception e) {
                        // Handle cases where input is not available
                        System.out.println("Continuing...");
                    }
                    System.out.println("\n"); 
                }
            }
        }
    
        System.out.println("Thank you for using the Social Media Manager!");
        sc.close();
    }
}
