/*
 *    Copyright 2015 David Newell
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
/**
 * This class is a service which will use the Google Play services API
 * (location services) to poll the user's device for current location
 * and send it to the server as a name/value pair.
 * TODO: Refactor to send JSON or similar, for easier extension. (eg. to send more contextual data.)
 */
package ca.lightseed.winston;

import android.os.AsyncTask;
import android.util.Log;
// Realizing that learning with deprecated tools isn't the best strategy
// TODO: refactor with more modern alternative
import org.apache.http.params.HttpParams;

import java.io.IOException;

/**
 * Created by David on 2015-12-23.
 */
public class LocationSendService extends AsyncTask<String, String, Boolean> {

    public LocationSendService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("location", arg0[1]));

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(arg0[0]);
        HttpParams httpParameters = new BasicHttpParams();

        httpclient = new DefaultHttpClient(httpParameters);

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response;
            response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

                Log.e("Google", "Server Responded OK");

            } else {

                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
