package com.xpker.xpicture;

import com.xpker.xpicture.model.entity.User;
import com.xpker.xpicture.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class XPictureApplicationTests {
    @Resource
    private UserService userService;
    @Test
    void contextLoads() {
        User user = new User();
        user.setUserName("x");
        boolean save = userService.save(user);
        System.out.println(save);
    }

}
