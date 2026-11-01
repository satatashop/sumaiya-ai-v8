package com.sumaiya.aiassistant;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.JSObject;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@CapacitorPlugin(name = "AppList")
public class AppListPlugin extends Plugin {

    @PluginMethod
    public void getInstalledApps(PluginCall call) {
        try {
            PackageManager pm = getContext().getPackageManager();

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ApplicationInfo> apps =
                pm.getInstalledApplications(0);

            JSONArray result = new JSONArray();

            for (ApplicationInfo app : apps) {
                Intent launchIntent =
                    pm.getLaunchIntentForPackage(app.packageName);

                if (launchIntent != null) {
                    JSONObject item = new JSONObject();

                    item.put("name", pm.getApplicationLabel(app).toString());
                    item.put("packageName", app.packageName);

                    result.put(item);
                }
            }

            JSObject response = new JSObject();
            response.put("apps", result);

            call.resolve(response);

        } catch (Exception e) {
            call.reject("অ্যাপ তালিকা পাওয়া যায়নি", e);
        }
    }

    @PluginMethod
    public void openApp(PluginCall call) {
        String packageName = call.getString("packageName");

        if (packageName == null) {
            call.reject("অ্যাপের নাম পাওয়া যায়নি");
            return;
        }

        try {
            Intent launchIntent =
                getContext().getPackageManager()
                .getLaunchIntentForPackage(packageName);

            if (launchIntent == null) {
                call.reject("এই অ্যাপ খোলা যাচ্ছে না");
                return;
            }

            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(launchIntent);

            call.resolve();

        } catch (Exception e) {
            call.reject("অ্যাপ খোলা যায়নি", e);
        }
    }
}
