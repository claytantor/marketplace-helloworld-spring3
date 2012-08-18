marketplace-helloworld-spring3
==============================

a working fully integrated example of a oauth and openid integration with google suitable for a marketplace app backbone, includes spreadsheet feed call.

Scribe OAuth
----------
Scribe libraries are far superior to the extensive dependencies required when you use the google oauth libraries and feed apis. We reccomend that you keep it simple and deserialize yourself and stay away from all the api libraries and clients they provide. There is alot to say about the way they approached those apis, and not much is good.

[Scribe OAuth on Github](https://github.com/fernandezpablo85/scribe-java) 

## Notes
the application.properties file needs to be in the classpath, an example of what you need is the client id and secret for your app key. You will also want to put the "callbackPrefix" in the properties, this is because tuckey rewrite will confuse the callback if you try to use what is in the request.

    google.oauth2.clientId=97350962347-ou4vfsjded8c3ir90e7tlpqp9puvapjn.apps.googleusercontent.com
    google.oauth2.clientSecret=fS5e16cEMrNE66sTDs2zpZ-J 
    google.oauth2.callbackPrefix=http://dev.marketapp.com:8088/helloworld/