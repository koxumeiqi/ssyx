package com.ly.ssyxsystem.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.activity.controller.CouponInfoController;
import com.ly.ssyxsystem.activity.mapper.CouponRangeMapper;
import com.ly.ssyxsystem.activity.mapper.CouponUseMapper;
import com.ly.ssyxsystem.activity.service.CouponInfoService;
import com.ly.ssyxsystem.activity.mapper.CouponInfoMapper;
import com.ly.ssyxsystem.client.product.ProductFeignClient;
import com.ly.ssyxsystem.enums.CouponRangeType;
import com.ly.ssyxsystem.enums.CouponStatus;
import com.ly.ssyxsystem.enums.CouponType;
import com.ly.ssyxsystem.model.activity.CouponInfo;
import com.ly.ssyxsystem.model.activity.CouponRange;
import com.ly.ssyxsystem.model.activity.CouponUse;
import com.ly.ssyxsystem.model.order.CartInfo;
import com.ly.ssyxsystem.model.product.Category;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.vo.activity.CouponRuleVo;
import net.bytebuddy.implementation.attribute.RecordComponentAttributeAppender;
import org.aspectj.weaver.ConstantPoolReader;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author myz03
 * @description 针对表【coupon_info(优惠券信息)】的数据库操作Service实现
 * @createDate 2023-12-10 22:26:48
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo>
        implements CouponInfoService {

    @Autowired
    private CouponRangeMapper couponRangeMapper;

    @Autowired
    private CouponUseMapper couponUseMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public IPage<CouponInfo> selectPageCouponInfo(Long page, Long limit) {

        Page<CouponInfo> pageObj = new Page<>(page, limit);
        Page<CouponInfo> pageInfo = baseMapper.selectPage(pageObj, null);
        List<CouponInfo> records = pageInfo.getRecords();
        records.forEach(record -> {
            record.setCouponTypeString(record.getCouponType().getComment());
            record.setRangeTypeString(record.getRangeType().getComment());
        });
        return pageInfo;
    }

    @Override
    public Map<String, Object> findCouponRuleList(Long id) {

        Map<String, Object> res = new HashMap<>();

        // 根据优惠券的id查询优惠券基本信息 coupon_info
        CouponInfo couponInfo = baseMapper.selectById(id);
        // 根据优惠券id查询coupon_range 查询里面对应的range_id
        // 如果规则类型是 SKU，那么range_id就是skuId值
        // 如果规则类型是 CATEGORY range_id 就是分类Id值
        List<CouponRange> couponRanges = couponRangeMapper.selectList(new LambdaQueryWrapper<CouponRange>()
                .eq(CouponRange::getCouponId, id)
                .select(CouponRange::getRangeId)
        );
        List<Long> rangeIds = couponRanges
                .stream()
                .map(CouponRange::getRangeId)
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(rangeIds)) {
            // 分别判断封装的数据
            if (couponInfo.getRangeType().equals(CouponRangeType.SKU)) {
                // 如果规则类型是 SKU，得到skuId，远程调用根据多个skuId值获取对应sku信息
                List<SkuInfo> skuInfos = productFeignClient.getSkuInfos(rangeIds);
                res.put("skuInfoList", skuInfos);
            } else if (couponInfo.getRangeType().equals(CouponRangeType.CATEGORY)) {
                // 如果规则类型是 CATEGORY，得到分类Id，远程调用根据多个分类Id值获取对应分类信息
                List<Category> categoryList = productFeignClient.findCategoryList(rangeIds);
                res.put("categoryList", categoryList);
            }
        }

        return res;
    }

    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        // 根据优惠券id删除之前的数据
        couponRangeMapper.delete(new LambdaQueryWrapper<CouponRange>()
                .eq(CouponRange::getCouponId, couponRuleVo.getCouponId())
        );
        // 更新优惠券基本信息
        CouponInfo couponInfo = baseMapper.selectById(couponRuleVo.getCouponId());
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());
        baseMapper.updateById(couponInfo);

        // 添加优惠券新规则数据
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        if (!CollectionUtils.isEmpty(couponRangeList)) {
            couponRangeList.forEach(couponRange -> {
                couponRange.setCouponId(couponRuleVo.getCouponId());
                couponRangeMapper.insert(couponRange);
            });
        }

    }

    @Override
    public void deleteById(Long id) {
        baseMapper.deleteById(id);
        couponRangeMapper.delete(new LambdaQueryWrapper<CouponRange>()
                .eq(CouponRange::getCouponId, id));
    }

    @Override
    public List<CouponInfo> findCouponInfoList(Long skuId, Long userId) {
        //远程调用：根据skuId获取skuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        //根据条件查询：skuId + 分类id + userId
        List<CouponInfo> couponInfoList = baseMapper.selectCouponInfoList(skuInfo.getId(),
                skuInfo.getCategoryId(), userId);

        return couponInfoList;
    }

    @Override
    public List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId) {

        //1 根据userId获取用户全部优惠卷
        //coupon_use  coupon_info
        List<CouponInfo> userAllCouponInfoList =
                baseMapper.selectCartCouponInfoList(userId);
        if (CollectionUtils.isEmpty(userAllCouponInfoList)) {
            return new ArrayList<CouponInfo>();
        }

        //2 从第一步返回list集合中，获取所有优惠卷id列表
        List<Long> couponIdList = userAllCouponInfoList.stream().map(couponInfo -> couponInfo.getId())
                .collect(Collectors.toList());

        //3 查询优惠卷对应的范围  coupon_range
        //couponRangeList
        LambdaQueryWrapper<CouponRange> wrapper = new LambdaQueryWrapper<>();
        // id in (1,2,3)
        wrapper.in(CouponRange::getCouponId, couponIdList);
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(wrapper);

        //4 获取优惠卷id 对应skuId列表
        //优惠卷id进行分组，得到map集合
        //     Map<Long,List<Long>>
        Map<Long, List<Long>> couponIdToSkuIdMap =
                this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);

        //5 遍历全部优惠卷集合，判断优惠卷类型
        //全场通用  sku和分类
        BigDecimal reduceAmount = new BigDecimal(0);
        CouponInfo optimalCouponInfo = null;
        for (CouponInfo couponInfo : userAllCouponInfoList) {
            //全场通用
            if (CouponRangeType.ALL == couponInfo.getRangeType()) {
                //全场通用
                //判断是否满足优惠使用门槛
                //计算购物车商品的总价
                BigDecimal totalAmount = computeTotalAmount(cartInfoList);
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                    couponInfo.setIsSelect(1);
                }
            } else {
                //优惠卷id获取对应skuId列表
                List<Long> skuIdList
                        = couponIdToSkuIdMap.get(couponInfo.getId());
                //满足使用范围购物项
                List<CartInfo> currentCartInfoList = cartInfoList.stream()
                        .filter(cartInfo -> skuIdList.contains(cartInfo.getSkuId()))
                        .collect(Collectors.toList());
                BigDecimal totalAmount = computeTotalAmount(currentCartInfoList);
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                    couponInfo.setIsSelect(1);
                }
            }
            if (couponInfo.getIsSelect().intValue() == 1 && couponInfo.getAmount().subtract(reduceAmount).doubleValue() > 0) {
                reduceAmount = couponInfo.getAmount();
                optimalCouponInfo = couponInfo;
            }

        }
        //6 返回List<CouponInfo>
        if (null != optimalCouponInfo) {
            optimalCouponInfo.setIsOptimal(1);
        }
        return userAllCouponInfoList;
    }

    //获取购物车对应优惠卷
    @Override
    public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList,
                                         Long couponId) {
        //根据优惠卷id基本信息查询
        CouponInfo couponInfo = baseMapper.selectById(couponId);
        if (couponInfo == null) {
            return null;
        }
        //根据couponId查询对应CouponRange数据
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(
                new LambdaQueryWrapper<CouponRange>()
                        .eq(CouponRange::getCouponId, couponId)
        );
        //对应sku信息
        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);
        //遍历map，得到value值，封装到couponInfo对象
        if (couponIdToSkuIdMap.size() > 0) {
            List<Long> skuIdList =
                    couponIdToSkuIdMap.entrySet().iterator().next().getValue();
            couponInfo.setSkuIdList(skuIdList);
        }
        return couponInfo;
    }

    @Override
    public void updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId) {
        LambdaQueryWrapper<CouponUse> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(CouponUse::getUserId, userId)
                .eq(CouponUse::getCouponId, couponId)
                .eq(CouponUse::getOrderId, orderId);
        CouponUse couponUse = couponUseMapper.selectOne(queryWrapper);
        couponUse.setCouponStatus(CouponStatus.USED);
        couponUseMapper.updateById(couponUse);

    }

    private Map<Long, List<Long>> findCouponIdToSkuIdMap(List<CartInfo> cartInfoList, List<CouponRange> couponRangeList) {
        Map<Long, List<Long>> couponIdToSkuIdMap = new HashMap<>();

        //couponRangeList数据处理，根据优惠卷id分组
        Map<Long, List<CouponRange>> couponRangeToRangeListMap = couponRangeList.stream()
                .collect(
                        Collectors.groupingBy(couponRange -> couponRange.getCouponId())
                );

        //遍历map集合
        Iterator<Map.Entry<Long, List<CouponRange>>> iterator =
                couponRangeToRangeListMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<CouponRange>> entry = iterator.next();
            Long couponId = entry.getKey();
            List<CouponRange> rangeList = entry.getValue();

            //创建集合 set
            Set<Long> skuIdSet = new HashSet<>();
            for (CartInfo cartInfo : cartInfoList) {
                for (CouponRange couponRange : rangeList) {
                    //判断
                    if (couponRange.getRangeType() == CouponRangeType.SKU
                            && couponRange.getRangeId().longValue() == cartInfo.getSkuId().longValue()) {
                        skuIdSet.add(cartInfo.getSkuId());
                    } else if (couponRange.getRangeType() == CouponRangeType.CATEGORY
                            && couponRange.getRangeId().longValue() == cartInfo.getCategoryId().longValue()) {
                        skuIdSet.add(cartInfo.getSkuId());
                    } else {

                    }
                }
            }
            couponIdToSkuIdMap.put(couponId, new ArrayList<>(skuIdSet));
        }
        return couponIdToSkuIdMap;

    }

    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if (cartInfo.getIsChecked().intValue() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }


}




