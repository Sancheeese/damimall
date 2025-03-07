package com.example.common.utils;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public class BatchOptUtils<T> {
    public void saveBatch(IService service, List<T> data, int batchSize){
        for (int i = 0; i < data.size(); i += batchSize){
            int toIdx = Math.min(i + batchSize, data.size());
            List<T> subData = data.subList(i, toIdx);
            service.saveBatch(subData);
        }
    }

    public void delBatch(IService service, List<Long> ids, int batchSize){
        for (int i = 0; i < ids.size(); i += batchSize){
            int toIdx = Math.min(i + batchSize, ids.size());
            List<Long> subIds = ids.subList(i, toIdx);
            service.removeBatchByIds(subIds);
        }
    }


}
