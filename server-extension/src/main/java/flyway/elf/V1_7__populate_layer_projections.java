package flyway.elf;

import fi.mml.map.mapwindow.service.wms.WebMapService;
import fi.mml.map.mapwindow.service.wms.WebMapServiceFactory;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.geometry.ProjectionHelper;
import fi.nls.oskari.map.layer.formatters.LayerJSONFormatterWMS;
import fi.nls.oskari.map.layer.formatters.LayerJSONFormatterWMTS;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.service.capabilities.CapabilitiesCacheService;
import fi.nls.oskari.service.capabilities.OskariLayerCapabilities;
import fi.nls.oskari.wfs.GetGtWFSCapabilities;
import fi.nls.oskari.wmts.WMTSCapabilitiesParser;
import fi.nls.oskari.wmts.domain.WMTSCapabilities;
import fi.nls.oskari.wmts.domain.WMTSCapabilitiesLayer;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Generates resource URL information for WMTS layers
 */
public class V1_7__populate_layer_projections implements JdbcMigration {

    private static final Logger LOG = LogFactory.getLogger(V1_7__populate_layer_projections.class);

    private static final CapabilitiesCacheService CAPABILITIES_SERVICE = OskariComponentManager.getComponentOfType(CapabilitiesCacheService.class);
    private static final WMTSCapabilitiesParser WMTSPARSER = new WMTSCapabilitiesParser();

    public void migrate(Connection connection) throws SQLException {

        List<OskariLayer> layers = getLayers(connection);
        LOG.info("Start populating supported crs for Oskari layers  - count:", layers.size());
        int progress = 0;
        int rowcount = 0;
        int tot = 0;
        int delta = 50000;  // Do commits after delta row intserts


        for (OskariLayer layer : layers) {
            switch (layer.getType()) {
                case "wmtslayer":
                    rowcount += populateWMTS(connection, layer);
                    break;
                case "wmslayer":
                    rowcount += populateWMS(connection, layer);
                    break;
                case "wfslayer":
                    rowcount += populateWFS(connection, layer);
                    break;
                default:
                    rowcount += populateOthers(connection, layer);
            }
            progress++;
            LOG.info("Layer projections populate in process: layer: ", layer.getId(), " - type:", layer.getType(), "---", progress, "/", layers.size());
            if(rowcount > delta){
                connection.commit();
                tot += rowcount;
                LOG.info("Intermediate commit done after ", tot, " rows");
                rowcount = 0;
            }
        }
        LOG.info("Total number of insert rows into  oskari_maplayer_projections table:  ", tot);
    }

    private int populateWMTS(Connection connection, OskariLayer layer) {
        int count = 0;
        try {
            // update
            OskariLayerCapabilities caps = CAPABILITIES_SERVICE.getCapabilities(layer);
            if (caps != null) {
                WMTSCapabilities parsed = WMTSPARSER.parseCapabilities(caps.getData());
                if (parsed != null) {
                    WMTSCapabilitiesLayer capsLayer = parsed.getLayer(layer.getName());
                    if (capsLayer != null) {
                        Set<String> crss = LayerJSONFormatterWMTS.getCRSs(capsLayer);
                        if (crss != null && crss.size() > 0) {
                            removeProjections(layer.getId(), connection);
                            insertProjections(layer.getId(), crss, connection);
                            count = crss.size();
                        } else {
                            LOG.info("No projections found for layer: ", layer.getName());
                        }


                    } else {
                        LOG.info("WMTS Layer projections populate / layer parse failed - layer: ", layer.getName());
                    }
                } else {
                    LOG.info("WMTS Layer projections populate / capabilities parse failed - layer: ", layer.getName());
                }
            } else {
                LOG.info("WMTS Layer projections populate / get capabilities failed - layer: ", layer.getName());
            }
        } catch (Exception e) {
            LOG.error(e, "Error getting WMTS capabilities for layer", layer);
        }
        return count;
    }

