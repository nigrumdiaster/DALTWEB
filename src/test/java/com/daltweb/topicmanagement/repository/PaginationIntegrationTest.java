package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.controller.DepartmentController;
import com.daltweb.topicmanagement.dto.common.Pagination;
import com.daltweb.topicmanagement.entity.Department;
import com.daltweb.topicmanagement.exception.GlobalExceptionHandler;
import com.daltweb.topicmanagement.service.DepartmentService;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DataJpaTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.generate_statistics=true"
})
@Import({DepartmentController.class, DepartmentService.class})
class PaginationIntegrationTest {
    @Autowired DepartmentController controller;
    @Autowired DepartmentRepository departments;
    @Autowired EntityManager entityManager;
    MockMvc mvc;
    List<Long> ids;

    @BeforeEach void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
        ids = new ArrayList<>();
        for (int i = 0; i < 57; i++) {
            ids.add(departments.save(Department.builder().code("DEPT" + i).name("Bộ môn " + i).build()).getId());
        }
        departments.flush();
        entityManager.clear();
        entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics().clear();
    }

    @Test void defaultRequestIsLimitedToTenRows() throws Exception {
        mvc.perform(get("/api/departments")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(10))
                .andExpect(jsonPath("$.pagination.totalElements").value(57))
                .andExpect(jsonPath("$.pagination.hasNext").value(true));
        assertThat(entityManager.getEntityManagerFactory().unwrap(SessionFactory.class)
                .getStatistics().getEntityLoadCount()).isEqualTo(10);
    }

    @Test void offsetAndLimitAreAppliedBeforeEntitiesAreLoaded() throws Exception {
        mvc.perform(get("/api/departments").param("page", "1").param("limit", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(10))
                .andExpect(jsonPath("$.data[0].id").value(ids.get(10)))
                .andExpect(jsonPath("$.pagination.page").value(1))
                .andExpect(jsonPath("$.pagination.totalPages").value(6));
        assertThat(entityManager.getEntityManagerFactory().unwrap(SessionFactory.class)
                .getStatistics().getEntityLoadCount()).isEqualTo(10);
    }

    @Test void minusOneExplicitlyReturnsEveryRow() throws Exception {
        mvc.perform(get("/api/departments").param("limit", "-1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(57))
                .andExpect(jsonPath("$.pagination.totalElements").value(57))
                .andExpect(jsonPath("$.pagination.hasNext").value(false));
    }

    @Test void invalidPaginationReturnsBadRequest() throws Exception {
        for (String limit : List.of("0", "-2", "201", "invalid")) {
            mvc.perform(get("/api/departments").param("limit", limit)).andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/departments").param("page", "-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/departments").param("page", "1").param("limit", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test void pagesHaveStableOrderingAndCoverTheEntireResultWithoutDuplicates() {
        List<Long> loaded = new ArrayList<>();
        for (int page = 0; page < 6; page++) {
            loaded.addAll(departments.findAll(Pagination.request(page, 10)).map(Department::getId).getContent());
        }
        assertThat(loaded).containsExactlyElementsOf(ids).doesNotHaveDuplicates();
    }
}
