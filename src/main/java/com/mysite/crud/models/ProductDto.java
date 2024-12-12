package com.mysite.crud.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

//유저에게 제품 입력을 받는 객체
public class ProductDto {

    @NotEmpty(message = "이름을 입력하세요")
    private String name;
    @NotEmpty(message = "브랜드를 입력하세요")
    private String brand;
    @NotEmpty(message = "카테고리를 입력하세요")
    private String category;
    @Min(value = 0, message = "최솟값은 0이상 입니다")
    private int price;

    @Size(min = 10, message = "제품설명은 10자 이상이여야 합니다")
    @Size(max = 100, message = "제품설명은 100자 이하여야 합니다")
    private String description;

    //날짜는 현재날짜시간으로 자동입력
    //DB에는 파일의 이름만 저장되지만 실제 유저로부터 파일이미지를 받음
    private MultipartFile imageFile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