    private int populateWFS(Connection connection, OskariLayer layer) {
        int count = 0;
        try {
            // Get supported projections
            Map<String, Object> capa = GetGtWFSCapabilities.getGtDataStoreCapabilities(layer.getUrl(), layer.getVersion(), layer.getUsername(),
                    layer.getPassword(), layer.getSrs_name());


            if (capa != null) {
                Set<String> crss = GetGtWFSCapabilities.parseProjections(capa, layer.getName());
                if (crss == null) {
                    crss = new HashSet<>();
                }
                // Add default csr
                if (layer.getSrs_name() != null) {
                    if (crss.size() == 0 || !crss.contains(ProjectionHelper.shortSyntaxEpsg(layer.getSrs_name()))) {
                        crss.add(ProjectionHelper.shortSyntaxEpsg(layer.getSrs_name()));
                    }

                }

                if (crss.size() > 0) {
                    removeProjections(layer.getId(), connection);
                    insertProjections(layer.getId(), crss, connection);
                    count = crss.size();
                } else {
                    LOG.info("WFS Capabilities / no projections found - layer: ", layer.getName());
                }

            } else {
                LOG.info("WFS Capabilities / layer projections populate failed - layer: ", layer.getName());
            }
        } catch (Exception e) {
            LOG.error(e, "Error getting WFS capabilities for layer", layer);
        }
        return count;
    }

    private int populateOthers(Connection connection, OskariLayer layer) {
        int count = 0;
        try {

            Set<String> crss = new HashSet<>();

            // Add default csr
            if (layer.getSrs_name() != null) {
                crss.add(ProjectionHelper.shortSyntaxEpsg(layer.getSrs_name()));
            }

            if (crss.size() > 0) {
                removeProjections(layer.getId(), connection);
                insertProjections(layer.getId(), crss, connection);
                count = crss.size();
            } else {
                LOG.info("No projections found - layer: ", layer.getName(), " - type: ", layer.getType());
            }


        } catch (Exception e) {
            LOG.error(e, "Error getting capabilities for layer", layer);
        }
        return count;
    }

    private int populateWMS(Connection connection, OskariLayer layer) {
        int count = 0;
        try {
            // Get supported projections
            OskariLayerCapabilities capabilities = CAPABILITIES_SERVICE.getCapabilities(layer);
            if (capabilities != null) {
                // parse capabilities
                WebMapService wms = WebMapServiceFactory.createFromXML(layer.getName(), capabilities.getData());
                if (wms != null) {
                    Set<String> crss = LayerJSONFormatterWMS.getCRSs(wms);

                    if (crss != null && crss.size() > 0) {
                        removeProjections(layer.getId(), connection);
                        insertProjections(layer.getId(), crss, connection);
                        count = crss.size();

                    } else {
                        LOG.info("WMS no supported projections found for layer: ", layer.getName());
                    }
                } else {
                    LOG.info("WMS Layer projections populate / capabilities xml parse failed - layer: ", layer.getName());
                }

            } else {
                LOG.info("WMS Layer projections populate / get capabilities failed - layer: ", layer.getName());
            }
        } catch (Exception e) {
            LOG.error(e, "Error getting WMS capabilities for layer", layer);
        }
        return count;
    }

    private void insertProjections(int layerId, Set<String> crss, Connection conn) throws SQLException {
        final String sql = "INSERT INTO oskari_maplayer_projections (name, maplayerid) VALUES (?,?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            for (String crs : crss) {
                statement.setString(1, crs);
                statement.setInt(2, layerId);
                statement.executeUpdate();
            }
            statement.close();
        } catch (Exception ignored) {
        }

    }

    private void removeProjections(int layerId, Connection conn) throws SQLException {
        final String sql = "DELETE FROM oskari_maplayer_projections WHERE maplayerid=?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, layerId);
            statement.execute();
            statement.close();
        } catch (Exception ignored) {
        }


    }

    private void removeAllProjections(Connection conn) throws SQLException {
        final String sql = "DELETE FROM oskari_maplayer_projections";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.execute();
            statement.close();
        } catch (Exception ignored) {
        }


    }


    private List<OskariLayer> getLayers(Connection conn) throws SQLException {
        List<OskariLayer> layers = new ArrayList<>();
        // only process wmts-layers
        final String sql = "SELECT id, name, url, type, username, password, srs_name, version FROM oskari_maplayer";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    OskariLayer layer = new OskariLayer();
                    layer.setId(rs.getInt("id"));
                    layer.setName(rs.getString("name"));
                    layer.setUrl(rs.getString("url"));
                    layer.setType(rs.getString("type"));
                    layer.setUsername(rs.getString("username"));
                    layer.setPassword(rs.getString("password"));
                    layer.setSrs_name(rs.getString("srs_name"));
                    layer.setVersion(rs.getString("version"));
                    layers.add(layer);
                }
            }
        }
        return layers;
    }
}
