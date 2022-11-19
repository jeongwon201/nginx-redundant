package com.psc.cloud.standard.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ShareDto {

    boolean isOk = true;
    String message = "SUCCESS";

    public ShareDto(boolean isOk, String message) {
        this.isOk = isOk;
        this.message = message;
    }
}
