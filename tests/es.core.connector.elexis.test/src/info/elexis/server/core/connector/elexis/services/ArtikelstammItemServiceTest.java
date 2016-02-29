package info.elexis.server.core.connector.elexis.services;

import static org.junit.Assert.*;

import org.junit.Test;

import at.medevit.ch.artikelstamm.ArtikelstammConstants.TYPE;
import info.elexis.server.core.connector.elexis.services.ArtikelstammItemService;

public class ArtikelstammItemServiceTest {

	@Test
	public void testGetImportSetCumulatedVersion()  {
		 int pharma = ArtikelstammItemService.INSTANCE.getImportSetCumulatedVersion(TYPE.P);
		 int nonPharma = ArtikelstammItemService.INSTANCE.getImportSetCumulatedVersion(TYPE.N);
		 assertTrue("pharma version is >0: "+pharma, pharma > 0);
		 assertTrue("nonpharma version is >0: "+nonPharma, nonPharma > 0);
	}

}
