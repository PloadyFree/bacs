package istu.bacs.service;

import istu.bacs.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
	
	User findById(int userId);
	void register(User user) throws UsernameAlreadyInUseException;
	
}