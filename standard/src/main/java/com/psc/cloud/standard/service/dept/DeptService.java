package com.psc.cloud.standard.service.dept;

import com.psc.cloud.standard.controller.dto.ResponseDeptDto;
import com.psc.cloud.standard.core.exception.BusinessException;
import com.psc.cloud.standard.domain.dept.Dept;

import java.util.List;

public interface DeptService {
    public List<ResponseDeptDto> deptList() throws BusinessException;
    public ResponseDeptDto deptDetail(Integer deptno) throws BusinessException;
    public ResponseDeptDto deptInsert(Dept dept) throws BusinessException;
    public ResponseDeptDto deptUpdate(Dept dept) throws BusinessException;
    public void deptDelete(Integer deptno) throws BusinessException;
}
