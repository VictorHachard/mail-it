package com.mailit.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class MainController {

    @GetMapping("/")
    public ResponseEntity<String> index() {
        String style = "<style>" +
                "html {" +
                    "background-color: #181a1b !important;" +
                    "color: #e8e6e3 !important;" +
                "}" +
                ".center {" +
                    "height: 50%;" +
                    "width: 100%;" +
                    "display: flex;" +
                    "position: fixed;" +
                    "flex-direction: column;" +
                    "align-items: center;" +
                    "justify-content: center;" +
                "}" +
                "</style>";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(style + "<div class='center'><h1>mail-it</h1></div>", headers, HttpStatus.OK);
    }

}
