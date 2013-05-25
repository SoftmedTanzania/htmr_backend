package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.contract.BirthPlanningRequest;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.formSubmissionHandler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.text.MessageFormat.format;
import static org.ei.drishti.common.AllConstants.ANCCloseCommCareFields.DEATH_OF_WOMAN_COMMCARE_VALUE;
import static org.ei.drishti.common.AllConstants.ANCCloseCommCareFields.PERMANENT_RELOCATION_COMMCARE_VALUE;
import static org.ei.drishti.common.AllConstants.ANCFormFields.*;
import static org.ei.drishti.common.AllConstants.CaseCloseCommCareFields.CLOSE_REASON_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.DETAILS_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.common.AllConstants.IFAFields.IFA_TABLETS_DATE;
import static org.ei.drishti.common.AllConstants.IFAFields.NUMBER_OF_IFA_TABLETS_GIVEN;
import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_DATA_KEY_NAME;
import static org.joda.time.LocalDate.parse;

@Service
public class ANCService {
    private static Logger logger = LoggerFactory.getLogger(ANCService.class.toString());

    private AllMothers allMothers;
    private AllEligibleCouples eligibleCouples;
    private ANCSchedulesService ancSchedulesService;
    private ActionService actionService;
    private MotherReportingService reportingService;
    private ReportFieldsDefinition reportFieldsDefinition;

    @Autowired
    public ANCService(AllMothers allMothers,
                      AllEligibleCouples eligibleCouples,
                      ANCSchedulesService ancSchedulesService,
                      ActionService actionService,
                      MotherReportingService reportingService,
                      ReportFieldsDefinition reportFieldsDefinition) {
        this.allMothers = allMothers;
        this.eligibleCouples = eligibleCouples;
        this.ancSchedulesService = ancSchedulesService;
        this.actionService = actionService;
        this.reportingService = reportingService;
        this.reportFieldsDefinition = reportFieldsDefinition;
    }

    public void registerANC(FormSubmission submission) {
        String motherId = submission.getField(MOTHER_ID);

        if (!eligibleCouples.exists(submission.entityId())) {
            logger.warn(format("Found mother without registered eligible couple. Ignoring: {0} for mother with id: {1} for ANM: {2}",
                    submission.entityId(), motherId, submission.anmId()));
            return;
        }

        Mother mother = allMothers.findByCaseId(motherId);
        allMothers.update(mother.withAnm(submission.anmId()));

        ancSchedulesService.enrollMother(motherId, parse(submission.getField(REFERENCE_DATE)));

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.registerANC(new SafeMap(submission.getFields(reportFields)));
    }

    public void registerOutOfAreaANC(FormSubmission submission) {
        String motherId = submission.getField(MOTHER_ID);

        if (!eligibleCouples.exists(submission.entityId())) {
            logger.warn(format("Found mother without registered eligible couple. Ignoring: {0} for mother with id: {1} for ANM: {2}",
                    submission.entityId(), motherId, submission.anmId()));
            return;
        }

        Mother mother = allMothers.findByCaseId(motherId);
        allMothers.update(mother.withAnm(submission.anmId()));

        ancSchedulesService.enrollMother(motherId, parse(submission.getField(REFERENCE_DATE)));
    }

    public void ancVisit(FormSubmission submission) {
        if (!allMothers.exists(submission.entityId())) {
            logger.warn("Found ANC visit without registered mother for Entity ID: " + submission.entityId());
            return;
        }

        ancSchedulesService.ancVisitHasHappened(submission.entityId(), submission.anmId(),
                parseInt(submission.getField(ANC_VISIT_NUMBER_FIELD)), submission.getField(ANC_VISIT_DATE_FIELD));

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.ancVisit(new SafeMap(submission.getFields(reportFields)));
    }

    public void closeANCCase(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Tried to close case without registered mother for case ID: " + submission.entityId());
            return;
        }

        allMothers.close(submission.entityId());
        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.closeANC(new SafeMap(submission.getFields(reportFields)));

        ancSchedulesService.unEnrollFromSchedules(submission.entityId());
        actionService.markAllAlertsAsInactive(submission.entityId());

        if (DEATH_OF_WOMAN_COMMCARE_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_COMMCARE_FIELD_NAME))
                || PERMANENT_RELOCATION_COMMCARE_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_COMMCARE_FIELD_NAME))) {
            logger.info("Closing EC case along with ANC case. Submission: " + submission);
            eligibleCouples.close(mother.ecCaseId());
        }
    }

    public void ttProvided(FormSubmission submission) {
        ancSchedulesService.ttVisitHasHappened(submission.entityId(), submission.anmId(), submission.getField(TT_DOSE_FIELD), submission.getField(TT_DATE_FIELD));

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.ttProvided(new SafeMap(submission.getFields(reportFields)));
    }

    public void ifaTabletsGiven(FormSubmission submission) {
        if (!allMothers.exists(submission.entityId())) {
            logger.warn("Tried to handle ifa tablets given without registered mother. Submission: " + submission);
            return;
        }

        ancSchedulesService.ifaTabletsGiven(
                submission.entityId(),
                submission.anmId(),
                submission.getField(NUMBER_OF_IFA_TABLETS_GIVEN),
                submission.getField(IFA_TABLETS_DATE));
    }

    @Deprecated
    public void updatePregnancyOutcome(AnteNatalCareOutcomeInformation outcomeInformation, Map<String, Map<String, String>> extraData) {
        String caseId = outcomeInformation.motherCaseId();
        if (!allMothers.exists(caseId)) {
            logger.warn("Failed to update delivery outcome as there is no mother registered: " + outcomeInformation);
            return;
        }
        reportingService.updatePregnancyOutcome(new SafeMap(extraData.get(REPORT_EXTRA_DATA_KEY_NAME)));
        ancSchedulesService.unEnrollFromSchedules(caseId);
        allMothers.updateDeliveryOutcomeFor(caseId, outcomeInformation.deliveryOutcomeDate());
        Mother updatedMother = allMothers.updateDetails(caseId, extraData.get(DETAILS_EXTRA_DATA_KEY_NAME));
        actionService.updateANCOutcome(caseId, outcomeInformation.anmIdentifier(), updatedMother.details());
    }

    @Deprecated
    public void updateBirthPlanning(BirthPlanningRequest request, Map<String, Map<String, String>> extraData) {
        if (!allMothers.exists(request.caseId())) {
            logger.warn("Tried to update birth planning without registered mother: " + request);
            return;
        }

        Mother motherWithUpdatedDetails = allMothers.updateDetails(request.caseId(), extraData.get(DETAILS_EXTRA_DATA_KEY_NAME));
        actionService.updateBirthPlanning(request.caseId(), request.anmIdentifier(), motherWithUpdatedDetails.details());
    }
}
