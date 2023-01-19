package com.example.security.service.impl;

import com.example.dto.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Users;
import com.example.repository.UserDAO;

import java.util.Date;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserDAO userDAO;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = userDAO.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		if(!user.getEnabled()){
			throw new LockedException("Your account is not verify!");
		}


		return UserDetailsImpl.build(user);
	}
	public static final int MAX_FAILED_ATTEMPTS = 5;

	private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours



	public void increaseFailedAttempts(Users user) {
		user.setFailedAttempt(user.getFailedAttempt() + 1);
		userDAO.save(user);
	}

	public void resetFailedAttempts(String username) {
		Users user = userDAO.findByUsername(username).orElseThrow(() -> new RuntimeException("USER NOT FOUND"));
		user.setAccountNonLocked(true);
		user.setLockTime(null);
		user.setFailedAttempt(0);
		userDAO.save(user);
	}

	public void lock(Users user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());

		userDAO.save(user);
	}

	public boolean unlockWhenTimeExpired(Users user) {
		long lockTimeInMillis = user.getLockTime().getTime();
		long currentTimeInMillis = System.currentTimeMillis();

		if (lockTimeInMillis + LOCK_TIME_DURATION <currentTimeInMillis) {
			user.setAccountNonLocked(true);
			user.setLockTime(null);
			user.setFailedAttempt(0);

			userDAO.save(user);

			return true;
		}

		return false;
	}
}