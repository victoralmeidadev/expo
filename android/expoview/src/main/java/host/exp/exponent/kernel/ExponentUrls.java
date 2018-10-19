// Copyright 2015-present 650 Industries. All rights reserved.

package host.exp.exponent.kernel;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import host.exp.exponent.Constants;
import expolib_v1.okhttp3.Request;

public class ExponentUrls {

  private static final List<String> HTTPS_HOSTS = new ArrayList<>();
  static {
    HTTPS_HOSTS.add("exp.host");
    HTTPS_HOSTS.add("exponentjs.com");
  }

  private static boolean isHttpsHost(final String host) {
    for (int i = 0; i < HTTPS_HOSTS.size(); i++) {
      if (HTTPS_HOSTS.get(i).equals(host)) {
        return true;
      }
    }

    return false;
  }

  public static String toHttp(final String rawUrl) {
    if (rawUrl.startsWith("http")) {
      return rawUrl;
    }

    Uri uri = Uri.parse(rawUrl);
    boolean useHttps = isHttpsHost(uri.getHost()) || rawUrl.startsWith("exps");
    return uri.buildUpon().scheme(useHttps ? "https" : "http").build().toString();
  }

  public static Request.Builder addExponentHeadersToUrl(String urlString) {
    // TODO: set user agent
    Request.Builder builder = new Request.Builder()
        .url(urlString)
        .header("Exponent-SDK-Version", Constants.SDK_VERSIONS)
        .header("Exponent-Platform", "android");

    if (ExpoViewKernel.getInstance().getVersionName() != null) {
      builder.header("Exponent-Version", ExpoViewKernel.getInstance().getVersionName());
    }

    return builder;
  }

  public static Request.Builder addExponentHeadersToManifestUrl(String urlString, boolean isShellAppManifest) {
    Request.Builder builder = addExponentHeadersToUrl(urlString)
        .header("Accept", "application/expo+json,application/json");

    if (KernelConfig.FORCE_UNVERSIONED_PUBLISHED_EXPERIENCES) {
      builder.header("Exponent-SDK-Version", "UNVERSIONED");
    }

    if (isShellAppManifest) {
      builder.header("Expo-Release-Channel", Constants.RELEASE_CHANNEL);
    }

    return builder;
  }
}
