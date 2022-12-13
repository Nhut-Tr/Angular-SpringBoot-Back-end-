package com.example.controller;

import com.example.dto.response.ResponseObject;
import com.example.security.service.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(path = "api/v1/FileUpload")
public class FileUploadController {
@Autowired
private IStorageService iStorageService;
  @PostMapping("")
  public ResponseEntity<ResponseObject> uploadFile(@RequestParam("file")MultipartFile file){
    try {
      String generatedFileName = iStorageService.storeFile(file);
      return ResponseEntity.status(HttpStatus.OK)
          .body(new ResponseObject("ok","Upload file successfully!",generatedFileName));
    }catch (Exception ex){
      return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
          .body(new ResponseObject("ok", ex.getMessage(), ""));
    }
  }


  @GetMapping("/file/{fileName:.+}")
  public ResponseEntity<byte[]> readDetailFile(@PathVariable String fileName){
    try{
        byte[] bytes = iStorageService.readFilecontent(fileName);
      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }catch(Exception ex){
      return ResponseEntity.noContent().build();
    }
  }
}
