package com.zerobase.order.controller;

import com.zerobase.order.domain.product.ProductDto;
import com.zerobase.order.service.ProductSearchService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search/product")
public class SearchController {

    private final ProductSearchService productSearchService;

    /**
     * 검색 기준 조회
     * @param name
     * @return
     */
    @GetMapping
    public ResponseEntity<List<ProductDto>> searchByName(@RequestParam String name){
        return ResponseEntity.ok(
            productSearchService.searchByName(name).stream()
                .map(ProductDto::from).collect(Collectors.toList())
        );
    }

    /**
     * 상품 상세 조회
     * @param productId
     * @return
     */
    @GetMapping("/detail")
    public ResponseEntity<ProductDto> getDetail(@RequestParam Long productId){
        return ResponseEntity.ok(
            ProductDto.from(productSearchService.getByProductId(productId)));
    }
}
