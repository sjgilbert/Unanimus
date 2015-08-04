<!--
	Unanimus - CHANGELOG.md
	Copyright (c) 2015 Sam Gilbert et. al.
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
* Users can login to existing acccounts
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
* Reorganized UnanmusProject/res

###v0.7.0 -- Facebook Login
* Added facebook login
* Minor layout changes & renaming
* Modified MainActivity

<!-- vim : set ts=2 sw=2 et syn=markdown : -->
