package org.claytantor.samples.appsmarketplace.mvc;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.claytantor.samples.appsmarketplace.security.LocalMemoryCredentialStore;
import org.claytantor.samples.appsmarketplace.security.LocalMemoryUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;


@Controller
@RequestMapping(value="/oauth2callback")
public class UserCallbackController {
    
    
    private static final Logger logger = Logger.getLogger(UserCallbackController.class);
    

    @Autowired LocalMemoryCredentialStore localMemoryCredentialStore;
    @Autowired LocalMemoryUserDetailsService localMemoryUserDetailsService;
    
    private static final long serialVersionUID = 1L;

    /** Lock on the flow and credential. */
    private final Lock lock = new ReentrantLock();

    /**
     * Authorization code flow to be used across all HTTP servlet requests or {@code null} before
     * initialized in {@link #initializeFlow()}.
     */
    private AuthorizationCodeFlow flow;
    
    @Value("${google.oauth2.clientId}")
    private String clientId;
    
    @Value("${google.oauth2.clientSecret}")
    private String clientSecret;
    
    @Value("${google.oauth2.callback}")
    private String callback;
           
    
    @RequestMapping(method=RequestMethod.GET)
    public String callback(HttpServletRequest req, HttpServletResponse resp) {
        
        logger.debug("callback");
        
        try {
            StringBuffer buf = req.getRequestURL();
            if (req.getQueryString() != null) {
              buf.append('?').append(req.getQueryString());
            }
            AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(buf.toString());
            String code = responseUrl.getCode();
            if (responseUrl.getError() != null) {
              onError(req, resp, responseUrl);
            } else if (code == null) {
              resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
              resp.getWriter().print("Missing authorization code");
            } else {
              String redirectUri = getRedirectUri(req);
              lock.lock();
              try {
                if (flow == null) {
                  flow = initializeFlow();
                }
                TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
                String userId = getUserId(req);
                Credential credential = flow.createAndStoreCredential(response, userId);  
                
                return "redirect:/user/calendar";
                
                
              } finally {
                lock.unlock();
              }
            }
        } catch (ServletException e) {
           logger.error("problem", e);
        } catch (IOException e) {
           logger.error("problem", e);
        }
        
        
        return "home";
    }
    
    protected AuthorizationCodeFlow initializeFlow() throws IOException {        
      return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new JacksonFactory(),
      clientId, clientSecret,
      Collections.singleton(CalendarScopes.CALENDAR)).setCredentialStore(
              localMemoryCredentialStore)
      .build();
             
    }

    /** Returns the redirect URI for the given HTTP servlet request. */
    protected  String getRedirectUri(HttpServletRequest req)
        throws ServletException, IOException{
        return this.callback;
    }

    /** Returns the user ID for the given HTTP servlet request. */
    protected  String getUserId(HttpServletRequest req) throws ServletException, IOException{

        UserDetails user = 
            (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername();
    }
 
    
    /**
     * Handles a successfully granted authorization.
     *
     * <p>
     * Default implementation is to do nothing, but subclasses should override and implement. Sample
     * implementation:
     * </p>
     *
     * <pre>
        resp.sendRedirect("/granted");
     * </pre>
     *
     * @param req HTTP servlet request
     * @param resp HTTP servlet response
     * @param credential credential
     * @throws ServletException HTTP servlet exception
     * @throws IOException some I/O exception
     */
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
        throws ServletException, IOException {
    }

    /**
     * Handles an error to the authorization, such as when an end user denies authorization.
     *
     * <p>
     * Default implementation is to do nothing, but subclasses should override and implement. Sample
     * implementation:
     * </p>
     *
     * <pre>
        resp.sendRedirect("/denied");
     * </pre>
     *
     * @param req HTTP servlet request
     * @param resp HTTP servlet response
     * @param errorResponse error response ({@link AuthorizationCodeResponseUrl#getError()} is not
     *        {@code null})
     * @throws ServletException HTTP servlet exception
     * @throws IOException some I/O exception
     */
    protected void onError(
        HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
        throws ServletException, IOException {
    }

}
