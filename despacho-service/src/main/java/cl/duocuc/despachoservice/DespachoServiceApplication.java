package cl.duocuc.despachoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "cl.duocuc.despachoservice.client")
public class DespachoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DespachoServiceApplication.class, args);
    }

}
