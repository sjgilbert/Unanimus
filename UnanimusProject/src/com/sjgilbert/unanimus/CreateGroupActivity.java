package com.sjgilbert.unanimus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.sjgilbert.unanimus.parsecache.ParseCache;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.Locale;

import static com.sjgilbert.unanimus.FriendPickerActivity.FPA;
import static com.sjgilbert.unanimus.PlacePickActivity.PPA;

/**
 * Activity for creating group.  Calls 3 other activities for input to build group.
 */
public class CreateGroupActivity extends UnanimusActivityTitle {
    public static final String CGA = "cgaContainer";
    private static final String TAG = "cga";
    @SuppressWarnings("FieldCanBeLocal")
    private final int NO_REQUEST = -1;
    private final int FPA_REQUEST = 1;
    private final int PPA_REQUEST = 2;
    private final int GSPA_REQUEST = 3;

    private final CgaContainer cgaContainer = new CgaContainer();

    public CreateGroupActivity() {
        super(TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launchNext(NO_REQUEST);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        log(
                ELog.i,
                String.format(
                        Locale.getDefault(),
                        "%s.  %s: %d  %s: %d",
                        "Received result",
                        "Request code",
                        requestCode,
                        "Result code",
                        resultCode
                )
        );
        if (RESULT_OK != resultCode) {
            log(
                    ELog.w,
                    "CreateGroupActivity got non-OK result from activity"
            );

            finish();
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    switch (requestCode) {
                        case GSPA_REQUEST:
                            processGspaResult(data);
                            break;
                        case FPA_REQUEST:
                            processFpaResult(data);
                            break;
                        case PPA_REQUEST:
                            processPpaResult(data);
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                } catch (IContainer.NotSetException e) {
                    log(ELog.e, e.getMessage(), e);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (!cgaContainer.isSet()) return;

                cgaContainer.commit();

                Intent intent = new Intent(CreateGroupActivity.this, VotingActivity.class);
                intent.putExtra("UnanimusGroup", cgaContainer.getAsBundle());
                startActivity(intent);

                UnanimusGroup.Builder builder;

                builder = new UnanimusGroup.Builder(cgaContainer);

                builder.getInBackground(new UnanimusGroup.Builder.Callback() {
                    @Override
                    public void done(final UnanimusGroup unanimusGroup) {
                        unanimusGroup.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    log(ELog.e, e.getMessage(), e);
                                    return;
                                }

                                log(
                                        ELog.i,
                                        String.format(
                                                Locale.getDefault(),
                                                "%s.  %s: %s",
                                                "Successfully saved Unanimus group",
                                                ParseCache.OBJECT_ID,
                                                unanimusGroup.getObjectId()
                                        )
                                );

                                String objectId = unanimusGroup.getObjectId();

                                ParseQuery parseQuery = ParseQuery.getQuery(UnanimusGroup.class)
                                        .whereEqualTo(ParseCache.OBJECT_ID, objectId);

                                ParseCache.parseCache.put(
                                        objectId,
                                        (ParseQuery<ParseObject>) parseQuery
                                );
                            }
                        });
                    }
                });

                finish();
            }
        }.execute();

        launchNext(requestCode);
    }

    private void launchNext(int requestCode) {
        if (
                requestCode != GSPA_REQUEST
                        && cgaContainer.getGspaContainer() == null)
            startGspaForResult();
        else if (
                requestCode != PPA_REQUEST
                        && cgaContainer.getPpaContainer() == null)
            startPpaForResult();
        else if (
                requestCode != FPA_REQUEST
                        && cgaContainer.getFpaContainer() == null)
            startFpaForResult();
    }

    private void startGspaForResult() {
        startActivityForResult(
                new Intent(this, GroupSettingsPickerActivity.class),
                GSPA_REQUEST
        );
    }

    private void setGspaContainer(Bundle bundle) throws IContainer.NotSetException {
        cgaContainer.setGspaContainer(bundle);
    }

    private void processGspaResult(Intent data) throws IContainer.NotSetException {
        final Bundle gspaBundle = data.getBundleExtra(GroupSettingsPickerActivity.GSPA);
        setGspaContainer(gspaBundle);
    }

    private void startFpaForResult() {
        startActivityForResult(
                new Intent(this, FriendPickerActivity.class),
                FPA_REQUEST
        );
    }

    private void setFpaContainer(Bundle bundle) throws IContainer.NotSetException {
        cgaContainer.setFpaContainer(bundle);
    }

    private void processFpaResult(Intent data) throws IContainer.NotSetException {
        Bundle fpaBundle = data.getBundleExtra(FPA);
        setFpaContainer(fpaBundle);
    }

    private void startPpaForResult() {
        startActivityForResult(
                new Intent(this, PlacePickActivity.class),
                PPA_REQUEST
        );
    }

    private void setPpaContainer(Bundle bundle) throws IContainer.NotSetException {
        cgaContainer.setPpaContainer(bundle);
    }

    private void processPpaResult(Intent data) throws IContainer.NotSetException {
        Bundle ppaBundle = data.getBundleExtra(PPA);
        setPpaContainer(ppaBundle);
    }

}
