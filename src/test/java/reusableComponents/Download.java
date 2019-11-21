package reusableComponents;

import java.net.HttpURLConnection;
import java.net.URL;

public class Download 
{
	int respCode = 0;

	// testDownloadLinkResponse method is used to check the response code of the download link
	public void downloadLinkResponse(String url)
	{
		 HttpURLConnection huc = null;
		 
		 try {
		        huc = (HttpURLConnection)(new URL(url).openConnection());
		        huc.setRequestMethod("GET");
		        huc.connect();
		        respCode = huc.getResponseCode();
		        System.out.println("URL : " + huc.getURL());
		        System.out.println("Response Message : " + huc.getResponseMessage());
		        
		        // checking for response code 200, 400 and 401
				if (respCode == 200) {
					System.out.println("Download Link Is Working Properly\n-------------------------------"
							+ "------------------------------------------------------------");
				} else if (respCode == 404) {
					System.out.println("Link Is Broken\n--------------------------------------------------"
							+ "-------------------------------------------------------------");
				} else if (respCode == 401) {
					System.out.println("Authentication Error\n---------------------------------------------"
							+ "-------------------------------------------------------------");
				}
			} catch (Exception e) {
				System.out.println("Connection Error :- " + url + "\n---------------------------------------"
						+ "-----------------------------------------------------------------");
			}
	}
	
}
