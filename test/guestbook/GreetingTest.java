package guestbook;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import org.easymock.EasyMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spamcheck.SpamChecker;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

@SuppressWarnings("unchecked")
public class GreetingTest {

  private final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  
  private SpamChecker spamCheckerMock;
  private Greeting greeting;
  private PersistenceManager pm;
  
  
  @Before
  public void setUp() throws Exception {
    helper.setUp();
    spamCheckerMock = EasyMock.createMock(SpamChecker.class);
    greeting = new Greeting(null, "content", new Date());
    greeting.setSpamChecker(spamCheckerMock);

    pm = PMF.get().getPersistenceManager();
    
  }

  @After
  public void tearDown() throws Exception {
    pm.close();
    helper.tearDown();
    
  }
  
  @Test public void testSave() {

    pm.makePersistent(greeting);
    pm.close();
    
    pm = PMF.get().getPersistenceManager();
    List<Greeting> results = (List<Greeting>) pm.newQuery(Greeting.class).execute();
    
    assert(results.size() == 1);
    
  }
  
  @Test public void testSpamChecker() throws Exception {
    greeting.setUserIp("127.0.0.1");
    greeting.setUserAgent("Chrome");
    
    // Let's make sure we set the Greeting isSpam to false if SpamChecker returns false
    EasyMock.expect(spamCheckerMock.isSpam(greeting.getUserIp(), 
        greeting.getUserAgent(), 
        greeting.getContent())).andReturn(false);
    EasyMock.replay(spamCheckerMock);
    
    pm.makePersistent(greeting);
    
    assertFalse(greeting.isSpam());
    EasyMock.verify(spamCheckerMock);

    // Now let's make sure we set the Greeting isSpam to false if SpamChecker returns true
    setUp();
    
    EasyMock.expect(spamCheckerMock.isSpam(greeting.getUserIp(), 
        greeting.getUserAgent(), 
        greeting.getContent())).andReturn(true);
    EasyMock.replay(spamCheckerMock);
    
    pm.makePersistent(greeting);
    
    assertTrue(greeting.isSpam());
    EasyMock.verify(spamCheckerMock);

       
    
  }

}
