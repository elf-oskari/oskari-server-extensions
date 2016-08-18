package flyway.elf;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.db.ViewHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

/**
 * Created by PHELESUO on 3.2.2016.
 */
public class V1_4__add_view_with_projection_epsg_3035 implements JdbcMigration {
    private static final Logger LOG = LogFactory.getLogger(V1_4__add_view_with_projection_epsg_3035.class);
    public void migrate(Connection connection)
            throws Exception {
        ViewHelper.insertView(connection, "elf-guest-view-EPSG_3035.json");
    }
}