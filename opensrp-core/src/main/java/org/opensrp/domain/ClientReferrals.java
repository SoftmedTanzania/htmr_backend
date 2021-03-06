package org.opensrp.domain;

import javax.persistence.*;
import java.util.Date;

import static org.opensrp.domain.ReferralType.COL_REFERRAL_TYPE_ID;

@Entity
@Table(name = "referral")
public class ClientReferrals {

	public static final String tbName = "referral";

	public static final String COL_CLIENT_ID = "client_id";

	public static final String COL_REFERRAL_ID = "referral_id";

	public static final String COL_REFERRAL_UUID = "referral_uuid";

	public static final String COL_REFERRAL_REASON = "referral_reason";

	public static final String COL_SERVICE_ID = "referral_service_id";

	public static final String COL_FACILITY_ID = "facility_id";

	public static final String COL_REFERRAL_DATE= "referral_date";

	public static final String COL_APPOINTMENT_DATE= "appointment_date";

	public static final String COL_IS_EMERGENCY= "is_emergency";

	public static final String COL_SERVICE_PROVIDER_UIID= "service_provider_uiid";

	public static final String COL_REFERRAL_FEEDBACK_ID = "referral_feedback_id";

	public static final String COL_FROM_FACILITY_ID= "from_facility_id";

	public static final String COL_OTHER_CLINICAL_INFORMATION= "other_clinical_information";

	public static final String COL_LAB_TEST= "lab_test";

	public static final String COL_TEST_RESULTS= "test_results";

	public static final String COL_OTHER_NOTES= "other_notes";

	public static final String COL_REFERRAL_TYPE= "referral_type";

	public static final String COL_REFERRAL_SOURCE= "referral_source";

	public static final String COL_REFERRAL_STATUS= "referral_status";

	public static final String COL_INSTANCE_ID= "instance_id";

	public static final String COL_CREATED_AT = "created_at";

	public static final String COL_UPDATED_AT = "updated_at";


	@Id
	@GeneratedValue
	@Column(name = COL_REFERRAL_ID)
	private Long id;


	@ManyToOne
	@JoinColumn(name= COL_CLIENT_ID)
	private ReferralClient patient;

	@Column(name = COL_REFERRAL_REASON)
	private String referralReason;

	@Column(name = COL_SERVICE_ID)
	private int serviceId;

	@Column(name = COL_INSTANCE_ID,unique = true)
	private String instanceId;

	@Column(name = COL_REFERRAL_UUID)
	private String referralUUID;

	@Column(name = COL_SERVICE_PROVIDER_UIID)
	private String serviceProviderUIID;

	@Column(name = COL_OTHER_CLINICAL_INFORMATION)
	private String otherClinicalInformation;

	@Column(name = COL_OTHER_NOTES)
	private String otherNotes;

	@ManyToOne
	@JoinColumn(name=COL_REFERRAL_TYPE, referencedColumnName=COL_REFERRAL_TYPE_ID)
	private ReferralType referralType;

	@ManyToOne
	@JoinColumn(name= COL_REFERRAL_FEEDBACK_ID, referencedColumnName="_id")
	private ReferralFeedback referralFeedback;

	@Column(name = COL_FROM_FACILITY_ID)
	private String fromFacilityId;

	@Column(name = COL_REFERRAL_SOURCE)
	private int referralSource;

	@Column(name = COL_REFERRAL_DATE)
	private Date referralDate;

	@Column(name = COL_APPOINTMENT_DATE)
	private Date appointmentDate;

	@Column(name = COL_IS_EMERGENCY)
	private boolean isEmergency;

	@Column(name = COL_FACILITY_ID)
	private String facilityId;

	/*
	 *  0 = new
	 * -1 = rejected/discarded
	 *  1 = complete referral
	 */
	@Column(name = COL_REFERRAL_STATUS)
	private int referralStatus;


	@Column(name = COL_LAB_TEST)
	private int labTest;

	@Column(name = COL_TEST_RESULTS)
	private int testResults;


	@Column(name = COL_CREATED_AT, columnDefinition = "TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name = COL_UPDATED_AT, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	public ReferralClient getPatient() {
		return patient;
	}

	public void setPatient(ReferralClient patient) {
		this.patient = patient;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReferralReason() {
		return referralReason;
	}

	public void setReferralReason(String referralReason) {
		this.referralReason = referralReason;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceProviderUIID() {
		return serviceProviderUIID;
	}

	public void setServiceProviderUIID(String serviceProviderUIID) {
		this.serviceProviderUIID = serviceProviderUIID;
	}

	public Date getReferralDate() {
		return referralDate;
	}

	public void setReferralDate(Date referralDate) {
		this.referralDate = referralDate;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public int getReferralStatus() {
		return referralStatus;
	}

	public void setReferralStatus(int referralStatus) {
		this.referralStatus = referralStatus;
	}

	public String getOtherClinicalInformation() {
		return otherClinicalInformation;
	}

	public void setOtherClinicalInformation(String otherClinicalInformation) {
		this.otherClinicalInformation = otherClinicalInformation;
	}

	public String getOtherNotes() {
		return otherNotes;
	}

	public void setOtherNotes(String otherNotes) {
		this.otherNotes = otherNotes;
	}

	public int getLabTest() {
		return labTest;
	}

	public void setLabTest(int labTest) {
		this.labTest = labTest;
	}

	public ReferralType getReferralType() {
		return referralType;
	}

	public void setReferralType(ReferralType referralType) {
		this.referralType = referralType;
	}

	public int getTestResults() {
		return testResults;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public int getReferralSource() {
		return referralSource;
	}

	public void setReferralSource(int referralSource) {
		this.referralSource = referralSource;
	}

	public String getFromFacilityId() {
		return fromFacilityId;
	}

	public void setFromFacilityId(String fromFacilityId) {
		this.fromFacilityId = fromFacilityId;
	}

	public int isTestResults() {
		return testResults;
	}

	public void setTestResults(int testResults) {
		this.testResults = testResults;
	}

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public boolean isEmergency() {
		return isEmergency;
	}

	public void setEmergency(boolean emergency) {
		isEmergency = emergency;
	}

	public String getReferralUUID() {
		return referralUUID;
	}

	public void setReferralUUID(String referralUUID) {
		this.referralUUID = referralUUID;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public ReferralFeedback getReferralFeedback() {
		return referralFeedback;
	}

	public void setReferralFeedback(ReferralFeedback referralFeedback) {
		this.referralFeedback = referralFeedback;
	}
}
