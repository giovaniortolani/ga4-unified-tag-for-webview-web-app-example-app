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
import java.util.Objects;

public class WebviewInterface {

    public static final String TAG = "WebviewInterface";
    private final FirebaseAnalytics mAnalytics;

    public WebviewInterface(Context context) {
        mAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @JavascriptInterface
    public void postMessage(String data) {
        LOGD("postMessage:" + data);
        Bundle bundle = bundleFromJson(data);
        String command = (String) bundle.get("command");
        switch (Objects.requireNonNull(command)) {
            case "logEvent":
                logEvent((String) bundle.get("name"), (Bundle) bundle.get("parameters"));
                break;
            case "setUserProperty":
                setUserProperty((String) bundle.get("name"), (String) bundle.get("value"));
                break;
            case "setUserId":
                setUserId((String) bundle.get("userId"));
                break;
            case "setDefaultEventParameters":
                setDefaultEventParameters((Bundle) bundle.get("parameters"));
                break;
            case "setConsent":
                setConsent((HashMap<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus>) bundle.getSerializable("consentSettings"));
                break;
            case "resetAnalyticsData":
                resetAnalyticsData();
                break;
            case "setAnalyticsCollectionEnabled":
                setAnalyticsCollectionEnabled((boolean) bundle.getBoolean("value"));
                break;
        }
    }

    public void logEvent(String name, Bundle params) {
        LOGD("logEvent:" + name + "|" + params);
        mAnalytics.logEvent(name, params);
    }


    public void setUserProperty(String name, String value) {
        LOGD("setUserProperty:" + name + "=" + value);
        mAnalytics.setUserProperty(name, value);
    }

    public void setUserId(String userId) {
        LOGD("setUserId:" + userId);
        mAnalytics.setUserId(userId);
    }

    public void setDefaultEventParameters(Bundle params) {
        LOGD("setDefaultEventParameters:" + params);
        mAnalytics.setDefaultEventParameters(params);
    }

    public void setConsent(HashMap<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> consentSettings) {
        LOGD("setConsent:" + consentSettings);
        mAnalytics.setConsent(consentSettings);
    }

    public void resetAnalyticsData() {
        LOGD("resetAnalyticsData");
        mAnalytics.resetAnalyticsData();
    }

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
                } else if (key.equals("parameters")) {
                    result.putBundle("parameters", bundleFromJson(value.toString()));
                } else if (key.equals("consentSettings")) {
                    result.putSerializable(key, new HashMap<>(mapConsentFromJson(value.toString())));
                } else if (value instanceof Boolean) {
                    result.putBoolean(key, (boolean) value);
                } else if (value instanceof String) {
                    result.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    result.putInt(key, (Integer) value);
                } else if (value instanceof Double) {
                    result.putDouble(key, (Double) value);
                } else {
                    Log.w(TAG, "Value for key " + key + " not one of [String, Integer, Double, Boolean, JSONArray] or [parameters, consentSettings");
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, "Failed to parse JSON, returning empty Bundle.", e);
            return new Bundle();
        }

        return result;
    }

    private Map<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> mapConsentFromJson(String json) {
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
                    Log.w(TAG, "Value for key '" + key + "' not String");
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
