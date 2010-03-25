package guestbook;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

import spamcheck.SpamChecker;
import spamcheck.SpamCheckerFactory;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.listener.StoreCallback;

@PersistenceCapable
public class Greeting implements StoreCallback {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private User author;

    @Persistent
    private String content;

    @Persistent
    private Date date;
    
    @Persistent
    private String userIp;
    
    @Persistent
    private String userAgent;
    
    @Persistent
    private boolean isSpam;
    
    @NotPersistent
    private SpamChecker spamChecker = SpamCheckerFactory.getSpamChecker();
    

    public Greeting(User author, String content, Date date) {
        this.author = author;
        this.content = content;
        this.date = date;
    }
    
    public Key getKey() {
        return key;
    }

    public User getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserIp() {
      return userIp;
    }

    public void setUserIp(String userIp) {
      this.userIp = userIp;
    }

    public String getUserAgent() {
      return userAgent;
    }

    public void setUserAgent(String userAgent) {
      this.userAgent = userAgent;
    }

    public boolean isSpam() {
      return isSpam;
    }

    public void setSpam(boolean isSpam) {
      this.isSpam = isSpam;
    }

    public SpamChecker getSpamChecker() {
      return spamChecker;
    }

    public void setSpamChecker(SpamChecker spamChecker) {
      this.spamChecker = spamChecker;
    }

    @Override
    public void jdoPreStore() {
       this.isSpam = this.spamChecker.isSpam(userIp, userAgent, content);  
    }
        
}