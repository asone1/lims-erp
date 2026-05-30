package com.lims.core.domain.common.id;

import java.io.Serializable;

/**
 * 遵守: DDD 核心通用邊界 - 所有業務值物件 ID 的基底介面
 */
public interface Identity extends Serializable {
    String getValue();
}