package com.ds.ws.health.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.ws.health.domain.User;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
	
	

}
