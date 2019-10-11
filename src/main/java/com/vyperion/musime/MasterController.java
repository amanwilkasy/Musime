package com.vyperion.musime;


import com.vyperion.musime.services.AuthorizationCodeExample;
import com.vyperion.musime.services.AuthorizationCodeRefreshExample;
import com.vyperion.musime.services.AuthorizationCodeUriExample;
import com.vyperion.musime.services.BasicService;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("master")
public class MasterController {

//    @Autowired
//    private final BasicService basicService;

    @GetMapping("sync")
    public ResponseEntity<ClientCredentials> clientCredentials_Sync() {

        return ResponseEntity.ok().body(BasicService.clientCredentials_Sync());
    }

    @GetMapping("uri")
    public ResponseEntity<URI> getTest() {

        return ResponseEntity.ok().body(AuthorizationCodeUriExample.authorizationCodeUri_Sync());
    }



    @GetMapping("code")
    public ResponseEntity<AuthorizationCodeCredentials> code() {
        return ResponseEntity.ok().body(AuthorizationCodeExample.authorizationCode_Sync());
    }

    @GetMapping("fresh")
    public ResponseEntity<AuthorizationCodeCredentials> refresh() {
        return ResponseEntity.ok().body(AuthorizationCodeRefreshExample.authorizationCodeRefresh_Sync());
    }


}













