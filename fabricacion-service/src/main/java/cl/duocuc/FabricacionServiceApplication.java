package cl.duocuc.fabricacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "cl.duocuc.fabricacion")
public class FabricacionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FabricacionServiceApplication.class, args);
    }
}
