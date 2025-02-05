package mgks.os.swv;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// [START analytics_android_web_interface]
public class AnalyticsAndroidWebInterface {

    public static final String TAG = "AnalyticsAndroidWebInterface";
    private final FirebaseAnalytics mAnalytics;

    public AnalyticsAndroidWebInterface(Context context) {
        mAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @JavascriptInterface
    public void logEvent(String name, String jsonParams) {
        LOGD("logEvent:" + name + "|" + jsonParams);
        mAnalytics.logEvent(name, bundleFromJson(jsonParams));
    }

    @JavascriptInterface
    public void setUserProperty(String name, String value) {
        LOGD("setUserProperty:" + name + "=" + value);
        mAnalytics.setUserProperty(name, value);
    }

    @JavascriptInterface
    public void setUserId(String userId) {
        LOGD("setUserId:" + userId);
        mAnalytics.setUserId(userId);
    }

    @JavascriptInterface
    public void setDefaultEventParameters(String jsonParams) {
        LOGD("setDefaultEventParameters:" + jsonParams);
        mAnalytics.setDefaultEventParameters(bundleFromJson(jsonParams));
    }

    @JavascriptInterface
    public void setConsent(String jsonParams) {
        LOGD("setConsent:" + jsonParams);
        mAnalytics.setConsent(mapFromJson(jsonParams));
    }

    @JavascriptInterface
    public void resetAnalyticsData() {
        LOGD("resetAnalyticsData");
        mAnalytics.resetAnalyticsData();
    }

    @JavascriptInterface
    public void setAnalyticsCollectionEnabled(boolean value) {
        LOGD("setAnalyticsCollectionEnabled:" + value);
        mAnalytics.setAnalyticsCollectionEnabled(value);
    }

    private void LOGD(String message) {
        // Only log on debug builds, for privacy
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    private Bundle bundleFromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return new Bundle();
        }

        Bundle result = new Bundle();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);

                if (key.equals("items") && value instanceof JSONArray itemsArray) {
                    Bundle[] itemsBundles = new Bundle[itemsArray.length()];
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemObject = itemsArray.getJSONObject(i);
                        itemsBundles[i] = bundleFromJson(itemObject.toString());
                    }
                    result.putParcelableArray(key, itemsBundles);
                } else if (value instanceof String) {
                    result.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    result.putInt(key, (Integer) value);
                } else if (value instanceof Double) {
                    result.putDouble(key, (Double) value);
                } else {
                    Log.w(TAG, "Value for key " + key + " not one of [String, Integer, Double, JSONArray]");
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, "Failed to parse JSON, returning empty Bundle.", e);
            return new Bundle();
        }

        return result;
    }

    private Map<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> mapFromJson(String json) {
        // [START_EXCLUDE]
        Map<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> consentSettings = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                String statusValue = jsonObject.getString(key);
                FirebaseAnalytics.ConsentType consentType = mapKeyToConsentType(key);
                FirebaseAnalytics.ConsentStatus consentStatus = mapValueToConsentStatus(statusValue);

                // Add the mapping to the consentSettings map
                if (consentType != null && consentStatus != null) {
                    consentSettings.put(consentType, consentStatus);
                } else {
                    Log.w(TAG, "Value for key " + key + " not String");
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, "Failed to parse JSON, returning empty Bundle.", e);
            return new HashMap<>();
        }

        return consentSettings;
        // [END_EXCLUDE]
    }

    private static FirebaseAnalytics.ConsentType mapKeyToConsentType(String key) {
        return switch (key) {
            case "analytics_storage" -> FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE;
            case "ad_storage" -> FirebaseAnalytics.ConsentType.AD_STORAGE;
            case "ad_user_data" -> FirebaseAnalytics.ConsentType.AD_USER_DATA;
            case "ad_personalization" -> FirebaseAnalytics.ConsentType.AD_PERSONALIZATION;
            default -> null; // Unknown key
        };
    }

    private static FirebaseAnalytics.ConsentStatus mapValueToConsentStatus(String value) {
        return switch (value.toLowerCase()) {
            case "granted" -> FirebaseAnalytics.ConsentStatus.GRANTED;
            case "denied" -> FirebaseAnalytics.ConsentStatus.DENIED;
            default -> null; // Unknown value
        };
    }


}
// [END analytics_android_web_interface]