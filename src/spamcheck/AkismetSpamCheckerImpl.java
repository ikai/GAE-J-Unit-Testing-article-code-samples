package spamcheck;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class AkismetSpamCheckerImpl implements SpamChecker {
  
  final String apiKey;
  final String blogUrl;
  
  public AkismetSpamCheckerImpl(String apiKey, String blogUrl) {
    this.apiKey = apiKey;
    this.blogUrl = blogUrl;
  }
  
  /* (non-Javadoc)
   * @see guestbook.SpamChecker#isSpam(java.lang.String, java.lang.String, java.lang.String)
   */
  public boolean isSpam(String userIp, String userAgent, String commentContent){
    String restUrl = "http://" + apiKey + ".rest.akismet.com/1.1/comment-check";

        try {
      URL url = new URL(restUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      
      OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
      writer.write(
                   "blog="              + URLEncoder.encode(blogUrl, "UTF-8") +
                   "&user_ip="          + URLEncoder.encode(userIp, "UTF-8") +
                   "&user_agent="       + URLEncoder.encode(userAgent, "UTF-8") +                   
                   "&comment_content="  + URLEncoder.encode(commentContent, "UTF-8")                    
                  );
      
      
      
      writer.close();
      
      if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        
        while(((line = reader.readLine()) != null)){

          if(line.equals("true")) {
            return true;
          } else if (line.equals("false")) {
            return false;
          }
        }
        reader.close();
        
      } 
      
    } catch (MalformedURLException e) {
      System.out.println(e);      
    } catch (IOException e) {
      System.out.println(e);
    }
    
    
    return false;

  }
  
  
  
  /* (non-Javadoc)
   * @see guestbook.SpamChecker#verifyKey()
   */
  public boolean verifyKey(){
    String restUrl = "http://rest.akismet.com/1.1/verify-key";
    try {
      URL url = new URL(restUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      
      OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
      writer.write("key=" + URLEncoder.encode(apiKey, "UTF-8") + 
                   "&blog=" + URLEncoder.encode(blogUrl, "UTF-8"));
      
      writer.close();
      
      if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        
        while(((line = reader.readLine()) != null)){
          if(line.equals("valid")) {
            return true;
          } else if (line.equals("invalid")) {
            return false;
          }
        }
        reader.close();
        
      } 
      
    } catch (MalformedURLException e) {
      
    } catch (IOException e) {
      
    }
    
    
    return false;
  }

  @Override
  public String getApiKey() {
    return apiKey;
  }

  @Override
  public String getBlogUrl() {
    return blogUrl;
  }

}
