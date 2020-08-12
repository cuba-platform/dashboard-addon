<p>
    <a href="http://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat" alt="license" title=""></a>
    <a href="https://travis-ci.org/cuba-platform/dashboard-addon"><img src="https://travis-ci.org/cuba-platform/dashboard-addon.svg?branch=master" alt="Build Status" title=""></a>
</p>

# Dashboards

- [Overview](#overview)   
- [Installation](#installation)
  - [From the Marketplace](#from-the-marketplace)  
  - [By coordinates](#by-coordinates)
- [Configuration](#configuration)
  - [Extending Application Theme](#extending-application-theme)
  - [Adding Widget Types](#adding-widget-types)
- [Usage](#usage)
 - [Dashboards](#dashboards)
   - [Dashboard Fields](#dashboard-fields)
   - [Dashboard Parameters](#dashboards-parameters)
   - [Palette](#palette)
     - [Widgets](#widgets)
     - [Layouts](#layouts)
     - [Templates](#templates)
   - [Canvas](#canvas)
   - [Dashboard Layout Structure](#dashboard-layout-structure)
   - [Buttons Panel](#buttons-panel)
  - [Dashboard Groups](#dashboard-groups)
  - [Widget Templates](#widget-templates)
   - [Widget Template Editor](#widget-template-editor)
   - [Parameter Editor](#parameter-editor)
 - [Integration of the Component Dashboard-UI](#integration-of-the-dashboard-ui-component)
   - [Loading a dashboard from JSON file](#loading-a-dashboard-from-json-file)


# 1. Overview <a name="overview"></a>

This component enables creating and embedding dashboards into your application screens. Dashboards allow visualizing summarized information, data sets, charts and can be accessible only by authorized users.

A dashboard consists of widgets — individual elements based on a frame.  An integrated set of layouts allows positioning widgets on a dashboard according to your needs. Use responsive layouts to adapt your dashboards to different displays.

You can add your own widgets or use  [Dashboard Chart Add-on](https://github.com/cuba-platform/dashboard-chart-addon) that provides additional chart widgets for dashboard add-on.

See [sample application](https://github.com/cuba-platform/dashboard-addon-demo) using this add-on.

See [webinar](https://www.youtube.com/watch?v=nl-wsnC9K4A) on the CUBA Platform channel.

# 2. Installation <a name="installation"></a>

The add-on can be added to your project in one of the ways described below. Installation from the Marketplace is the simplest way. The last version of the add-on compatible with the used version of the platform will be installed.
Also, you can install the add-on by coordinates choosing the required version of the add-on from the table.

In case you want to install the add-on by manual editing or by building from sources see the complete add-ons installation guide in [CUBA Platform documentation](https://doc.cuba-platform.com/manual-latest/manual.html#app_components_usage)

## 2.1. From the Marketplace <a name="from-the-marketplace"></a>

1. Open your application in CUBA Studio. Check the latest version of CUBA Studio on the  [CUBA Platform site](https://www.cuba-platform.com/download/previous-studio/).
2. Go to *CUBA -> Marketplace* in the main menu.

  ![marketplace](img/marketplace.png)

3. Find the Dashboards add-on there.

  ![addons](img/addons.png)

4. Click *Install* and apply the changes.
The add-on corresponding to the used platform version will be installed.


## 2.2. By Coordinates <a name="by-coordinates"></a>

1. Open your application in CUBA Studio. Check the latest version of CUBA Studio on the [CUBA Platform site](https://www.cuba-platform.com/download/previous-studio/).
2. Go to *CUBA -> Marketplace* in the main menu.
3. Click the icon in the upper-right corner.

 ![by-coordinates](img/by-coordinates.png)

4. Paste the add-on coordinates in the corresponding field as follows:

 `com.haulmont.addon.dashboard:dashboard-global:<add-on version>`

 where `<add-on version>` is compatible with the used version of the CUBA platform.

 | Platform Version | Add-on Version |
|------------------|----------------|
| 7.2.X            | 3.2.3          |
| 7.1.X            | 3.1.3          |
| 7.0.X            | 3.0.4          |
| 6.10.X           | 2.0.1          |

5. Click *Install* and apply the changes. The add-on will be installed to your project.

# 3.Configuration <a name="configuration"></a>

Before starting working with dashboard editor in your application you should do some configuration setting: extend application theme and add widgets.

## 3.1. Extending Application Theme <a name="extending-application-theme"></a>

For the correct rendering of the add-on UI controls, it is recommended to extend the `hover` theme in your application as described in [CUBA Platform documentation](https://doc.cuba-platform.com/manual-latest/web_theme_extension.html).

## 3.2. Adding Widget Types <a name='adding-widget-types'></a>

By default, the add-on does not have preset widgets. To add an additional widget type, you need to do the following:

1. Create a fragment as described in [CUBA Platform documentation](https://doc.cuba-platform.com/manual-latest/using_screen_fragments.html).
2. Add the annotation `com.haulmont.addon.dashboard.web.annotation.DashboardWidget`. Fill in the fields: `name`, `editFrameId`(optional, leave empty if there is no parameter in widget) in the annotation (see JavaDoc).
3. `widget`, `dashboard`, `dashboardFrame` can be included in widget via `@WindowParam` annotation. Widget parameters in widget editor and widget frames should have `@WidgetParam` and `@WindowParam` annotations.
For example:

```java
@DashboardWidget(name = CAPTION, editFrameId = "dashboard$LookupWidget.edit")
public class LookupWidget extends AbstractFrame implements RefreshableWidget {
    public static final String CAPTION = "Lookup";

    @WindowParam
    protected Widget widget;

    @WindowParam
    protected Dashboard dashboard;

    @WindowParam
    protected DashboardFrame dashboardFrame;

    @WidgetParam
    @WindowParam
    protected String lookupWindowId;

    public String getLookupWindowId() {
        return lookupWindowId;
    }

    public void setLookupWindowId(String lookupWindowId) {
        this.lookupWindowId = lookupWindowId;
    }
}
```

4. Add the frame for editing widget in the web module and register it in `web-screens.xml`. For example:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<window xmlns="http://schemas.haulmont.com/cuba/window.xsd"
        class="com.haulmont.addon.dashboard.web.widget.lookup.LookupWidgetEdit">
    <layout spacing="true">
        <hbox id="lookupIdBox"
              spacing="true">
            <label width="85px"
                   value="msg://lookupId"/>
            <lookupField id="lookupIdLookup"
                         nullOptionVisible="false"
                         required="true"/>
        </hbox>
    </layout>
</window>
```

```java
public class LookupWidgetEdit extends AbstractFrame {
    @Inject
    protected LookupField lookupIdLookup;
    @Inject
    protected WindowConfig windowConfig;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ScreenXmlLoader screenXmlLoader;

    protected Datasource<Widget> widgetDs;

    @WidgetParam
    @WindowParam
    protected String lookupWindowId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        lookupIdLookup.setOptionsList(getAllLookupIds());
        lookupIdLookup.addValueChangeListener(e -> lookupIdSelected((String) e.getValue()));

        initWidgetDs(params);
        selectLookupId();
    }

    protected void initWidgetDs(Map<String, Object> params) {
        widgetDs = (Datasource<Widget>) params.get(WidgetEdit.ITEM_DS);
    }
}
```

If widget frame implements `RefreshableWidget` interface, then method `refresh()` will be invoked automatically on each dashboard update event.

# 4. Usage <a name="usage"></a>

After installation the add-on in your project the *Dashboard* menu will appear containing *Dashboards* and *Widget Template* screens.

![menu-dashboards](img/menu-dashboards.png)

## 4.1. Dashboards <a name='dashboards'></a>

*Dashboard browser* contains the list of dashboards and buttons for creating, editing and removing dashboards in a database.

![persistent-dashboard](img/persistent-dashboards.png)

*Dashboard editor* screen appears after clicking the *Create* button on the *Dashboards* screen and allows editing a dashboard.

![dashboard-editor-common](img/dashboard-editor-common.png)

*Dashboard Editor* screen contains 6 areas:
- the dashboard fields;
- the dashboard parameters;
- the palette with widgets and layouts;
- the canvas  where the position of dashboard elements (widgets and layouts) is specified;
- the tree representation of the edited dashboard structure;
- the buttons panel.

### 4.1.1 Dashboard Fields <a name='dashboard-fields'></a>

To save the created dashboard it is necessary to fill in at least the required fields:
- *Title* - a name of the dashboard;
- *Code* - a unique identifier for a more convenient search in a database;

The following fields are available to set:
- *Refresh period (sec)* - a time period in seconds for refresh a dashboard UI;
- *Assistant bean name* - an optional reference to a Spring bean class that should be used for customizing the dashboard (assistance bean must have `prototype` bean scope).
- *Group* - a dashboard group;
- *Available for all users* - a flag which defines the user access to the dashboard. If set to `false`, then only the user who created the dashboard can view and edit it. Otherwise, all users can view and edit the dashboard.

### 4.1.2 Dashboard Parameters <a name='dashboards-parameters'></a>

The frame with dashboard parameters which allows adding, editing and removing dashboard parameters. These parameters are passed as input parameters for the widgets in this dashboard. For more information on adding and editing parameters, see [Parameter Editor](#parameter-editor).

### 4.1.3 Palette <a name='palette'></a>

It is a container with 3 collapsible tabs. Each tab contains a container with components. When a component is dragged to the canvas, the corresponding element will be added to the canvas.

#### 4.1.3.1 Widgets <a name='widgets'></a>

By default, there are no widgets in this tab. You can add widgets as described in [Adding Widget Types](#adding-widget-types) section. Drag an element from the palette for adding it on the canvas, and the widget editor will be opened in a dialog window. It is possible to make the widget a template (in this case, it is added to the tab *Widget Templates*).

![palette-widgets](img/palette-widgets.png)

#### 4.1.3.2 Layouts <a name='layouts'></a>

Layouts help to place widgets in the specific way. Add the required layout before adding widgets on the canvas.

![palette-layouts](img/palette-layouts.png)

The following layouts are available:
- **vertical** - widgets are placed vertically one after another;
- **horizontal** -  widgets are placed horizontally one after another;
- **grid** - widgets are placed inside a grid with a specified number of rows and columns;
- **css** - enables full control over placement and styling of enclosed components using CSS;
- **responsive** - widgets are placed vertically, but depending on the screen width, the number of columns with widgets changes.
After adding this layout on the canvas the setting form will appear. The slider shows which part of the screen will be occupied by one widget when opening the dashboard on a particular device.

![responsive-settings](img/responsive-settings.png)

#### 4.1.3.3. Templates <a name='templates'></a>

Contains widget templates from a database. Templates can be created from the widgets added on the canvas using the corresponding button, or by using [Widget Template Browser](#widget-template-browser).

![palette-widget-templates](img/palette-widget-templates.png)


### 4.1.4 Canvas <a name='canvas'></a>

It is a container in which you can place widgets and layouts. Drag an element from the palette for adding it on the canvas.

![canvas-drag-grid-layout](img/canvas-drag-grid-layout.png)

When dragging the *Grid Layout* to the canvas the dialog will open where you can set the number of rows and columns. When dragging a widget, the *Widget Editor* dialog will open. When dragging the *Responsive Layout* the dialog with settings will open.

Example of the dashboard with widgets:

![canvas-with-widgets](img/canvas-with-widgets.png)

Click on a layout or a widget to select it. The selected element contains the panel with the following buttons:

![layout-buttons](img/layout-buttons.png)    

![trash](img/trash.png) - deletes a container from the canvas;

![gear](img/gear.png) - opens the *Widget Editor*;

![template](img/template.png) - opens the *Template Weidget Editor*

![arrows](img/arrows.png) - changes the weight (expand ratio) of a container in a parent container, or define `colspan` and `rowspan` attributes for the grid layout cells:

![colspan-rowspan](img/colspan-rowspan.png)

![brush](img/brush.png) - changes the style of a container: define the style name, modify the container's width and height:

![style-editor](img/style-editor.png)

![name](img/name.png) - displays the element name specified by user.

For more information on using custom styles see [CUBA Platform documentation](https://doc.cuba-platform.com/manual-latest/web_theme_extension.html#web_theme_extension_styles).

### 4.1.5. Dashboard Layout Structure <a name='dashboard-layout-structure'></a>

Displays the current dashboard structure as a tree. The *Root* element is available by default and cannot be removed.

![palette-tree](img/palette-tree.png)

The following actions are available for the tree elements from the context menu:

- **Expand** - defines a component within a container that should be expanded to use all available space in the direction of component placement. For a container with vertical placement, this attribute sets 100% height to a component; for the containers with horizontal placement - 100% width. Additionally, resizing a container will resize the expanded component.
- **Style** - enables setting a style name and modifying the component's height and width.
- **Remove** - removes a component from the tree.
- **Weight** - changes the weight (expand ratio) of a container in a parent container.
- **Edit** - opens the widget editor.
- **Template** - opens the widget template editor.

![tree-context-menu](img/tree-context-menu_1.png) ![tree-context-menu](img/tree-context-menu_2.png)


### 4.1.6. Buttons Panel <a name='buttons-panel'></a>

This panel contains the following buttons:

- *OK* - saves the dashboard and close the editor;
- *Cancel* - closes the editor without saving the dashboard;
- *Propagate* - publishes event `com.haulmont.addon.dashboard.web.events.DashboardUpdatedEvent`;
- *Export Json* - export the dashboard to a JSON file;
- *Import Json* - import the dashboard from a JSON file and refresh the editor.

## 4.2 Dashboard Groups <a name='dashboard-groups'></a>

The screen *Dashboard Groups* allows creating, editing, and removing dashboard groups. The screen *Dashboard Group Editor* allows adding or excluding dashboards in a dashboard group from a database.

To open the *Dashboard Groups* browser, click the *Groups* button in the *Dashboard browser* screen.

 ![dashboard-group-browser](img/dashboard-group-browser.png)

 ![dashboard-group-editor](img/dashboard-group-editor.png)

## 4.3. Widget Templates <a name="widget-templates"></a>

 This screen allows creating, editing and removing widget templates. Widget templates are  preconfigured widgets which can be reused. Widget templates are stored in a database. This screen is available from the application menu.

 ![menu-widget-templates](img/menu-widget-templates.png)

 ![widget-template-browser](img/widget-template-browser.png)

### 4.3.1 Widget Template Editor <a name="widget-template-editor"></a>

This screen allows editing a widget and consists of the following elements:

- the *Name* field;
- the *Group* drop-down;
- the *Widget Type* lookup field;
- the *Customize* button;
- the checkbox to set the widget visibility.

 ![widget-editor](img/widget-editor.png)

When a user clicks the *Customize* button, the enhanced widget editor will be opened, consisting of the following elements:

- the *Caption* field;
- the *Widget Id* field;
- the *Description* field;
- the *Show Widget Caption* checkbox;
- the *Lookup id* field, for the frames inherited from the `com.haulmont.cuba.gui.components.AbstractLookup`. Widgets of this type fire `WidgetEntitiesSelectedEvent` which contains the selected entities;  
- the frame with widget parameters which allows adding, editing and removing widget parameters. These parameters are passed as input parameters for the frame, based on which the widget was taken. For more information on adding and editing parameters, see [Parameter Editor](#parameter-editor).

 ![widget-editor-customize](img/widget-editor-customize.png)

### 4.3.2 Parameter Editor <a name='parameter-editor'></a>

 This screen allows editing parameters. A parameter is a key-value pair, where the *Name* field is the key and the *Value* field is a value.
 The following types of values are available:
 - ENTITY
 - LIST_ENTITY
 - ENUM
 - DATE
 - DATETIME
 - TIME
 - UUID
 - INTEGER
 - STRING
 - DECIMAL
 - BOOLEAN
 - LONG

 ![parameter-editor](img/parameter-editor.png)

## 4.4. Integration of the Dashboard-UI Component <a name='integration-of-the-dashboard-ui-component'></a>

To use the `dashboard-ui` component in your screen, you need to add the special scheme `http://schemas.haulmont.com/cubadshb/ui-component.xsd` in the XML descriptor of the screen.    
Then add a namespace like `dashboard` for the schema. The schema contains information about the tag `dashboard`, which can contain the `parameter` elements.

Here is an example of adding dashboard to the screen:

```xml
 <?xml version="1.0" encoding="UTF-8" standalone="no"?>
 <window xmlns="http://schemas.haulmont.com/cuba/window.xsd"
         class="com.haulmont.example.web.SomeController"
         xmlns:dashboard="http://schemas.haulmont.com/cubadshb/ui-component.xsd">   
     ...
         <dashboard:dashboard id="dashboardId"
                              code="usersDashboard"
                              timerDelay="60">
              <dashboard:parameter name="role" value="Admin" type="string"/>           
         </dashboard:dashboard>
     ...
```

Dashboard tag contains the following attributes:

- `code` - the attribute which will serve for a dashboard search in a database;
- `jsonPath` - the `classPath` to the dashboard JSON file;
- `class` - the controller class of the `dashboard-ui` component which has to be inherited from `com.haulmont.addon.dashboard.web.dashboard.frames.uicomponent.WebDashboardFrame`;
- `timerDelay` - the time period in seconds for refresh a dashboard-ui.

**Note:** when embedding a dashboard, you must specify the `code` or `jsonPath` attribute. When specifying at the same time, the attribute `code` takes precedence over `jsonPath`.

Dashboard parameter tag has the following attributes:

- `name` - the name of the parameter, required;
- `value` - the value of the parameter, required;
- `type` - the type of the value, can take one of the following values: boolean, date, dateTime, decimal, int, long, string, time, uuid.

**Note:** by default, the parameter type is set to string.

### 4.4.1. Loading a dashboard from JSON file <a name='loading-a-dashboard-from-json-file'></a>

You can load a dashboard to the screen from JSON file. Use `jsonPath` attribute and set the relative path to the file in the XML descriptor. For example:

```xml
         <dashboard:dashboard id="dashboardId"
                              jsonPath="com/haulmont/sample/petclinic/web/screens/main/Users Dashboard.json"
                              timerDelay="60">
              <dashboard:parameter name="role" value="Admin" type="string"/>           
         </dashboard:dashboard>
     ...
```

A dashboard created in the *Dashboard Editor* screen can be exported into JSON file, just click the *Export json* button and specify the path to the file.

Also, you can import a dashboard from the file in the *Dashboard Editor* screen, just click the *Import json* button, enter the path to the file, specify the unique code and click *OK* to save a dashboard.

## 4.5. Predefined roles <a name='predefined-roles'></a>

Predefined security roles:
- **Dashboard admin** - allows user to create or edit dashboards
- **Dashboard view** - allows user to see embedded dashboards
- **Dashboard browse** - allows user to browse a list of available dashboards
