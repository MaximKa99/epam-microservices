package com.epam.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@RestController
class ResourceController {

    @RequestMapping("/user")
    fun user(user: Principal): Principal {
        return user
    }
}
