package me.hanqin.demos.email.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.Toast;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import me.hanqin.demos.email.EmailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

public class MailUtility {

    public static final String TO_ME = "hanhaify@gmail.com";

    public static void sendMail(Activity activity, CharSequence subject, CharSequence text) {
        Intent intent = mailToIntent(subject, text);

        activity.startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    public static void sendMailEnhanced(Activity activity, CharSequence subject, CharSequence text) {
        Intent intent = mailToIntent(subject, text);

        //Find all apps that supports a mail with "message/rfc822" support
        List<ResolveInfo> resolveInfos = activity.getPackageManager().queryIntentActivities(intent, 0);

        //Your filter logic
        Iterable<ResolveInfo> filtered = filter(resolveInfos, new ImplementsYourFilterHere());

        //How you would like to arrange the apps
        ArrayList<ResolveInfo> list = newArrayList(filtered);
        Collections.sort(list, new YourSortLogic());

        //More customization before displaying chooser dialog
        //Like in this post:
        //http://stackoverflow.com/a/12804063
        Iterable<Intent> intents = transform(list, new BuildYourOwnIntent(intent));
        List<Intent> intentsToBroadcast = newArrayList(intents);

        if (intentsToBroadcast.isEmpty()) {
            activity.startActivity(Intent.createChooser(intent, "Send mail..."));
            return;
        }

        Intent chooserIntent = Intent.createChooser(intentsToBroadcast.remove(intentsToBroadcast.size() - 1), "Send mail ...");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentsToBroadcast.toArray(new Parcelable[intentsToBroadcast.size()]));
        activity.startActivity(chooserIntent);
    }

    private static Intent mailToIntent(CharSequence subject, CharSequence text) {
        Intent intent = new Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", TO_ME, null));
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{TO_ME});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        return intent;
    }

    public static void sendMailWithJavaApi(Activity activity, CharSequence subject, CharSequence text) {
        Toast.makeText(activity, "Working in progress", Toast.LENGTH_LONG).show();
    }

    private static class ImplementsYourFilterHere implements Predicate<ResolveInfo> {
        @Override
        public boolean apply(ResolveInfo resolveInfo) {
            return true;
        }
    }

    private static class YourSortLogic implements Comparator<ResolveInfo> {
        @Override
        public int compare(ResolveInfo resolveInfo, ResolveInfo anotherResolveInfo) {
            return resolveInfo.activityInfo.name.compareTo(anotherResolveInfo.activityInfo.name);
        }
    }

    private static class BuildYourOwnIntent implements Function<ResolveInfo, Intent> {
        private Intent mailtoIntent;

        public BuildYourOwnIntent(Intent mailtoIntent) {
            this.mailtoIntent = mailtoIntent;
        }

        @Override
        public Intent apply(ResolveInfo resolveInfo) {
            Intent result = new Intent(mailtoIntent);
            result.setPackage(resolveInfo.activityInfo.packageName);
            result.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
            return result;
        }
    }
}
