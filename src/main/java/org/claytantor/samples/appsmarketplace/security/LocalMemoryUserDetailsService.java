package org.claytantor.samples.appsmarketplace.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;



@Component(value="localUserDetailsService")
public class LocalMemoryUserDetailsService implements UserDetailsService {
    
    /**
     * bypass any store, assume provider has properly authenticated and
     * give roles
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException, DataAccessException {       
        String[] sa = {"ROLE_USER"};
        List<GrantedAuthority> authoritiesList = new ArrayList<GrantedAuthority>();
        for (int i = 0; i < sa.length; i++) {
            GrantedAuthority ga = new GrantedAuthorityImpl(sa[i]);
            authoritiesList.add(ga);
        }       
        User u = new User(username,"",true,true,true,true,authoritiesList);
        return u;
        
    }


}
