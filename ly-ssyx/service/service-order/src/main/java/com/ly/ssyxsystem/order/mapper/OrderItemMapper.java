package com.ly.ssyxsystem.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.ssyxsystem.model.order.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 订单项信息 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2023-04-18
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

}
