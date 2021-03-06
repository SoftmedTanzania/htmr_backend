package org.opensrp.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "service_indicator")
public class ServiceIndicator implements Serializable {

	public static final String tbName = "service_indicator";

	public static final String COL_SERVICE_INDICATOR_ID = "service_indicator_id";

	public static final String COL_SERVICE_ID = "service_id";

	public static final String COL_INDICATOR_ID = "indicator_id";

	public static final String COL_IS_ACTIVE = "is_active";

	public static final String COL_CREATED_AT = "created_at";

	public static final String COL_UPDATED_AT = "updated_at";



	@GeneratedValue
	@Column(name = COL_SERVICE_INDICATOR_ID)
	private Long serviceIndicatorId;


	@EmbeddedId
	private PKReferralServiceIndicator pkReferralServiceIndicator;


	@MapsId("serviceId")
	@ManyToOne
	@JoinColumn(name = COL_SERVICE_ID)
	private ReferralService referralService;

	@MapsId("indicatorId")
	@ManyToOne
	@JoinColumn(name=COL_INDICATOR_ID)
	private Indicator indicator;


	@Column(name = COL_IS_ACTIVE)
	private boolean isActive;

	@Column(name = COL_CREATED_AT, columnDefinition = "TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name = COL_UPDATED_AT, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	public Long getServiceIndicatorId() {
		return serviceIndicatorId;
	}

	public void setServiceIndicatorId(Long serviceIndicatorId) {
		this.serviceIndicatorId = serviceIndicatorId;
	}


	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
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

	public PKReferralServiceIndicator getPkReferralServiceIndicator() {
		return pkReferralServiceIndicator;
	}

	public void setPkReferralServiceIndicator(PKReferralServiceIndicator pkReferralServiceIndicator) {
		this.pkReferralServiceIndicator = pkReferralServiceIndicator;
	}
}
