package socialmedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ContentCreator extends Admin{
	
	private List<String> brandAffiliations;
    
    private List<String> productSponsored;
    
    private static final String CREATOR_DATA_FILE = "profiles/creator_data.csv";

    
    public ContentCreator(String username, String... brands) {
        super(username);
        this.brandAffiliations = new ArrayList<>();
        this.productSponsored = new ArrayList<>();
        if (brands != null && brands.length > 0) {
            this.brandAffiliations.addAll(Arrays.asList(brands));
        }
        loadCreatorData();
    }

    private void loadCreatorData() {
        try {
            File dir = new File("profiles");
            if (!dir.exists()) dir.mkdirs();
            
            File csvFile = new File(CREATOR_DATA_FILE);
            if (!csvFile.exists()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
                writer.write("Username,Type,Items");
                writer.newLine();
                writer.close();
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 3 && data[0].equals(username)) {
                    if (data[1].equals("BRANDS")) {
                        brandAffiliations = new ArrayList<>(Arrays.asList(data[2].split(";")));
                    } else if (data[1].equals("PRODUCTS")) {
                        productSponsored = new ArrayList<>(Arrays.asList(data[2].split(";")));
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error loading creator data: " + e.getMessage());
        }
    }

    private void saveCreatorData() {
        try {
          
            Map<String, Map<String, String>> existingData = new HashMap<>();
            File csvFile = new File(CREATOR_DATA_FILE);
            if (csvFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(csvFile));
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] data = line.split(",");
                    if (data.length >= 3 && !data[0].equals(username)) {
                        existingData.computeIfAbsent(data[0], k -> new HashMap<>())
                                  .put(data[1], data[2]);
                    }
                }
                reader.close();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
            writer.write("Username,Type,Items");
            writer.newLine();

            writer.write(String.format("%s,BRANDS,%s", username, 
                String.join(";", brandAffiliations)));
            writer.newLine();
            writer.write(String.format("%s,PRODUCTS,%s", username, 
                String.join(";", productSponsored)));
            writer.newLine();

            for (Map.Entry<String, Map<String, String>> entry : existingData.entrySet()) {
                String user = entry.getKey();
                Map<String, String> userData = entry.getValue();
                for (Map.Entry<String, String> typeData : userData.entrySet()) {
                    writer.write(String.format("%s,%s,%s", user, typeData.getKey(), typeData.getValue()));
                    writer.newLine();
                }
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving creator data: " + e.getMessage());
        }
    }

    @Override
    public void displayOptions() {
        System.out.println("\n========================================");
        System.out.println("        Content Creator Dashboard       ");
        System.out.println("========================================\n");
        
        System.out.println("Choose an action:");
        System.out.println("----------------------------------------");
        System.out.println("1. Display Brand Affiliations");
        System.out.println("2. Display Sponsored Products");
        System.out.println("3. Update Brand Affiliations");
        System.out.println("4. Update Sponsored Products");
        System.out.println("5. Post Content");
        System.out.println("6. Exit");
        System.out.println("----------------------------------------\n");
    }

    public void displayBrandAffiliations() {
        System.out.println("\n----------------------------------------");
        System.out.println("         Brand Affiliations            ");
        System.out.println("----------------------------------------");
        if (brandAffiliations.isEmpty()) {
            System.out.println("No brand affiliations yet.");
        } else {
            System.out.println(String.join(", ", brandAffiliations));
        }
        System.out.println("----------------------------------------\n");
    }

    public void displayProductSponsored() {
        System.out.println("\n----------------------------------------");
        System.out.println("         Sponsored Products            ");
        System.out.println("----------------------------------------");
        if (productSponsored.isEmpty()) {
            System.out.println("No sponsored products yet.");
        } else {
            System.out.println(String.join(", ", productSponsored));
        }
        System.out.println("----------------------------------------\n");
    }

    public void updateBrandAffiliations(Scanner sc) {
        System.out.println("\n----------------------------------------");
        System.out.println("       Update Brand Affiliations        ");
        System.out.println("----------------------------------------");
        System.out.println("Current affiliations: " + String.join(", ", brandAffiliations));
        System.out.println("Enter new brand affiliations (separated by comma):");
        String input = sc.nextLine();
        brandAffiliations = Arrays.asList(input.split("\\s*,\\s*"));
        saveCreatorData();
        System.out.println("Brand affiliations updated successfully!");
        System.out.println("----------------------------------------\n");
    }

    // Overloaded method to update brand affiliations using varargs
    public void updateBrandAffiliations(String... brands) {
        brandAffiliations = Arrays.asList(brands);
        saveCreatorData();
        System.out.println("Brand affiliations updated successfully!");
    }

    public void updateSponsoredProducts(Scanner sc) {
        System.out.println("\n----------------------------------------");
        System.out.println("      Update Sponsored Products         ");
        System.out.println("----------------------------------------");
        System.out.println("Current products: " + String.join(", ", productSponsored));
        System.out.println("Enter new sponsored products (separated by comma):");
        String input = sc.nextLine();
        productSponsored = Arrays.asList(input.split("\\s*,\\s*"));
        saveCreatorData();
        System.out.println("Sponsored products updated successfully!");
        System.out.println("----------------------------------------\n");
    }

}
