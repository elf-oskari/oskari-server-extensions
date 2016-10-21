package flyway.elf;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.JSONHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

/**
 *
 1) select views... where mapfull.conf.mapOptions.srsName = "EPSG:3035"
 2) update views...
 mapfull.conf.mapOptions.maxExtent = {bottom: 1528101.2618, left: -1426378.0132, right: 7293974.6215, top: 6446513.5222}
 ->
 mapfull.conf.mapOptions.maxExtent = {bottom: 1528101.2618, left: 2426378.0132, right: 6293974.6215, top: 5446513.5222}

 mapfull.conf.mapOptions.resolutions = [15107.79925117, 7553.89962559, 3776.94981279, 1888.47490640, 944.23745320, 472.11872660, 236.05936330, 118.02968165, 59.01484082, 29.50742041, 14.75371021, 7.376855103, 3.688427552, 1.844213776, 0.922106888, 0.461053444, 0.230526722, 0.115263361, 0.05763168, 0.02881584]

 */
public class V1_14__update_resolutions_and_extent implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_14__update_resolutions_and_extent.class);
    private static final ViewService VIEW_SERVICE = new ViewServiceIbatisImpl();
    private static final String MAPFULL = "mapfull";
    private static final int BATCH_SIZE = 50;
    private int updatedViewCount = 0;
    private JSONObject newExtent;
    private JSONArray newResolutions;

    public void migrate(Connection connection)
            throws Exception {
        buildNewConfigs();
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
            if (mapfull == null) {
                continue;
            }
            try {
                JSONObject config = mapfull.getConfigJSON();
                JSONObject opts = config.getJSONObject("mapOptions");
                if(!"EPSG:3035".equalsIgnoreCase(opts.optString("srsName"))) {
                    // only interested in 3035 maps
                    continue;
                }
                // update values
                JSONHelper.putValue(opts, "maxExtent", newExtent);
                JSONHelper.putValue(opts, "resolutions", newResolutions);

                // update changes back to db
                mapfull.setConfig(config.toString());
                VIEW_SERVICE.updateBundleSettingsForView(view.getId(), mapfull);
                updatedViewCount++;
            } catch(Exception ex) {
                LOG.warn(ex, "Couldn't update view", view.getId());
            }
        }
        return list.size() == BATCH_SIZE;
    }

    private void buildNewConfigs() throws Exception {
        newExtent = new JSONObject();
        JSONHelper.putValue(newExtent, "bottom", 1528101.2618);
        JSONHelper.putValue(newExtent, "left",   2426378.0132);
        JSONHelper.putValue(newExtent, "right",  6293974.6215);
        JSONHelper.putValue(newExtent, "top",    5446513.5222);

        newResolutions = new JSONArray();
        newResolutions.put(15107.79925117);
        newResolutions.put(7553.89962559);
        newResolutions.put(3776.94981279);
        newResolutions.put(1888.47490640);
        newResolutions.put(944.23745320);
        newResolutions.put(472.11872660);
        newResolutions.put(236.05936330);
        newResolutions.put(118.02968165);
        newResolutions.put(59.01484082);
        newResolutions.put(29.50742041);
        newResolutions.put(14.75371021);
        newResolutions.put(7.376855103);
        newResolutions.put(3.688427552);
        newResolutions.put(1.844213776);
        newResolutions.put(0.922106888);
        newResolutions.put(0.461053444);
        newResolutions.put(0.230526722);
        newResolutions.put(0.115263361);
        newResolutions.put(0.05763168);
        newResolutions.put(0.02881584);
    }
}
