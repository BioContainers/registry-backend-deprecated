package pro.biocontainers.readers.quayio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import pro.biocontainers.readers.quayio.model.ListShortContainers;
import pro.biocontainers.readers.quayio.model.ShortQuayIOContainer;
import pro.biocontainers.readers.quayio.services.QueryQuayIOService;

import java.util.List;

@SpringBootApplication
public class QuayIOReaderApp {

    private static final Logger log = LoggerFactory.getLogger(QuayIOReaderApp.class);

    @Value("${quay.io.token}")
    private String accessToken;

    public static void main(String args[]) {
        SpringApplication.run(QuayIOReaderApp.class);
    }

    @Bean
    public CommandLineRunner run(RestTemplateBuilder builder) throws Exception {
        String accessToken = "bdxE6pcOcmd0YZUAmpsW8gO8yw7JEKirISxTQqBQ";
        return args -> {
            QueryQuayIOService service = new QueryQuayIOService(builder);
            service.setToken(accessToken);
            ListShortContainers listShortContainers = service.getListContainers("biocontainers");
            log.info(listShortContainers.getRepositories().toString());
        };
    }
}