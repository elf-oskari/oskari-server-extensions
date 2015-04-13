package eu.elf.oskari.metadatafeedback;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ResponseHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.sql.Timestamp;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by RLINKALA on 16.3.2015.
 */
@OskariActionRoute("GiveMetadataFeedback")
public class GiveMetadataFeedbackHandler extends ActionHandler {


    private static final Logger log = LogFactory.getLogger(GiveMetadataFeedbackHandler.class);

    @Override
    public void init() {

    }

    @Override
    public void handleAction(ActionParameters params) throws ActionException {

        String test = params.getHttpParam("test");
        log.debug("&&&&&&&&&:" + params.getHttpParam("data"));
        if(log.isDebugEnabled()) printRequestData(params.getHttpParam("data"));

        sendFeedBackToServer("https://geoviqua.stcorp.nl/devel/api/v1/feedback/items/", params);
        //sendFeedBackToServer("http://dev.paikkatietoikkuna.fi/gf/api/v1/feedback/items/", params);


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


    private String getRole(ActionParameters params){
        // TODO check which role to get and make also null checks
        Set roles = params.getUser().getRoles();

        String returnRole = null;
        for(Iterator rolesIterator = roles.iterator(); rolesIterator.hasNext(); ){
            returnRole = (String)rolesIterator.next();
            log.debug("role: " + returnRole);
        }
        return returnRole;
    }

    private void sendFeedBackToServer(String url, ActionParameters params){
        String USER_AGENT = "Mozilla/5.0";

        try{

            org.json.simple.JSONObject requestParameters = getRequestParameters(params.getHttpParam("data"));

            //String url = "https://geoviqua.stcorp.nl/devel/api/v1/feedback/items/";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");


            String postJSON = createJSONObjectForPOSTRequest(requestParameters);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postJSON);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            // For test use  --> Remove
            log.debug("\nSending 'POST' request to URL : " + url);
            log.debug("Post parameters : " + postJSON);
            log.debug("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            log.debug(response.toString());

        }catch(Exception e){
            System.out.println("MESSU: " + e.getMessage());
            System.out.println(e.toString());
        }

    }

    private String createJSONObjectForPOSTRequest(org.json.simple.JSONObject requestParameters){


        JSONObject JSONbase = new JSONObject();

        JSONObject jsonDateTime = JSONHelper.createJSONObject("DateTime", getTimeStamp());

        JSONHelper.putValue(JSONbase, "dateStamp", jsonDateTime);
        JSONHelper.putValue(JSONbase, "subject", requestParameters.get("subject"));


        JSONObject jsonGVQ_UserRoleCode = JSONHelper.createJSONObject("GVQ_UserRoleCode", "NonResearchEndUser");
        JSONHelper.putValue(JSONbase, "userRole", jsonGVQ_UserRoleCode);


        JSONObject jsonComment = JSONHelper.createJSONObject("comment", (String)requestParameters.get("userComment"));
        JSONObject jsonMimetype = JSONHelper.createJSONObject("mime-type", "text/plain");
        JSONHelper.putValue(JSONbase, "userComment", jsonComment);
        JSONHelper.putValue(JSONbase, "userComment", jsonMimetype);


        JSONObject jsonCharacterString = JSONHelper.createJSONObject("CharacterString", (String)requestParameters.get("primaryTargetCode"));
        JSONObject jsonCharacterString2 = JSONHelper.createJSONObject("CharacterString", (String)requestParameters.get("primaryTargetCodeSpace"));


        JSONObject JSONbase2 = new JSONObject();
        JSONHelper.putValue(JSONbase2, "code", jsonCharacterString);
        JSONHelper.putValue(JSONbase2, "codespace", jsonCharacterString2);
        JSONObject jsonMD_Identifier = JSONHelper.createJSONObject("MD_Identifier", JSONbase2);


        JSONArray jsonArray = JSONHelper.createJSONArray(jsonMD_Identifier);
        JSONObject jsonReSourceRef = JSONHelper.createJSONObject("resourceRef", jsonArray);

        JSONArray jsonArray2 = JSONHelper.createJSONArray(jsonReSourceRef);

        JSONHelper.putValue(JSONbase, "primaryTarget", jsonArray2);

        JSONArray jsonArrayx = new JSONArray();
        jsonArrayx.put(JSONHelper.createJSONObject("justification", (String)requestParameters.get("justification")));
        jsonArrayx.put(JSONHelper.createJSONObject("score", Integer.parseInt((String) requestParameters.get("score"))));

        JSONHelper.put(JSONbase,"rating", jsonArrayx);
        JSONHelper.putValue(JSONbase, "user", "testihemmo");

        log.debug("##############" + JSONbase.toString());

        return JSONbase.toString();
   }

    private String getTimeStamp(){

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        log.debug("timestamp: " + timestamp.toString());
        return timestamp.toString();
    }

    private org.json.simple.JSONObject getRequestParameters(String data) throws ActionException {

        try {
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject jsonData = (org.json.simple.JSONObject) parser.parse(data);
            return jsonData;

        } catch (Exception e) {
            log.debug(e.toString());
            throw new ActionException("Couldn't parse rating form's JSON");
        }
    }


    private String printRequestData(String data) throws ActionException{

        log.debug("data: " + data);
        try{
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject jsonData = (org.json.simple.JSONObject)parser.parse(data);
            log.debug(jsonData.get("subject"));
            log.debug(jsonData.get("score"));
            log.debug(jsonData.get("justification"));
            log.debug(jsonData.get("userRole"));
            log.debug(jsonData.get("userComment"));
            log.debug(jsonData.get("primaryTargetCode"));
            log.debug(jsonData.get("primaryTargetCodeSpace"));
            log.debug(jsonData.get("natureOfTarget"));
            log.debug(jsonData.get("expertiseLevel"));
            log.debug(jsonData.get("genUserRole"));
            log.debug(jsonData.get("username"));
            log.debug(jsonData.get("organisation"));
            log.debug(jsonData.get("position"));
            log.debug(jsonData.get("ciRole"));
            log.debug(jsonData.get("onlineReference"));

        }catch(Exception e){
            log.debug(e.toString());
            throw new ActionException("Couldn't parse rating form's JSON");
        }
        return null;

    }
}
