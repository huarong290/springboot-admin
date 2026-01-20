package com.springboot.admin.assemble;

import com.springboot.admin.model.dto.user.UserInfoDTO;

public interface UserInfoAssembler {

    UserInfoDTO assemble(Long userId, String loginIp, String clientType, String deviceId);
}

