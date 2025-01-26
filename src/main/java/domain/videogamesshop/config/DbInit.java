package domain.videogamesshop.config;

import domain.videogamesshop.model.Role;
import domain.videogamesshop.model.User;
import domain.videogamesshop.repository.RoleRepository;
import domain.videogamesshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DbInit {

    @Bean
    public CommandLineRunner initRolesAndAdmin(RoleRepository roleRepository,
                                               UserRepository userRepository,
                                               PasswordEncoder passwordEncoder) {
        return args -> {
            // Проверяем, есть ли уже роль ADMIN
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                roleRepository.save(adminRole);
            }

            // Проверяем, есть ли уже роль VISITOR
            if (roleRepository.findByName("VISITOR").isEmpty()) {
                Role visitorRole = new Role();
                visitorRole.setName("VISITOR");
                roleRepository.save(visitorRole);
            }

            // Создаём учётку админа (если ещё нет)
            if (userRepository.findByLogin("admin").isEmpty()) {
                User adminUser = new User();
                adminUser.setLogin("admin");
                adminUser.setPassword(passwordEncoder.encode("admin")); // пароли хешируем
                adminUser.setRole(roleRepository.findByName("ADMIN").get());
                userRepository.save(adminUser);
            }
        };
    }
}
