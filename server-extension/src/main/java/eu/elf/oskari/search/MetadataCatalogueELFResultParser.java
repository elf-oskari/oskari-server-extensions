package eu.elf.oskari.search;

import fi.mml.portti.service.search.SearchResultItem;
import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.rating.RatingService;
import fi.nls.oskari.rating.RatingServiceMybatisImpl;
import fi.nls.oskari.search.channel.MetadataCatalogueResultParser;
import fi.nls.oskari.util.PropertyUtil;
import org.apache.axiom.om.OMElement;

/**
 * Hooking information about service license to metadata search results.
 * If the service URL starts with the configured prefix, url is added to the
 * search result as license url. Property to configure the prefix is:
 * "search.channel.METADATA_CATALOGUE_CHANNEL.licenseUrlPrefix"
 */
public class MetadataCatalogueELFResultParser extends MetadataCatalogueResultParser {

    private static final Logger log = LogFactory.getLogger(MetadataCatalogueELFResultParser.class);

    public static final String KEY_LICENSE = "license";
    public static final String KEY_RATING = "score";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_ADMIN_RATING = "latestAdminRating";
    public static final String ELF_METADATA = "ELF_METADATA";

    private final RatingService ratingService = new RatingServiceMybatisImpl();

    private String urlPrefix = PropertyUtil.getOptional("search.channel.METADATA_CATALOGUE_CHANNEL.licenseUrlPrefix");

    @Override
    public SearchResultItem parseResult(OMElement elem, String locale) throws Exception {

        final SearchResultItem item = super.parseResult(elem, locale);
        log.debug("Parsed service url for", item.getResourceId(), ":", item.getGmdURL());

        // Check OnlineResource URL: if matches securitymanagerin url -> setup KEY_LICENSE = service url
        if(urlPrefix != null && item.getGmdURL() != null && item.getGmdURL().startsWith(urlPrefix)) {
            item.addValue(KEY_LICENSE, item.getGmdURL());
        }

        String[] rating = ratingService.getAverageRatingFor(ELF_METADATA, item.getResourceId());
        String adminRole = Role.getAdminRole().getName();
        String adminRating = ratingService.findLatestAdminRating(ELF_METADATA, item.getResourceId(), adminRole);

        if(rating == null)
            rating = new String[] {"0","0"};

        item.addValue(KEY_RATING, rating[0]);
        item.addValue(KEY_AMOUNT, rating[1]);
        item.addValue(KEY_ADMIN_RATING, adminRating);
        return item;
    }
}
