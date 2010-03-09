package guestbook;

import com.google.appengine.api.datastore.Cursor;

import org.datanucleus.store.appengine.query.JDOCursorHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MailReportJob extends HttpServlet {
  private static final String REPORT_SUBJECT = "Hourly Greetings report";
  private static final String REPORT_RECIPIENT_NAME = "Mr. User";
  private static final String REPORT_RECIPIENT_EMAIL = "ikai.l+reporttest@google.com";
  private static final String REPORT_SENDER_NAME = "Report Admin";
  private static final String REPORT_SENDER_EMAIL = "ikai.lan@gmail.com";
  private static final Logger logger = Logger.getLogger(MailReportJob.class.getName());
  private static final long serialVersionUID = 1L;

  @Override
  @SuppressWarnings("unchecked")
  public void doGet(HttpServletRequest request, HttpServletResponse response) {

    // Find the first instance of a saved cursor
    PersistenceManager pm = PMF.get().getPersistenceManager();
    Query query = pm.newQuery(ReportCursor.class);
    query.setOrdering("timestamp DESC");
    query.setRange(0, 1);
    query.setUnique(true);

    ReportCursor reportCursor = (ReportCursor) query.execute();

    List<Greeting> newestGreetings;
    Query greetingsQuery = pm.newQuery(Greeting.class);

    if (reportCursor != null) {
      Cursor cursor = Cursor.fromWebSafeString(reportCursor.getSerializedCursor());
      Map<String, Object> extensionMap = new HashMap<String, Object>();
      extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
      greetingsQuery.setExtensions(extensionMap);
    }

    greetingsQuery.setRange(0, 100);
    newestGreetings = (List<Greeting>) greetingsQuery.execute();

    if (!newestGreetings.isEmpty()) {
      Cursor cursor = JDOCursorHelper.getCursor(newestGreetings);
      ReportCursor latestCursor = new ReportCursor();
      latestCursor.setSerializedCursor(cursor.toWebSafeString());
      pm.makePersistent(latestCursor);

    }

    mailReport(newestGreetings);

    pm.close();

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    doGet(request, response);
  }

  protected void mailReport(List<Greeting> greetings) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    StringBuilder builder = new StringBuilder();

    if (!greetings.isEmpty()) {

      for (Greeting greeting : greetings) {
        builder.append(greeting.toString());
        builder.append("\r\n");
      }

      String msgBody = builder.toString();

      try {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(REPORT_SENDER_EMAIL, REPORT_SENDER_NAME));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(REPORT_RECIPIENT_EMAIL,
            REPORT_RECIPIENT_NAME));
        msg.setSubject(REPORT_SUBJECT);
        msg.setText(msgBody);
        Transport.send(msg);
      } catch (AddressException e) {
        // ...
      } catch (MessagingException e) {
        // ...
      } catch (UnsupportedEncodingException e) {

      }
    } else {
      logger.info("No new greetings to send to admin in email report.");
    }
  }

}
