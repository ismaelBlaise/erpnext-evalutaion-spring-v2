package com.evaluation.erpnext_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.evaluation.erpnext_spring.service.ImportService;

import jakarta.servlet.http.HttpSession;

@SuppressWarnings(value = "unused")
@Controller
@RequestMapping("/imports")
public class ImportController {
    @Autowired
    private ImportService importService;

    @GetMapping
    public ModelAndView form(HttpSession session){
        ModelAndView modelAndView=new ModelAndView("template");
        // try {
            modelAndView.addObject("page","imports/form");
        // } catch (Exception e) {
        //     modelAndView.addObject("error",e.getMessage());
        //     modelAndView.addObject("page", "error");
        // }
        return modelAndView;
    }
}
