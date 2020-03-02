package com.example.ryanazrian.livechat.controllers;


import com.example.ryanazrian.livechat.model.ExampleModel;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Object> addMember(@RequestBody ExampleModel model){
        System.out.println(model.getEmail());
        System.out.println(model.getPassword());

        return new ResponseEntity<Object>(model, HttpStatus.OK);
    }

}
