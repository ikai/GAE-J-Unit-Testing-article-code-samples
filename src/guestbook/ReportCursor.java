package guestbook;

import com.google.appengine.api.datastore.Key;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.listener.StoreCallback;

@PersistenceCapable
public class ReportCursor implements StoreCallback {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) 
  private Key key;
  
  @Persistent
  private String serializedCursor;
  
  @Persistent
  private Date timestamp;

  public Key getKey() {
    return key;
  }

  public String getSerializedCursor() {
    return serializedCursor;
  }

  public void setSerializedCursor(String serializedCursor) {
    this.serializedCursor = serializedCursor;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  @Override
  public void jdoPreStore() {
    this.timestamp = new Date();
    
  }
  
}
