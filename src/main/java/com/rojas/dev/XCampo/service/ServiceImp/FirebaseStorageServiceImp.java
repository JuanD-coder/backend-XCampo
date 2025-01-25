package com.rojas.dev.XCampo.service.ServiceImp;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.rojas.dev.XCampo.service.Interface.FirebaseStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class FirebaseStorageServiceImp implements FirebaseStorageService {

    @Override
    public ResponseEntity<?> uploadMultipleFile(List<MultipartFile> images, Long idSeller, String context){
        String url = "";
        for (MultipartFile image : images) {
            try {
                String imageUrl = uploadFile(image, idSeller, context);
                url = url.concat(imageUrl+ " ");
            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error occurred " + e);
            }
        }
        return ResponseEntity.ok(url);
    }


    private String uploadFile(MultipartFile file, Long idSeller, String context) throws IOException {

        final String phat = context+"/"+idSeller+"/";

        String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();

        Bucket bucket = StorageClient.getInstance().bucket();

        Blob blob = bucket.create(phat+fileName,file.getBytes(),file.getContentType());

        return blob.getMediaLink();
    }

    @Override
    public String uploadLogFile(String rutaArchivo, String nombreArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            FileInputStream contenido = new FileInputStream(archivo);

            Bucket bucket = StorageClient.getInstance().bucket();
            Blob blob = bucket.create("logs/" + nombreArchivo, contenido, "text/plain");

            return blob.getMediaLink(); // Devuelve el enlace público del archivo
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
