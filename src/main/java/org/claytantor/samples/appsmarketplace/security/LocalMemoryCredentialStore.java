package org.claytantor.samples.appsmarketplace.security;

import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.MemoryCredentialStore;

@Component
public class LocalMemoryCredentialStore extends MemoryCredentialStore {

}
