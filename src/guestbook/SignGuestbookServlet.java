package guestbook;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import spamcheck.SpamChecker;
import spamcheck.SpamCheckerFactory;

import java.io.IOException;
import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignGuestbookServlet extends HttpServlet {
  
    protected final SpamChecker spamChecker = SpamCheckerFactory.getSpamChecker();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String content = req.getParameter("content");
        Date date = new Date();
        
        String userAgent = req.getHeader("User-Agent");
        String userIp = req.getRemoteAddr();
        
        if(!spamChecker.isSpam(userIp, userAgent, content)) {
                
          Greeting greeting = new Greeting(user, content, date);

          PersistenceManager pm = PMF.get().getPersistenceManager();
          try {
              pm.makePersistent(greeting);
          } finally {
              pm.close();
          }
          resp.sendRedirect("/view");          
        } else {
          resp.sendRedirect("/view?spam=true");
        }

         
    }
}