package org.claytantor.samples.appsmarketplace.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.claytantor.samples.appsmarketplace.security.LocalMemoryCredentialStore;
import org.claytantor.samples.appsmarketplace.service.ScribeOAuthSpreadsheetService;
import org.scribe.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(value="/user")
@Scope("request")
public class UserController {
    
    
    private static final Logger logger = Logger.getLogger(UserController.class);
    
    @Autowired LocalMemoryCredentialStore localMemoryCredentialStore;

    @Value("${google.oauth2.callbackPrefix}")
    private String callbackPrefix;
     
    @Autowired ScribeOAuthSpreadsheetService authService;
    
    @RequestMapping(value="/home", method=RequestMethod.GET)
    public String home(Model model) {
        
        logger.debug("home");
        
        //see if the user is a member
        UserDetails user = 
            (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        model.addAttribute("user", user);    
        
        return "home";
    }
    
    @RequestMapping(value="/spreadsheet", method=RequestMethod.GET)
    public String spreadsheet(
            @RequestParam String key, 
            @RequestParam(value = "oauth_verifier", required = false) String oauth_verifier,
            Model model, 
            HttpServletRequest request, 
            HttpServletResponse resp) {
        
        logger.debug("spreadsheet auth:"+authService.toString());
        
             
        Token accessToken = authService.getAccessToken();
        if (accessToken == null) {
                  
            //there is a better way
            String callbackURL = callbackPrefix+"/user/spreadsheet?key="+key;
            
            if (oauth_verifier == null) {
                String authUrl = authService.getOAuthorizationURL(callbackURL);                
                return "redirect:" + authUrl;
            } else {
                authService.authorizeWith(oauth_verifier);
                return "redirect:" + callbackURL;
            }
        }
        
        model.addAttribute("data", authService.retrieveWorksheets(key));
      
        return "spreadsheet";
              
    }
    


    /** Returns the user ID for the given HTTP servlet request. */
    protected  String getUserId(HttpServletRequest req) throws ServletException, IOException{
        UserDetails user = 
            (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername();
    }

   

}
