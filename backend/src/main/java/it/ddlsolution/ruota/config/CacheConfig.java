package it.ddlsolution.ruota.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static it.ddlsolution.ruota.service.CacheableService.CAMPIONATI;


@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        Caffeine<Object, Object> oneDayCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .maximumSize(1000);
        Caffeine<Object, Object> tenMinutesCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(9).plusSeconds(30).toSeconds(), TimeUnit.SECONDS)
                .maximumSize(1000);

        CaffeineCache campionati = new CaffeineCache(CAMPIONATI, tenMinutesCache.build());

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(campionati));
        return manager;
    }

}
