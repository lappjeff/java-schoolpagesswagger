package com.lambdaschool.school.service;

import com.lambdaschool.school.model.Role;
import com.lambdaschool.school.model.User;
import com.lambdaschool.school.model.UserRoles;
import com.lambdaschool.school.repository.RoleRepository;
import com.lambdaschool.school.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Transactional
@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService
{

	@Autowired
	private UserRepository userRepos;
	@Autowired
	private RoleRepository roleRepos;

	@Override
	public User findUserByUsername(String username) throws UsernameNotFoundException
	{
		User user = userRepos.findByUsername(username);
		if(user == null)
		{
			throw new UsernameNotFoundException("Invalid username or password");
		}
		return user;
	}

	@Override
	public List<User> findAll()
	{
		ArrayList<User> users = new ArrayList<>();
		userRepos.findAll().iterator().forEachRemaining(users::add);
		return users;
	}

	@Override
	public User findUserById(long id) throws EntityNotFoundException
	{
		return userRepos.findById(id).orElseThrow(() -> new EntityNotFoundException(Long.toString(id)));
	}

	@Override
	public void delete(long id)
	{
		if(userRepos.findById(id).isPresent())
		{
			userRepos.deleteUserRole(id);
			userRepos.deleteById(id);
		} else
		{
			throw new EntityNotFoundException(Long.toString(id));
		}
	}

	@Transactional
	@Override
	public User save(User user)
	{
		User newUser = new User();

		newUser.setUsername(user.getUsername());
		newUser.setPlainTextPass(user.getPassword());

		ArrayList<UserRoles> newRoles = new ArrayList<>();


		Role newRole = roleRepos.findByRolename("admin");
		newRoles.add(new UserRoles(newUser, newRole));

		newUser.setUserRoles(newRoles);

		return userRepos.save(newUser);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		User user = userRepos.findByUsername(username);

		if (user == null)
		{
			throw new UsernameNotFoundException("Invalid username or password.");
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthority());
	}

	public User getCurrentUser(Principal principal)
	{
		User currentUser = findUserByUsername(principal.getName());
		return currentUser;
	}
}