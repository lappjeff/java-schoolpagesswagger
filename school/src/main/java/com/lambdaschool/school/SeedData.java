package com.lambdaschool.school;


import com.lambdaschool.school.model.Role;
import com.lambdaschool.school.model.User;
import com.lambdaschool.school.model.UserRoles;
import com.lambdaschool.school.repository.RoleRepository;
import com.lambdaschool.school.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Transactional
@Component
public class SeedData implements CommandLineRunner
{
	private RoleRepository rolerepos;
	private UserRepository userrepos;

	public SeedData(RoleRepository rolerepos, UserRepository userrepos)
	{
		this.rolerepos = rolerepos;
		this.userrepos = userrepos;
	}

	@Override
	public void run(String[] args) throws Exception
	{
		Role r1 = new Role("user");

		ArrayList<UserRoles> users = new ArrayList<>();
		users.add(new UserRoles(new User(), r1));


		rolerepos.save(r1);

		User u2 = new User("admin", "password", users);

		userrepos.save(u2);
	}
}