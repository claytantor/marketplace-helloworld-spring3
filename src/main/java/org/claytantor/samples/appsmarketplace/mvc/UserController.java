package org.claytantor.samples.appsmarketplace.mvc;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.claytantor.samples.appsmarketplace.security.LocalCredentialStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.Calendar.Builder;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;


@Controller
@RequestMapping(value="/user")
public class UserController {
    
    
    private static final Logger logger = Logger.getLogger(UserController.class);
    
    @Autowired LocalCredentialStore localCredentialStore;
    
    @Value("${google.oauth2.clientId}")
    private String clientId;
    
    @Value("${google.oauth2.clientSecret}")
    private String clientSecret;
    
    private static final long serialVersionUID = 1L;
    
    //com.google.api.services.calendar.Calendar client;
    
    final HttpTransport transport = new NetHttpTransport();

    final JsonFactory jsonFactory = new JacksonFactory();
        

    /** Lock on the flow and credential. */
    private final Lock lock = new ReentrantLock();

    /** Persisted credential associated with the current request or {@code null} for none. */
    private Credential credential;

    /**
     * Authorization code flow to be used across all HTTP servlet requests or {@code null} before
     * initialized in {@link #initializeFlow()}.
     */
    private AuthorizationCodeFlow flow;
           
    
    @RequestMapping(value="/home", method=RequestMethod.GET)
    public String home(Model model) {
        
        logger.debug("home");
        
        //see if the user is a member
        UserDetails user = 
            (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        model.addAttribute("user", user);    
        
        return "home";
    }
    
    
    @RequestMapping(value="/calendar", method=RequestMethod.GET)
    public String auth(Model model, HttpServletRequest req, HttpServletResponse resp) {
        
        logger.debug("auth");
        
        lock.lock();
        try {
          // load credential from persistence store
          String userId = getUserId(req);
          if (flow == null) {
            flow = initializeFlow();
          }
          credential = flow.loadCredential(userId);
          // if credential found with an access token, invoke the user code
          if (credential != null && credential.getAccessToken() != null) {
              model.addAttribute("status", "authorized");
              
                
              Builder builder = 
                  new Builder(credential.getTransport(), jsonFactory, credential);
              Calendar client = builder.build();
              CalendarList feed = client.calendarList().list().execute();
              List<CalendarListEntry> entries = feed.getItems();
              model.addAttribute("entries", entries);
                                                
              return "calendar";
              
          }
          // redirect to the authorization flow
//          String redirectUri = getRedirectUri(req);
          credential = null;
          return "redirect:https://accounts.google.com/o/oauth2/auth?response_type=code&redirect_uri=http://dev.welocally.com:8088/helloworld/oauth2callback&state=/user/calendar&client_id="+clientId+"&scope="+CalendarScopes.CALENDAR;
     
        } 
        catch (ServletException e) {
            logger.error("problem", e);
            return "error";
        } 
        catch (IOException e) {
            logger.error("problem", e);
            return "error";
        } 
        finally {
          lock.unlock();
        }       
        
    }
    
    
  
    
   
    
//    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
//      GenericUrl url = new GenericUrl(req.getRequestURL().toString());
//      url.setRawPath("/helloworld/oauth2callback");
//      return url.build();
//    }

    protected AuthorizationCodeFlow initializeFlow() throws IOException {
//      return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new JacksonFactory(),
//          clientId, clientSecret,
//          Collections.singleton(CalendarScopes.CALENDAR)).setCredentialStore(
//          new JdoCredentialStore(JDOHelper.getPersistenceManagerFactory("transactions-optional")))
//          .build();
        
      return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new JacksonFactory(),
      clientId, clientSecret,
      Collections.singleton(CalendarScopes.CALENDAR)).setCredentialStore(
              localCredentialStore)
      .build();
        
        
    }

    /** Returns the user ID for the given HTTP servlet request. */
    protected  String getUserId(HttpServletRequest req) throws ServletException, IOException{
      //see if the user is a member
        UserDetails user = 
            (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername();
    }

    /**
     * Return the persisted credential associated with the current request or {@code null} for none.
     */
    protected final Credential getCredential() {
      return credential;
    }
    
    /**
     * Handles the actual query for the Google Calendar API.  This is a simple query
     * that checks the user's default calendar for the next event (limit 1, sort by start
     * time).  It also uses 2-legged OAuth (xoauth_requestor_id) to impersonate the user
     * rather than using an individual access token.
     *
     * @param userEmail User to request data for
     * @return Next calendar event, null if none found
     * @throws IOException If error retrieving data
     * @throws ServiceException If error retrieving data
     */
//    private CalendarEventEntry nextEvent(String userId) throws IOException, ServiceException {
//        URL feedUrl = new URL("https://www.google.com/calendar/feeds/default/private/full");
//        CalendarQuery query = new CalendarQuery(feedUrl);
//        query.setMinimumStartTime(DateTime.now());
//        query.setMaxResults(1);
//        query.addCustomParameter(new Query.CustomParameter("orderby", "starttime"));
//        query.addCustomParameter(new Query.CustomParameter("sortorder","a"));
//        query.addCustomParameter(new Query.CustomParameter("singleevents","true"));
//        query.addCustomParameter(new Query.CustomParameter("xoauth_requestor_id", userId));
//
//        CalendarEventFeed results = calendarService.query(query, CalendarEventFeed.class);
//        if (!results.getEntries().isEmpty()) {
//            return results.getEntries().get(0);
//        } else {
//            return null;
//        }
//    }

}
