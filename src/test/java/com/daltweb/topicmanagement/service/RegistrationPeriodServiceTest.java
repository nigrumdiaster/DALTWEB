package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.*;
import com.daltweb.topicmanagement.dto.period.PeriodStatusRequest;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationPeriodServiceTest {
    @Mock RegistrationPeriodRepository periods;
    @Mock UserRepository users;
    @Mock TopicRepository topics;
    @Mock StudentGroupRepository groups;
    @Mock ResultValidationService validation;
    @InjectMocks RegistrationPeriodService service;
    RegistrationPeriod period;

    @BeforeEach void setup() {
        period = RegistrationPeriod.builder().id(1L).type(PeriodType.GRADUATION_THESIS)
                .status(PeriodStatus.DRAFT).build();
        when(periods.findForUpdate(1L)).thenReturn(Optional.of(period));
    }

    @Test void cannotSkipPhases() {
        assertThatThrownBy(() -> service.updatePeriodStatus(1L, new PeriodStatusRequest(PeriodStatus.PHASE2_STUDENT_REGISTRATION)))
                .isInstanceOf(BadRequestException.class);
        verify(periods, never()).save(any());
    }

    @Test void cannotGoBackwards() {
        period.setStatus(PeriodStatus.IN_PROGRESS);
        assertThatThrownBy(() -> service.updatePeriodStatus(1L, new PeriodStatusRequest(PeriodStatus.PHASE2_STUDENT_REGISTRATION)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test void cannotReopenCompletedOrCancelledPeriod() {
        for (PeriodStatus status : List.of(PeriodStatus.COMPLETED, PeriodStatus.CANCELLED)) {
            period.setStatus(status);
            assertThatThrownBy(() -> service.updatePeriodStatus(1L, new PeriodStatusRequest(PeriodStatus.DRAFT)))
                    .isInstanceOf(BadRequestException.class);
        }
    }

    @Test void cannotOpenStudentRegistrationUntilAllTopicsAreReviewed() {
        period.setStatus(PeriodStatus.PHASE1_REVIEWING);
        when(topics.findByPeriodId(1L)).thenReturn(List.of(
                Topic.builder().approvalStatus(TopicApprovalStatus.APPROVED).build(),
                Topic.builder().approvalStatus(TopicApprovalStatus.PENDING).build()));
        assertThatThrownBy(() -> service.updatePeriodStatus(1L, new PeriodStatusRequest(PeriodStatus.PHASE2_STUDENT_REGISTRATION)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test void permitsApprovedTopicsToProceedToStudentRegistration() {
        period.setStatus(PeriodStatus.PHASE1_REVIEWING);
        when(topics.findByPeriodId(1L)).thenReturn(List.of(Topic.builder().approvalStatus(TopicApprovalStatus.APPROVED).build()));
        when(periods.save(period)).thenReturn(period);
        service.updatePeriodStatus(1L, new PeriodStatusRequest(PeriodStatus.PHASE2_STUDENT_REGISTRATION));
        assertThat(period.getStatus()).isEqualTo(PeriodStatus.PHASE2_STUDENT_REGISTRATION);
    }

    @Test void graduationThesisCannotSkipDefense() {
        period.setStatus(PeriodStatus.REVIEWING);
        assertThatThrownBy(() -> service.updatePeriodStatus(1L, new PeriodStatusRequest(PeriodStatus.COMPLETED)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test void completionChecksAllFinalResults() {
        period.setStatus(PeriodStatus.DEFENSE);
        when(validation.validatePeriodResults(period)).thenThrow(new BadRequestException("Chưa đủ điểm"));
        assertThatThrownBy(() -> service.updatePeriodStatus(1L, new PeriodStatusRequest(PeriodStatus.COMPLETED)))
                .isInstanceOf(BadRequestException.class);
        assertThat(period.getStatus()).isEqualTo(PeriodStatus.DEFENSE);
        verify(periods, never()).save(any());
    }

    @Test void nonThesisCanCompleteAfterReviewingWithAllResults() {
        period.setType(PeriodType.SPECIALIZED_PROJECT);
        period.setStatus(PeriodStatus.REVIEWING);
        when(periods.save(period)).thenReturn(period);
        service.updatePeriodStatus(1L, new PeriodStatusRequest(PeriodStatus.COMPLETED));
        verify(validation).validatePeriodResults(period);
        assertThat(period.getStatus()).isEqualTo(PeriodStatus.COMPLETED);
    }
}
