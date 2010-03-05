package guestbook;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

@SuppressWarnings("unchecked")
public class GreetingTest {

  private final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  
  @Before
  public void setUp() throws Exception {
    helper.setUp();    
  }

  @After
  public void tearDown() throws Exception {
    helper.tearDown();
  }
  
  @Test
  public void testTrue() {
    assert(true);
  }
  
  @Test public void testFalse() {
    assertTrue(false);
  }
  
  @Test public void trivialThing() {
    assert(true);
  }
  
  @Test public void testSave() {
    Greeting g = new Greeting(null, "content", new Date());
    g.setContent("content");
    g.setDate(new Date());
    PersistenceManager pm = PMF.get().getPersistenceManager();
    pm.makePersistent(g);
    pm.close();
    
    pm = PMF.get().getPersistenceManager();
    List<Greeting> results = (List<Greeting>) pm.newQuery(Greeting.class).execute();
    
    assert(results.size() == 1);
    pm.close();
    
  }

}
