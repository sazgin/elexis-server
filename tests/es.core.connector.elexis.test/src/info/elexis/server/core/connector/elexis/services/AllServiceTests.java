package info.elexis.server.core.connector.elexis.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ArtikelServiceTest.class, ArtikelstammItemServiceTest.class, BehandlungServiceTest.class,
		BriefServiceTest.class, DocHandleServiceTest.class, EigenleistungServiceTest.class, FallServiceTest.class,
		InvoiceServiceTest.class, JPAQueryTest.class, KontaktServiceTest.class, LabOrderServiceTest.class,
		LabResultServiceTest.class, LockServiceTest.class, PrescriptionServiceTest.class, StockServiceTest.class,
		UserServiceTest.class, UserconfigServiceTest.class, PhysioLeistungTest.class, StoreToStringTest.class,
		TarmedLeistungServiceTest.class, TerminServiceTest.class, XidServiceTest.class, StickerServiceTest.class,
		ReminderServiceTest.class })
public class AllServiceTests {

}
