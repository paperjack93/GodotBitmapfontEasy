package com.dashur.integration.extw.rgs;

import com.dashur.integration.extw.rgs.data.*;
import java.util.List;
import javax.ws.rs.core.Response;

public interface RgsServiceProvider {
	
	List<GameHash> gameHashes(String currencies);

	PlaycheckExtResponse playcheckExt(final PlaycheckExtRequest request);
}