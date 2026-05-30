package com.lims.core.domain.common.id;

import java.lang.annotation.*;

/**
 * 遵守: 總控層合約化設計 - 拒絕硬編碼，透過註解宣告業務識別規則
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrefixedId {
    String prefix();       // 例如: "LIMS"
    int sequenceLength() default 6; // 流水號長度
}