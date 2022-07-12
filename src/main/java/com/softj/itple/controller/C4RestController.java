package com.softj.itple.controller;

import com.softj.itple.service.C4Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/c4")
@RestController
@RequiredArgsConstructor
public class C4RestController {
    final private C4Service c4Service;
}
