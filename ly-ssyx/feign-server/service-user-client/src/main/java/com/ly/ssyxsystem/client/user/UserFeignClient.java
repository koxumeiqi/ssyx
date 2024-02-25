package com.ly.ssyxsystem.client.user;


import com.ly.ssyxsystem.vo.user.LeaderAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-user")
public interface UserFeignClient {

    @GetMapping("/api111/user/inner/userAddr/{userId}")
    LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") Long userId);

}
