/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.services;


import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author PaoloDooM
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if ("sppcontroller".equals(username)) {
			return new User("sppcontroller", "$2a$10$v3F9d4Vw1f9HQ89jIxPzR.aeF03ZpZLqpSccV0Xeui2lfW5KPBxdG",
					new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}
}
