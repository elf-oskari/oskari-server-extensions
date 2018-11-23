package flyway.elf;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;


/**
 *  Set toolbar color scheme to white bg, gray hover
 */
public class V1_18__set_toolbar_colors implements JdbcMigration {
    private static final String BUNDLE_ID = "toolbar";
    private ViewService service = new ViewServiceIbatisImpl();

    public void migrate(Connection connection) throws Exception {
        final List<Long> views = FlywayHelper.getViewIdsForTypes(connection, "DEFAULT", "SYSTEM", "USER");
        for (Long viewId : views) {
            if (!FlywayHelper.viewContainsBundle(connection, BUNDLE_ID, viewId)) {
                continue;
            }
            final Bundle toolbar = FlywayHelper.getBundleFromView(connection, BUNDLE_ID, viewId);
            JSONObject conf = toolbar.getConfigJSON();
            conf.put("colours", new JSONObject("{\"hover\": \"#666\", \"background\": \"#ffffff\"}"));
            service.updateBundleSettingsForView(viewId, toolbar);
        }
    }
}
