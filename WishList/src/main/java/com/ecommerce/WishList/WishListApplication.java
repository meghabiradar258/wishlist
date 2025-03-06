package com.ecommerce.WishList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class WishListApplication {

	private static final Logger logger = LoggerFactory.getLogger(WishListApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WishListApplication.class, args);
		logger.info("WishListApplication is running...");
	}

	@Bean
	public CacheManager cacheManager() {
		logger.info("Initializing CacheManager...");
		return new ConcurrentMapCacheManager("wishlistCache");
	}
}
