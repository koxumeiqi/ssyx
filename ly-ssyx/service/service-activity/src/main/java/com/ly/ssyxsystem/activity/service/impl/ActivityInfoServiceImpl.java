package com.ly.ssyxsystem.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.activity.mapper.ActivityRuleMapper;
import com.ly.ssyxsystem.activity.mapper.ActivitySkuMapper;
import com.ly.ssyxsystem.activity.service.ActivityInfoService;
import com.ly.ssyxsystem.activity.mapper.ActivityInfoMapper;
import com.ly.ssyxsystem.activity.service.CouponInfoService;
import com.ly.ssyxsystem.client.product.ProductFeignClient;
import com.ly.ssyxsystem.enums.ActivityType;
import com.ly.ssyxsystem.model.activity.ActivityInfo;
import com.ly.ssyxsystem.model.activity.ActivityRule;
import com.ly.ssyxsystem.model.activity.ActivitySku;
import com.ly.ssyxsystem.model.activity.CouponInfo;
import com.ly.ssyxsystem.model.order.CartInfo;
import com.ly.ssyxsystem.model.product.Category;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.vo.activity.ActivityRuleVo;
import com.ly.ssyxsystem.vo.order.CartInfoVo;
import com.ly.ssyxsystem.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author myz03
 * @description 针对表【activity_info(活动表)】的数据库操作Service实现
 * @createDate 2023-12-10 22:26:48
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo>
        implements ActivityInfoService {

    @Autowired
    private CouponInfoService couponInfoService;

    @Autowired
    private ActivityRuleMapper activityRuleMapper;

    @Autowired
    private ActivitySkuMapper activitySkuMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public IPage<ActivityInfo> getPageList(Page<ActivityInfo> pageParam) {

        IPage<ActivityInfo> pageList = this.baseMapper.selectPage(pageParam, null);
        for (ActivityInfo info : pageList.getRecords()) {
            info.setActivityTypeString(info.getActivityType().getComment());
        }
        return pageList;
    }

    @Override
    public Map<String, Object> findActivityRuleList(Long id) {
        Map<String, Object> res = new HashMap<>();
        // 1. 根据活动id查询，查询规则列表
        LambdaQueryWrapper<ActivityRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityRule::getActivityId, id);
        List<ActivityRule> activityRules = activityRuleMapper.selectList(queryWrapper);
        res.put("activityRuleList", activityRules);

        // 2. 根据活动id查询使用活动的商品 skuids
        List<ActivitySku> activitySkus = activitySkuMapper.selectList(new LambdaQueryWrapper<ActivitySku>()
                .eq(ActivitySku::getActivityId, id));
        List<Long> skuIds = activitySkus.stream().map(ActivitySku::getSkuId).collect(Collectors.toList());
        // 远程调用获取到商品信息
        List<SkuInfo> skuInfos = productFeignClient.getSkuInfos(skuIds);
        res.put("skuInfoList", skuInfos);
        return res;
    }

    @Override
    public void saveActivityRule(ActivityRuleVo activityRuleVo) {
        // 删除原有数据
        activityRuleMapper.delete(new LambdaQueryWrapper<ActivityRule>()
                .eq(ActivityRule::getActivityId, activityRuleVo.getActivityId()));

        // 添加新的规则列表数据
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        ActivityInfo activityInfo = baseMapper.selectById(activityRuleVo.getActivityId());
        if (activityRuleList != null && activityRuleList.size() > 0) {
            for (ActivityRule activityRule : activityRuleList) {
                activityRule.setActivityId(activityRuleVo.getActivityId());
                activityRule.setActivityType(activityInfo.getActivityType());
                activityRuleMapper.insert(activityRule);
            }
        }

        // 插入规则范围数据
        List<ActivitySku> skuList = activityRuleVo.getActivitySkuList();
        if (skuList != null && skuList.size() > 0) {
            for (ActivitySku activitySku : skuList) {
                activitySku.setActivityId(activityRuleVo.getActivityId());
                activitySkuMapper.insert(activitySku);
            }
        }
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {

        // 1. 根据关键字查询 sku 匹配的内容列表
        // service-product 模块创建接口 根据关键字查询 sku 匹配内容列表
        // service-activity 远程调用得到 sku 内容列表
        List<SkuInfo> skuInfos = productFeignClient.findSkuInfoByKeyword(keyword);
        if (skuInfos == null || skuInfos.size() == 0) {
            return skuInfos;
        }

        List<Long> skuIdList = skuInfos.stream()
                .map(SkuInfo::getId)
                .collect(Collectors.toList());
        // 2. 判断添加商品之前是否参加过活动，如果之前参加过，活动正在进行中，排除商品
        // 查询两张表判断 activity_info 和 activity_sku
        List<Long> usableIds = baseMapper.selectSkuIdListExist(skuIdList);
        // 判断逻辑处理
        List<SkuInfo> findSkuList = skuInfos.stream()
                .filter(
                        skuInfo -> !usableIds.contains(skuInfo.getId())
                )
                .collect(Collectors.toList());
        return findSkuList;
    }

    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIdList) {
        Map<Long, List<String>> result = new HashMap<>();
        //skuIdList遍历，得到每个skuId
        skuIdList.forEach(skuId -> {
            //根据skuId进行查询，查询sku对应活动里面规则列表
            List<ActivityRule> activityRuleList =
                    baseMapper.findActivityRule(skuId);
            //数据封装，规则名称
            if (!CollectionUtils.isEmpty(activityRuleList)) {
                List<String> ruleList = new ArrayList<>();
                //把规则名称处理
                for (ActivityRule activityRule : activityRuleList) {
                    ruleList.add(this.getRuleDesc(activityRule));
                }
                result.put(skuId, ruleList);
            }
        });
        return result;
    }

    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {
        //1 根据skuId获取sku营销活动，一个活动有多个规则
        List<ActivityRule> activityRuleList = this.findActivityRuleBySkuId(skuId);

        //2 根据skuId+userId查询优惠卷信息
        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfoList(skuId, userId);

        //3 封装到map集合，返回
        Map<String, Object> map = new HashMap<>();
        map.put("couponInfoList", couponInfoList);
        map.put("activityRuleList", activityRuleList);
        return map;
    }

    @Override
    public OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId) {

        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        // 1. 获取购物车，每个购物项参与活动，根据活动规则分组
        // 一个规则对应多个商品
        // CartInfoVo
        List<CartInfoVo> cartInfoVoList = this.findCartActivityList(cartInfoList);

        //2 计算参与活动之后金额
        BigDecimal activityReduceAmount = cartInfoVoList.stream()
                .filter(cartInfoVo -> cartInfoVo.getActivityRule() != null)
                .map(cartInfoVo -> cartInfoVo.getActivityRule().getReduceAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add); // BigDecimal.ZERO 就是说初始值是0，这样没有值也不会抛异常

        //3 获取购物车可以使用优惠卷列表
        List<CouponInfo> couponInfoList =
                couponInfoService.findCartCouponInfo(cartInfoList, userId);

        //4 计算商品使用优惠卷之后金额，一次只能使用一张优惠卷
        BigDecimal couponReduceAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(couponInfoList)) {
            couponReduceAmount = couponInfoList.stream()
                    .filter(couponInfo -> couponInfo.getIsOptimal().intValue() == 1)
                    .map(couponInfo -> couponInfo.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        //5 计算没有参与活动，没有使用优惠卷原始金额
        BigDecimal originalTotalAmount = cartInfoList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                .map(cartInfo -> cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //6 最终金额
        BigDecimal totalAmount =
                originalTotalAmount.subtract(activityReduceAmount).subtract(couponReduceAmount);

        //7 封装需要数据到OrderConfirmVo,返回
        OrderConfirmVo orderTradeVo = new OrderConfirmVo();
        orderTradeVo.setCarInfoVoList(cartInfoVoList);
        orderTradeVo.setActivityReduceAmount(activityReduceAmount);
        orderTradeVo.setCouponInfoList(couponInfoList);
        orderTradeVo.setCouponReduceAmount(couponReduceAmount);
        orderTradeVo.setOriginalTotalAmount(originalTotalAmount);
        orderTradeVo.setTotalAmount(totalAmount);
        return orderTradeVo;

    }

    public List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList) {
        //创建最终返回集合
        List<CartInfoVo> cartInfoVoList = new ArrayList<>();
        List<Long> skuIdList = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());
        //根据所有skuId列表获取参与活动
        List<ActivitySku> activitySkuList = baseMapper.selectCartActivity(skuIdList);
        //根据活动进行分组，每个活动里面有哪些skuId信息
        //map里面key是分组字段 活动id
        // value是每组里面sku列表数据，set集合
        Map<Long, Set<Long>> activityIdToSkuIdListMap = activitySkuList.stream()
                .collect(Collectors.groupingBy(
                        ActivitySku::getActivityId,
                        Collectors.mapping(ActivitySku::getSkuId, Collectors.toSet())
                ));
        //获取活动里面规则数据
        //key是活动id  value是活动里面规则列表数据
        Map<Long, List<ActivityRule>> activityIdToActivityRuleListMap
                = new HashMap<>();
        // 所有活动id
        List<Long> activitySet = activitySkuList.stream().map(ActivitySku::getActivityId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(activitySet)) {
            // activity_rule 表
            LambdaQueryWrapper<ActivityRule> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(ActivityRule::getConditionAmount,
                    ActivityRule::getConditionNum);
            queryWrapper.in(ActivityRule::getActivityId, activitySet);
            List<ActivityRule> activityRuleList = activityRuleMapper.selectList(queryWrapper);
            activityIdToActivityRuleListMap =
                    activityRuleList.stream()
                            .collect(Collectors.groupingBy(
                                    ActivityRule::getActivityId
                            ));
        }

        // 有活动的记录项
        Set<Long> activitySkuIdSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(activityIdToSkuIdListMap)) {
            // 遍历activityIdToSkuIdListMap集合
            Iterator<Map.Entry<Long, Set<Long>>> iterator = activityIdToSkuIdListMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Set<Long>> entry = iterator.next();
                //活动id
                Long activityId = entry.getKey();
                //每个活动对应skuId列表
                Set<Long> currentActivitySkuIdSet = entry.getValue();

                //获取当前活动对应的购物项列表
                List<CartInfo> currentActivityCartInfoList = cartInfoList.stream()
                        .filter(cartInfo -> currentActivitySkuIdSet
                                .contains(cartInfo.getSkuId())).collect(Collectors.toList());

                //计数购物项总金额和总数量
                BigDecimal activityTotalAmount =
                        this.computeTotalAmount(currentActivityCartInfoList);
                int activityTotalNum = this.computeCartNum(currentActivityCartInfoList);

                //计算活动对应规则
                //根据activityId获取活动对应规则
                List<ActivityRule> currentActivityRuleList =
                        activityIdToActivityRuleListMap.get(activityId);
                ActivityRule activityRule = null;
                if (!CollectionUtils.isEmpty(currentActivityRuleList)) {
                    ActivityType activityType = currentActivityRuleList.get(0).getActivityType();
                    //判断活动类型：满减和打折
                    if (activityType == ActivityType.FULL_REDUCTION) {//满减"
                        activityRule = this.computeFullReduction(activityTotalAmount, currentActivityRuleList);
                    } else {//满量
                        activityRule = this.computeFullDiscount(activityTotalNum, activityTotalAmount, currentActivityRuleList);
                    }
                }

                //CartInfoVo封装
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(activityRule);
                cartInfoVo.setCartInfoList(currentActivityCartInfoList);
                cartInfoVoList.add(cartInfoVo);

                //记录哪些购物项参与活动
                activitySkuIdSet.addAll(currentActivitySkuIdSet);
            }
        }

        //没有活动购物项skuId
        //获取哪些skuId没有参加活动
        skuIdList.removeAll(activitySkuIdSet);
        if (!CollectionUtils.isEmpty(skuIdList)) {
            //skuId对应购物项
            Map<Long, CartInfo> skuIdCartInfoMap = cartInfoList.stream().collect(
                    Collectors.toMap(CartInfo::getSkuId, CartInfo -> CartInfo)
            );

            for (Long skuId : skuIdList) {
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(null);//没有活动

                List<CartInfo> cartInfos = new ArrayList<>();
                cartInfos.add(skuIdCartInfoMap.get(skuId));
                cartInfoVo.setCartInfoList(cartInfos);

                cartInfoVoList.add(cartInfoVo);
            }
        }

        return cartInfoVoList;
    }

    private List<ActivityRule> findActivityRuleBySkuId(Long skuId) {
        List<ActivityRule> activityRuleList = baseMapper.findActivityRule(skuId);
        for (ActivityRule activityRule : activityRuleList) {
            String ruleDesc = this.getRuleDesc(activityRule);
            activityRule.setRuleDesc(ruleDesc);
        }
        return activityRuleList;
    }

    //构造规则名称的方法
    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
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

    private int computeCartNum(List<CartInfo> cartInfoList) {
        int total = 0;
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if (cartInfo.getIsChecked().intValue() == 1) {
                total += cartInfo.getSkuNum();
            }
        }
        return total;
    }

    /**
     * 计算满量打折最优规则
     *
     * @param totalNum
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠折扣从大到小排序了
     */
    private ActivityRule computeFullDiscount(Integer totalNum, BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项购买个数大于等于满减件数，则优化打折
            if (totalNum.intValue() >= activityRule.getConditionNum()) {
                BigDecimal skuDiscountTotalAmount = totalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                BigDecimal reduceAmount = totalAmount.subtract(skuDiscountTotalAmount);
                activityRule.setReduceAmount(reduceAmount);
                optimalActivityRule = activityRule;
                break;
            }
        }
        if (null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size() - 1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，还差")
                    .append(totalNum - optimalActivityRule.getConditionNum())
                    .append("件");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    /**
     * 计算满减最优规则
     *
     * @param totalAmount
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
     */
    private ActivityRule computeFullReduction(BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项金额大于等于满减金额，则优惠金额
            if (totalAmount.compareTo(activityRule.getConditionAmount()) > -1) {
                //优惠后减少金额
                activityRule.setReduceAmount(activityRule.getBenefitAmount());
                optimalActivityRule = activityRule;
                break;
            }
        }
        if (null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size() - 1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，还差")
                    .append(totalAmount.subtract(optimalActivityRule.getConditionAmount()))
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }
}




