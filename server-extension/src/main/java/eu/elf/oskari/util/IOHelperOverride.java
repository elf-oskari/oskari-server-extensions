package eu.elf.oskari.util;

import fi.nls.oskari.util.IOHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Oskari team on 24.10.2017.
 */
public class IOHelperOverride extends IOHelper {
    public static String getParams(Map<String, String> kvps) {
        if (kvps == null || kvps.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : kvps.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (key == null || key.isEmpty() || value == null) {
                continue;
            }
            try {
                final String keyEnc = URLEncoder.encode(key, DEFAULT_CHARSET);
                final String valueEnc = URLEncoder.encode(value, DEFAULT_CHARSET);
                if (!first) {
                    sb.append('&');
                }
                sb.append(keyEnc).append('=').append(valueEnc);
                first = false;
            } catch (UnsupportedEncodingException ignore) {
                // Ignore the exception, UTF-8 _IS_ supported
            }
        }
        return sb.toString();
    }
}
