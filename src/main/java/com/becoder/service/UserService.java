package com.becoder.service;

import com.becoder.entity.User;

import java.util.List;

public interface UserService {

	public User saveUser(User user);

	public boolean existEmailCheck(String email);

    User findByEmail(String email);

	List<User> getAllUsers();

	void deleteUser(int userId);



}
