package com.ly.ssyxsystem.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.ssyxsystem.activity.client.ActivityFeignClient;
import com.ly.ssyxsystem.auth.AuthContextHolder;
import com.ly.ssyxsystem.client.product.ProductFeignClient;
import com.ly.ssyxsystem.enums.SkuType;
import com.ly.ssyxsystem.model.product.Category;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.model.search.SkuEs;
import com.ly.ssyxsystem.search.service.SkuService;
import com.ly.ssyxsystem.vo.search.PageVO;
import com.ly.ssyxsystem.vo.search.SkuEsQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.ContentModel;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SkuServiceImpl implements SkuService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ElasticsearchClient client;

    @Value("${sku.index}")
    private String index;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void upperSku(Long skuId) {
        try {
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if (skuInfo == null) {
                return;
            }
            Category category = productFeignClient.getCategory(skuInfo.getCategoryId());
            // 获取数据封装 SkuEs 对象
            SkuEs skuEs = new SkuEs();
            if (category != null) {
                skuEs.setCategoryId(category.getId());
                skuEs.setCategoryName(category.getName());
            }
            // 封装 sku 信息部分
            skuEs.setId(skuInfo.getId());
            skuEs.setKeyword(skuInfo.getSkuName() + "," + skuEs.getCategoryName());
            skuEs.setWareId(skuInfo.getWareId());
            skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
            skuEs.setImgUrl(skuInfo.getImgUrl());
            skuEs.setTitle(skuInfo.getSkuName());
            if (skuInfo.getSkuType() == SkuType.COMMON.getCode()) {
                skuEs.setSkuType(0);
                skuEs.setPrice(skuInfo.getPrice().doubleValue());
                skuEs.setStock(skuInfo.getStock());
                skuEs.setSale(skuInfo.getSale());
                skuEs.setPerLimit(skuInfo.getPerLimit());
            }
            // 调用方法添加 ES
            client.index(i -> i.index(index)
                    .id(skuId.toString())
                    .document(skuEs)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lowerSku(Long skuId) {
        try {
            client.delete(d -> d.index(index)
                    .id(skuId.toString())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<SkuEs> findHotSkuList() {

        try {
            SearchResponse<SkuEs> hotSkuResp = client.search(
                    r -> r.index(index)
                            .query(q -> q.matchAll(ma -> ma))
                            .sort(SortOptions.of(
                                            f -> f.field(
                                                    fs -> fs.field("hotScore")
                                                            .order(SortOrder.Desc))
                                    )
                            )
                            .from(0)
                            .size(10)
                    , SkuEs.class);
            List<SkuEs> skuEs = new ArrayList<>(hotSkuResp.hits().hits().size());
            for (Hit<SkuEs> hit : hotSkuResp.hits().hits()) {
                skuEs.add(hit.source());
            }
            return skuEs;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageVO<SkuEs> search(int page, int limit, SkuEsQueryVo skuEsQueryVo) {
        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());

        try {
            // 条件（title+categoryId+wareId）+分页查询
            SearchResponse<SkuEs> res = client.search(r -> r.index(index)
                            .query(
                                    q -> {
                                        if (skuEsQueryVo.getKeyword() != null && skuEsQueryVo.getKeyword().length() > 0)
                                            return q.fuzzy(f -> f.field("title")
                                                    .value(skuEsQueryVo.getKeyword())
                                                    .fuzziness("1"));
                                        return q.matchAll(ma -> ma);
                                    }
                            )
                            .query(q -> q.bool(
                                    b -> b.must(m -> m.match(mm -> mm.field("categoryId")
                                                    .query(skuEsQueryVo.getCategoryId())
                                                    .field("wareId")
                                                    .query(skuEsQueryVo.getWareId())

                                            )
                                    )
                            ))

                    , SkuEs.class);

            List<SkuEs> content = new ArrayList<>();
            for (Hit<SkuEs> hit : res.hits().hits()) {
                content.add(hit.source());
            }
            if (skuEsQueryVo.getWareId() != null && skuEsQueryVo.getCategoryId() != null) {
                content = content.stream()
                        .filter(s -> s.getWareId() == skuEsQueryVo.getWareId() && skuEsQueryVo.getCategoryId() == s.getCategoryId())
                        .collect(Collectors.toList());
            }
            int allSize = page * limit;
            PageVO<SkuEs> pageVO = new PageVO<>();
            pageVO.setEmpty(content.size() < 1);
            pageVO.setContent(content);
            pageVO.setFirst(content.size() > 0);
            pageVO.setLast(allSize >= content.size());

            if (!CollectionUtils.isEmpty(content)) {
                // 遍历 skuEsList，得到所有 skuId
                List<Long> skuIdList = content.stream()
                        .map(item -> item.getId())
                        .collect(Collectors.toList());
                // 根据 skuId 列表远程调用，调用 service-activity 里面的接口得到数据
                // key 是对应的 skuId，value是对应的活动规则，一个活动有多个规则信息
                Map<Long, List<String>> skuIdToRuleListMap = activityFeignClient.findActivity(skuIdList);
                // 封装数据到 SkuEs 中
                content.stream().forEach(skuEs ->
                        skuEs.setRuleList(skuIdToRuleListMap.get(skuEs.getId())));
            }
            return pageVO;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void incrHotScore(Long skuId) {
        String key = "hotScore";
        Double score = redisTemplate.opsForZSet().incrementScore(key, "skuId:" + skuId, 1);
        long updateHotScore = Math.round(score);
        if (updateHotScore > 0 && updateHotScore % 10 == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("hotScore", updateHotScore);
            try {
                UpdateResponse<SkuEs> update = client.update(
                        r -> r.index(index)
                                .id(skuId.toString())
                                .doc(map)
                        , SkuEs.class);
                log.info("更新热卖商品的热度成功，skuId：{}，热度：{}", skuId, updateHotScore);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
