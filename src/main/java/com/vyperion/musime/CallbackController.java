package com.vyperion.musime;

import com.vyperion.musime.services.AuthorizationCodeExample;
import com.vyperion.musime.services.AuthorizationCodeUriExample;
import com.vyperion.musime.services.BasicService;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("callback")
public class CallbackController {

    @GetMapping("")
    public ResponseEntity<String> callback() {

        return ResponseEntity.ok().body("hit callback");
    }
}














