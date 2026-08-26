package it.ddlsolution.ruota.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class CacheableService {
    private final CacheManager cacheManager;
    private final RestTemplate restTemplate;


    // Self provider per invocare metodi transazionali sul proxy del bean (per worker threads)
    private final ObjectProvider<CacheableService> selfProvider;

    public final static String CAMPIONATI = "campionati";

    @Value("${cache.allcampionati.threads:11}")
    private int allCampionatiThreads;

    @Value("${cache.allcampionati.timeout-seconds:240}")
    private long allCampionatiTimeoutSeconds;

    // Limite di quanti worker possono accedere al DB contemporaneamente (configurabile)
    @Value("${cache.allcampionati.db-parallelism:6}")
    private int allCampionatiDbParallelism;


    @Cacheable(value = CAMPIONATI, sync = true)
    public List<String> allCampionati() {
        List<String> campionatiDTO = new ArrayList<>();
            return campionatiDTO;

    }
/*
    @CacheEvict(cacheNames = PARTITE, key = "#root.args[0] + '_' + #root.args[1]")
    public void clearCachePartite(String idCampionato, short anno) {
    }
*/
    /**
     * Invalida manualmente la cache specificata
     */
    public void invalidateCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("Cache '{}' invalidata manualmente.", cacheName);
        } else {
            log.warn("Cache '{}' non trovata.", cacheName);
        }
    }

    private void invalidateCacheEntry(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            log.info("Entry '{}' della cache '{}' invalidata manualmente.", key, cacheName);
        } else {
            log.warn("Cache '{}' non trovata. Impossibile invalidare la key '{}'.", cacheName, key);
        }
    }

}
