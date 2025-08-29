package socialmedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class InstagramAnalysis extends MarketAnalysis {
	
	private int followers;

    @Override
    public void extractMetrics(File csvFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        String line = br.readLine(); 
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length >= 4) {
                try {
                    int currentFollowers = Integer.parseInt(data[2]);
                    followers += currentFollowers;
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid followers count: " + data[2]);
                }
            }
        }
        followers /= 10;
        br.close();
    }

    @Override
    public int getFollowerCount() {
        return followers;
    }

}
