package com.edifecs.xboard.portal.service;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.epp.security.service.ISecurityService;
import com.edifecs.xboard.portal.AppBar;
import com.edifecs.xboard.portal.DoormatMenu;
import com.edifecs.xboard.portal.DoormatMenuEntry;

import java.util.List;

public class XPNavigationHandler extends AbstractCommandHandler implements IXPNavigationCommandHandler {

    private final XboardPortalService service;

    public XPNavigationHandler(XboardPortalService service) {
        this.service = service;
    }

    public boolean registerMenu(String id, String name, int weight, Boolean active, String type,
                                String taskHeading, String iconUrl, String defaultLinkUrl, byte[] iconData) {
        if (iconData != null)
            throw new UnsupportedOperationException(
                    "Uploading icons is not yet" + " implemented.");
        if (active == null)
            active = true;
        if (taskHeading == null)
            taskHeading = "tasks";

        final DoormatMenu menu = new DoormatMenu(id, name, iconUrl, active,
                taskHeading, defaultLinkUrl, type, weight);
        return service.addMenu(menu);
    }

    public boolean registerMenuEntry(String menuId, String sectionStr, String namespace, String id, int weight,
                                     String target, String javascript, String permission, String text, String linkUrl, String[][] subMenu) {
        // Parse 'section' parameter.
        final DoormatMenu.Section section;
        switch (sectionStr) {
            case "columnOne":
                section = DoormatMenu.Section.COLUMN1;
                break;
            case "columnTwo":
                section = DoormatMenu.Section.COLUMN2;
                break;
            case "tasks":
                section = DoormatMenu.Section.TASKS;
                break;
            default:
                throw new IllegalArgumentException("Menu entry section must be"
                        + " one of 'columnOne', 'columnTwo', or 'tasks'. '"
                        + sectionStr + "' is not a valid value.");
        }
        // Parse 'subMenu' parameter.
        final DoormatMenuEntry[] subMenuEntries;
        if (subMenu != null) {
            subMenuEntries = new DoormatMenuEntry[subMenu.length];
            for (int i = 0; i < subMenu.length; i++)
                subMenuEntries[i] = new DoormatMenuEntry(namespace, id + "-"
                        + i, subMenu[i][0], weight, subMenu[i][1]);
        } else {
            subMenuEntries = new DoormatMenuEntry[0];
        }
        // Create the entry.
        final DoormatMenuEntry entry = new DoormatMenuEntry(namespace, id,
                text, weight, linkUrl, target, javascript, permission, subMenuEntries);
        return service.addEntry(menuId, section, entry);
    }

    public List<DoormatMenu> getMenus() throws Exception {
        return service.filterMenu();
    }

    public AppBar getAppBar() throws SecurityManagerException {
        User user = getSecurityManager().getSubjectManager().getUser();
        Organization organization = getSecurityManager().getSubjectManager().getOrganization();

        return service.getAppBar(user, organization);
    }

    public String getLogo(String tenant) {
        String logo = SystemVariables.DEFAULT_EDIFECS_LOGO;
        try {
            String tLogo = isc().getService(ISecurityService.class).tenants().getTenantLogo(tenant);
            if (tLogo != null)
                logo = tLogo;
        } catch (Exception e) {
            getLogger().debug("No custom tenant logo found", e);
        }
        return logo;
    }

}
