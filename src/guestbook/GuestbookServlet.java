package guestbook;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.datanucleus.store.appengine.query.JDOCursorHelper;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class GuestbookServlet extends HttpServlet {
	protected static final int PAGE_SIZE = 3;
	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		if(user == null) {
			String loginUrl = userService.createLoginURL(request.getRequestURI());
			request.setAttribute("loginUrl", loginUrl);
		} else {
			String logoutUrl = userService.createLogoutURL(request.getRequestURI());
			request.setAttribute("logoutUrl", logoutUrl);
			request.setAttribute("nickname", user.getNickname());
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Greeting.class);
		
		/*
		 * Now let's deserialize the cursor if we've been passed one
		 */
		String pageCursor = request.getParameter("page");
		if(pageCursor != null) {
			// TODO: Catch exception
			Cursor cursor = Cursor.fromWebSafeString(pageCursor);
			Map<String, Object> extensionMap = new HashMap<String, Object>();
			extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
			query.setExtensions(extensionMap);
		}
		
		query.setRange(0, PAGE_SIZE);
		

	    List<Greeting> greetings = (List<Greeting>) query.execute();
	        	
	    if(!greetings.isEmpty()) {
    		Cursor cursor = JDOCursorHelper.getCursor(greetings);
    		
    		Query lookaheadQuery = pm.newQuery(Greeting.class);
    		
			Map<String, Object> extensionMap = new HashMap<String, Object>();
			extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
    		lookaheadQuery.setExtensions(extensionMap);
    		lookaheadQuery.setUnique(true);
    		lookaheadQuery.setRange(0, 1);
    		Greeting nextGreeting = (Greeting) lookaheadQuery.execute();
	    		
    		if(nextGreeting != null) {
    			String nextPageCursor = cursor.toWebSafeString();
    			request.setAttribute("nextCursor", nextPageCursor);
    		}

	    }
	    
	    pm.close();
	    
	    request.setAttribute("greetings", greetings);
	    request.getRequestDispatcher("WEB-INF/guestbook.jsp").forward(request, response);
	    
	    
    }
}
    