package eu.elf.oskari.metadatafeedback;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ResponseHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by RLINKALA on 16.3.2015.
 */
@OskariActionRoute("GiveMetadataFeedback")
public class GiveMetadataFeedbackHandler extends ActionHandler {

    @Override
    public void init() {

    }

    @Override
    public void handleAction(ActionParameters params) throws ActionException {


        sendFeedBackFormToServer(params);

        String role = getRole(params);


        JSONArray result = new JSONArray();

        try {
            JSONObject item = new JSONObject();
            item.put("id", "xxxxxxxxxxxxxxxx");
            item.put("rating", "4");
            result.put(item);
        } catch (Exception e) {
            throw new ActionException("Failed to save feedback");

        }
        ResponseHelper.writeResponse(params, result);
    }

    private void sendFeedBackFormToServer(ActionParameters params){
        String JSONData = params.getHttpParam("data");

        JSONObject jsonDataObject = JSONHelper.createJSONObject(JSONData);

    }

    private String getRole(ActionParameters params){
        // TODO check which role to get and make also null checks
        // get first role
        Set roles = params.getUser().getRoles();
        Iterator rolesIter = roles.iterator();

        return (String)rolesIter.next();
    }

}
