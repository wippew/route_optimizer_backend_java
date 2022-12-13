package DG.DA;

//Java Program to Illustrate DemoController

//Importing package to code module
//package com.example.demo.controller;
//Importing required classes
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//Annotation
@Controller

//Class
public class DemoController {

 @RequestMapping("/hello")
 @ResponseBody

 // Method
 public String helloWorld()
 {

     // Print statement
     return "Hello World!";
 }
}
