<!--
  Unanimus - CHANGELOG.md Copyright (c) 2015 Sam Gilbert et. al.
-->
#Unanimus -- CHANGELOG.md
##v0 -- Alpha
###v0.0.0 -- Initial Commit
* Make group with parse
* Echo object id locally

###v0.1.0 -- Added group object
* User is assigned to the group

###v0.2.0 -- Users can join groups
* Groups can have multiple users
* Users can join groups with the objectID

###v0.3.0 -- Register Users
* Users can register accounts
* Users can login to existing accounts
* App directs users to screen based on cached user (or lack of)
* Cleaned up code in MakeGroupActivity

###v0.4.0 -- Group View
* New Main view links to Join and Make
* Main shows all groups user is a member of
* New Group view shows groupID, creator and members
* Renamed ParseApplication to UnanimusApplication
* Touched up logic in Join activity
* Changed "members" in UnanimusGroup to array of Strings (usernames)

###v0.5.0 -- Settings and User data
* Framework for settings and user data in place
* Default settings json created
* Settings load at StartupActivity

###v0.6.0 -- Theme updated
* "Custom" theme
* Reorganized UnanimusProject/res

###v0.7.0 -- Facebook Login
* Added facebook login
* Minor layout changes & renaming
* Modified MainActivity

###v0.8.0 -- Facebook Friend Picker
* FriendPickerActivity implemented
* Touch up to GroupActivity
* Added facebookID as a field stored written ParseUser for future lookups

###v0.9.0 -- Google place picker
* Select location on map
* Preview location
* Dummy submit, pick place by address, and use current location

###v0.10.0 -- Group Settings Picker
* Created GroupSettingsPickerActivity
* GroupSettingsPickerActivity started for result
* User selects:
  * date
  * time
  * radius
  * price

###v0.11.0 -- Select location by last known
* Result from `PlacePickActivity` changed from `Place` to `LatLng`
* Users can select location by their last known location

###v0.12.0 -- Select location by address
* Users can select a location by address

###v0.13.0 -- Start and get result from CreateGroupActivity Activity dependencies
* CreateGroupActivity starts PlacePickActivity, FriendPickerActivity, and GroupSettingsActivity for result
* CreateGroupActivity receives and processes results from dependencies
* CreateGroupActivity Activity dependencies extend CreateGroupActivity.ADependencyContainer

###v0.14.0 -- Voting Logic
* Voting logic for single user works
* reimplemented GroupActivity

<!-- vim : set ts=2 sw=2 et syn=markdown : -->
