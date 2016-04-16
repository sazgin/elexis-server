package info.elexis.server.core.connector.elexis.services;

import info.elexis.server.core.connector.elexis.jpa.model.annotated.Brief;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.Heap;

public class BriefService extends AbstractService<Brief> {

	public static BriefService INSTANCE = InstanceHolder.INSTANCE;

	private static final class InstanceHolder {
		static final BriefService INSTANCE = new BriefService();
	}

	private BriefService() {
		super(Brief.class);
	}

	@Override
	public Brief create() {
		Brief document = super.create();
		Heap heap = HeapService.INSTANCE.create(document.getId(), true);
		document.setContent(heap);
		return document;
	}

}
