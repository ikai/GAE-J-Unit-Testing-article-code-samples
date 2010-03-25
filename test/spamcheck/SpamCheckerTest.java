package spamcheck;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import spamcheck.SpamChecker;


public class SpamCheckerTest {
  
  protected SpamChecker spamChecker;
  
  @Before public void setup() {
    this.spamChecker = new AkismetSpamCheckerImpl("f4bd6e8f7616", "http://www.ikaisays.com");
  }
  
  
  @Test public void verifyKey(){
    assertTrue("Key verification", spamChecker.verifyKey());
    
    SpamChecker invalidSpamChecker = new AkismetSpamCheckerImpl("invalidKey", "http://www.ikaisays.com");
    assertFalse("InvalidSpamChecker should not verify", invalidSpamChecker.verifyKey());
  }
  
  @Test public void isSpam(){
    assertTrue("viagra-test-123 should always result in spam", spamChecker.isSpam("4.2.2.4",  
              "Mozilla/5.0 (X11; U; Linux x86_64; en-US) AppleWebKit/532.9 (KHTML, like Gecko) Chrome/5.0.307.11 Safari/532.9", 
              "viagra-test-123"));
    
    
  }
  
  @Test public void testFactory() {
    SpamChecker spamChecker = SpamCheckerFactory.getSpamChecker();
    assertTrue(spamChecker != null);
    assertTrue(spamChecker.getApiKey().equals("123"));
  }
  
  
  


}
