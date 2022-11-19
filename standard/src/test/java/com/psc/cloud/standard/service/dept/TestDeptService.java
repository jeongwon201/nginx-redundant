package com.psc.cloud.standard.service.dept;

import com.psc.cloud.standard.controller.dto.ResponseDeptDto;
import com.psc.cloud.standard.domain.dept.Dept;
import com.psc.cloud.standard.domain.dept.DeptRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@ActiveProfiles({"dev", "db-h2"})
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDeptService {

    @Autowired
    DeptService deptService;

    @Autowired
    DeptRepository deptRepository;

    @Test
    @Order(1)
    @Commit
    @Transactional
    public void A001_DEPT_SERVICE_입력() throws Exception {
        List<Dept> deptList = new ArrayList<>();
        deptList.add(Dept.builder().deptno(10).dname("ACCOUNTING").loc("NEW YORK").build());
        deptList.add(Dept.builder().deptno(20).dname("RESEARCH").loc("DALLAS").build());
        deptList.add(Dept.builder().deptno(30).dname("SALES").loc("CHICAGO").build());
        deptList.add(Dept.builder().deptno(40).dname("OPERATIONS").loc("BOSTON").build());

        for(Dept dept : deptList) {
            deptService.deptInsert(dept);
        }

        for(Dept dept : deptList) {
            Integer deptno = dept.getDeptno();
            ResponseDeptDto responseDeptDto = deptService.deptDetail(deptno);
            Assertions.assertThat(responseDeptDto.getDeptno()).isEqualTo(deptno);
        }
    }

    @Test
    @Order(2)
    @Commit
    @Transactional
    public void A002_DEPT_SERVICE_수정() throws Exception {
        String changeDname = "ACCOUNTING2";
        deptService.deptUpdate(Dept.builder().deptno(10).dname(changeDname).loc("NEW YORK").build());
        ResponseDeptDto responseDeptDto = deptService.deptDetail(10);
        log.debug(responseDeptDto.toString());
        Assertions.assertThat(changeDname).isEqualTo(responseDeptDto.getDname());
    }

    @Test
    @Order(3)
    @Commit
    @Transactional
    public void A003_DEPT_SERVICE_삭제() throws Exception {
        Integer[] deptnos = {10, 20, 30, 40};
        for(Integer deptno : deptnos) {
            deptService.deptDelete(deptno);

            boolean isPresent = deptRepository.findById(deptno).isPresent();
            log.debug(String.valueOf(isPresent));
            Assertions.assertThat(false).isEqualTo(isPresent);
        }
    }
}
