package eu.elf.oskari.metadatafeedback;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ResponseHelper;
import fi.nls.oskari.domain.Role;
import fi.nls.oskari.util.PropertyUtil;
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

import org.apache.commons.codec.binary.Base64;

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

        if(log.isDebugEnabled()) printRequestData(params.getHttpParam("data"));
        //getRole(params);

        String feedbackServerUrl = PropertyUtil.get("oskari.elf.feedback.server");
        sendFeedBackToServer(feedbackServerUrl + "items/", params);


        JSONArray result = new JSONArray();


        try {
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject jsonData = (org.json.simple.JSONObject)parser.parse(params.getHttpParam("data"));
            String resourceId = (String)jsonData.get("primaryTargetCode");

            String average = MetadataFeedback.getAverage(resourceId);
            log.debug("average: " + average);


            JSONObject item = new JSONObject();
            item.put("id", resourceId);
            item.put("rating", average);
            result.put(item);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionException("Failed to save feedback");
        }
        ResponseHelper.writeResponse(params, result);
    }


    private String getRole(ActionParameters params){
        // TODO check which role to get and make also null checks
        Set roles = params.getUser().getRoles();

        Role returnRole = null;
        for(Iterator rolesIterator = roles.iterator(); rolesIterator.hasNext(); ){
            returnRole = (Role)rolesIterator.next();
            log.debug("role: " + returnRole.getName());
        }
        return returnRole.getName();
    }

    private void sendFeedBackToServer(String url, ActionParameters params){

        try{
            org.json.simple.JSONObject requestParameters = getRequestParameters(params.getHttpParam("data"));

            log.debug("URL: " + url);

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            String name = PropertyUtil.get("oskari.elf.feedback.username");
            String password = PropertyUtil.get("oskari.elf.feedback.password");


            String authString = name + ":" + password;
            log.debug("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            log.debug("Base64 encoded auth string: " + authStringEnc);


            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Authorization", "Basic " + authStringEnc);


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

        } catch (Exception e){
            log.error(e.getMessage());
            log.error(e.toString());
        }

    }

    private String createJSONObjectForPOSTRequest(org.json.simple.JSONObject requestParameters){


        JSONObject JSONbase = new JSONObject();

        JSONObject jsonDateTime = JSONHelper.createJSONObject("DateTime", getTimeStamp());

        JSONHelper.putValue(JSONbase, "dateStamp", jsonDateTime);
        JSONHelper.putValue(JSONbase, "subject", requestParameters.get("subject"));


        JSONObject jsonGVQ_UserRoleCode = JSONHelper.createJSONObject("GVQ_UserRoleCode", "NonResearchEndUser");
        JSONHelper.putValue(JSONbase, "userRole", jsonGVQ_UserRoleCode);


        JSONObject jsonCharacterString = JSONHelper.createJSONObject("CharacterString", (String)requestParameters.get("primaryTargetCode"));
        JSONObject jsonCharacterString2 = JSONHelper.createJSONObject("CharacterString", "ELF_METADATA");


        JSONObject JSONbase2 = new JSONObject();
        JSONHelper.putValue(JSONbase2, "code", jsonCharacterString);
        JSONHelper.putValue(JSONbase2, "codeSpace", jsonCharacterString2);
        JSONObject jsonMD_Identifier = JSONHelper.createJSONObject("MD_Identifier", JSONbase2);


        JSONArray jsonArray = JSONHelper.createJSONArray(jsonMD_Identifier);
        JSONObject jsonReSourceRef = JSONHelper.createJSONObject("resourceRef", jsonArray);

        JSONArray jsonArray2 = JSONHelper.createJSONArray(jsonReSourceRef);

        JSONHelper.putValue(JSONbase, "primaryTarget", jsonArray2);


        JSONObject ratingJSONObject  = new JSONObject();

        JSONHelper.putValue(ratingJSONObject, "justification", requestParameters.get("justification"));
        JSONHelper.putValue(ratingJSONObject, "score", Integer.parseInt((String) requestParameters.get("score")));


        JSONArray jsonArrayx = new JSONArray();
        jsonArrayx.put(ratingJSONObject);

        JSONHelper.put(JSONbase,"rating", jsonArrayx);
        JSONHelper.putValue(JSONbase, "user", "/api/v1/feedback/userinformation/1/");

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
        log.debug("Got from FrontEnd:" + data);
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
