package es.fhir.rest.core.resources;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.MedicationRequest.MedicationRequestStatus;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ch.elexis.core.findings.util.ModelUtil;
import info.elexis.server.core.connector.elexis.jpa.test.TestDatabaseInitializer;

public class MedicationRequestTest {

	private static IGenericClient client;

	private static Patient patient;

	@BeforeClass
	public static void setupClass() throws IOException, SQLException {
		TestDatabaseInitializer initializer = new TestDatabaseInitializer();
		initializer.initializePrescription();

		client = ModelUtil.getGenericClient("http://localhost:8380/fhir");
		assertNotNull(client);
		patient = client.read().resource(Patient.class).withId(TestDatabaseInitializer.getPatient().getId())
				.execute();
		assertNotNull(patient);
	}

	@Test
	public void getMedicationRequest() {
		// test with full id url
		Bundle results = client.search().forResource(MedicationRequest.class)
				.where(MedicationRequest.PATIENT.hasId(patient.getId())).returnBundle(Bundle.class)
				.execute();
		assertNotNull(results);
		List<BundleEntryComponent> entries = results.getEntry();
		assertFalse(entries.isEmpty());
		MedicationRequest order = (MedicationRequest) entries.get(0).getResource();
		// read
		MedicationRequest readOrder = client.read().resource(MedicationRequest.class).withId(order.getId()).execute();
		assertNotNull(readOrder);
		assertEquals(order.getId(), readOrder.getId());
		// test with id part only
		results = client.search().forResource(MedicationRequest.class)
				.where(MedicationRequest.PATIENT.hasId(patient.getIdElement().getIdPart())).returnBundle(Bundle.class)
				.execute();
		assertNotNull(results);
		entries = results.getEntry();
		assertFalse(entries.isEmpty());
		MedicationRequest foundOrder = (MedicationRequest) entries.get(0).getResource();
		assertEquals(order.getId(), foundOrder.getId());
	}

	@Test
	public void updateMedicationRequest() {
		// load existing order
		Bundle results = client.search().forResource(MedicationRequest.class)
				.where(MedicationRequest.PATIENT.hasId(patient.getId())).returnBundle(Bundle.class).execute();
		assertNotNull(results);
		List<BundleEntryComponent> entries = results.getEntry();
		assertFalse(entries.isEmpty());
		Optional<MedicationRequest> activeOrder = getActiveOrderWithDosage(entries);
		assertTrue(activeOrder.isPresent());
		MedicationRequest updateOrder = activeOrder.get();
		updateOrder.getDosageInstruction().get(0).setText("test");

		MethodOutcome outcome = client.update().resource(updateOrder).execute();
		// read and validate change
		MedicationRequest oldOrder = client.read().resource(MedicationRequest.class).withId(activeOrder.get().getId())
				.execute();
		assertNotNull(oldOrder);
		MedicationRequest newOrder = client.read().resource(MedicationRequest.class).withId(outcome.getId()).execute();
		assertNotNull(newOrder);
		assertEquals(MedicationRequestStatus.COMPLETED, oldOrder.getStatus());
		assertEquals(MedicationRequestStatus.ACTIVE, newOrder.getStatus());
		assertEquals("test", newOrder.getDosageInstruction().get(0).getText());
	}

	private Optional<MedicationRequest> getActiveOrderWithDosage(List<BundleEntryComponent> orders) {
		for (BundleEntryComponent bundleEntryComponent : orders) {
			if (bundleEntryComponent.getResource() instanceof MedicationRequest) {
				MedicationRequest order = (MedicationRequest) bundleEntryComponent.getResource();
				if (order.getStatus() == MedicationRequestStatus.ACTIVE && !order.getDosageInstruction().isEmpty()) {
					return Optional.of(order);
				}
			}
		}
		return Optional.empty();
	}
}
