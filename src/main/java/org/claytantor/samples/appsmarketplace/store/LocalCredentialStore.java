package org.claytantor.samples.appsmarketplace.store;

import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.MemoryCredentialStore;

@Component
public class LocalCredentialStore extends MemoryCredentialStore {

}
