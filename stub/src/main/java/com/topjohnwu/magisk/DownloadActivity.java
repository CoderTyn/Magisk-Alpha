package com.topjohnwu.magisk;

import static android.R.string.no;
import static android.R.string.ok;
import static android.R.string.yes;
import static com.topjohnwu.magisk.DelegateApplication.dynLoad;
import static com.topjohnwu.magisk.R2.string.dling;
import static com.topjohnwu.magisk.R2.string.no_internet_msg;
import static com.topjohnwu.magisk.R2.string.relaunch_app;
import static com.topjohnwu.magisk.R2.string.upgrade_msg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.topjohnwu.magisk.net.Networking;
import com.topjohnwu.magisk.net.Request;
import com.topjohnwu.magisk.utils.APKInstall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class DownloadActivity extends Activity {

    private static final String APP_NAME = "Magisk";
    private static final String CDN_URL = "https://cdn.jsdelivr.net/gh/vvb2060/magisk_files@%s/%s";

    private String apkLink;
    private String sha;
    private Context themed;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themed = new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault);

        // Inject resources
        loadResources();

        if (Networking.checkNetworkStatus(this)) {
            fetchAPKURL();
        } else {
            new AlertDialog.Builder(themed)
                    .setCancelable(false)
                    .setTitle(APP_NAME)
                    .setMessage(getString(no_internet_msg))
                    .setNegativeButton(ok, (d, w) -> finish())
                    .show();
        }
    }

    private void fetchAPKURL() {
        dialog = ProgressDialog.show(themed, "", "", true);
        String url = "https://api.github.com/repos/vvb2060/magisk_files/branches/alpha";
        request(url).getAsJSONObject(this::handleCanary);
    }

    private void error(Throwable e) {
        Log.e(getClass().getSimpleName(), "", e);
        finish();
    }

    private Request request(String url) {
        return Networking.get(url).setErrorHandler((conn, e) -> error(e));
    }

    private void handleCanary(JSONObject json) {
        try {
            sha = json.getJSONObject("commit").getString("sha");
            String url = String.format(CDN_URL, sha, "alpha.json");
            request(url).getAsJSONObject(this::handleJSON);
        } catch (JSONException e) {
            error(e);
        }
    }

    private void handleJSON(JSONObject json) {
        dialog.dismiss();
        try {
            apkLink = json.getJSONObject("magisk").getString("link");
            apkLink = String.format(CDN_URL, sha, apkLink);
            new AlertDialog.Builder(themed)
                    .setCancelable(false)
                    .setTitle(APP_NAME)
                    .setMessage(getString(upgrade_msg))
                    .setPositiveButton(yes, (d, w) -> dlAPK())
                    .setNegativeButton(no, (d, w) -> finish())
                    .show();
        } catch (JSONException e) {
            error(e);
        }
    }

    private void dlAPK() {
        dialog = ProgressDialog.show(themed, getString(dling), getString(dling) + " " + APP_NAME, true);
        // Download and upgrade the app
        File apk = dynLoad ? DynAPK.current(this) : new File(getCacheDir(), "manager.apk");
        request(apkLink).setExecutor(AsyncTask.THREAD_POOL_EXECUTOR).getAsFile(apk, file -> {
            if (dynLoad) {
                InjectAPK.setup(this);
                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(themed, relaunch_app, Toast.LENGTH_LONG).show();
                    finish();
                });
            } else {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    APKInstall.install(this, file);
                    finish();
                });
            }
        });
    }

    private void loadResources() {
        File apk = new File(getCacheDir(), "res.apk");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKey key = new SecretKeySpec(Bytes.key(), "AES");
            IvParameterSpec iv = new IvParameterSpec(Bytes.iv());
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            InputStream is = new CipherInputStream(new ByteArrayInputStream(Bytes.res()), cipher);
            try (InputStream gzip = new GZIPInputStream(is);
                 OutputStream out = new FileOutputStream(apk)) {
                byte[] buf = new byte[4096];
                for (int read; (read = gzip.read(buf)) >= 0;) {
                    out.write(buf, 0, read);
                }
            }
            DynAPK.addAssetPath(getResources().getAssets(), apk.getPath());
        } catch (Exception e) {
            // Should not happen
            e.printStackTrace();
        }
    }

}
