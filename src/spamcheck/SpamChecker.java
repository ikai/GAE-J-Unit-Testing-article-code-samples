package spamcheck;



public interface SpamChecker {

  public abstract boolean isSpam(String userIp, String userAgent, String commentContent);

  public abstract boolean verifyKey();
  
  public abstract String getApiKey();
  
  public abstract String getBlogUrl();

}
