package fi.nls.oskari;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

import fi.nls.oskari.cache.Cache;
import fi.nls.oskari.cache.CacheManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.elf.oskari.sitemap.MetadataSitemapPopulator;


/**
 * Created by RLINKALA on 24.2.2016.
 */
@Controller
public class ELFMapController {

    private static final Logger LOG = LogFactory.getLogger(ELFMapController.class);

    private static final String SITEMAPXML = "sitemap";
    private final Cache<String> siteMapCache = CacheManager.getCache(ELFMapController.class.getCanonicalName());

    /**
     * Returns SiteMap.xml when request http://[domain]/sitemap.xml is made.
     * Domain info is needed in the ext-poperties file.
     * Path info to the sitemap.xml is required in robots.txt.... Read more from Google
     *
     * @return
     */
    @RequestMapping(value="/sitemap.xml")
    @ResponseBody
    public String handle(){
        LOG.debug("building SiteMap");

        String siteMap = siteMapCache.get(SITEMAPXML);

        if(siteMap == null){
            try{
                MetadataSitemapPopulator metadataSitemapPopulator = new MetadataSitemapPopulator();
                siteMap = metadataSitemapPopulator.buildSiteMap();
                siteMapCache.put(SITEMAPXML, siteMap);
            }catch(Exception e){
                LOG.error("Sitemap creation failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return siteMap;
    }

}
