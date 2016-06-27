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
 * Created by MHURME on 9.10.2015.
 */
public class V1_13__change_termsUrl_to_unlocalized_url implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_13__change_termsUrl_to_unlocalized_url.class);
    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final String MAPFULL = "mapfull";
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
            final Bundle mapfull = view.getBundleByName(MAPFULL);
            if (mapfull != null) {
                JSONObject config = mapfull.getConfigJSON();
                JSONArray plugins = config.getJSONArray("plugins");
                for (int i=0 ; i < plugins.length() ; ++i) {
                    JSONObject plugin = plugins.getJSONObject(i);
                    if (plugin.get("id").equals("Oskari.mapframework.bundle.mapmodule.plugin.LogoPlugin")) {
                        JSONObject logoConfig = plugin.optJSONObject("config");
                        if(logoConfig == null) {
                            continue;
                        }
                        Object termsUrl = logoConfig.opt("termsUrl");
                        if(termsUrl == null) {
                            continue;
                        }
                        if(termsUrl instanceof JSONObject) {
                            logoConfig.put("termsUrl", ((JSONObject)termsUrl).optString("en"));
                        }
                        plugin.put("config", logoConfig);
                        plugins.put(i, plugin);
                    }
                }
                config.put("plugins", plugins);
                mapfull.setConfig(config.toString());
                VIEW_SERVICE.updateBundleSettingsForView(view.getId(), mapfull);
            }
            updatedViewCount++;
        }
        return list.size() == BATCH_SIZE;
    }
}
