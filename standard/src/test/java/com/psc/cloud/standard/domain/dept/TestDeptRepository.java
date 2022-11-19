package com.psc.cloud.standard.domain.dept;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ActiveProfiles({"dev", "db-h2"})
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDeptRepository {

    @Autowired
    DeptRepository deptRepository;

    @Test
    @Order(1)
    @Commit
    public void A001_DEPT_TABLE_입력() {
        List deptList = new ArrayList<Dept>();
        deptList.add(Dept.builder().deptno(10).dname("ACCOUNTING").loc("NEW YORK").build());
        deptList.add(Dept.builder().deptno(20).dname("RESEARCH").loc("DALLAS").build());
        deptList.add(Dept.builder().deptno(30).dname("SALES").loc("CHICAGO").build());
        deptList.add(Dept.builder().deptno(40).dname("OPERATIONS").loc("BOSTON").build());
        deptRepository.saveAll(deptList);

        Assertions.assertThat(deptRepository.findById(10).isPresent()).isEqualTo(true);
        Assertions.assertThat(deptRepository.findById(20).isPresent()).isEqualTo(true);
        Assertions.assertThat(deptRepository.findById(30).isPresent()).isEqualTo(true);
        Assertions.assertThat(deptRepository.findById(40).isPresent()).isEqualTo(true);
    }

    @Test
    @Order(2)
    @Commit
    public void A001_DEPT_TABLE_수정() {
        String changeDname = "ACCOUNTING2";
        deptRepository.save(Dept.builder().deptno(10).dname(changeDname).loc("NEW YORK").build());
        Dept dept = deptRepository.findById(10).get();
        log.debug(dept.toString());
        Assertions.assertThat(changeDname).isEqualTo(dept.getDname());
    }

    @Test
    @Order(3)
    @Commit
    public void A001_DEPT_TABLE_삭제() {
        Integer [] deptnos = {10,20,30,40};
        for(Integer deptno : deptnos) {
            deptRepository.delete(Dept.builder().deptno(deptno).build());
            boolean isPresent = deptRepository.findById(deptno).isPresent();
            log.debug(String.valueOf(isPresent));
            Assertions.assertThat(false).isEqualTo(isPresent);
        }
    }
}
