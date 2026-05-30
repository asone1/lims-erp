// 所屬路徑: com.lims.core.infrastructure.id.QuarkusGcpIdDispatcher
package com.lims.core.infrastructure.id;

import com.lims.core.domain.order.OrderId;
import io.quarkus.redis.datasource.RedisDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

/**
 * 遵守: 基礎設施層 - 負責分散式唯一 ID 的生成與 Redis 計數器管理
 */
@ApplicationScoped
public class QuarkusGcpIdDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(QuarkusGcpIdDispatcher.class);

    // 遵守: 直接注入 Quarkus 官方的 Redis 資料來源
    @Inject
    RedisDataSource redis;

    /**
     * 根據傳入的領域類別，分派對應的業務自然鍵
     */
    public String dispatch(Class<?> idClass) {
        if (idClass.equals(OrderId.class)) {
            return generateOrderId();
        }
        
        // 若未來有 SampleId, TestReportId 可在此擴充
        throw new IllegalArgumentException("尚未支援此聚合根的 ID 分派: " + idClass.getSimpleName());
    }

    private String generateOrderId() {
        // 1. 取得當天日期字串 (例如: 20260525)
        // 遵守: 企業實務 - 強制鎖定時區 (台灣時間)，避免伺服器跨時區導致日期錯亂
        String datePart = LocalDate.now(ZoneId.of("Asia/Taipei"))
                                   .format(DateTimeFormatter.BASIC_ISO_DATE);

        // 2. 組合 Redis 的 Key (例如: lims:seq:order:20260525)
        String redisKey = "lims:seq:order:" + datePart;

        // 3. 利用 Redis 的 INCR 指令進行原子性遞增 (Atomic Increment)
        // 即使有 1000 個 Request 同時進來，Redis 也保證絕對不會拿到重複的數字
        long sequence = redis.value(Long.class).incr(redisKey);

        // 4. [企業級優化] 記憶體釋放 (TTL)
        // 如果是今天的第一筆，代表這個 Key 剛被建立，設定 24 小時後自動刪除，避免 Redis 塞滿歷史無用 Key
        if (sequence == 1) {
            redis.key().expire(redisKey, Duration.ofHours(24));
        }

        // 5. 遵守: SSOT 原則 - 使用 Domain 定義好的 Template 組裝字串
        String generatedId = String.format(
            OrderId.GENERATION_TEMPLATE, 
            OrderId.PREFIX, 
            datePart, 
            sequence
        );

        LOG.debug("Dispatcher 發出新的 OrderNo: {}", generatedId);

        return generatedId;
    }
}