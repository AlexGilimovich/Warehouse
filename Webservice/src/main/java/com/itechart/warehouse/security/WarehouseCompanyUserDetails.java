package com.itechart.warehouse.security;

import com.itechart.warehouse.entity.Role;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
import netscape.security.Privilege;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Details about the authenticated user.
 */
public class WarehouseCompanyUserDetails implements UserDetails {

    private User user;

    public WarehouseCompanyUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //todo get authorities
//        return getGrantedAuthorities(user.getRoles());
        return null;
    }

    @Override
    public String getPassword() {
        if (user == null) return null;
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        if (user == null) return null;
        return user.getPassword();
    }

    public WarehouseCompany getCompany() {
        return user.getWarehouseCompany();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    private List<GrantedAuthority> getGrantedAuthorities(List<Role> roles) {
        if (roles == null) return null;
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return authorities;
    }
}