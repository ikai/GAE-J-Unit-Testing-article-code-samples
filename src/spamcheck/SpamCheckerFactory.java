package spamcheck;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class SpamCheckerFactory {
  
  private static final String AKISMET_PROPERTIES_FILE = "akismet.properties";
  private static SpamChecker spamChecker = SpamCheckerFactory.initSpamChecker();
  
  public static SpamChecker getSpamChecker() {
    return spamChecker;
  }
   
  protected static SpamChecker initSpamChecker() {
    
    if(spamChecker == null) {
      Properties properties = new Properties();
      try {
        properties.load(new FileInputStream(AKISMET_PROPERTIES_FILE));
        String apiKey = properties.getProperty("akismet.apiKey");
        String blogUrl = properties.getProperty("akismet.blogUrl");
        
        spamChecker = new AkismetSpamCheckerImpl(apiKey, blogUrl);
      } catch (IOException e) {
        // SpamChecker not initialized. Initialize a NoopSpamChecker
        spamChecker = new NoopSpamChecker();
      }
    }
    
    return spamChecker;
  }
    
  private SpamCheckerFactory() {}
  
  public static class NoopSpamChecker implements SpamChecker {

    @Override
    public String getApiKey() {
      return null;
    }

    @Override
    public String getBlogUrl() {
      return null;
    }

    @Override
    public boolean isSpam(String userIp, String userAgent, String commentContent) {
      return false;
    }

    @Override
    public boolean verifyKey() {
      return true;
    }
    
  }
  
  
}
