package com.rojas.dev.XCampo.controller;

import com.rojas.dev.XCampo.service.Interface.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * controlador para el ingreso de imagenes
 */
@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    FirebaseStorageService firebaseStorageService;

    /**
     * sube imagenes a storage
     * @param images
     * @param context
     * @param id
     * @return estdo http
     */
    @PostMapping("{id}")
    public ResponseEntity<?> upload(
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam("context") String context,
            @PathVariable Long id
    )  {
        return firebaseStorageService.uploadMultipleFile(images,id,context);
    }

}
