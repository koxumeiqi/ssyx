package com.ly.ssyxsystem.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.client.search.SearchFeignClient;
import com.ly.ssyxsystem.constant.MqConst;
import com.ly.ssyxsystem.constant.RedisConst;
import com.ly.ssyxsystem.exception.SsyxException;
import com.ly.ssyxsystem.model.product.SkuAttrValue;
import com.ly.ssyxsystem.model.product.SkuImage;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.model.product.SkuPoster;
import com.ly.ssyxsystem.product.mapper.SkuInfoMapper;
import com.ly.ssyxsystem.product.service.SkuAttrValueService;
import com.ly.ssyxsystem.product.service.SkuImageService;
import com.ly.ssyxsystem.product.service.SkuInfoService;
import com.ly.ssyxsystem.product.service.SkuPosterService;
import com.ly.ssyxsystem.RabbitService;
import com.ly.ssyxsystem.result.ResultCodeEnum;
import com.ly.ssyxsystem.vo.product.SkuInfoQueryVo;
import com.ly.ssyxsystem.vo.product.SkuInfoVo;
import com.ly.ssyxsystem.vo.product.SkuStockLockVo;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author myz03
 * @description 针对表【sku_info(sku信息)】的数据库操作Service实现
 * @createDate 2023-11-04 15:51:21
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
        implements SkuInfoService {

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private SkuImageService skuImageService;

    @Autowired
    private SkuPosterService skuPosterService;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private SearchFeignClient searchFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfoVo skuInfoVo) {
        // 存取sku商品的基本信息
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
//        baseMapper.insertAndGetId(skuInfo);

        baseMapper.insert(skuInfo);

        // 存取平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!Collections.isEmpty(skuAttrValueList)) {
            // peek观察流中元素情况消费完返回的还是流，forEach不是是终端方法
            skuAttrValueList.stream()
                    .forEach(skuAttrValue -> skuAttrValue.setSkuId(skuInfo.getId()));
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
        // 存取商品图片
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!Collections.isEmpty(skuImagesList)) {
            skuImagesList.stream()
                    .forEach(skuImage -> skuImage.setSkuId(skuInfo.getId()));
            skuImageService.saveBatch(skuImagesList);
        }
        // 存取商品海报
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!Collections.isEmpty(skuPosterList)) {
            skuPosterList.stream()
                    .forEach(skuPoster -> skuPoster.setSkuId(skuInfo.getId()));
            skuPosterService.saveBatch(skuPosterList);
        }

    }

    @Override
    public SkuInfoVo getSkuInfo(Long id) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();
        // 获取skuInfo信息
        SkuInfo skuInfo = baseMapper.selectById(id);
        BeanUtils.copyProperties(skuInfo, skuInfoVo);
        // 通过skuId搜索对应的属性信息
        LambdaQueryWrapper<SkuAttrValue> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SkuAttrValue::getSkuId, skuInfo.getId());
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.list(queryWrapper);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);
        // 通过skuId获取sku图片
        LambdaQueryWrapper<SkuImage> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(SkuImage::getSkuId, skuInfo.getId());
        List<SkuImage> skuImagesList = skuImageService.list(queryWrapper1);
        skuInfoVo.setSkuImagesList(skuImagesList);
        // 通过skuId获取sku海报
        LambdaQueryWrapper<SkuPoster> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(SkuPoster::getSkuId, skuInfo.getId());
        List<SkuPoster> skuPosterList = skuPosterService.list(queryWrapper2);
        skuInfoVo.setSkuPosterList(skuPosterList);
        return skuInfoVo;
    }

    @Override
    public void updateSkuInfo(SkuInfoVo skuInfoVo) {
        // 1. 修改 sku 的基本信息
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        baseMapper.updateById(skuInfo);
        Long id = skuInfoVo.getId();
        // 2. 修改 sku 图片信息
        skuImageService.updateImageInfo(skuInfoVo.getSkuImagesList(), id);
        // 3. 修改 sku 海报信息
        skuPosterService.updatePosterInfo(skuInfoVo.getSkuPosterList(), id);
        // 4. 修改 sku 属性信息
        skuAttrValueService.updateAttrValueInfo(skuInfoVo.getSkuAttrValueList(), id);
    }

    @Override
    public void check(Long skuId, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(skuId);
        skuInfo.setCheckStatus(status);
        baseMapper.updateById(skuInfo);
    }

    @Override
    public void publish(Long skuId, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(skuId);
        skuInfo.setPublishStatus(status);
        baseMapper.updateById(skuInfo);
        if (status == 1) { // 上架
            // 上架逻辑
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,
                    MqConst.ROUTING_GOODS_UPPER,
                    skuId);
            return;
        }
        // 下架逻辑
        rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,
                MqConst.ROUTING_GOODS_LOWER,
                skuId);

    }

    //新人专享
    @Override
    public void isNewPerson(Long skuId, Integer status) {
        SkuInfo skuInfoUp = new SkuInfo();
        skuInfoUp.setId(skuId);
        skuInfoUp.setIsNewPerson(status);
        baseMapper.updateById(skuInfoUp);
    }

    @Override
    public void removeSkuInfo(Long id) {
        // 移除数据库的相关信息
        baseMapper.deleteById(id);
        // 移除es库中的相关信息
        Boolean a = searchFeignClient.lowerSku(id);
        System.out.println();
    }

    @Override
    public void removeSkuInfoByIds(List<Long> idList) {
        baseMapper.deleteBatchIds(idList);
        idList.forEach(searchFeignClient::lowerSku);
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        return baseMapper.selectList(new LambdaQueryWrapper<SkuInfo>()
                .like(SkuInfo::getSkuName, keyword));
    }

    @Override
    public List<SkuInfo> findNewPersonSkuInfoList() {
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuInfo::getIsNewPerson, 1)
                .eq(SkuInfo::getPublishStatus, 1)
                .orderByDesc(SkuInfo::getId);
        Page<SkuInfo> pageParam = new Page<>(1, 3);
        IPage<SkuInfo> skuInfoPage = baseMapper.selectPage(pageParam, queryWrapper);
        return skuInfoPage.getRecords();
    }

    @Override
    public SkuInfoVo getSkuInfoVo(Long skuId) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();

        //skuId查询skuInfo
        SkuInfo skuInfo = baseMapper.selectById(skuId);

        //skuId查询sku图片
        List<SkuImage> imageList = skuImageService.getImageListBySkuId(skuId);

        //skuId查询sku海报
        List<SkuPoster> posterList = skuPosterService.getPosterListBySkuId(skuId);

        //skuId查询sku属性
        List<SkuAttrValue> attrValueList = skuAttrValueService.getAttrValueListBySkuId(skuId);

        //封装到skuInfoVo对象
        BeanUtils.copyProperties(skuInfo, skuInfoVo);
        skuInfoVo.setSkuImagesList(imageList);
        skuInfoVo.setSkuPosterList(posterList);
        skuInfoVo.setSkuAttrValueList(attrValueList);
        return skuInfoVo;
    }

    @Override
    public Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo) {

        // 判断 skuStockLockVoList 集合是否为空
        if (Collections.isEmpty(skuStockLockVoList)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        // 遍历其得到每个商品，验证库存并锁定库存，具备原子性
        skuStockLockVoList.stream()
                .forEach(
                        skuStockLockVo -> {
                            this.checkLock(skuStockLockVo);
                        }
                );
        // 只要有一个商品锁定失败，所有锁定成功的商品都解锁
        boolean flag = skuStockLockVoList.stream()
                .anyMatch(skuStockLockVo -> !skuStockLockVo.getIsLock());
        if (flag) {
            // 所有锁定成功的商品都解锁
            skuStockLockVoList.stream()
                    .filter(SkuStockLockVo::getIsLock)
                    .forEach(skuStockLockVo -> {
                        baseMapper.unLockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
                    });
            return false;
        }
        // 如果商品都锁定成功，Redis缓存相关数据，为了后续解锁和减库存
        redisTemplate.opsForValue()
                .set(RedisConst.SROCK_INFO + orderNo, skuStockLockVoList);

        return true;
    }

    @Override
    public void minusStock(String orderNo) {
        //从redis获取锁定库存信息
        List<SkuStockLockVo> skuStockLockVoList =
                (List<SkuStockLockVo>) redisTemplate.opsForValue()
                        .get(RedisConst.SROCK_INFO + orderNo);
        if (CollectionUtils.isEmpty(skuStockLockVoList)) {
            return;
        }
        // 遍历集合，得到每个对象，减库存
        skuStockLockVoList.forEach(skuStockLockVo -> {
            baseMapper.minusStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
        });

        // 删除redis数据
        redisTemplate.delete(RedisConst.SROCK_INFO + orderNo);
    }

    private void checkLock(SkuStockLockVo skuStockLockVo) {
        // 获取到锁
        // 公平锁
        RLock rlock = this.redissonClient
                .getFairLock(RedisConst.SKUKEY_PREFIX + skuStockLockVo.getSkuId());
        // 加锁
        rlock.lock();
        try {
            // 验证库存
            SkuInfo skuInfo =
                    baseMapper.checkStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            // 判断
            if (skuInfo == null) {
                skuStockLockVo.setIsLock(false);
            }
            // 锁定库存
            Integer rows = baseMapper.lockStock(skuStockLockVo.getSkuId(),
                    skuStockLockVo.getSkuNum());
            if (rows == 1)
                skuStockLockVo.setIsLock(true);
        } finally {
            rlock.unlock();
        }
    }


    @Override
    public IPage<SkuInfo> selectPageList(SkuInfoQueryVo skuInfoQueryVo, Page<SkuInfo> skuInfoPage) {

        String skuType = skuInfoQueryVo.getSkuType();
        String skuName = skuInfoQueryVo.getKeyword();
        Long categoryId = skuInfoQueryVo.getCategoryId();
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(skuType)) {
            queryWrapper.eq(SkuInfo::getSkuType, skuType);
        }
        if (StringUtils.isNotEmpty(skuName)) {
            queryWrapper.like(SkuInfo::getSkuName, skuName);
        }
        if (categoryId != null) {
            queryWrapper.eq(SkuInfo::getCategoryId, categoryId);
        }
        return baseMapper.selectPage(skuInfoPage, queryWrapper);
    }


}




