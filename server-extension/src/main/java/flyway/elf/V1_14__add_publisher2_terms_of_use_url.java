package flyway.elf;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

/**
 * Created by MKUOSMANEN on 27.6.2016.
 */
public class V1_14__add_publisher2_terms_of_use_url implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_14__add_publisher2_terms_of_use_url.class);
    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final String PUBLISHER2 = "publisher2";
    private static final String TERMSOFUSEURL_CONF_NAME = "termsOfUseUrl";
    private static final String TERMSOFUSEURL_URL = "http://www.elfproject.eu/content/terms-service";
    private static final int BATCH_SIZE = 50;
    private int updatedViewCount = 0;

    public void migrate(Connection connection)
            throws Exception {
        int page = 1;
        while(updateViews(page)) {
            page++;
        }
        LOG.info("Updated views:", updatedViewCount);
    }

    private boolean updateViews(int page)
            throws Exception {
        List<View> list = VIEW_SERVICE.getViews(page, BATCH_SIZE);
        LOG.info("Got", list.size(), "views on page", page);
        for(View view : list) {
            final Bundle publisher2 = view.getBundleByName(PUBLISHER2);
            if (publisher2 != null) {
                JSONObject config = publisher2.getConfigJSON();
                if(config == null) {
                    config = new JSONObject();
                }
                config.put(TERMSOFUSEURL_CONF_NAME, TERMSOFUSEURL_URL);

                publisher2.setConfig(config.toString());
                VIEW_SERVICE.updateBundleSettingsForView(view.getId(), publisher2);
            }
            updatedViewCount++;
        }
        return list.size() == BATCH_SIZE;
    }
}
