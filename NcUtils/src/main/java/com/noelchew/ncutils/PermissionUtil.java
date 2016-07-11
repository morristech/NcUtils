package com.noelchew.ncutils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.greysonparrelli.permiso.Permiso;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by noelchew on 3/31/16.
 */
public class PermissionUtil {
    public static void getPermissionPickPictureVideo(final Context context, final PermissionListener listener) {
        ArrayList<Integer> rationaleStringResourceArrayList = new ArrayList<>();
        rationaleStringResourceArrayList.add(R.string.ncutils_permission_rationale_feature_pick_picture);
        rationaleStringResourceArrayList.add(R.string.ncutils_permission_rationale_feature_pick_video);
        getPermission(context, listener, rationaleStringResourceArrayList, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static void getPermissionTakePicture(final Context context, final PermissionListener listener) {
        ArrayList<Integer> rationaleStringResourceArrayList = new ArrayList<>();
        rationaleStringResourceArrayList.add(R.string.ncutils_permission_rationale_feature_take_picture);
        getPermission(context, listener, rationaleStringResourceArrayList, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static void getPermissionTakeVideo(final Context context, final PermissionListener listener) {
        ArrayList<Integer> rationaleStringResourceArrayList = new ArrayList<>();
        rationaleStringResourceArrayList.add(R.string.ncutils_permission_rationale_feature_take_video);
        getPermission(context, listener, rationaleStringResourceArrayList, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static void getPermissionVoiceMessage(final Context context, final PermissionListener listener) {
        ArrayList<Integer> rationaleStringResourceArrayList = new ArrayList<>();
        rationaleStringResourceArrayList.add(R.string.ncutils_permission_rationale_feature_voice_messaging);
        getPermission(context, listener, rationaleStringResourceArrayList, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static void getPermissionVoiceCall(final Context context, final PermissionListener listener) {
        ArrayList<Integer> rationaleStringResourceArrayList = new ArrayList<>();
        rationaleStringResourceArrayList.add(R.string.ncutils_permission_rationale_feature_voice_call);
        getPermission(context, listener, rationaleStringResourceArrayList, Manifest.permission.RECORD_AUDIO);
    }

    public static void getPermissionVideoCall(final Context context, final PermissionListener listener) {
        ArrayList<Integer> rationaleStringResourceArrayList = new ArrayList<>();
        rationaleStringResourceArrayList.add(R.string.ncutils_permission_rationale_feature_video_call);
        getPermission(context, listener, rationaleStringResourceArrayList, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
    }

    public static void getPermissionShareLocation(final Context context, final PermissionListener listener) {
        ArrayList<Integer> rationaleStringResourceArrayList = new ArrayList<>();
        rationaleStringResourceArrayList.add(R.string.ncutils_permission_rationale_feature_share_location);
        getPermission(context, listener, rationaleStringResourceArrayList, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static void getPermissionAutoFillAreaCode(final Context context, final PermissionListener listener) {
        ArrayList<Integer> rationaleStringResourceArrayList = new ArrayList<>();
        rationaleStringResourceArrayList.add(R.string.ncutils_permission_rationale_feature_auto_fill_area_code);
        getPermission(context, listener, rationaleStringResourceArrayList, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static void getPermissionRecommendNewFriends(final Context context, final PermissionListener listener) {
        ArrayList<Integer> rationaleStringResourceArrayList = new ArrayList<>();
        rationaleStringResourceArrayList.add(R.string.ncutils_permission_rationale_feature_recommend_new_friends);
        getPermission(context, listener, rationaleStringResourceArrayList, Manifest.permission.READ_CONTACTS);
    }


    public static void getPermissionSaveMediaToStorage(final Context context, final PermissionListener listener) {
        ArrayList<Integer> rationaleStringResourceArrayList = new ArrayList<>();
        rationaleStringResourceArrayList.add(R.string.ncutils_permission_rationale_feature_save_media_to_storage);
        getPermission(context, listener, rationaleStringResourceArrayList, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static void getPermission(final Context context, final PermissionListener listener, final ArrayList<Integer> rationaleStringResourceArrayList, final String... permissions) {
        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                boolean hasPermanentlyDeniedPermission = false;
                if (resultSet.areAllPermissionsGranted()) {
                    listener.onPermissionGranted();
                } else {
                    ArrayList<String> permissionsDeniedArrayList = new ArrayList<>();
                    for (String strPermission : permissions) {
                        if (resultSet.toMap().containsKey(strPermission)) {
                            if (!resultSet.isPermissionGranted(strPermission)) {
                                permissionsDeniedArrayList.add(strPermission);
                            }
                            if (resultSet.isPermissionPermanentlyDenied(strPermission)) {
                                hasPermanentlyDeniedPermission = true;
                            }
                        }
                    }
                    String strDialogMessage = context.getString(R.string.ncutils_permission_denied_general) +
                            getPermissionGroupStrings(context, permissionsDeniedArrayList) +
                            context.getString(R.string.ncutils_permission_denied_general_part2) +
                            formatRationaleStrings(context, rationaleStringResourceArrayList);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.app_name)
                            .setCancelable(false);
                    if (hasPermanentlyDeniedPermission) {
                        strDialogMessage += "\n" + context.getString(R.string.ncutils_permission_denied_general_instructions);
                        builder.setNegativeButton(R.string.ncutils_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onPermissionDenied();
                            }
                        })
                                .setPositiveButton(R.string.ncutils_permission_settings, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        listener.onPermissionDenied();
                                        PermissionUtil.startInstalledAppDetailsActivity(context);
                                    }
                                });

                    } else {
                        builder.setNeutralButton(R.string.ncutils_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onPermissionDenied();
                            }
                        });
                    }
                    builder.setMessage(strDialogMessage)
                            .create()
                            .show();
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog(context.getString(R.string.app_name),
                        context.getString(R.string.ncutils_permission_rationale_general) +
                                getPermissionGroupStrings(context, permissions) +
                                context.getString(R.string.ncutils_permission_rationale_general_part2) +
                                formatRationaleStrings(context, rationaleStringResourceArrayList),
                        null,
                        callback);
            }
        }, permissions);
    }

    public interface PermissionListener {
        void onPermissionGranted();

        void onPermissionDenied();

    }

    private static String formatRationaleStrings(Context context, ArrayList<Integer> rationaleStringResourceArrayList) {
        String formattedRationaleStrings = "";
        for (int rationaleStringResource : rationaleStringResourceArrayList) {
            formattedRationaleStrings += context.getString(R.string.ncutils_permission_four_spaces) + context.getString(rationaleStringResource) + "\n";
        }
        return formattedRationaleStrings;
    }

    private static String getPermissionGroupStrings(Context context, String... permissions) {
        String permissionGroupStrings = "";
        ArrayList<String> permissionStringArrayList = removeRepetitivePermissionGroup(context, permissions);
        for (String permission : permissionStringArrayList) {
            permissionGroupStrings += context.getString(R.string.ncutils_permission_four_spaces) + permission + "\n";
        }
        return permissionGroupStrings;
    }

    private static String getPermissionGroupStrings(Context context, ArrayList<String> permissions) {
        String permissionGroupStrings = "";
        ArrayList<String> permissionStringArrayList = removeRepetitivePermissionGroup(context, permissions);
        for (String permission : permissionStringArrayList) {
            permissionGroupStrings += context.getString(R.string.ncutils_permission_four_spaces) + permission + "\n";
        }
        return permissionGroupStrings;
    }

    private static ArrayList<String> removeRepetitivePermissionGroup(Context context, String... permissions) {
        HashSet<String> permissionSet = new HashSet();
        ArrayList<String> permissionStringArrayList = new ArrayList<>();

        for (String permission : permissions) {
            permissionSet.add(context.getString(getPermissionGroupResourceString(permission)));
        }
        permissionStringArrayList.addAll(permissionSet);
        return permissionStringArrayList;
    }

    private static ArrayList<String> removeRepetitivePermissionGroup(Context context, ArrayList<String> permissions) {
        HashSet<String> permissionSet = new HashSet();
        ArrayList<String> permissionStringArrayList = new ArrayList<>();

        for (String permission : permissions) {
            permissionSet.add(context.getString(getPermissionGroupResourceString(permission)));
        }
        permissionStringArrayList.addAll(permissionSet);
        return permissionStringArrayList;
    }

    private static int getPermissionGroupResourceString(String permission) {
        if (permission.equalsIgnoreCase(Manifest.permission.READ_CALENDAR) ||
                permission.equalsIgnoreCase(Manifest.permission.WRITE_CALENDAR)) {
            return R.string.ncutils_permission_group_calendar;
        } else if (permission.equalsIgnoreCase(Manifest.permission.CAMERA)) {
            return R.string.ncutils_permission_group_camera;
        } else if (permission.equalsIgnoreCase(Manifest.permission.READ_CONTACTS) ||
                permission.equalsIgnoreCase(Manifest.permission.WRITE_CONTACTS) ||
                permission.equalsIgnoreCase(Manifest.permission.GET_ACCOUNTS)) {
            return R.string.ncutils_permission_group_contacts;
        } else if (permission.equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION) ||
                permission.equalsIgnoreCase(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return R.string.ncutils_permission_group_location;
        } else if (permission.equalsIgnoreCase(Manifest.permission.RECORD_AUDIO)) {
            return R.string.ncutils_permission_group_microphone;
        } else if (permission.equalsIgnoreCase(Manifest.permission.READ_PHONE_STATE) ||
                permission.equalsIgnoreCase(Manifest.permission.CALL_PHONE) ||
                permission.equalsIgnoreCase(Manifest.permission.READ_CALL_LOG) ||
                permission.equalsIgnoreCase(Manifest.permission.WRITE_CALL_LOG) ||
                permission.equalsIgnoreCase(Manifest.permission.ADD_VOICEMAIL) ||
                permission.equalsIgnoreCase(Manifest.permission.USE_SIP) ||
                permission.equalsIgnoreCase(Manifest.permission.PROCESS_OUTGOING_CALLS)) {
            return R.string.ncutils_permission_group_phone;
        } else if (permission.equalsIgnoreCase(Manifest.permission.BODY_SENSORS)) {
            return R.string.ncutils_permission_group_sensors;
        } else if (permission.equalsIgnoreCase(Manifest.permission.SEND_SMS) ||
                permission.equalsIgnoreCase(Manifest.permission.RECEIVE_SMS) ||
                permission.equalsIgnoreCase(Manifest.permission.READ_SMS) ||
                permission.equalsIgnoreCase(Manifest.permission.RECEIVE_WAP_PUSH) ||
                permission.equalsIgnoreCase(Manifest.permission.RECEIVE_MMS)) {
            return R.string.ncutils_permission_group_sms;
        } else if (permission.equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                permission.equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return R.string.ncutils_permission_group_storage;
        } else {
            return R.string.ncutils_permission_group_phone;
        }
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // ----------------------------- Basic Permission Utils - begin below ------------------------------ //

    /**
     * Returns true if the Activity has access to all given permissions.
     * Always returns true on platforms below M.
     *
     * @see Activity#checkSelfPermission(String)
     */
    public static boolean hasSelfPermission(Context context, String[] permissions) {
        // Below Android M all permissions are granted at install time and are already available.
        if (!isMNC()) {
            return true;
        }

        // Verify that all required permissions have been granted
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if the Activity has access to a given permission.
     * Always returns true on platforms below M.
     *
     * @see Activity#checkSelfPermission(String)
     */
    public static boolean hasSelfPermission(Context context, String permission) {
        // Below Android M all permissions are granted at install time and are already available.
        if (!isMNC()) {
            return true;
        }
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isMNC() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static PermissionStatus getPermissionStatus(Activity activity, String permission) {
        // Below Android M all permissions are granted at install time and are already available.
        if (!isMNC()) {
            return PermissionStatus.GRANTED;
        }
        if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            return PermissionStatus.GRANTED;
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            return PermissionStatus.NEVER_SHOW_AGAIN;
        } else {
            return PermissionStatus.DENIED;
        }
    }

    // returns NEVER_SHOW_AGAIN if any one is NEVER_SHOW_AGAIN
    // else returns DENIED if any one is DENIED
    // else returns GRANTED
    public static PermissionStatus getPermissionStatus(Activity activity, String[] permissions) {
        // Below Android M all permissions are granted at install time and are already available.
        if (!isMNC()) {
            return PermissionStatus.GRANTED;
        }

        // Verify that all required permissions have been granted
        PermissionStatus permissionsStatus = PermissionStatus.GRANTED;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                // do nothing
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return PermissionStatus.NEVER_SHOW_AGAIN;
            } else {
                permissionsStatus =  PermissionStatus.DENIED;
            }
        }

        return permissionsStatus;
    }

    public enum PermissionStatus {
        GRANTED, DENIED, NEVER_SHOW_AGAIN
    }

    public static void startInstalledAppDetailsActivity(final Context context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }
}