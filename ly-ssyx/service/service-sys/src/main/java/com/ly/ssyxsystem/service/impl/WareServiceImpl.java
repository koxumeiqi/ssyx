package com.ly.ssyxsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.model.sys.Ware;
import com.ly.ssyxsystem.service.WareService;
import com.ly.ssyxsystem.mapper.WareMapper;
import org.springframework.stereotype.Service;

/**
* @author myz03
* @description 针对表【ware(仓库表)】的数据库操作Service实现
* @createDate 2023-10-15 12:29:35
*/
@Service
public class WareServiceImpl extends ServiceImpl<WareMapper, Ware>
    implements WareService{

}




