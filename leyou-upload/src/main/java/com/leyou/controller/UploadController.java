package com.leyou.controller;


import com.leyou.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/upload")
public class UploadController {


    @Autowired
    private UploadService uploadService;

    @PostMapping("/image")
    public ResponseEntity<String> uploadImg(@RequestParam("file") MultipartFile multipartFile/*,
                                            @RequestParam("image") String image, @RequestParam("isEdit")Boolean isEdit*/) {

      /*  if (isEdit) {
            uploadService.delete(image);
        }*/

        String url = uploadService.upload(multipartFile);
        if (StringUtils.isEmpty(url)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

//    @RequestMapping(method = RequestMethod.DELETE)
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteImage(@RequestParam("image") String image) {
        if (StringUtils.isEmpty(image)) {
            return ResponseEntity.notFound().build();
        }
        Boolean delete = uploadService.delete(image);
        if (delete) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.badRequest().build();
    }


}
