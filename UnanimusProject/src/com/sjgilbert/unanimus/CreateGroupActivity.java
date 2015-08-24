package com.sjgilbert.unanimus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.Locale;

import static com.sjgilbert.unanimus.FriendPickerActivity.FPA;
import static com.sjgilbert.unanimus.FriendPickerActivity.FpaContainer;
import static com.sjgilbert.unanimus.GroupSettingsPickerActivity.GspaContainer;
import static com.sjgilbert.unanimus.PlacePickActivity.PPA;
import static com.sjgilbert.unanimus.PlacePickActivity.PpaContainer;

/**
 * Activity for creating group.  Calls 3 other activities for input to build group.
 */
public class CreateGroupActivity extends UnanimusActivityTitle {
    private final int FPA_REQUEST = 1;
    private final int PPA_REQUEST = 2;
    private final int GSPA_REQUEST = 3;

    private CgaGroup cgaGroup;

    public CreateGroupActivity() {
        super("cga");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_group_activity);
        try {
            setTitleBar(R.string.cga_title, (ViewGroup) findViewById(R.id.create_group_activity));
        } catch (ClassCastException e) {
            log(ELog.e, e.getMessage(), e);
        }

        cgaGroup = new CgaGroup();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            return;
        }

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
                throw new UnsupportedOperationException();
        }
    }

    @SuppressWarnings({"UnusedParameters", "WeakerAccess", "unused"})
    public void cga_viewCreateGroup(View view) {
//        final ProgressDialog wait = new ProgressDialog(CreateGroupActivity.this);
//        wait.setMessage(getString(R.string.wait_message));
//        wait.show();

        final CgaGroup newGroup = new CgaGroup();

        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(true);
        acl.setPublicReadAccess(true);

        newGroup.setACL(acl);
        newGroup.put("user", ParseUser.getCurrentUser());

        startGspaForResult();
        startPpaForResult();
        startFpaForResult();
    }

    private void startGspaForResult() {
        startActivityForResult(
                new Intent(this, GroupSettingsPickerActivity.class),
                GSPA_REQUEST
        );
    }

    private void setGspaContainer(Bundle bundle) {
        cgaGroup.setGspaContainer(bundle);
    }

    private void processGspaResult(Intent data) {
        final Bundle gspaBundle = data.getBundleExtra(GroupSettingsPickerActivity.GSPA);
        setGspaContainer(gspaBundle);
    }

    private void startFpaForResult() {
        startActivityForResult(
                new Intent(this, FriendPickerActivity.class),
                FPA_REQUEST
        );
    }

    private void setFpaContainer(Bundle bundle) {
        cgaGroup.setFpaContainer(bundle);
    }

    private void processFpaResult(Intent data) {
        Bundle fpaBundle = data.getBundleExtra(FPA);
        setFpaContainer(fpaBundle);
    }

    private void startPpaForResult() {
        startActivityForResult(
                new Intent(this, PlacePickActivity.class),
                PPA_REQUEST
        );
    }

    private void setPpaContainer(Bundle bundle) {
        cgaGroup.setPpaContainer(bundle);
    }

    private void processPpaResult(Intent data) {
        Bundle ppaBundle = data.getBundleExtra(PPA);
        setPpaContainer(ppaBundle);
    }

    @SuppressWarnings("WeakerAccess")
    static abstract class ADependencyContainer {
        @SuppressWarnings("WeakerAccess")
        protected boolean isSet = false;

        Bundle getAsBundle() throws NotSetException {
            if (!isSet) {
                throw new NotSetException();
            }
            return null;
        }

        void setDefault() {
            isSet = true;
        }

        void setFromBundle(Bundle bundle) {
            isSet = true;
        }

        boolean isSet() {
            return isSet;
        }

        public static class NotSetException extends Exception {
            private NotSetException() {
                // Private instantiation
            }
        }
    }

    @ParseClassName("CgaGroup")
    public static class CgaGroup extends ParseObject {
        private final FpaContainer fpaContainer;
        private final GspaContainer gspaContainer;
        private final PpaContainer ppaContainer;

        public CgaGroup() {
            gspaContainer = new GspaContainer();
            fpaContainer = new FpaContainer();
            ppaContainer = new PpaContainer();
        }

        static ParseQuery<CgaGroup> getQuery() {
            return ParseQuery.getQuery(CgaGroup.class);
        }

        private void setContainer(ADependencyContainer container, Bundle bundle) {
            container.setFromBundle(bundle);
        }

        @SuppressWarnings("unused")
        GspaContainer getGspaContainer() {
            return gspaContainer;
        }

        private void setGspaContainer(Bundle bundle) {
            setContainer(gspaContainer, bundle);
        }

        @SuppressWarnings("unused")
        FpaContainer getFpaContainer() {
            return fpaContainer;
        }

        private void setFpaContainer(Bundle bundle) {
            setContainer(fpaContainer, bundle);
        }

        @SuppressWarnings("unused")
        PpaContainer getPpaContainer() {
            return ppaContainer;
        }

        public void setPpaContainer(Bundle bundle) {
            setContainer(ppaContainer, bundle);
        }
    }
}
