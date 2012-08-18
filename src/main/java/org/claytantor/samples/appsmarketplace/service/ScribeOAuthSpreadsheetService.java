package org.claytantor.samples.appsmarketplace.service;

import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.claytantor.samples.appsmarketplace.mvc.UserController;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(value = "session")
public class ScribeOAuthSpreadsheetService {
    
    private static final Logger logger = Logger.getLogger(UserController.class);
    
    public static final String SCOPE = "https://spreadsheets.google.com/feeds";
    
	private OAuthService service;
	
	private String key;   
    private String authUrl;
    private Token authToken;
    private Token accessToken;
    
    @Value("${google.oauth2.clientId}")
    String apiKey;
    @Value("${google.oauth2.clientSecret}")
    String apiSecret;
    
    @PostConstruct
    public synchronized void init(){
        try {
            Thread.sleep(1);
            this.key = "auth-"+Calendar.getInstance().getTimeInMillis();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }       
    }

    public String getOAuthorizationURL(String forUrl) {
        service = new ServiceBuilder().provider(GoogleApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .scope(SCOPE)
                .callback(forUrl)
                .build();
        authToken = service.getRequestToken();
        authUrl = service.getAuthorizationUrl(authToken);
        return authUrl;
    }
    public Token getAuthToken() {
        return authToken;
    }
    public void authorizeWith(String oauth_verifier) {
        accessToken = service.getAccessToken(authToken, new Verifier(oauth_verifier));      
    }
    public Token getAccessToken() {
        return accessToken;
    }
    
    public String retrieveWorksheets(String spreadsheetName) {
         OAuthRequest request = new OAuthRequest(Verb.GET, "https://spreadsheets.google.com/feeds/worksheets/" + spreadsheetName + "/private/full");
         service.signRequest(accessToken, request);
         Response response = request.send();
         return response.getBody();      
    }
    
    
    public OAuthService getService() {
        return service;
    }



    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("key: "+key.toString());
        if(accessToken != null)
            buf.append(" accessToken: "+accessToken.toString());
        if(authUrl != null)
            buf.append(" authUrl: "+authUrl.toString());
        if(authToken != null)
            buf.append(" authToken: "+authToken.toString());
        return buf.toString();
    }
	

}
