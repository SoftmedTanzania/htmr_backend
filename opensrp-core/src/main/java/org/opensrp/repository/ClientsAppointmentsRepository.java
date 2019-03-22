package org.opensrp.repository;

import org.opensrp.domain.AppointmentType;
import org.opensrp.domain.HealthFacilitiesReferralClients;
import org.opensrp.domain.ClientAppointments;
import org.opensrp.domain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;


@Repository
public class ClientsAppointmentsRepository {


	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public int save(ClientAppointments clientAppointments) throws Exception {
		String insertQuery = "insert into " + ClientAppointments.tbName + " (" +
				ClientAppointments.COL_HEALTH_FACILITY_CLIENT_ID + "," +
				ClientAppointments.COL_APPOINTMENT_DATE + "," +
				ClientAppointments.COL_IS_CANCELLED + "," +
				ClientAppointments.COL_STATUS + "," +
				ClientAppointments.COL_APPOINTMENT_TYPE + "," +
				ClientAppointments.COL_UPDATED_AT + "," +
				ClientAppointments.COL_CREATED_AT + ") values (?,?,?,?,?,?,?) ";

		Object[] params = new Object[] {
				clientAppointments.getHealthFacilitiesReferralClients().getHealthFacilityClientId(),
				clientAppointments.getAppointmentDate(),
				clientAppointments.getIsCancelled(),
				clientAppointments.getStatus(),
				clientAppointments.getAppointmentType(),
				clientAppointments.getUpdatedAt(),
				clientAppointments.getCreatedAt() };

		int[] types = new int[] {
				Types.BIGINT,
				Types.DATE,
				Types.BOOLEAN,
				Types.VARCHAR,
				Types.INTEGER,
				Types.DATE,
				Types.TIMESTAMP };
		
		return jdbcTemplate.update(insertQuery, params, types);
		
	}


	
	public void executeQuery(String query) throws Exception {
		jdbcTemplate.execute(query);
	}
	
	public int checkIfExists(String query, String[] args) throws Exception {
		return this.jdbcTemplate.queryForObject(query, args, Integer.class);
		
	}
	
	public void clearTable() throws Exception {
		String query = "DELETE FROM " + ClientAppointments.tbName;
		executeQuery(query);
	}



	public List<ClientAppointments> getAppointments(String sql, Object[] args) throws Exception {
		return this.jdbcTemplate.query(sql,args, new PatientsAppointmentsRowMapper());
	}



	public class PatientsAppointmentsRowMapper implements RowMapper<ClientAppointments> {
		public ClientAppointments mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClientAppointments clientAppointments = new ClientAppointments();

			clientAppointments.setAppointment_id(rs.getLong(rs.findColumn(ClientAppointments.COL_APPOINTMENT_ID)));

			HealthFacilitiesReferralClients healthFacilitiesReferralClients = new HealthFacilitiesReferralClients();
			healthFacilitiesReferralClients.setHealthFacilityClientId(rs.getLong(rs.findColumn(ClientAppointments.COL_HEALTH_FACILITY_CLIENT_ID)));

			clientAppointments.setHealthFacilitiesReferralClients(healthFacilitiesReferralClients);
			clientAppointments.setAppointmentDate(rs.getDate(rs.findColumn(ClientAppointments.COL_APPOINTMENT_DATE)));
			clientAppointments.setIsCancelled(rs.getBoolean(rs.findColumn(ClientAppointments.COL_IS_CANCELLED)));

			Status status = new Status();
			status.setStatusId(rs.getInt(rs.findColumn(ClientAppointments.COL_STATUS)));
			clientAppointments.setStatus(status);

			AppointmentType appointmentType = new AppointmentType();
			appointmentType.setId(rs.getInt(rs.findColumn(ClientAppointments.COL_APPOINTMENT_TYPE)));

			clientAppointments.setAppointmentType(appointmentType);
			clientAppointments.setCreatedAt(new Date(rs.getTimestamp(rs.findColumn(ClientAppointments.COL_CREATED_AT)).getTime()));
			clientAppointments.setUpdatedAt(rs.getDate(rs.findColumn(ClientAppointments.COL_UPDATED_AT)));
			return clientAppointments;
		}
		
	}

}