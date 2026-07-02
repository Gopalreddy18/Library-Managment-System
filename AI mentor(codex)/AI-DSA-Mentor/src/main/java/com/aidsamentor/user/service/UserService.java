package com.aidsamentor.user.service;

import com.aidsamentor.user.dto.UserResponse;
import java.security.Principal;

public interface UserService {

    UserResponse getCurrentUser(Principal principal);
}
