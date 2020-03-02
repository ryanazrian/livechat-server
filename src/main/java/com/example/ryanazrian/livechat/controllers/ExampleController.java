package com.example.ryanazrian.livechat.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/example")
public class ExampleController {

    @GetMapping("/hello-world")
    public ResponseEntity<String> get(){
        return ResponseEntity.ok("Hello Yan!!");
    }

    @PostMapping(path = "/members", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> addMember(@RequestBody String ast){
        System.out.println(ast);

        return ResponseEntity.ok("MANTAP");
    }

}
