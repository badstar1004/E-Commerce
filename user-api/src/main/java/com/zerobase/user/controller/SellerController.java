package com.zerobase.user.controller;

import static com.zerobase.user.exception.ErrorCode.NOT_FOUND_USER;

import com.zerobase.domain.common.UserVo;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.user.domain.model.Seller;
import com.zerobase.user.domain.seller.SellerDto;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final SellerService sellerService;

    @GetMapping("/getInfo")
    public ResponseEntity<SellerDto> getInfo(@RequestHeader(name = "X-AUTO-TOKEN") String token){
        UserVo userVo = jwtAuthenticationProvider.getUserVo(token);
        Seller seller = sellerService.findByIdAndEmail(userVo.getId(), userVo.getEmail())
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        return ResponseEntity.ok(SellerDto.from(seller));
    }
}
