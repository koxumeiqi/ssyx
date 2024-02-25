package com.ly.ssyxsystem.user.api;


import com.ly.ssyxsystem.user.service.UserService;
import com.ly.ssyxsystem.vo.user.LeaderAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api111/user")
public class LeaderAddressApiController {

    @Autowired
    private UserService userService;

    @GetMapping("/inner/userAddr/{userId}")
    public LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") Long userId) {
        return userService.getLeaderAddressByUserId(userId);
    }

}
