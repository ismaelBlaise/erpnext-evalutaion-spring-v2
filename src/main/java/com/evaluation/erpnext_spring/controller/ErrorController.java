// package com.evaluation.erpnext_spring.controller;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.ModelAndView;

// @Controller
// @RequestMapping("/")
// public class ErrorController {
//     @GetMapping
//     public ModelAndView error(@RequestParam(required = false) String error ){
//         ModelAndView modelAndView = new ModelAndView();
//         modelAndView.setViewName("template");
//         modelAndView.addObject("page", "error");
//         modelAndView.addObject("error",error);
//         modelAndView.addObject("fullName", "Administrator");
//         return modelAndView;
//     }
// }
