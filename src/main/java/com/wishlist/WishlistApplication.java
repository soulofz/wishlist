package com.wishlist;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Final project for TMS course.",
                description = "Wishlist App)",
                version = "1.0.0",
                contact = @Contact(
                        name = "Zhan",
                        email = "zhanminskzhan@gmail.com",
                        url = "https://github.com/soulofz"
                )
        )
)

@SpringBootApplication
public class WishlistApplication {
    public static void main(String[] args) {
        SpringApplication.run(WishlistApplication.class, args);
    }

}
