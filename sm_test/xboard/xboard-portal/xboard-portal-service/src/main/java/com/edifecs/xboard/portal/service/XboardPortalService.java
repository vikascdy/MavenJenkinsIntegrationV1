package com.edifecs.xboard.portal.service;

import com.edifecs.core.configuration.Configuration;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.security.data.Contact;
import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.User;
import com.edifecs.servicemanager.api.AbstractService;
import com.edifecs.xboard.portal.*;
import com.edifecs.xboard.portal.api.XPFeatureItemDatastore;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class XboardPortalService extends AbstractService implements IXboardPortalService {

    private static final String ICON_DIR = "/packages/ext-theme-edifecs/build/resources/images/edifecs-components/doormat/";
    private static final AppBarButton[] DEFAULT_BUTTONS = {
            new AppBarButton("workspaceId", "home", null, 0, ICON_DIR + "home.png", "/", false),
            new AppBarButton("doormatId", "doormatNavigation", null, 0, ICON_DIR
                    + "navigation.png", null, false),
            new AppBarButton("spacerId", null, "tbspacer", 0, null, null, false),
            new AppBarButton("searchId", "search", null, 0, null, null, true),
            new AppBarButton("notifyId", "notification", null, 0, ICON_DIR
                    + "notifications.png", null, true),
            new AppBarButton("favouriteId", "favourite", null, 0, ICON_DIR
                    + "favorites.png", null, true)};
    private static final Logger logger = LoggerFactory
            .getLogger(XboardPortalService.class);
    final SortedSet<DoormatMenu> menus = new TreeSet<>();
    final Map<String, Set<MenuEntryWithSection>> waitingEntries = new HashMap<>();

    private final Object menuLock = new Object();

    @Override
    public IXPNavigationCommandHandler getNavigationCommandHandler() {
        return new XPNavigationHandler(this);
    }

    @Override
    public IXPFeatureItemCommandHandler getFeatureItemCommandHandler() {
        return new XPFeatureItemHandler();
    }

    @Override
    public void start() throws Exception {
        // Create the Settings menu.
        // This menu is always available by default.
        final DoormatMenu settingsMenu = new DoormatMenu("settings",
                "Settings", ICON_DIR + "settings.png", true, null,
                "/", null, 0);
        addMenu(settingsMenu);
        setup();

        getLogger().debug(SystemVariables.NAVIGATION_SERVICE_TYPE_NAME + " started");
    }

    @Override
    public void stop() throws Exception {
    }

    private void setup() {
        // get nav.json files
        for (File f : Configuration.getNavigationFiles(SystemVariables.SERVICE_MANAGER_ROOT_PATH)) {
            parseNavJsonAndConfigureMenus(f);
        }

        XPFeatureItemDatastore.load(Configuration.getFeatureItemFiles(SystemVariables
                .SERVICE_MANAGER_ROOT_PATH));
    }

    public List<DoormatMenu> filterMenu() throws Exception {

        List<DoormatMenu> filteredMenu = new ArrayList<>();
        for (Iterator<DoormatMenu> menuIt = getMenus().iterator(); menuIt
                .hasNext(); ) {
            DoormatMenu m = menuIt.next();
            DoormatMenu m2 = null;
            try {
                m2 = m.clone();
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(
                        "Unable to clone Doormat Menu Obj");
            }

            if (null != m2) {
                for (Iterator<DoormatMenuEntry> entryIt = m2.getEntries(
                        DoormatMenu.Section.COLUMN1).iterator(); entryIt.hasNext(); ) {
                    DoormatMenuEntry mEntry = entryIt.next();
                    if (!hasPermission(mEntry.getPermission())) {
                        entryIt.remove();
                        logger.debug(
                                "user does not have permission to view {} entry",
                                mEntry.getText());
                    }
                }
                for (Iterator<DoormatMenuEntry> entryIt = m2.getEntries(
                        DoormatMenu.Section.COLUMN2).iterator(); entryIt.hasNext(); ) {
                    DoormatMenuEntry mEntry = entryIt.next();
                    if (!hasPermission(mEntry.getPermission())) {
                        entryIt.remove();
                        logger.debug(
                                "user does not have permission to view {} entry",
                                mEntry.getText());
                    }
                }
                for (Iterator<DoormatMenuEntry> entryIt = m2.getEntries(
                        DoormatMenu.Section.TASKS).iterator(); entryIt.hasNext(); ) {
                    DoormatMenuEntry mEntry = entryIt.next();
                    if (!hasPermission(mEntry.getPermission())) {
                        entryIt.remove();
                        logger.debug(
                                "user does not have permission to view {} entry",
                                mEntry.getText());
                    }
                }
                filteredMenu.add(m2);
            }
        }
        return filteredMenu;
    }

    private boolean hasPermission(String permission) {
        if (null != permission) {
            return getSecurityManager().getAuthorizationManager().isPermitted(permission);
        }
        return true;
    }

    private void parseNavJsonAndConfigureMenus(File f) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(f), "UTF-8");
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            logger.error("failed reading nav json : {}", f.getName(), e);
        }

        Gson gson = new Gson();
        NavMenu menu = gson.fromJson(reader, NavMenu.class);

        if (null != menu) {
            addMenu(menu.getId(), menu.getName(), menu.isActive(),
                    menu.getType(), menu.getTaskHeading(), menu.getIconUrl(),
                    menu.getDefaultLinkUrl(), menu.getWeight());

            if (menu.getColumnOne() != null)
                for (NavEntry entry : menu.getColumnOne())
                    configureNavEntry(entry, menu.getId(), "columnOne");

            if (menu.getColumnTwo() != null)
                for (NavEntry entry : menu.getColumnTwo())
                    configureNavEntry(entry, menu.getId(), "columnTwo");
            if (menu.getTasks() != null)
                for (NavEntry entry : menu.getTasks())
                    configureNavEntry(entry, menu.getId(), "tasks");

            logger.debug("Registered menu : {} ", f.getName());
        }
    }

    // nav menu entry to doormat menu entry
    private void configureNavEntry(NavEntry entry, String menuId, String section) {
        try {
            if (entry.getNamespace() == null) {
                getLogger().warn(
                        "Entry in menu '{}' with text '{}' does not"
                                + " have a 'namespace' attribute. Skipping.",
                        menuId, entry.getText());
                return;
            }
            if (entry.getId() == null) {
                getLogger().warn(
                        "Entry in menu '{}' with text '{}' does not"
                                + " have an 'id' attribute. Skipping.", menuId,
                        entry.getText());
                return;
            }
            if (entry.getPermission() == null) {
                throw new IllegalStateException(String.format(
                        "Entry in menu '%s' with text '%s' does not"
                                + " have a 'permission' attribute", menuId,
                        entry.getText()));
            }
            final String[][] subMenu = new String[entry.getSubMenu() == null ? 0
                    : entry.getSubMenu().length][];
            for (int i = 0; i < subMenu.length; i++)
                subMenu[i] = new String[]{entry.getSubMenu()[i].getText(),
                        entry.getSubMenu()[i].getLinkUrl(), String.valueOf(entry.getSubMenu()[i].getWeight())};

            registerMenuEntry(menuId, section, entry.getNamespace(),
                    entry.getId(), entry.getText(), entry.getLinkUrl(), entry.getWeight(),
                    entry.getTarget(), entry.getJavascript(),
                    entry.getPermission(), subMenu);

        } catch (NullPointerException ex) {
            getLogger().warn("Error registering an entry from the nav.json file. Please verify that the file structure are correct and that there are no trailing comma's.", ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            getLogger()
                    .warn("Failed to register navigation menu entry with id '"
                                    + entry.getNamespace() + ":" + entry.getId() + "'.",
                            ex);
        }
    }

    // nav Menu to doormat menu
    public boolean addMenu(String id, String name, Boolean active, String type,
                           String taskHeading, String iconUrl, String defaultLinkUrl, int weight) {
        if (active == null)
            active = true;
        if (taskHeading == null)
            taskHeading = "tasks";

        final DoormatMenu menu = new DoormatMenu(id, name, iconUrl, active,
                taskHeading, defaultLinkUrl, type, weight);
        return addMenu(menu);
    }

    public boolean registerMenuEntry(String menuId, String sectionStr,
                                     String namespace, String id, String text, String linkUrl, int weight,
                                     String target, String javascript, String permission,
                                     String[][] subMenu) {
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
                        + i, subMenu[i][0], Integer.valueOf(subMenu[i][2]), subMenu[i][1]);
        } else {
            subMenuEntries = new DoormatMenuEntry[0];
        }
        // Create the entry.
        final DoormatMenuEntry entry = new DoormatMenuEntry(namespace, id,
                text, weight, linkUrl, target, javascript, permission, subMenuEntries);
        return addEntry(menuId, section, entry);
    }

    boolean addMenu(DoormatMenu menu) {
        synchronized (menuLock) {
            getLogger().debug("Registering menu '{}'.", menu.getId());
            boolean result = menus.add(menu);
            if (result && waitingEntries.containsKey(menu.getId())) {
                for (MenuEntryWithSection e : waitingEntries.remove(menu
                        .getId())) {
                    getLogger().debug(
                            String.format("Adding queued menu entry '%s:%s' to"
                                            + " menu '%s'.", e.entry.getNamespace(),
                                    e.entry.getId(), menu.getId()));
                    menu.addEntry(e.section, e.entry);
                }
            } else if (!result) {
                getLogger().debug("A menu already exists with the id '{}'.",
                        menu.getId());
            }
            return result;
        }
    }

    boolean addEntry(String menuId, DoormatMenu.Section section,
                     DoormatMenuEntry entry) {
        synchronized (menuLock) {
            getLogger().debug(
                    String.format("Registering menu entry '%s:%s' in section"
                                    + " '%s' of menu '%s'.", entry.getNamespace(),
                            entry.getId(), section.name(), menuId));
            for (DoormatMenu menu : menus) {
                if (menu.getId().equals(menuId)) {
                    boolean result = menu.addEntry(section, entry);
                    if (!result)
                        getLogger()
                                .debug(String
                                        .format("A menu entry already exists with the"
                                                        + " id '%s:%s' in section '%s' of menu '%s'.",
                                                entry.getNamespace(),
                                                entry.getId(), section.name(),
                                                menuId));
                    return result;
                }
            }
            getLogger().debug(
                    String.format(
                            "No menu with the id '%s' exists. Queueing menu"
                                    + " entry '%s:%s'.", menuId,
                            entry.getNamespace(), entry.getId()));
            Set<MenuEntryWithSection> queue = waitingEntries.get(menuId);
            if (queue == null) {
                queue = new HashSet<MenuEntryWithSection>();
                waitingEntries.put(menuId, queue);
            }
            boolean result = queue
                    .add(new MenuEntryWithSection(entry, section));
            if (!result)
                getLogger()
                        .debug(String
                                .format("A menu entry is already queued with the id"
                                                + " '%s:%s' for section '%s' of menu id '%s'.",
                                        entry.getNamespace(), entry.getId(),
                                        section.name(), menuId));
            return result;
        }
    }

    Collection<DoormatMenu> getMenus() {
        return menus;
    }

    boolean removeMenu(String id) {
        synchronized (menuLock) {
            final Iterator<DoormatMenu> iter = menus.iterator();
            while (iter.hasNext()) {
                if (iter.next().getId().equals(id)) {
                    iter.remove();
                    return true;
                }
            }
            return false;
        }
    }

    void clearMenus() {
        synchronized (menuLock) {
            menus.clear();
            waitingEntries.clear();
        }
    }

    AppBar getAppBar(User user, Organization organization) {
        final AppBar appBar = new AppBar(DEFAULT_BUTTONS);
        if (user != null) {
            final Contact contact = user.getContact();
            String organizationName = "";
            if (organization != null) {
                organizationName = organization.getCanonicalName();
            }
            appBar.addButton(new AppBarButton("userId", "simpleMenu", null, 0,
                    ICON_DIR + "user.png", null, false, new AppBarMenuEntry(
                    "userProfile", contact.getFirstName() + " " + contact.getLastName(),
                    "/esm/#!/AccountSettings",
                    organizationName), new AppBarMenuEntry("about",
                    "About", "/#!/about"), new AppBarMenuEntry(
                    "logout", "Logout", "/esm/#!/logout")));
        }
        return appBar;
    }

    private static class MenuEntryWithSection {
        DoormatMenuEntry entry;
        DoormatMenu.Section section;

        MenuEntryWithSection(DoormatMenuEntry entry, DoormatMenu.Section section) {
            this.entry = entry;
            this.section = section;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + entry.hashCode();
            result = prime * result + section.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MenuEntryWithSection other = (MenuEntryWithSection) obj;
            if (!entry.equals(other.entry))
                return false;
            if (section != other.section)
                return false;
            return true;
        }
    }
}
