package es.uah.clientepeliculasactores;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
public class ClientePeliculasActoresApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientePeliculasActoresApplication.class, args);
    }

    @Bean
    public RestTemplate template() {
        RestTemplate template = new RestTemplate();
        return template;
    }

}
