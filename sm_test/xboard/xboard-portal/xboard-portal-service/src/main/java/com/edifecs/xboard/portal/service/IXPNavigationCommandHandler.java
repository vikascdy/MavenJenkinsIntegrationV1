package com.edifecs.xboard.portal.service;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.xboard.portal.*;

import java.util.List;

@JsonSerialization(adapters = {
	@TypeAdapter(MenuSerializer.class),
	@TypeAdapter(MenuEntrySerializer.class),
	@TypeAdapter(AppBarSerializer.class)})
@CommandHandler()
public interface IXPNavigationCommandHandler {

	@SyncCommand(name = SystemVariables.NAVIGATION_REGISTER_MENU_COMMAND)
	public boolean registerMenu(
			@Arg(name = "id", required = true) String id,
			@Arg(name = "name", required = true) String name,
            @Arg(name = "weight", required = false) int weight,
			@Arg(name = "active", required = false) Boolean active,
			@Arg(name = "type", required = false) String type,
			@Arg(name = "taskHeading", required = false) String taskHeading,
			@Arg(name = "iconUrl", required = false) String iconUrl,
			@Arg(name = "defaultLinkUrl", required = false) String defaultLinkUrl,
			@Arg(name = "iconData", required = false) byte[] iconData);

	@SyncCommand(name = SystemVariables.NAVIGATION_REGISTER_ENTRY_COMMAND)
	public boolean registerMenuEntry(
			@Arg(name = "menuId", required = true) String menuId,
			@Arg(name = "section", required = true) String sectionStr,
			@Arg(name = "namespace", required = true) String namespace,
			@Arg(name = "id", required = true) String id,
            @Arg(name = "weight", required = false) int weight,
			@Arg(name = "target", required = true) String target,
			@Arg(name = "javascript", required = true) String javascript,
			@Arg(name = "permission", required = true) String permission,
			@Arg(name = "text", required = true) String text,
			@Arg(name = "linkUrl", required = false) String linkUrl,
			@Arg(name = "subMenu", required = false) String[][] subMenu);

	@SyncCommand(name = "getMenus")
	public List<DoormatMenu> getMenus() throws Exception;

	@SyncCommand(name = "getAppBar")
	public AppBar getAppBar() throws SecurityManagerException ;

	@SyncCommand
	public String getLogo(
            @Arg(name = "tenant", required = false) String tenant);
}
