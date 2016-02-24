package eu.elf.oskari.sitemap;

import fi.mml.portti.service.search.*;
import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.data.GetGeoLocatorSearchResultHandler;
import fi.nls.oskari.search.channel.MetadataCatalogueChannelSearchService;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.PropertyUtil;
import fi.nls.oskari.util.ServiceFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RLINKALA on 24.2.2016.
 */
@OskariActionRoute("MetaDataSiteMapPopulator")
public class MetadataSitemapPopulator extends ActionHandler {

    private final static Logger log = LogFactory.getLogger(MetadataSitemapPopulator.class);

    private final static String SITEMAP_PATH = "sitemap.path";
    private static String DOMAIN_URL = "oskari.domain";

    private static final SearchService service = ServiceFactory.getSearchService();

    @Override
    public void handleAction(ActionParameters params) throws ActionException {

        log.debug("creating the sitemap");
        String sitemapPath = PropertyUtil.get(SITEMAP_PATH, "C:\\Omat\\temppi\\filename.txt");

        try{
            createSiteMapFile(sitemapPath);
        }catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }

    }

    private void createSiteMapFile(String path) throws Exception {

        File file = new File(path);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        bw.write(getContent());
        bw.write("</urlset>");
        bw.close();

        log.debug("File created to location: " + path);

    }

    private String getContent() throws Exception{

        String domain = PropertyUtil.get(DOMAIN_URL, "http://localhost:8884");

        StringBuffer sb = new StringBuffer();
        List<String> metadataUuids = getUuids();

        if(metadataUuids == null || metadataUuids.size() == 0)
            throw new Exception("things are bad... there are no UUids");

        log.debug("size of uuid list: " + metadataUuids.size());

        for(String metadataId : metadataUuids){
            sb.append("<url><loc>");
            sb.append(domain+"/?metadata=");
            sb.append(metadataId);
            sb.append("</loc></url>");
        }
        return sb.toString();
    }


    private List<String> getUuids() {

        List<String> uuids = new ArrayList<>();

        final SearchCriteria sc = new SearchCriteria();
        final String language = "en";
        sc.setLocale(language);
        sc.setSRS("EPSG:3857");
        sc.setSearchString("*");

        sc.addChannel(MetadataCatalogueChannelSearchService.ID);
        final Query query = service.doSearch(sc);
        final ChannelSearchResult searchResult = query.findResult(MetadataCatalogueChannelSearchService.ID);

        log.debug("done search... now creating a list");

        for (SearchResultItem item : searchResult.getSearchResultItems()) {
            if(item.getResourceId() != null){
                log.debug("Adding id: " + item.getResourceId());
                uuids.add(item.getResourceId());
            }
        }
        return uuids;
    }
}
