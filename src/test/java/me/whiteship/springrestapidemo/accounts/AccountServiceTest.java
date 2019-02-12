package me.whiteship.springrestapidemo.accounts;

import org.assertj.core.util.Sets;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRepository accountRepository;
	@Test
	public void findByUsername() {
		// Given

		Set<AccountRole> userAccountRoles = Stream.of(AccountRole.ADMIN, AccountRole.USER).collect(Collectors.toSet());
		String password = "keesun";
		String username = "keesun@email.com";
		Account account = Account.builder()
				.email(username)
				.password(password)
				.roles(userAccountRoles)
				.build();
		this.accountRepository.save(account);

		// When
		UserDetailsService userDetailsService = this.accountService;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		// Then
		assertThat(userDetails.getPassword()).isEqualTo(password);
	}

	@Test
	public void findByUsernameFail_notFound() {
		String username = "random@email.com";
		expectedException.expect(UsernameNotFoundException.class);
		expectedException.expectMessage(Matchers.containsString(username));

		this.accountService.loadUserByUsername(username);
	}
}