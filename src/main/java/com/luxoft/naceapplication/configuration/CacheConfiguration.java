package com.luxoft.naceapplication.configuration;

import com.luxoft.naceapplication.constants.NaceApplicationConstants;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        return CacheManager.create();
    }

    @Bean
    public EhCacheCacheManager naceEhCacheConfigManager() {

        Cache existingCache = cacheManager().getCache(NaceApplicationConstants.NACE_ORDER_DETAILS_CACHE);

        if (Objects.isNull(existingCache)) {
            net.sf.ehcache.config.CacheConfiguration ehCacheConfig = new net.sf.ehcache.config.CacheConfiguration()
                    .eternal(false).maxEntriesLocalHeap(0)
                    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                    .name(NaceApplicationConstants.NACE_ORDER_DETAILS_CACHE)
                    .timeToLiveSeconds(Long.valueOf(86400));

            Cache cache = new Cache(ehCacheConfig);
            cacheManager().addCache(cache);
        }
        return new EhCacheCacheManager(cacheManager());
    }

}
