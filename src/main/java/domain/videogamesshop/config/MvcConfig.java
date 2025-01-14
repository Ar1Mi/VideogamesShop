package domain.videogamesshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Задаём путь, по которому будут доступны файлы из папки uploads
        // "file:uploads/" означает, что берем файлы из физической папки "uploads" в корне проекта
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
