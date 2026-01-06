package com.productmanagement.config;

import com.productmanagement.entity.Role;
import com.productmanagement.repository.RoleRepository;
import com.productmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.super-admin.username:superadmin}")
    private String superAdminUsername;

    @Value("${app.super-admin.password:superadmin123}")
    private String superAdminPassword;

    @Value("${app.super-admin.email:superadmin@example.com}")
    private String superAdminEmail;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Initialize roles if they don't exist
        if (roleRepository.findByName(Role.RoleType.USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(Role.RoleType.USER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName(Role.RoleType.ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(Role.RoleType.ADMIN);
            roleRepository.save(adminRole);
        }

        if (roleRepository.findByName(Role.RoleType.SUPER_ADMIN).isEmpty()) {
            Role superAdminRole = new Role();
            superAdminRole.setName(Role.RoleType.SUPER_ADMIN);
            roleRepository.save(superAdminRole);
        }

        // Create default Admin user
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            Role adminRole = roleRepository.findByName(Role.RoleType.ADMIN).orElseThrow();
            com.productmanagement.entity.User admin = new com.productmanagement.entity.User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail(adminEmail);
            admin.setFirstName("Admin");
            admin.setLastName("User");
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);
            userRepository.save(admin);
            System.out.println("DEBUG: Created default admin user: " + adminUsername);
        }

        // Create default Super Admin user
        if (userRepository.findByUsername(superAdminUsername).isEmpty()) {
            Role superAdminRole = roleRepository.findByName(Role.RoleType.SUPER_ADMIN).orElseThrow();
            com.productmanagement.entity.User superAdmin = new com.productmanagement.entity.User();
            superAdmin.setUsername(superAdminUsername);
            superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Admin");
            Set<Role> roles = new HashSet<>();
            roles.add(superAdminRole);
            superAdmin.setRoles(roles);
            userRepository.save(superAdmin);
            System.out.println("DEBUG: Created default superadmin user: " + superAdminUsername);
        }



    }
}
