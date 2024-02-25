package com.ly.ssyxsystem.search.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.ssyxsystem.model.search.SkuEs;
import com.ly.ssyxsystem.vo.search.PageVO;
import com.ly.ssyxsystem.vo.search.SkuEsQueryVo;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkuService {
    void upperSku(Long skuId);

    void lowerSku(Long skuId);

    List<SkuEs> findHotSkuList();

    PageVO<SkuEs> search(int page, int limit, SkuEsQueryVo skuEsQueryVo);

    void incrHotScore(Long skuId);
}
