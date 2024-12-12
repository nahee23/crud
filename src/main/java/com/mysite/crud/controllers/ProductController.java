package com.mysite.crud.controllers;

import com.mysite.crud.models.Product;
import com.mysite.crud.models.ProductDto;
import com.mysite.crud.services.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository pRepo;

    @GetMapping({"","/"})
    public String showProductList(Model model) {
        List<Product> products = pRepo.findAll();
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/createProduct";
    }

    @PostMapping("/create")
    public String CreatProduct(@Valid @ModelAttribute ProductDto productDto,
                               BindingResult result) {

        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "Image file is required"));
        }

        if (result.hasErrors()) {
            return "products/createProduct";
        }
        //에러가 없을시 새 제품데이터를 저장
        MultipartFile image = productDto.getImageFile();
        Date createDate = new Date();
        String storeFileName = createDate.getTime() + "_" + image.getOriginalFilename();
        //이미지를 public/images 폴더에 저장
        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectory(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()){
                Files.copy(inputStream, Paths.get(uploadDir + storeFileName), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setImageFileName(storeFileName);

        pRepo.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        try {
            Product product = pRepo.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        return "products/editProduct";
    }

    //수정하기
    @PostMapping("/edit")
    public String editProduct(@Valid ProductDto productDto, BindingResult bindingResult,
                              @RequestParam int id, Model model) {

        Product product = pRepo.findById(id).get();
        model.addAttribute("product", product);

        if (bindingResult.hasErrors()) {
            return "products/editProduct";
        }
        //수정할 이미지 있으면 기존이미지 삭제하고 수정 이미지를 업로드함
        if(!productDto.getImageFile().isEmpty()){
            String uploadDir = "public/images/";
            Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

            try {
                Files.delete(oldImagePath);
            } catch (Exception e){
                System.out.println("Error: " + e.getMessage());
            }
            //새 이미지 업로드
            MultipartFile image = productDto.getImageFile();
            Date createDate = new Date();
            String storeFileName = createDate.getTime() + "_" + image.getOriginalFilename();

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir+storeFileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            product.setImageFileName(storeFileName); //이미지 파일 이름을 업데이트
        }
        //이미지 제외한 수정 내용을 업데이트
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());

        pRepo.save(product); //수정이 완료된 제품객체로 DB 업데이트함

        return "redirect:/products/";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {

        try {
            Product product = pRepo.findById(id).get();
            //이미지 파일 삭제하기
            String uploadDir = "public/images/";
            Path imagePath = Paths.get(uploadDir + product.getImageFileName());

            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
            // 제품 삭제
            pRepo.delete(product);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return "redirect:/products/";
    }
}
