package br.com.ifpe.intelifones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("br.com.ifpe.intelifones")
public class IntelifonesApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelifonesApplication.class, args);
    }
}
