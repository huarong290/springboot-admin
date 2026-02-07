package com.springboot.admin.model.dto.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class CaptchaStoreDTO implements Serializable {
    private String codeHash;
    private String scene;
    private String target;
}

