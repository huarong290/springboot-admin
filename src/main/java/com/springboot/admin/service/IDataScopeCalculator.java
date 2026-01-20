package com.springboot.admin.service;

import com.springboot.admin.model.dto.datascope.DataScopeDTO;

public interface IDataScopeCalculator {

    DataScopeDTO calculate(Long userId);
}
