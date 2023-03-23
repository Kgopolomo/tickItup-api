package za.co.tickItup.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import za.co.tickItup.api.entity.Role;
import za.co.tickItup.api.entity.RoleName;
import za.co.tickItup.api.entity.UserProfile;
import za.co.tickItup.api.repository.RoleRepository;
import za.co.tickItup.api.repository.UserProfileRepository;
import za.co.tickItup.api.request.LoginRequest;
import za.co.tickItup.api.response.JwtAuthenticationResponse;
import za.co.tickItup.api.utils.JWTUtil;

import java.util.*;

@Service
public class UserProfileService implements UserDetailsService {
    @Autowired private UserProfileRepository userProfileRepository;

    @Autowired private RoleRepository roleRepository;

    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private JavaMailSender javaMailSender;

    public UserProfile updateProfile(UserProfile userProfile) {
        UserProfile existingUser = userProfileRepository.findByUsername(userProfile.getUsername());
        if (userProfileRepository.existsByUsername(userProfile.getUsername())) {
            throw new UsernameNotFoundException("User not found with username: " + userProfile.getUsername());
        }
        // Update user fields
        existingUser.setFirstName(userProfile.getFirstName());
        existingUser.setLastName(userProfile.getLastName());
        existingUser.setEmail(userProfile.getEmail());
        existingUser.setPhoto(userProfile.getPhoto());
        existingUser.setUsername(userProfile.getUsername());

        // Encrypt updated user password before storing in database
        if (userProfile.getPassword() != null && !userProfile.getPassword().isEmpty()) {
            existingUser.setPassword(new BCryptPasswordEncoder().encode(userProfile.getPassword()));
        }

        return userProfileRepository.save(userProfile);
    }

    public UserProfile getProfile(Long userId) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(userId);
        return userProfile.orElse(null);
    }

    public UserProfile getUserByEmail(String email) {
        UserProfile userProfile = userProfileRepository.findByEmail(email);
        return userProfile;
    }

    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll();
    }

    public void deleteProfile(Long userId) {
        userProfileRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserProfile userProfile = userProfileRepository.findByUsername(username);
        if (userProfileRepository.existsByUsername(userProfile.getUsername())) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(userProfile.getUsername(), userProfile.getPassword(),
                new ArrayList<>());
    }

    public UserProfile createProfile(UserProfile userProfile) {

        // Validate user input and ensure that the user does not already exist
        if (userProfile.getUsername() == null || userProfile.getPassword() == null || userProfile.getEmail() == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }

        if (userProfileRepository.existsByUsername(userProfile.getUsername())) {
            throw new IllegalArgumentException("User with username " + userProfile.getUsername() + " already exists");
        }


        // Set default user role to ROLE_USER
        Role userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);
        Set<Role> role =new HashSet<>();
        role.add(userRole);
        userProfile.setRoles(role);
        roleRepository.save(userRole);



        // Encrypt user password before storing in database
        userProfile.setPassword(bCryptPasswordEncoder.encode(userProfile.getPassword()));
        return userProfileRepository.save(userProfile);

    }

    public JwtAuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = loadUserByUsername(loginRequest.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);
        JwtAuthenticationResponse token = new JwtAuthenticationResponse(jwt, "Bearer", userDetails);
        return token;
    }

    public UserProfile getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserProfile profile = userProfileRepository.findByUsername(userDetails.getUsername());
        return profile;
    }

    public UserProfile getUserProfile(String username) {
        return  userProfileRepository.findByUsername(username);
    }
}
